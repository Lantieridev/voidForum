# API.md - Documentación de la API

## Base URL

```
http://localhost:8080/api
```

## MongoDB

- **Database**: voidforum
- **URI**: `mongodb+srv://<username>:<password>@<your-cluster>.mongodb.net/voidforum` (se lee de la variable de entorno `MONGO_URI`, ver README.md — nunca commitear valores reales)

## Autenticación

Todos los endpoints (excepto `/auth/register` y `/auth/login`) requieren el header:
```
Authorization: Bearer <token_jwt>
```

---

## Colecciones MongoDB (dominio)

Los campos reflejan el modelo de dominio real (`domain/model/`), no necesariamente 1:1 con los `@Document` de `infrastructure/persistence/entity/` (que pueden agregar el `_id` de Mongo, etc).

### users
```json
{
  "id": "string",
  "username": "string (unique)",
  "email": "string (unique)",
  "password": "string (hasheado con BCrypt)",
  "displayName": "string",
  "bio": "string",
  "notifyLikes": true,
  "notifyComments": true,
  "notifyMentions": true,
  "followingIds": ["userId"],
  "followerCount": 0,
  "followingCount": 0,
  "savedPosts": ["postId"],
  "createdAt": "datetime"
}
```

### posts
```json
{
  "id": "string",
  "content": "string",
  "authorId": "string (user.id)",
  "authorUsername": "string",
  "tags": ["string"],
  "voteCount": 0,
  "commentCount": 0,
  "savedCount": 0,
  "createdAt": "datetime"
}
```
Nota: los posts no tienen campo `title` — solo `content` y `tags`.

### comments
```json
{
  "id": "string",
  "content": "string",
  "postId": "string (post.id)",
  "parentCommentId": "string | null",
  "authorId": "string (user.id)",
  "authorUsername": "string",
  "createdAt": "datetime"
}
```
`parentCommentId` habilita replies anidados (`null` = comentario de primer nivel).

### votes
```json
{
  "id": "string",
  "userId": "string (user.id)",
  "targetId": "string (post.id o comment.id)",
  "targetType": "post | comment",
  "value": 1
}
```
`value` es `1` (upvote) o `-1` (downvote). El voto se aplica sobre posts o comentarios indistintamente vía `targetType`, no hay endpoints de voto separados por recurso.

---

## Endpoints

### Auth

#### POST /auth/register
Registro de nuevo usuario. No requiere auth.

**Request**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response** (201) — `UserResponseDto` (sin token):
```json
{
  "id": "...",
  "username": "string",
  "email": "string",
  "displayName": null,
  "bio": null,
  "notifyLikes": true,
  "notifyComments": true,
  "notifyMentions": true,
  "createdAt": "datetime"
}
```

#### POST /auth/login
Login de usuario. No requiere auth.

**Request**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Response** (200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "...",
    "username": "string",
    "email": "string",
    "displayName": "string",
    "bio": "string",
    "notifyLikes": true,
    "notifyComments": true,
    "notifyMentions": true,
    "createdAt": "datetime"
  }
}
```

#### GET /auth/me
Obtener usuario actual a partir del token (requiere header `Authorization`).

**Response** (200): mismo shape que `login` (`token` + `user`).

---

### Posts

Todos bajo `/api/posts`. No hay paginación server-side (`GET /posts` devuelve la lista completa como `PostResponseDto[]`).

#### POST /posts
Crear post (requiere auth).

**Request**:
```json
{
  "content": "string",
  "tags": ["tag1", "tag2"]
}
```

**Response** (201) — `PostResponseDto`:
```json
{
  "id": "...",
  "content": "string",
  "authorUsername": "string",
  "authorId": "...",
  "tags": ["tag1", "tag2"],
  "voteCount": 0,
  "commentCount": 0,
  "createdAt": "datetime",
  "savedCount": 0,
  "authorDisplayName": "string"
}
```

#### GET /posts
Listar todos los posts.

**Response** (200): `PostResponseDto[]` (ver shape arriba).

#### GET /posts/search?q={texto}
Búsqueda libre de posts.

#### GET /posts/search/by-tag?tag={tag}
Filtrar posts por tag exacto.

#### GET /posts/search/by-author?username={username}
Posts de un autor.

#### GET /posts/search/by-content?content={texto}
Búsqueda de posts por contenido.

Las cuatro rutas de búsqueda devuelven `PostResponseDto[]`.

#### PUT /posts/{id}
Editar post (solo autor).

**Request**: mismo shape que `POST /posts` (`content`, `tags`).

**Response** (200): `PostResponseDto` actualizado.

#### DELETE /posts/{id}
Eliminar post (solo autor).

**Response** (204): No content

#### GET /posts/feed
Feed personalizado: posts de los usuarios que sigue el usuario autenticado (requiere auth). Devuelve `[]` si no sigue a nadie.

**Response** (200): `PostResponseDto[]`.

---

### Comments

Nota: las rutas de comentarios cuelgan de `/api` directamente, no de `/api/posts` ni `/api/comments` como recurso propio — ver rutas exactas abajo.

#### GET /posts/{postId}/comments
Obtener comentarios de un post.

**Response** (200): `CommentResponseDto[]`:
```json
[
  {
    "id": "...",
    "content": "string",
    "authorUsername": "string",
    "postId": "...",
    "parentCommentId": null,
    "createdAt": "datetime",
    "voteCount": 0,
    "userVote": 0,
    "replies": []
  }
]
```

#### POST /posts/{postId}/comments
Crear comentario o reply (requiere auth vía header `Authorization`, validado manualmente en el controller — no usa `Principal`).

**Request**:
```json
{
  "content": "string",
  "parentCommentId": "string | null"
}
```

**Response** (200) — nota: este endpoint devuelve `200`, no `201`. `CommentResponseDto` (ver shape arriba).

#### PUT /comments/{id}
Editar comentario (solo autor).

**Request**:
```json
{
  "content": "string"
}
```

**Response** (200): `CommentResponseDto` actualizado.

#### DELETE /comments/{id}
Eliminar comentario (solo autor).

**Response** (204): No content

---

### Votes

Sistema de votos genérico bajo `/api/votes` — no hay endpoints de voto anidados bajo `/posts/{id}/vote` ni `/comments/{id}/vote`.

#### POST /votes/{targetId}?value={1|-1}&targetType={post|comment}
Votar (o cambiar/quitar voto) sobre un post o comentario. Requiere auth. `targetType` default es `post` si se omite.

**Response** (200): objeto de resultado del toggle (conteo actualizado, definido por `VoteUseCase.toggleVote`).

#### GET /votes/user
Posts votados por el usuario autenticado (requiere auth).

**Response** (200): objeto con los votos del usuario.

#### GET /votes/{targetId}/count?targetType={post|comment}
Conteo de votos de un post o comentario. `targetType` default `post`.

**Response** (200):
```json
{ "votes": 6 }
```

#### POST /votes/cleanup
Limpia votos duplicados en la base (mantenimiento, sin auth explícita en el controller). Usar con cuidado — no filtra por usuario.

---

### Users

Todos bajo `/api/users`, devuelven `UserProfileDto` salvo que se indique lo contrario:
```json
{
  "id": "...",
  "username": "string",
  "displayName": "string",
  "bio": "string",
  "followerCount": 0,
  "followingCount": 0,
  "isFollowing": false,
  "createdAt": "datetime"
}
```

#### GET /users/me
Perfil del usuario autenticado.

#### PUT /users/me
Actualizar perfil (requiere auth).

**Request**:
```json
{
  "username": "string",
  "email": "string",
  "displayName": "string",
  "bio": "string"
}
```

#### PUT /users/me/password
Cambiar contraseña (requiere auth).

**Request**:
```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

**Response** (200): `{ "message": "Contraseña actualizada correctamente" }`

#### PUT /users/me/notifications
Actualizar preferencias de notificación (requiere auth).

**Request**:
```json
{
  "notifyLikes": true,
  "notifyComments": true,
  "notifyMentions": true
}
```

#### DELETE /users/me
Eliminar cuenta propia (requiere auth + confirmar contraseña).

**Request**: `{ "password": "string" }`

**Response** (200) o (400) si falta `password`.

#### GET /users/{id}
Perfil público de un usuario (incluye `isFollowing` respecto al usuario autenticado).

#### POST /users/{id}/follow
Seguir a un usuario (requiere auth).

#### DELETE /users/{id}/follow
Dejar de seguir (requiere auth).

#### GET /users/{id}/followers
Lista de seguidores (`UserProfileDto[]`).

#### GET /users/{id}/following
Lista de usuarios que sigue (`UserProfileDto[]`).

#### GET /users/{id}/isfollowing
`{ "isFollowing": true }` respecto al usuario autenticado.

#### GET /users/me/following
IDs de los usuarios que sigue el usuario autenticado (`string[]`), usado por `GET /posts/feed`.

#### POST /users/saved/{postId}
Guardar un post (requiere auth). Response: `{ "saved": true, "savedCount": N }`.

#### DELETE /users/saved/{postId}
Quitar un post guardado. Response: `{ "saved": false, "savedCount": N }`.

#### GET /users/saved
Posts guardados por el usuario autenticado. Response: `{ "savedPosts": PostResponseDto[] }`.

---

## Códigos de Respuesta

| Código | Descripción |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict (ej. username/email ya existe) |
| 500 | Internal Server Error |

---

## Errores Comunes

### 400 - Validation Error
```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

### 401 - Unauthorized
```json
{
  "error": "Invalid or expired token"
}
```

### 404 - Not Found
```json
{
  "error": "Resource not found"
}
```

Ver `GlobalExceptionHandler` (`exception/`) para el mapeo completo de excepciones de dominio a códigos HTTP.

---

## Testing

Usar Postman o curl:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password"}'

# Get posts (con token)
curl -X GET http://localhost:8080/api/posts \
  -H "Authorization: Bearer <token>"

# Votar un post
curl -X POST "http://localhost:8080/api/votes/<postId>?value=1&targetType=post" \
  -H "Authorization: Bearer <token>"
```
