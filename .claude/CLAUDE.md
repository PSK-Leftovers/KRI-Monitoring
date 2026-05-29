# KRI Monitoring — AI Context

## What this project is

B2B Key Risk Indicator monitoring platform. Organizations define risk indicators, record values over time, and the system classifies each indicator green/yellow/red against configurable thresholds. DIRECTOR users are notified by email whenever an indicator changes status. Multi-tenant — each client gets an isolated instance.

## Tech stack

**Backend**
- Java 21, Spring Boot 4, Maven (`./mvnw`)
- Spring Data JPA + PostgreSQL 17 (Docker Compose)
- Spring Security (session cookie auth, `@PreAuthorize` for role checks)
- Flyway migrations
- MapStruct for DTO ↔ entity mapping
- Lombok on entities/services, Java records for DTOs
- Spring Mail (SMTP) for notifications
- Springdoc OpenAPI — Swagger UI at `http://localhost:8080/swagger-ui.html`

**Frontend**
- React 19 + Vite, React Router 7
- Tailwind v4 (`@tailwindcss/vite`)
- Recharts for graphs, react-icons for icons
- No state library — `localStorage` for the logged-in user, fetch with `credentials: "include"`

## Key commands

```bash
task setup          # first-time: frontend install + DB up + backend build
task backend:dev    # Spring Boot with hot reload (DevTools)
task frontend:dev   # Vite dev server on :5173
task backend:test   # ./mvnw test
task db:up          # start Postgres
task db:reset       # wipe DB volume; Flyway re-runs all migrations on next backend start
```

## Backend layout

```
com.leftovers.kri/
├── auth/          ← login/logout, session-based
├── user/          ← /admin/users, ADMIN-only CRUD
├── indicator/     ← /api/indicators, values sub-resource
│   └── thresholds/← /api/indicators/{id}/thresholds (history)
├── notification/  ← email DIRECTORs on indicator status change
├── security/      ← SecurityConfig (CORS, session, BCrypt)
├── logging/       ← RequestIdFilter, RequestLoggingFilter
├── exception/     ← GlobalExceptionHandler + ApiError
└── config/        ← OpenApiConfig
```

One package per domain feature, always:

```
feature/
├── Feature.java                  ← JPA entity (Lombok @Getter/@Setter)
├── FeatureRepository.java        ← extends JpaRepository
├── FeatureService.java           ← business logic, @Transactional
├── FeatureController.java        ← HTTP layer, @Valid on request bodies
├── FeatureMapper.java            ← MapStruct interface
└── dto/
    ├── CreateFeatureRequest.java ← record + validation annotations
    └── FeatureResponse.java      ← record
```

## Auth model

- Session-based, cookie `JSESSIONID`. CSRF disabled, CORS allows `http://localhost:5173` and `:3000` with credentials.
- Public endpoints: `/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`. Everything else requires authentication.
- Roles: `ANALYST`, `DIRECTOR`, `ADMIN` (enforced via DB `CHECK` constraint and `@PreAuthorize("hasRole('X')")` on controller methods). Spring stores them as `ROLE_X` internally; strip the prefix when returning to the frontend.
- Passwords are BCrypt (cost 10). Seed users in `V1_2__user.sql` all have password `1234` — dev only.
- Frontend: every fetch uses `credentials: "include"`. The user object is cached in `localStorage` and used by `usePermissions` / `ProtectedRoute`. Role-gated UI: `ProtectedButton`, `ProtectedRoute allowedRoles={[...]}`.

## URL conventions

- `/auth/*` — login/logout, public
- `/admin/*` — admin-only operations (e.g. `/admin/users`), `@PreAuthorize("hasRole('ADMIN')")`
- `/api/*` — authenticated app data (e.g. `/api/indicators`)

## Flyway — current approach

Schema has stabilised enough that we now use **additive migrations**. Current files:

- `V1_0__initial-schema.sql` — indicator + indicator_value
- `V1_1__initial-data.sql` — seed indicators
- `V1_2__user.sql` — users table + role check + seed users
- `V1_3__updated-schema.sql` — moved thresholds into `indicator_thresholds` (history table)
- `V1_4__threholds_data.sql` — threshold history seed

Add a new `V1_N__description.sql` rather than editing existing migrations. If you must rewrite history during heavy schema churn, run `task db:reset` afterwards (it wipes the volume and re-applies everything). `ddl-auto: validate` — Hibernate verifies the schema matches entities on startup.

## Patterns to follow

- **Errors:** throw `EntityNotFoundException` (→ 404) or `EntityAlreadyExistsException` (→ 409). `GlobalExceptionHandler` also covers validation, access denied, malformed body, method-not-allowed, etc. — don't add `try/catch` for these in controllers/services.
- **Mapping:** always use the MapStruct mapper, never map fields manually.
- **DTOs:** records for all DTOs. Separate request and response types even if they look similar now.
- **Validation:** annotations on request record fields (`@NotBlank`, `@Min`, etc.) + `@Valid` on the controller `@RequestBody`.
- **Authorization:** put `@PreAuthorize("hasRole('X')")` on the controller method (see `UserController`). `@EnableMethodSecurity` is already on.
- **Logging:** `@Slf4j` + `log.info/warn/error` with `{}` placeholders. Don't log method/path/status — `RequestLoggingFilter` already does that. A `requestId` MDC value is included in every log line.
- **Transactions:** `@Transactional` on service methods that write; keep controllers thin.
- **Frontend services:** keep API calls in `src/services/*`. Always `credentials: "include"`. Map HTTP statuses to specific error strings the UI can switch on (see `authService.tsx`).

## What NOT to do

- Don't expose JPA entities from controllers — always map to a response DTO first.
- Don't use `ddl-auto: create/update` — Flyway owns the schema (`validate` only).
- Don't add Swagger `@Operation`/`@Parameter` annotations unless asked — springdoc infers them automatically.
- Don't add error handling for cases already covered by `GlobalExceptionHandler`.
- Don't hand-roll role checks (`if (user.getRole().equals(...))`) — use `@PreAuthorize`.
- Don't store secrets/passwords in plain text — always BCrypt via the configured `PasswordEncoder`.
- Don't bypass the MapStruct mapper or duplicate mapping logic in services.
