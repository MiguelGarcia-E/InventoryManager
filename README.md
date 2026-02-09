# 📦 Inventory Manager

Inventory Manager is a full-stack web application designed to manage products, categories, and inventory metrics in a clean and efficient way.  
The system supports search, filtering, pagination, statistics, and a modern UI for easy navigation.

This project combines a React + TypeScript frontend served with Nginx, and a backend using Spring Boot + Java 17 — all orchestrated through Docker Compose.

---

# 🚀 Tech Stack

### **Frontend**
- React + TypeScript  
- Vite  
- Axios  
- Served with **Nginx** in production  
- Runs at **http://localhost:8080**

### **Backend**
- Java 17  
- Spring Boot  
- Maven  
- REST API  
- Runs at **http://localhost:9090**

### **DevOps & Orchestration**
- Docker  
- Docker Compose  
- Multi-stage builds (frontend & backend)

---

# 🧩 Features

### ✅ Product Management  
- Create, list, search, and filter products  
- Pagination & sorting  
- Availability filters  

### ✅ Category Management  
- Predefined categories  
- Category-based product filtering  

### ✅ Metrics Dashboard  
- Useful product statistics via backend metrics endpoint  

### ✅ Modern Frontend  
- Clean UI  
- Fast builds with Vite  
- Fully containerized through Nginx

### ✅ Robust Backend  
- REST API built with Spring Boot  
- Automated build via Maven  
- Runs efficiently on Java 17 JRE

---

# 📁 Project Structure

```
InventoryManager/
│── Inventory-Manager/              # Frontend (React + TS + Vite)
│── back-end-inventory-manager/     # Backend (Spring Boot)
│── docker-compose.yml              # Orchestration
```

---

# 🔧 How to Run the Project

This is the recommended setup using **Docker Compose**, which runs both frontend and backend automatically.

---

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/MiguelGarcia-E/InventoryManager.git
cd InventoryManager
```

---

## 2️⃣ Run Everything with Docker (Recommended)

### Build all containers

```bash
docker compose build
```

### Start the application

```bash
docker compose up -d
```

### Access the system

| Service | URL |
|--------|--------------------------|
| Frontend | http://localhost:8080 |
| Backend API | http://localhost:9090 |

Test example:

```bash
curl http://localhost:9090/api/v1/categories
```

### Stop everything

```bash
docker compose down
```

---


# 🧪 Environment Variables (Frontend)

The frontend expects:

```
VITE_API_BASE_URL=/api/v1
```

Nginx redirects this automatically to the backend container.

---

# 🐳 Docker Architecture Overview

### Frontend (Nginx)
- Builds React app with Vite  
- Serves static files via Nginx  
- Proxies API requests to backend  
- Exposes port **8080**

### Backend (Spring Boot)
- Built with Maven  
- Converted to JAR and run in Java 17 JRE  
- Exposes port **9090**

### Nginx proxy snippet

```nginx
location /api/ {
    proxy_pass http://backend:9090/api/;
}
```

### Request Flow

```
Browser → Frontend (Nginx) → /api → Backend (Spring Boot)
```
---

# 📄 License

This project was developed by Miguel Garcia for Encora's Spark Program
