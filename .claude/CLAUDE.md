# KRI Monitoring — AI Context

## What this project is

B2B Key Risk Indicator monitoring platform. Organizations define risk indicators and track them against green/yellow/red thresholds. Multi-tenant — each client gets an isolated instance.

**The `book/` package is a demo**, not a real feature. It exists purely to demonstrate Spring patterns (controller, service, repository, DTOs, MapStruct, validation, exception handling). Real domain features (`kri/`, `organization/`, etc.) will replace it over time.

## Tech stack

- Java 21, Spring Boot 4, Maven
- Spring Data JPA + PostgreSQL 17 (via Docker Compose)
- Flyway for schema migrations
- MapStruct for DTO ↔ entity mapping
- Lombok on entities, Java records for DTOs
- Springdoc OpenAPI (Swagger UI at `http://localhost:8080/swagger-ui.html`)
- React + Vite frontend (minimal — mostly placeholder for now)

## Key commands

```bash
task db:reset       # wipe DB volume and restart — Flyway re-runs all migrations on next backend start
task backend:dev    # Spring Boot with hot reload
task frontend:dev   # Vite dev server
task backend:test   # run tests
```

## Architecture conventions

One package per domain feature, always with this structure:

```
feature/
├── Feature.java                  ← JPA entity
├── FeatureRepository.java        ← extends JpaRepository
├── FeatureService.java           ← business logic, @Transactional
├── FeatureController.java        ← HTTP layer, @Valid on request bodies
├── FeatureMapper.java            ← MapStruct interface
└── dto/
    ├── CreateFeatureRequest.java ← record with validation annotations
    └── FeatureResponse.java      ← record
```

## Flyway — current approach

Schema is still evolving. **Edit existing migration files directly** and run `task db:reset` rather than creating new versioned migrations. Files:

- `V1_0__initial-schema.sql` — CREATE TABLE statements (edit this for schema changes)
- `V1_1__initial-data.sql` — seed data

Switch to additive migrations (V1_2, V1_3, ...) once the schema stabilises.

## Patterns to follow

- **Errors:** throw `EntityNotFoundException` for missing resources — `GlobalExceptionHandler` returns 404 automatically. Never write try/catch in controllers/services for standard cases.
- **Mapping:** always use the MapStruct mapper, never map fields manually.
- **DTOs:** records for all DTOs. Separate request and response types even if they look similar now.
- **Logging:** `@Slf4j` + `log.info/warn/error` with `{}` placeholders. Don't log method/path/status — `RequestLoggingFilter` already does that.
- **Validation:** annotations on request record fields (`@NotBlank`, `@Min`, etc.) + `@Valid` on the controller `@RequestBody`.

## What NOT to do

- Don't expose JPA entities from controllers — always map to a response DTO first.
- Don't use `ddl-auto: create/update` — Flyway owns the schema.
- Don't add Swagger `@Operation`/`@Parameter` annotations unless asked — springdoc infers them automatically.
- Don't add error handling for cases already covered by `GlobalExceptionHandler`.
