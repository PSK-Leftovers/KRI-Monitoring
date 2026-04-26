# KRI Monitoring

B2B Key Risk Indicator monitoring platform. Organizations define measurable risk indicators, track values over time, and automatically classify risk levels using a green/yellow/red color system.

Each client runs their own isolated instance with their own database.

## Tech stack

- **Backend:** Java 21, Spring Boot 4, Spring Data JPA, Spring Security
- **Database:** PostgreSQL 17
- **Frontend:** React + Vite
- **Infrastructure:** Docker Compose
- **Task runner:** Task

## Prerequisites

- [Java 21](https://adoptium.net)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Node.js 20+](https://nodejs.org)
- [Task](https://taskfile.dev/docs/installation)

<details>
<summary>Task shell completion</summary>

**bash** (Linux / Mac with bash)
```bash
echo 'source <(task --completion bash)' >> ~/.bashrc && source ~/.bashrc
```

> **Git Bash on Windows** — bash completion requires the `bash-completion` package which Git Bash doesn't ship with. Use PowerShell completion instead, or just run `task` with no arguments to list all available tasks.

**zsh** (Mac default since Catalina)
```zsh
echo 'source <(task --completion zsh)' >> ~/.zshrc && source ~/.zshrc
```

**fish**
```fish
echo 'task --completion fish | source' >> ~/.config/fish/config.fish
```

**PowerShell**
```powershell
Add-Content $PROFILE "`nInvoke-Expression (task --completion powershell)"
```

</details>

## Getting started

**1. Clone the repo**
```bash
git clone <repo-url>
cd KRI-Monitoring
```

**2. First-time setup** — installs frontend dependencies, starts the database, and builds the backend
```bash
task setup
```

**3. Daily development** — open two terminals and run one in each
```bash
# Terminal 1
task backend:dev    # Spring Boot on http://localhost:8080

# Terminal 2
task frontend:dev   # Vite on http://localhost:5173
```

> If you don't want to install Task, see [Manual setup](#manual-setup) below.

## IDE setup

For the best experience, open `backend/` and `frontend/` as separate IDE projects alongside the repo root. When IntelliJ has `backend/pom.xml` as the project root it can properly index Maven dependencies, enable Spring support, and make code navigation and auto-complete work correctly. The same applies for the frontend.

You can keep the repo root open separately at the same time — useful for editing `docker-compose.yml`, `Taskfile.yml`, and other root-level files.

## Database migrations

Schema is managed with **Flyway** — migration files in `backend/src/main/resources/db/migration/` run automatically on startup. See [`backend/README.md`](backend/README.md#database--flyway) for the full workflow.

## Hot reload

**Backend** — Spring Boot DevTools is included and watches for class changes.

Two ways to trigger a reload after editing Java files:
- **CLI:** Press `Ctrl+F9` in IntelliJ to recompile — DevTools detects the new `.class` files and restarts the app context automatically (~1-2s). Works even when the app was started via `task backend:dev` in the terminal.
- **IntelliJ run:** Start the app via IntelliJ's run button instead of the terminal. Then enable Settings → Build, Execution, Deployment → Compiler → **Build project automatically**. Saving a file triggers recompile and DevTools restarts the app — no manual step needed.

**Frontend** — Vite HMR (Hot Module Replacement) updates the browser instantly on every save. No action needed.

## All available tasks

Run `task` with no arguments to list all available tasks. Common ones:

| Task | Description |
|---|---|
| `task setup` | First-time setup: install deps + start DB |
| `task backend:dev` | Start Spring Boot with hot reload |
| `task backend:test` | Run unit tests |
| `task frontend:dev` | Start Vite dev server |
| `task db:up` | Start the database |
| `task db:down` | Stop the database |
| `task db:reset` | Wipe all data and restart the database |

## Project structure

```
KRI-Monitoring/
├── backend/           # Spring Boot (Maven)
├── frontend/          # React + Vite
├── docker-compose.yml
├── Taskfile.yml
└── .gitignore
```

New to Spring? See [`backend/README.md`](backend/README.md) for a guide covering how the layers connect, validation, logging, Flyway, MapStruct, Swagger UI, and a step-by-step walkthrough for adding a new feature.

## Database

Docker Compose starts a PostgreSQL 17 instance with:

| Setting  | Value     |
|----------|-----------|
| Host     | localhost |
| Port     | 5432      |
| Database | kri       |
| Username | kri       |
| Password | kri       |

## Manual setup

If you prefer not to use Task:

```bash
# Start the database
docker compose up -d

# Start the backend
cd backend
./mvnw spring-boot:run

# Start the frontend (first time: npm install)
cd frontend
npm install
npm run dev
```
