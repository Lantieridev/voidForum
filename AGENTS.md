# VoidForum - AI Agent Guide

## Tech Stack

- **Backend**: Java 17 + Spring Boot 3.2 + MongoDB
- **Frontend**: Vite + Tailwind CSS 4 + Vanilla JS
- **Auth**: JWT (sin Redis)
- **Build**: Maven (backend), npm (frontend)

## Project Structure

```
voidForum/
├── src/main/java/com/voidforum/
│   ├── VoidForumApplication.java
│   ├── config/           # CORS, Security, Redis
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
- `spring.data.redis.host` - Host Redis
- `jwt.secret` - Clave JWT (生成 con java -jar)

Frontend (.env):
- `VITE_API_URL=http://localhost:8080/api`