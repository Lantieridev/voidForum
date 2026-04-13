# CONTRIBUTING.md - Guía de Contribución

## Requisitos Previos

Antes de contribuir, asegurate de tener instalado:

- **Java 17** o superior
- **Maven 3.8+**
- **Node.js 18+**
- **MongoDB** (local o remoto)
- **Git**

## Setup del Proyecto

### 1. Clonar el repositorio
```bash
git clone https://github.com/Lantieridev/voidForum.git
cd voidForum
```

### 2. Backend (Java/Spring Boot)
```bash
# Verificar Java
java -version

# Compilar
cd src
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### 3. Frontend (Vite + Tailwind)
```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar modo desarrollo
npm run dev

# Build para producción
npm run build
```

### 4. Configuración

**Backend** - Editar `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/voidforum
server.port=8080
```

**Frontend** - Crear `.env` en `frontend/`:
```env
VITE_API_URL=http://localhost:8080/api
```

## Workflow de Contribución

### Paso 1: Sincronizar
```bash
git checkout main
git pull origin main
```

### Paso 2: Crear branch
```bash
git checkout -b feature/nombre-descriptivo
```

### Paso 3: Desarrollar
- Escribir código siguiendo RULES.md
- Hacer commits frecuentes
- No commitear archivos grandes/binarios

### Paso 4: Testing
- Verificar que el backend inicie: `mvn spring-boot:run`
- Verificar que el frontend inicie: `npm run dev`
- Probar la funcionalidad implementada

### Paso 5: Push y Pull Request
```bash
git push -u origin feature/nombre-descriptivo
```

Luego crear Pull Request en GitHub con:
- Título descriptivo
- Descripción de los cambios
- Referencias a issues si aplica

## Estructura de Archivos

```
voidForum/
├── src/                    # Backend Java
│   ├── main/java/com/voidforum/
│   │   ├── config/        # Configuraciones
│   │   ├── controller/    # Endpoints REST
│   │   ├── model/         # Entidades
│   │   ├── repository/    # MongoDB repositories
│   │   └── service/      # Lógica de negocio
│   └── main/resources/
│       └── application.properties
├── frontend/              # Frontend Vite
│   ├── src/
│   │   ├── main.js
│   │   ├── api.js
│   │   └── components/
│   └── package.json
├── pom.xml
├── package.json
└── .gitignore
```

## Reglas Importantes

1. **No commitear credenciales** - Usar `.env` para secrets
2. **Seguir naming conventions** - Ver RULES.md
3. **Commits claros** - Mensajes descriptivos
4. **Probar antes de push** - Verificar que todo funcione
5. **Actualizar documentación** - Si agregás endpoints, actualizar API.md

## Issues y Features

- Crear issue para bugs o features
- Asignarse el issue antes de trabajar
- Cerrar el issue al resolver

## Ayuda

- Ver AGENTS.md para contexto del proyecto
- Ver SKILLS.md para comandos útiles
- Ver DEVELOPMENT.md para troubleshooting
- Ver API.md para referencia de endpoints

## Código de Conducta

- Respetar a los demás miembros del equipo
- Communicarse claramente
- Aceptar feedback constructivo
- Ayudar a los compañeros