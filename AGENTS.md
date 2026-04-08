# VoidForum - AI Agent Guide

## Tech Stack

- **Backend**: Java 17 + Spring Boot 3.2 + MongoDB
- **Frontend**: Vite + Tailwind CSS 4 + Vanilla JS
- **Auth**: JWT (sin Redis, sin sesión)
- **Build**: Maven (backend), npm (frontend)

## Project Structure

```
voidForum/
├── src/main/java/com/voidforum/
│   ├── VoidForumApplication.java
│   ├── config/           # CORS, Security
│   ├── controller/       # REST endpoints
│   ├── model/           # Entities (User, Post, Comment, Vote)
│   ├── repository/      # MongoRepository interfaces
│   └── service/         # Business logic
├── src/main/resources/
│   └── application.properties
├── src/test/java/        # Tests
├── frontend/
│   ├── src/
│   │   ├── main.js
│   │   ├── style.css
│   │   ├── api.js
│   │   └── components/
│   ├── public/
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── pom.xml
├── package.json
└── .gitignore
```

## MongoDB Configuration

### Connection
- **Atlas Cluster**: tpgrupo15.g7bd9qy.mongodb.net
- **Database**: voidforum
- **URI**: `mongodb+srv://TP_Grupo15:5Cd1S1JjAEcYj4s1@tpgrupo15.g7bd9qy.mongodb.net/voidforum`

### Colecciones
| Collection | Descripción |
|------------|-------------|
| `users` | Usuarios del sistema |
| `posts` | Publicaciones del foro |
| `comments` | Comentarios en posts |
| `votes` | Registro de votos (evita duplicados) |

### Índices Recomendados
```javascript
// users
db.users.createIndex({ "username": 1 }, { unique: true })
db.users.createIndex({ "email": 1 }, { unique: true })

// posts
db.posts.createIndex({ "authorId": 1 })
db.posts.createIndex({ "tags": 1 })
db.posts.createIndex({ "createdAt": -1 })

// comments
db.comments.createIndex({ "postId": 1, "createdAt": -1 })
db.comments.createIndex({ "authorId": 1 })

// votes
db.votes.createIndex({ "userId": 1, "targetId": 1, "targetType": 1 }, { unique: true })
```

## Important Notes

### Embeds (Videos)
El contenido de posts puede contener URLs de YouTube/Vimeo. El frontend debe:
1. Detectar URLs en el texto
2. Reemplazar por `<iframe>` embebido
3. Tipos soportados: YouTube, Vimeo

Ejemplo de detección:
```javascript
const youtubeRegex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com|youtu\.be)\/(?:watch\?v=)?([a-zA-Z0-9_-]+)/;
const vimeoRegex = /(?:https?:\/\/)?(?:www\.)?vimeo\.com\/(\d+)/;
```

### Link Previews
Para mostrar previews de enlaces externos:
1. Frontend puede usar API externa (linkpreview.net)
2. O crear endpoint en backend que haga scraping de OG tags
3. Mostrar como card con imagen, título, descripción

### Votos (Upvote/Downvote)
El sistema de votos usa colección `votes` para:
- Evitar doble voto del mismo usuario
- Permitir quitar/reversar voto
- Tracking de quién votó qué

Estructura de Vote:
```json
{
  "userId": "string",
  "targetId": "string",
  "targetType": "post|comment",
  "voteType": "up|down"
}
```

### Tags en Posts
Los tags son arrays de strings. Queries comunes:
- `findByTagsContaining` - posts con tag específico
- `findByTagsIn` - posts con cualquiera de los tags

## Coding Conventions

### Java (Backend)
- Paquetes: `com.voidforum.model`, `com.voidforum.service`, etc.
- Clases: PascalCase (UserService, PostController)
- Métodos: camelCase
- Lombok para reducir boilerplate
- Inyección por constructor

### JavaScript (Frontend)
- Módulos ES6+
- camelCase para funciones/variables
- Arrow functions preferidas
- Fetch API para requests

### Git
- Branch naming: `feature/nombre`, `fix/nombre`, `docs/nombre`
- Commits: `feat:`, `fix:`, `docs:`, `refactor:`

## Common Tasks

### Agregar nuevo endpoint
1. Crear método en Service
2. Crear endpoint en Controller
3. Agregar en API.md

### Agregar nueva entidad
1. Crear clase en model/
2. Crear Repository interface
3. Agregar Service si es necesario

### Agregar componente frontend
1. Crear archivo en `frontend/src/components/`
2. Importar en main.js o componente padre

## Environment Variables

Backend (application.properties):
- `spring.data.mongodb.uri` - URI de MongoDB
- `jwt.secret` - Clave JWT

Frontend (.env):
- `VITE_API_URL=http://localhost:8080/api`

## TODO List - Tareas del Proyecto

### BACKEND - Core (Prioridad Máxima)

#### 1. Configuración Base
- [ ] Configurar application.properties con DB remota
- [ ] Verificar conexión a MongoDB remota
- [ ] Probar que el servidor inicia correctamente
- [ ] Verificar CORS para frontend local

#### 2. Autenticación JWT
- [ ] Test endpoint `/api/auth/register` - crear usuario
- [ ] Test endpoint `/api/auth/login` - obtener token
- [ ] Test endpoint `/api/auth/me` - obtener usuario actual
- [ ] Validar que el token funciona en endpoints privados
- [ ] Manejar errores (usuario existe, credenciales inválidas)

#### 3. Posts CRUD
- [ ] Test `GET /api/posts` - listar posts con paginación
- [ ] Test `GET /api/posts?tag=xxx` - filtrar por tag
- [ ] Test `POST /api/posts` - crear post (con auth)
- [ ] Test `GET /api/posts/{id}` - obtener post específico
- [ ] Test `PUT /api/posts/{id}` - editar post (solo autor)
- [ ] Test `DELETE /api/posts/{id}` - eliminar post (solo autor)

#### 4. Votos (Posts)
- [ ] Test `POST /api/posts/{id}/vote?type=up` - upvote
- [ ] Test `POST /api/posts/{id}/vote?type=down` - downvote
- [ ] Verificar que usuario no puede votar dos veces igual
- [ ] Verificar que puede cambiar vote (up→down)
- [ ] Verificar que puede quitar vote (clickear mismo)
- [ ] Verificar conteo correcto de upvotes/downvotes

#### 5. Comentarios CRUD
- [ ] Test `GET /api/posts/{id}/comments` - listar comentarios
- [ ] Test `POST /api/posts/{id}/comments` - crear comentario
- [ ] Test `DELETE /api/comments/{id}` - eliminar comentario (solo autor)

#### 6. Votos (Comentarios)
- [ ] Test `POST /api/comments/{id}/vote?type=up`
- [ ] Test `POST /api/comments/{id}/vote?type=down`
- [ ] Mismos controles que votos de posts

#### 7. Users/Profile (Opcional para MVP)
- [ ] Test `GET /api/users/{id}/profile`
- [ ] Test `GET /api/users/{id}/posts`

---

### BACKEND - Extras

#### 8. Validación y Errores
- [ ] Validar request bodies (no vacíos, email formato)
- [ ] Manejo centralizado de excepciones
- [ ] Mensajes de error claros

#### 9. Testing
- [ ] Tests unitarios para AuthService
- [ ] Tests unitarios para PostService
- [ ] Tests unitarios para CommentService

#### 10. Seguridad
- [ ] Proteger endpoints correctamente
- [ ] No exponer passwords en responses
- [ ] Validar ownership (autor puede editar/borrar)

---

### FRONTEND

#### 11. Estructura Base
- [ ] Setup completo de Vite + Tailwind
- [ ] API client (`api.js`) con fetch
- [ ] Manejo de JWT (guardar en localStorage)
- [ ] Rutas/pages básicas

#### 12. Auth UI
- [ ] Página Login
- [ ] Página Register
- [ ] Manejo de sesión (logged in / logged out)
- [ ] Logout functionality

#### 13. Feed/Posts
- [ ] Lista de posts con paginación
- [ ] Mostrar votos (up/down counts)
- [ ] Filtrar por tags
- [ ] Links a detalle de post

#### 14. Post Detail
- [ ] Mostrar post completo
- [ ] Renderizar contenido (texto plano)
- [ ] Embeds de YouTube/Vimeo
- [ ] Lista de comentarios
- [ ] Formulario nuevo comentario

#### 15. Crear Post
- [ ] Formulario con title, content, tags
- [ ] Validación básica
- [ ] Redirect después de crear

#### 16. Votos UI
- [ ] Botones up/down en posts
- [ ] Botones up/down en comentarios
- [ ] Mostrar estado actual (voted)
- [ ] Actualizar counts en tiempo real

#### 17. Link Previews (Bonus)
- [ ] Detectar URLs en contenido
- [ ] Fetch metadata (Open Graph)
- [ ] Mostrar preview card

---

### DOCUMENTACIÓN

#### 18. Actualizar docs
- [ ] API.md con ejemplos reales
- [ ] DEVELOPMENT.md con troubleshooting real
- [ ] README.md actualizado

---

## Orden Sugerido de Trabajo

```
FASE 1: Backend Core
├── 1.1 Config DB + Server start
├── 1.2 Auth endpoints
├── 1.3 Posts CRUD
├── 1.4 Votos posts
├── 1.5 Comments CRUD
└── 1.6 Votos comments

FASE 2: Backend Extras
├── 2.1 Validación
├── 2.2 Testing
└── 2.3 Security review

FASE 3: Frontend
├── 3.1 Setup + API client
├── 3.2 Auth UI
├── 3.3 Posts list/detail
├── 3.4 Votos UI
└── 3.5 Embeds + Link previews
```
