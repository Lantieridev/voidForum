# RULES.md - Reglas del Equipo

## Branch Naming

Usar el siguiente formato para branches:

```
feature/nombre-corto       # Nuevas funcionalidades
fix/bug-descripcion        # Bug fixes
docs/nombre-documento     # Documentación
refactor/nombre           # Refactoring
test/nombre-test          # Tests
```

**Ejemplos**:
- `feature/login-jwt`
- `fix/votos-no-guardan`
- `docs/api-endpoints`
- `refactor/user-model`

## Commit Messages

Formato: `tipo: descripción`

| Tipo | Descripción |
|------|-------------|
| `feat` | Nueva funcionalidad |
| `fix` | Bug fix |
| `docs` | Documentación |
| `refactor` | Refactoring |
| `test` | Tests |
| `chore` | Tareas de mantenimiento |
| `style` | Estilos (CSS, formatting) |

**Ejemplos**:
```
feat: agregar sistema de login JWT
fix: corregir votos duplicados
docs: actualizar API.md
refactor: simplificar PostService
```

## Code Style

### Java (Backend)

- **Paquetes**: todo en minúsculas (`com.voidforum.model`)
- **Clases**: PascalCase (`UserService`, `PostController`)
- **Métodos**: camelCase (`findById`, `createPost`)
- **Variables**: camelCase (`userId`, `postTitle`)
- **Constantes**: UPPER_SNAKE_CASE
- **Lombok**: Usar `@Data`, `@Builder`, `@NoArgsConstructor`
- **Inyección**: Constructor injection preferida

**No usar**:
```java
// MAL
@Data
public class User {
    private String ID;
    private String UserName;
    public void DoSomething() { }
}

// BIEN
@Data
@Builder
public class User {
    private String id;
    private String username;
    public void doSomething() { }
}
```

### JavaScript (Frontend)

- **Variables/Funciones**: camelCase (`getPosts`, `userData`)
- **Constantes**: UPPER_SNAKE_CASE si es true constant
- **Clases**: PascalCase (`UserComponent`)
- **Módulos**: ES6+ con `import`/`export`
- **Arrow functions**: Preferidas para callbacks
- **Template literals**: Usar para strings con variables

**No usar**:
```javascript
// MAL
var User_Name = "Juan";
function Get_Posts() { }
const URL_API = "http://...";

// BIEN
const userName = "Juan";
const getPosts = () => { };
const API_URL = "http://...";
```

### CSS (Tailwind)

- Usar clases de Tailwind exclusivamente
- No crear archivos CSS personalizados salvo necesario
- Componentes en archivos separados

## Git Workflow

1. **Antes de trabajar**: `git pull origin main`
2. **Crear branch**: `git checkout -b feature/nombre`
3. **Hacer cambios**: Editar archivos
4. **Commit**: `git add .` + `git commit -m "feat: ..."`
5. **Push**: `git push -u origin feature/nombre`
6. **Pull Request**: Crear PR en GitHub

## Code Review

- Todo PR debe ser revisado por al menos 1 persona
- Revisar: nombres, convenciones, lógica, tests
- Comentar en el PR si hay cambios necesarios
- Aprobar solo si el código está listo

## Testing

- **Backend**: Mínimo tests unitarios para Services
- **Frontend**: Testing manual (browser)
- **Antes de push**: Verificar que todo compile y funcione

## Archivos a Ignorar

NO commitear:
- Archivos con credenciales (`.env`)
- Dependencias (`node_modules/`, `target/`)
- Archivos IDE (`.idea/`, `.vscode/`)
- Logs (`.log`)
- Build outputs (`dist/`, `*.class`)

## Comunicación

- Usar issues de GitHub para tasks
- Commits claros y descriptivos
- PRs con descripción del cambio
- Avisar al equipo antes de hacer cambios grandes