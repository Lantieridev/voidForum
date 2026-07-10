# 🌌 VoidForum | Community Hub

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=springboot)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=for-the-badge&logo=mongodb)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)
![CI](https://img.shields.io/github/actions/workflow/status/Lantieridev/voidForum/ci.yml?branch=main&style=for-the-badge&label=CI)

> [!IMPORTANT]
> **Technical Showcase:** This project is a full-stack community forum implementation utilizing a modern Reactive-ish approach with Spring Boot and MongoDB, developed as a core project for the Database II curriculum.

Welcome to **VoidForum**, a minimalist yet powerful community platform designed for seamless discussion and interaction. Built with a focus on performance, scalability, and modern UX standards.

---

## 🚀 Project Overview

VoidForum serves as a robust foundation for community-driven content. It leverages the flexibility of NoSQL (MongoDB) to handle dynamic discussion threads, user profiles, and real-time interaction patterns.

### ✨ Key Features
*   **Dynamic Threading:** Hierarchical discussion structures.
*   **Reactive Data Flow:** Optimized data retrieval from MongoDB Atlas.
*   **Modern Interface:** High-performance frontend built with Vite and Tailwind CSS.
*   **RESTful Architecture:** Clean, well-documented API endpoints.

---

## 🛠️ Tech Stack

### Backend (The Engine)
*   **Java 17+**: Core language for business logic.
*   **Spring Boot**: Main framework for RESTful services.
*   **Spring Data MongoDB**: Object-document mapping and repository layer.
*   **Maven**: Dependency management and build automation.

### Frontend (The Interface)
*   **JavaScript (ES6+)**: Dynamic client-side logic.
*   **Vite**: Next-generation frontend tooling.
*   **Tailwind CSS**: Utility-first CSS framework for custom, modern designs.

### Database
*   **MongoDB Atlas**: Distributed cloud database for high availability.

---

## 📂 Project Structure

```bash
├── src/                      # Backend Implementation
│   ├── main/java/com/voidforum/
│   │   ├── config/           # Infrastructure & Security configs
│   │   ├── controller/       # REST Controllers (API Layer)
│   │   ├── model/            # Domain Entities (MongoDB Collections)
│   │   ├── repository/       # Data Access Layer
│   │   └── service/          # Business Logic Layer
│   └── main/resources/       # Environment Properties
├── frontend/                 # Client Implementation
│   ├── src/                  # Components & Logic
│   └── img/                  # Static Assets
├── pom.xml                   # Backend Manifest
└── package.json              # Frontend Manifest
```

---

## ⚙️ Quick Start

### 1. Prerequisites
*   **Java JDK 17+**
*   **Maven 3.8+**
*   **Node.js 18+**
*   **MongoDB Atlas Cluster** (or a local MongoDB instance)

### 2. Backend Setup
Spring Boot reads `MONGO_URI`, `JWT_SECRET`, and `JWT_EXPIRATION` straight from the environment (see `.devcontainer/env.template` for the expected format) — export them before running, it won't pick up a `.env` file on its own:
```bash
export MONGO_URI="mongodb+srv://<user>:<password>@<cluster>.mongodb.net/voidforum"
export JWT_SECRET="<your-own-256-bit-secret>"
export JWT_EXPIRATION=86400000

mvn spring-boot:run
```

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

---

## 📜 Documentation & Notes
This project is part of a series of academic implementations focused on high-quality software engineering standards. Detailed technical notes live in [`Markdown/`](./Markdown):
- [API.md](./Markdown/API.md) — endpoint reference
- [DEVELOPMENT.md](./Markdown/DEVELOPMENT.md) — day-to-day dev commands
- [CONTRIBUTING.md](./Markdown/CONTRIBUTING.md) — contribution guide
- [RULES.md](./Markdown/RULES.md) — team conventions (branch naming, etc.)

---

*Developed by [Martin Lantieri](https://github.com/Lantieridev) - 2026*