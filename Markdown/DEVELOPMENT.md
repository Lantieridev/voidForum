# DEVELOPMENT.md - Guía de Desarrollo

## Comandos Rápidos

### Backend (Java/Spring Boot)

| Comando | Descripción |
|---------|-------------|
| `mvn spring-boot:run` | Iniciar servidor backend |
| `mvn clean compile` | Compilar el proyecto |
| `mvn test` | Ejecutar tests |
| `mvn clean package` | Crear JAR |
| `mvn dependency:tree` | Ver dependencias |

### Frontend (Vite/Tailwind)

| Comando | Descripción |
|---------|-------------|
| `npm install` | Instalar dependencias |
| `npm run dev` | Iniciar servidor dev |
| `npm run build` | Build producción |
| `npm run preview` | Preview build |

## Puertos常用

- **Backend**: `http://localhost:8080`
- **Frontend**: `http://localhost:5173`
- **MongoDB**: `localhost:27017`

## Troubleshooting

### Backend

#### "Port 8080 is already in use"
```bash
# Windows
netstat -ano | findstr 8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

Cambiar puerto en `application.properties`:
```properties
server.port=8081
```

#### "MongoDB connection refused"
1. Verificar que MongoDB esté corriendo
2. Checkear URI de conexión:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/voidforum
```
3. Si es remoto, verificar IP/credenciales

#### "Cannot find symbol" en Lombok
Asegurar que Lombok esté en pom.xml:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Y que el IDE tenga annotation processing habilitado.

#### "JWT secret key too short"
El secret debe ser al menos 256 bits. Generar uno:
```bash
# Linux/Mac
openssl rand -base64 32

# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### Frontend

#### "Cannot find module './api'"
Verificar que el archivo exista y la ruta sea correcta.
Los imports deben ser relativos:
```javascript
import { api } from './api.js';
```

#### "Tailwind not applying styles"
1. Verificar que Tailwind esté instalado:
```bash
npm list tailwindcss
```

2. Verificar vite.config.js:
```javascript
import tailwindcss from '@tailwindcss/vite'
export default {
  plugins: [tailwindcss()],
}
```

3. Verificar style.css:
```css
@import "tailwindcss";
```

#### "CORS error en navegador"
El backend debe tener CORS configurado. Ver `config/CorsConfig.java`.

#### "API returns 404"
Verificar que el endpoint existe y la URL es correcta.
El frontend debe apuntar a `http://localhost:8080/api`.

### Git

#### "Merge conflict"
1. Abrir los archivos en conflicto
2. Elegir qué cambios mantener
3. Marcar como resuelto:
```bash
git add .
git commit -m "fix: merge conflict"
```

#### "Push rejected"
Primero hacer pull:
```bash
git pull origin main
# Resolver conflictos si hay
git push
```

## Estructura de Archivos

```
src/main/java/com/voidforum/
├── VoidForumApplication.java    # Main class
├── config/
│   └── SecurityConfig.java      # JWT config
├── controller/
│   ├── AuthController.java
│   ├── PostController.java
│   └── CommentController.java
├── model/
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
│   └── Vote.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   └── VoteRepository.java
└── service/
    ├── AuthService.java
    ├── PostService.java
    └── CommentService.java
```

## Debugging

### Backend
- Logs en consola al ejecutar `mvn spring-boot:run`
- Agregar breakpoints en IDE
- Usar Postman/curl para testear endpoints

### Frontend
- Console del navegador (F12)
- Network tab para ver requests
- React DevTools si se usa React

## Tips

1. **Trabajar en branch**: Nunca hacer commit directo a main
2. **Commits pequeños**: mejor muchos commits pequeños que uno gigante
3. **Probar seguido**: ejecutar el proyecto frecuentemente
4. **Pedir ayuda**: si algo no funciona, consultar al equipo
5. **Documentar**: si descubren algo nuevo, agregarlo a DEVELOPMENT.md