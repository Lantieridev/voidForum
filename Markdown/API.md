# API.md - Documentación de la API

## Base URL

```
http://localhost:8080/api
```

## MongoDB

- **Host**: tpgrupo15.g7bd9qy.mongodb.net
- **Database**: voidforum
- **URI**: `mongodb+srv://TP_Grupo15:5Cd1S1JjAEcYj4s1@tpgrupo15.g7bd9qy.mongodb.net/voidforum`

## Autenticación

Todos los endpoints (excepto register y login) requieren el header:
```
Authorization: Bearer <token_jwt>
```

---

## Colecciones MongoDB

### users
```json
{
  "_id": "ObjectId",
  "username": "string (unique)",
  "email": "string (unique)",
  "password": "string (hashed)",
  "avatar": "string (URL)",
  "createdAt": "datetime"
}
```

### posts
```json
{
  "_id": "ObjectId",
  "title": "string",
  "content": "string",
  "authorId": "string (user._id)",
  "authorUsername": "string",
  "tags": ["string"],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### comments
```json
{
  "_id": "ObjectId",
  "postId": "string (post._id)",
  "authorId": "string (user._id)",
  "authorUsername": "string",
  "content": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### votes
```json
{
  "_id": "ObjectId",
  "userId": "string (user._id)",
  "targetId": "string (post._id o comment._id)",
  "targetType": "post | comment",
  "voteType": "up | down",
  "createdAt": "datetime"
}
```

---

## Endpoints

### Auth

#### POST /auth/register
Registro de nuevo usuario.

**Request**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response** (201):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "...",
    "username": "string",
    "email": "string"
  }
}
```

#### POST /auth/login
Login de usuario.

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
    "email": "string"
  }
}
```

#### GET /auth/me
Obtener usuario actual (requiere auth).

**Response** (200):
```json
{
  "id": "...",
  "username": "string",
  "email": "string",
  "avatar": "string",
  "createdAt": "datetime"
}
```

---

### Posts

#### GET /posts
Obtener posts con paginación.

**Query Parameters**:
| Param | Type | Default | Description |
|-------|------|---------|-------------|
| page | int | 0 | Número de página |
| size | int | 10 | Posts por página |
| tag | string | - | Filtrar por tag |

**Response** (200):
```json
{
  "content": [
    {
      "id": "...",
      "title": "string",
      "content": "string",
      "authorId": "...",
      "authorUsername": "string",
      "tags": ["tag1", "tag2"],
      "createdAt": "datetime",
      "upvotes": 0,
      "downvotes": 0,
      "userVote": null
    }
  ],
  "totalPages": 5,
  "totalElements": 50
}
```

#### POST /posts
Crear nuevo post (requiere auth).

**Request**:
```json
{
  "title": "string",
  "content": "string",
  "tags": ["tag1", "tag2"]
}
```

**Response** (201):
```json
{
  "id": "...",
  "title": "string",
  "content": "string",
  "authorId": "...",
  "authorUsername": "string",
  "tags": ["tag1", "tag2"],
  "createdAt": "datetime"
}
```

#### GET /posts/{id}
Obtener un post específico.

**Response** (200):
```json
{
  "id": "...",
  "title": "string",
  "content": "string",
  "authorId": "...",
  "authorUsername": "string",
  "tags": ["tag1"],
  "createdAt": "datetime",
  "upvotes": 5,
  "downvotes": 1,
  "userVote": "up"
}
```

#### PUT /posts/{id}
Editar post (solo autor).

**Request**:
```json
{
  "title": "string",
  "content": "string",
  "tags": ["nuevo", "tag"]
}
```

#### DELETE /posts/{id}
Eliminar post (solo autor).

**Response** (204): No content

#### POST /posts/{id}/vote
Votar un post (requiere auth).

**Query Parameters**:
- `type`: `up` | `down`

**Response** (200):
```json
{
  "upvotes": 6,
  "downvotes": 1,
  "userVote": "up"
}
```

---

### Comments

#### GET /posts/{postId}/comments
Obtener comentarios de un post.

**Response** (200):
```json
[
  {
    "id": "...",
    "postId": "...",
    "authorId": "...",
    "authorUsername": "string",
    "content": "string",
    "createdAt": "datetime",
    "upvotes": 2,
    "downvotes": 0,
    "userVote": null
  }
]
```

#### POST /posts/{postId}/comments
Crear comentario (requiere auth).

**Request**:
```json
{
  "content": "string"
}
```

**Response** (201):
```json
{
  "id": "...",
  "postId": "...",
  "authorId": "...",
  "authorUsername": "string",
  "content": "string",
  "createdAt": "datetime"
}
```

#### DELETE /comments/{id}
Eliminar comentario (solo autor).

**Response** (204): No content

#### POST /comments/{id}/vote
Votar comentario (requiere auth).

**Query Parameters**:
- `type`: `up` | `down`

---

### Users

#### GET /users/{id}/profile
Obtener perfil de usuario.

**Response** (200):
```json
{
  "id": "...",
  "username": "string",
  "avatar": "string",
  "createdAt": "datetime",
  "postsCount": 15,
  "commentsCount": 42
}
```

#### GET /users/{id}/posts
Obtener posts de un usuario.

**Query Parameters**:
| Param | Type | Default |
|-------|------|---------|
| page | int | 0 |
| size | int | 10 |

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
```