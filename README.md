# VoidForum

Foro comunitario creado para el trabajo práctico de Base de Datos II.

## Tech Stack

- **Frontend**: JavaScript + Tailwind CSS + Vite
- **Backend**: Java (Spring Boot)
- **Bases de Datos**: MongoDB

## Estructura del Proyecto

```
├── src/                      # Backend (Java/Spring Boot)
│   ├── main/java/com/voidforum/
│   │   ├── config/           # Configuraciones
│   │   ├── controller/        # Controladores REST
│   │   ├── model/            # Modelos/Entidades
│   │   ├── repository/       # Repositorios MongoDB
│   │   └── service/          # Servicios
│   └── main/resources/
│       └── application.properties
├── frontend/                 # Frontend (Vite + Tailwind)
│   ├── src/
│   └── public/
├── pom.xml                   # Maven config
└── package.json              # NPM config
```

## Requisitos

- Java 17+
- Maven 3.8+
- Node.js 18+
- MongoDB

## Ejecutar el proyecto

### Backend
```bash
cd src
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```