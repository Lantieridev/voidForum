# 🌌 VoidForum | Community Hub

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
│   └── public/               # Static Assets
├── pom.xml                   # Backend Manifest
└── package.json              # Frontend Manifest
```

---

## ⚙️ Quick Start

### 1. Prerequisites
*   **Java JDK 17+**
*   **Maven 3.8+**
*   **Node.js 18+**
*   **MongoDB Atlas Cluster** (Configured in `.env` or `application.properties`)

### 2. Backend Setup
```bash
# Navigate to root
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
This project is part of a series of academic implementations focused on high-quality software engineering standards. Detailed technical notes and architectural decisions are available in the internal documentation.

---

*Developed by [Martin Lantieri](https://github.com/Lantieridev) - 2026*