# Backend — Spring Boot Guide

A reference for teammates new to Spring. The `book/` package is a **demo** — it exists to show every pattern you'll use when building the real KRI features. Read it alongside this guide.

- [Package structure](#package-structure)
- [Request lifecycle](#request-lifecycle)
- [Database & Flyway](#database--flyway)
- [Validation](#validation)
- [MapStruct (DTO mapping)](#mapstruct-dto-mapping)
- [Logging & request tracing](#logging--request-tracing)
- [Swagger UI](#swagger-ui)
- [application.yaml highlights](#applicationyaml-highlights)
- [Adding a new feature →](docs/adding-a-feature.md)

---

## Package structure

```
com.leftovers.kri/
├── book/                        ← one package per domain feature
│   ├── Book.java                ← JPA entity (maps to a DB table)
│   ├── BookRepository.java      ← data access (Spring Data JPA)
│   ├── BookService.java         ← business logic
│   ├── BookController.java      ← HTTP layer
│   ├── BookMapper.java          ← DTO ↔ entity conversion (MapStruct)
│   └── dto/
│       ├── CreateBookRequest.java
│       ├── UpdateBookRequest.java
│       └── BookResponse.java
├── config/
│   └── OpenApiConfig.java       ← Swagger/OpenAPI setup
├── exception/
│   ├── GlobalExceptionHandler.java  ← catches exceptions app-wide, returns structured errors
│   └── ApiError.java               ← the shape of every error response
├── logging/
│   ├── RequestIdFilter.java         ← attaches X-Request-ID to every request
│   └── RequestLoggingFilter.java    ← logs method, path, status, duration on completion
└── security/
    └── SecurityConfig.java          ← auth config (currently wide open — no auth yet)
```

Each new domain feature (`kri/`, `organization/`, etc.) should follow the same layout as `book/`.

---

## Request lifecycle

Every HTTP request flows through the same layers in order:

```
HTTP Request
    ↓
RequestIdFilter          — generates/forwards X-Request-ID, puts it in MDC
    ↓
RequestLoggingFilter     — logs the completed request (method, path, status, duration)
    ↓
BookController           — validates input, calls the service
    ↓
BookService              — business logic, calls the repository
    ↓
BookRepository           — SQL queries (Spring Data JPA generates the implementation)
    ↓
PostgreSQL
```

If anything throws an exception at any layer, `GlobalExceptionHandler` intercepts it and returns a structured JSON error — you never write `try/catch` in controllers or services for standard cases.

**Start reading here, in this order:**

1. `book/dto/CreateBookRequest.java` — what the client sends
2. `book/BookController.java` — how requests come in
3. `book/BookService.java` — where the actual work happens
4. `book/BookRepository.java` — how data is fetched
5. `exception/GlobalExceptionHandler.java` — how errors become responses

---

## Database & Flyway

Flyway runs automatically on every startup and applies any pending SQL migrations in version order. Migration files live in:

```
src/main/resources/db/migration/
├── V1_0__initial-schema.sql   ← CREATE TABLE statements
└── V1_1__initial-data.sql     ← seed / sample data
```

### During early development (schema not stable yet)

Just edit the existing migration files directly and reset the database:

```bash
task db:reset
```

This wipes the volume and restarts the container — Flyway re-runs all migrations from scratch on the next backend start. Adding a column directly to the `CREATE TABLE` in `V1_0` is far easier than writing a separate `ALTER TABLE` for every small change, especially when the schema is still evolving.

### Once the schema stabilises

Stop editing existing files. Add new numbered migrations instead:

```
V1_2__add-organization-id-to-books.sql
V1_3__create-kri-table.sql
```

Each migration must be additive (add columns, create tables) — never destructive. Once a migration has run on any shared environment, treat it as immutable: Flyway checksums every file and will refuse to start if it detects a modification.

**Naming convention:** `V{major}_{minor}__{description}.sql` — double underscore before the description, words separated by hyphens.

---

## Validation

Input validation uses Jakarta Bean Validation annotations. Two steps:

**Step 1 — annotate the request DTO:**

```java
public record CreateBookRequest(
    @NotBlank String title,
    @NotBlank String author,
    @Min(1000) @Max(2100) Integer publishedYear
) {}
```

**Step 2 — add `@Valid` to the controller parameter:**

```java
@PostMapping
public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
    ...
}
```

That's it. When validation fails, Spring throws `MethodArgumentNotValidException` automatically. `GlobalExceptionHandler` catches it and returns a structured 400 with field-level error messages — no manual handling needed.

**Common annotations:** `@NotBlank`, `@NotNull`, `@Min`, `@Max`, `@Size`, `@Email`, `@Pattern`.

---

## MapStruct (DTO mapping)

MapStruct reads your mapper interface at compile time and generates the implementation. You only write the interface:

```java
@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toResponse(Book book);
    Book toEntity(CreateBookRequest request);
}
```

It matches fields by name automatically. If names differ between source and target, add `@Mapping`:

```java
@Mapping(source = "publishedYear", target = "year")
BookResponse toResponse(Book book);
```

The generated mapper is a Spring bean — inject it normally:

```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper mapper;

    public BookResponse getById(Long id) {
        Book book = repository.findById(id).orElseThrow(...);
        return mapper.toResponse(book);
    }
}
```

---

## Logging & request tracing

Every request automatically gets an `X-Request-ID` (generated if the client doesn't send one). It's stored in MDC so every log line during that request includes it — useful for tracing a request across log output.

**To log in a service or controller, add `@Slf4j` (Lombok) and use the generated `log` field:**

```java
@Slf4j
@Service
public class BookService {

    public BookResponse getById(Long id) {
        log.info("Fetching book id={}", id);  // use {} placeholders, never string concat
        ...
    }
}
```

**Log level guide:**

| Level | When to use |
|-------|-------------|
| `log.info(...)` | Normal operations — something happened successfully |
| `log.warn(...)` | Unexpected but recoverable — e.g. a 404 on a resource that should exist |
| `log.error("message", exception)` | Actual failures — always pass the exception as the last argument |

`RequestLoggingFilter` already logs every request's method, path, status, and duration — don't repeat that in controllers.

---

## Swagger UI

With the backend running, open:

```
http://localhost:8080/swagger-ui.html
```

Swagger UI documents every endpoint, shows expected request/response shapes, and lets you call the API directly from the browser. Use it to explore and manually test endpoints as you build — no extra tooling needed.

The OpenAPI configuration is in `config/OpenApiConfig.java`.

---

## application.yaml highlights

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kri
    username: kri
    password: kri

  jpa:
    hibernate:
      ddl-auto: validate   # Hibernate verifies the schema matches entities but never modifies it.
                           # Flyway owns the schema — Hibernate never creates or alters tables.
    open-in-view: false    # Prevents lazy-loading outside transactions. Leave this off.

  flyway:
    enabled: true          # Migrations run automatically on every startup.

logging:
  pattern:
    console: "..."         # Includes timestamp, X-Request-ID from MDC, logger name, and level.
```

The test config at `src/test/resources/application.yaml` overrides settings for tests (H2 in-memory database instead of PostgreSQL, so tests run without Docker).
