# KRI Monitoring

B2B Key Risk Indicator monitoring platform. Organizations define measurable risk indicators, track values over time, and automatically classify risk levels using a green/yellow/red color system.

Each client runs their own isolated instance with their own database.

## Tech stack

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA, Spring Security
- **Database:** PostgreSQL 17
- **Frontend:** React + Vite
- **Infrastructure:** Docker Compose

## Prerequisites

- [Java 21](https://adoptium.net)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Node.js 20+](https://nodejs.org)

## Getting started

**1. Clone the repo**
```bash
git clone <repo-url>
cd KRI-Monitoring
```

**2. Start the database**
```bash
docker compose up -d
```

**3. Start the backend**
```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on http://localhost:8080

**4. Start the frontend**
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on http://localhost:5173

## Project structure

```
KRI-Monitoring/
├── backend/          # Spring Boot (Maven)
├── frontend/         # React + Vite
├── docker-compose.yml
└── .gitignore
```

## Database

Docker Compose starts a PostgreSQL 17 instance with:

| Setting  | Value       |
|----------|-------------|
| Host     | localhost   |
| Port     | 5432        |
| Database | kri         |
| Username | kri         |
| Password | kri         |

To stop the database: `docker compose down`  
To wipe all data: `docker compose down -v`
