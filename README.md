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

## Task shell completion

Enable `tab` autocompletion for `task` commands:

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

## Getting started

**1. Clone the repo**
```bash
git clone <repo-url>
cd KRI-Monitoring
```

**2. First-time setup** — installs frontend dependencies and starts the database
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

## Database migrations

Schema is managed with **Flyway**. Migration files live in `backend/src/main/resources/db/migration/` and run automatically on startup.

### Early development — iterating on the schema

While the schema is still being figured out, edit `V1__initial_schema.sql` directly. After every change:

```bash
task db:reset       # wipe the DB (removes Flyway history too)
# then restart the backend — Flyway reruns V1 from scratch
```

### Stable schema — adding changes

Once V1 is locked in, never edit it again. Add new versioned files instead:

```
V2__add_users.sql
V3__add_thresholds_to_indicators.sql
```

Flyway runs only migrations that haven't been applied yet — teammates automatically get new migrations on next startup after pulling.

### Naming convention

```
V{version}__{description}.sql
```

Double underscore between version and description. Examples: `V2__create_users.sql`, `V3__add_risk_level_to_indicators.sql`

## Hot reload

**Backend** — Spring Boot DevTools is included and watches for class changes.

Two ways to trigger a reload after editing Java files:
- **CLI:** Press `Ctrl+F9` in IntelliJ to recompile — DevTools detects the new `.class` files and restarts the app context automatically (~1-2s). Works even when the app was started via `task backend:dev` in the terminal.
- **IntelliJ run:** Start the app via IntelliJ's run button instead of the terminal. Then enable Settings → Build, Execution, Deployment → Compiler → **Build project automatically**. Saving a file triggers recompile and DevTools restarts the app — no manual step needed.

**Frontend** — Vite HMR (Hot Module Replacement) updates the browser instantly on every save. No action needed.

## All available tasks

| Task | Description |
|---|---|
| `task setup` | First-time setup: install deps + start DB |
| `task backend:dev` | Start Spring Boot with hot reload |
| `task backend:build` | Build the backend JAR |
| `task backend:test` | Run backend tests |
| `task frontend:install` | Install frontend npm dependencies |
| `task frontend:dev` | Start Vite dev server |
| `task frontend:build` | Build frontend for production |
| `task frontend:lint` | Lint frontend code |
| `task db:up` | Start the database |
| `task db:down` | Stop the database |
| `task db:reset` | Wipe all data and restart the database |
| `task db:logs` | Tail database logs |

## Project structure

```
KRI-Monitoring/
├── backend/           # Spring Boot (Maven)
├── frontend/          # React + Vite
├── docker-compose.yml
├── Taskfile.yml
└── .gitignore
```

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
