# Adding a new feature end-to-end

This guide walks through adding a completely new domain feature. The `user/` package is a good reference implementation — every step below has a working example there (it also layers on auth and password hashing, which a basic CRUD feature won't need).

The steps use a hypothetical `Organization` feature. Follow them in order.

---

## 1. Add the database table

During early development, edit `src/main/resources/db/migration/V1_0__initial-schema.sql` directly:

```sql
CREATE TABLE organizations (
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR NOT NULL,
    slug  VARCHAR NOT NULL UNIQUE
);
```

Then reset the database so Flyway re-runs from scratch:

```bash
task db:reset
task backend:dev   # restart the backend — Flyway applies migrations on startup
```

See [Database & Flyway](../README.md#database--flyway) for when to switch to additive migrations instead.

---

## 2. Create the JPA entity

```java
// organization/Organization.java
@Entity
@Table(name = "organizations")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;
}
```

Field names map to column names by convention (`camelCase` → `snake_case`). Use `@Column(name = "...")` only when the names differ.

Reference: `user/User.java`

---

## 3. Create the repository

```java
// organization/OrganizationRepository.java
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    // findAll, findById, save, deleteById, existsById — all free from JpaRepository

    // Spring Data JPA reads the method name and generates the SQL:
    Optional<Organization> findBySlug(String slug);
}
```

You rarely need to write SQL. For anything complex, use `@Query` with JPQL or native SQL.

Reference: `user/UserRepository.java`

---

## 4. Create the DTOs

Use Java records — immutable, no boilerplate.

```java
// organization/dto/OrganizationResponse.java
public record OrganizationResponse(Long id, String name, String slug) {}
```

```java
// organization/dto/CreateOrganizationRequest.java
public record CreateOrganizationRequest(
    @NotBlank String name,
    @NotBlank String slug
) {}
```

Always keep request and response types separate — they look similar now but diverge as the feature evolves.

Reference: `user/dto/`

---

## 5. Create the mapper

```java
// organization/OrganizationMapper.java
@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationResponse toResponse(Organization organization);
    Organization toEntity(CreateOrganizationRequest request);
}
```

MapStruct generates the implementation at compile time by matching field names. See [MapStruct](../README.md#mapstruct-dto-mapping) for details.

Reference: `user/UserMapper.java`

---

## 6. Create the service

```java
// organization/OrganizationService.java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    public List<OrganizationResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public OrganizationResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found: " + id));
    }

    public OrganizationResponse create(CreateOrganizationRequest request) {
        log.info("Creating organization name={}", request.name());
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public OrganizationResponse update(Long id, CreateOrganizationRequest request) {
        Organization org = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found: " + id));
        org.setName(request.name());
        org.setSlug(request.slug());
        return mapper.toResponse(repository.save(org));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Organization not found: " + id);
        }
        repository.deleteById(id);
    }
}
```

Throw `EntityNotFoundException` for missing resources — `GlobalExceptionHandler` catches it and returns a 404 automatically. No `try/catch` needed.

Reference: `user/UserService.java`

---

## 7. Create the controller

```java
// organization/OrganizationController.java
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService service;

    @GetMapping
    public List<OrganizationResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OrganizationResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganizationResponse create(@Valid @RequestBody CreateOrganizationRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public OrganizationResponse update(@PathVariable Long id,
                                       @Valid @RequestBody CreateOrganizationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

- `@Valid` on `@RequestBody` triggers bean validation — errors are handled automatically
- Return DTOs, never JPA entities directly
- Use `@ResponseStatus` for non-200 success codes (201 Created, 204 No Content)

Reference: `user/UserController.java`

---

## 8. Verify in Swagger UI

Start the backend and open `http://localhost:8080/swagger-ui.html`. Your new endpoints should appear automatically — try them from the browser to confirm everything works end-to-end.

---

## Checklist

- [ ] Migration: table added, `task db:reset` run
- [ ] Entity: `@Entity`, `@Id`, `@GeneratedValue`, column constraints
- [ ] Repository: extends `JpaRepository<Entity, Long>`
- [ ] DTOs: separate request/response records, validation annotations on request fields
- [ ] Mapper: `@Mapper(componentModel = "spring")`, `toResponse` + `toEntity` methods
- [ ] Service: `EntityNotFoundException` for missing resources, explicit `save()` on updates
- [ ] Controller: `@RestController`, `@RequestMapping`, `@Valid` on request bodies, correct `@ResponseStatus` codes
- [ ] Verified in Swagger UI
