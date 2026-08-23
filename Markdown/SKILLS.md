# SKILLS.md - Habilidades para AI Agents

## Available Skills

### 1. Git Operations
- **Branch Management**: Create, switch, delete branches
- **Commit**: Stage, commit, amend commits
- **Push/Pull**: Sync with remote
- **Merge**: Handle merge conflicts

**Usage**:
```bash
git checkout -b feature/nueva-funcionalidad
git add .
git commit -m "feat: nueva funcionalidad"
git push -u origin feature/nueva-funcionalidad
```

### 2. Java/Spring Boot Development (Hexagonal Architecture)
- **Create Domain Model**: new class in `domain/model/` with Lombok (`@Data`/`@Builder`), no MongoDB annotations
- **Create Port**: use case interface in `domain/port/in/`, repository interface in `domain/port/out/`
- **Create Use Case**: implementation in `domain/service/` — this is where business logic lives
- **Create Persistence Adapter**: `@Document` in `infrastructure/persistence/entity/`, `MongoRepository` in `infrastructure/persistence/mongo/`, mapper in `infrastructure/persistence/mapper/`, adapter implementing the port in `infrastructure/persistence/adapter/`
- **Create Controller**: REST endpoint in `controller/`, delegates to a `port/in` interface

**Patterns**:
- Domain model stays framework-agnostic (no `@Document`, no Spring annotations)
- Constructor injection for dependencies
- Controllers depend on `port/in` interfaces, never on the `domain/service/` implementation directly
- Follow REST conventions for endpoints

### 3. Frontend Development (Vanilla JS + Tailwind)
- **Create Component**: New UI component
- **API Integration**: Fetch API calls
- **Event Handling**: DOM events
- **Styling**: Tailwind CSS classes

### 4. MongoDB Operations
- **Query**: Find, filter, aggregate
- **Indexes**: Create compound indexes
- **Data Modeling**: Schema design

### 5. Debugging
- **Backend**: Check Spring Boot logs
- **Frontend**: Browser console, Network tab
- **API**: Test endpoints with curl/postman

### 6. Testing
- **Unit Tests**: JUnit for Java
- **Integration Tests**: Spring Boot test
- **Manual Testing**: Browser testing

## Quick Commands

| Task | Command/Tool |
|------|---------------|
| Run backend | `mvn spring-boot:run` |
| Run frontend | `npm run dev` |
| Build frontend | `npm run build` |
| Run tests | `mvn test` |
| Check git status | `git status` |
| View git log | `git log --oneline` |

## Common Issues & Solutions

### Backend
- **Port 8080 in use**: Change port in application.properties
- **MongoDB connection failed**: Check URI in application.properties
- **JWT errors**: Verify secret key generation

### Frontend
- **Module not found**: Check imports in main.js
- **Tailwind not working**: Verify postcss.config.js
- **CORS errors**: Check CorsConfig in backend

## Tips for Working with This Project

1. Always check AGENTS.md first for conventions
2. Follow RULES.md for branch/commit naming
3. Test locally before pushing
4. Keep API.md updated with new endpoints
5. Use DEVELOPMENT.md for setup issues