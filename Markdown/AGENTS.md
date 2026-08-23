# VoidForum - AI Agent Guide

## Tech Stack

- **Backend**: Java 17 + Spring Boot 4.1 (Jackson 3) + MongoDB
- **Frontend**: Vite + Tailwind CSS 4 + Vanilla JS
- **Auth**: JWT (sin Redis, sin sesión)
- **Build**: Maven (backend), npm (frontend)
- **Testing**: JUnit 5 + Testcontainers (MongoDB) for the `@SpringBootTest` context-load test; Mockito for service unit tests

## Project Structure

El backend sigue Clean/Hexagonal Architecture (domain → application → infrastructure), no el clásico controller→service→repository de 3 capas:

```
voidForum/
├── src/main/java/com/voidforum/
│   ├── VoidForumApplication.java
│   ├── controller/          # Inbound adapters (REST endpoints)
│   ├── domain/
│   │   ├── model/            # Entities (User, Post, Comment, Vote) — sin deps de framework
│   │   ├── port/in/          # Use case interfaces (ej. PostUseCase, AuthUseCase)
│   │   ├── port/out/         # Repository interfaces (ej. PostRepositoryPort)
│   │   └── service/          # Use case implementations (lógica de negocio real)
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── adapter/      # Implementan los port/out contra MongoDB
│   │       ├── entity/       # @Document (PostDocument, UserDocument, ...)
│   │       ├── mapper/       # Document <-> domain model
│   │       └── mongo/        # Spring Data MongoRepository interfaces
│   ├── dto/                  # Request/response DTOs (records)
│   ├── exception/            # ConflictException, UnauthorizedException, etc.
│   ├── config/                # CORS, Security, JwtAuthenticationFilter
│   └── service/                # JwtService (helper de infraestructura, no es un use case)
├── src/main/resources/
│   └── application.properties
├── src/test/java/            # Tests (Testcontainers + Mockito)
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
└── .gitignore
```

**Al agregar una feature de backend**: la lógica va en `domain/service/`, no en `controller/`. El controller solo traduce HTTP <-> DTO y delega en un `port/in` (use case interface).

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
1. Agregar método a la interface del use case en `domain/port/in/`
2. Implementarlo en `domain/service/`
3. Exponerlo en el `controller/` correspondiente
4. Agregar en API.md

### Agregar nueva entidad
1. Crear clase en `domain/model/` (sin anotaciones de MongoDB)
2. Crear el port de salida en `domain/port/out/` (interface)
3. Crear `@Document` en `infrastructure/persistence/entity/`, su `MongoRepository` en `infrastructure/persistence/mongo/`, un mapper en `infrastructure/persistence/mapper/`, y el adapter en `infrastructure/persistence/adapter/` que implementa el port

### Agregar componente frontend
1. Crear archivo en `frontend/src/components/`
2. Importar en main.js o componente padre

## Environment Variables

Backend (application.properties):
- `spring.data.mongodb.uri` - URI de MongoDB
- `jwt.secret` - Clave JWT (生成 con java -jar)

Frontend (.env):
- `VITE_API_URL=http://localhost:8080/api`

## MongoDB Configuration

### Connection
- **Atlas Cluster**: tpgrupo15.g7bd9qy.mongodb.net
- **Database**: voidforum
- **URI**: `mongodb+srv://<username>:<password>@<your-cluster>.mongodb.net/voidforum` (ver `.env`, nunca commitear valores reales)

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

## Estado actual del proyecto

El MVP original (auth JWT, Posts/Comments CRUD, votos, seguridad, tests unitarios y la estructura base del frontend con login/register/settings/create-post) ya está implementado — el backend fue además refactorizado a Clean/Hexagonal Architecture (ver sección "Project Structure" arriba) y migrado a Spring Boot 4.1. La API real incluye, más allá del MVP original, follow/unfollow, saved posts, búsqueda de posts (`/api/posts/search*`) y feed personalizado (`/api/posts/feed`) — ver [API.md](./API.md) para el detalle completo de endpoints.

No mantengas un checklist de tareas en este archivo: para saber qué está pendiente, revisar los issues abiertos del repo en GitHub en lugar de una lista estática que se desactualiza.