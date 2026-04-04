# Architecture Decisions

Explains *why* key choices were made. Read before suggesting changes to these areas.

---

## ADR-001 — Read-Only Public API (No Mutations)

**Decision:** This API exposes only GET endpoints. All create/update/delete operations are handled exclusively by `gjp-admin-api-boot`.

**Why:** The public API is consumed by unauthenticated clients (browsers, mobile apps). Allowing mutations without authentication would be a security risk. The admin API handles all writes with proper authentication and authorization.

**Consequence:** Never add POST, PUT, PATCH, or DELETE endpoints to this project. If a new mutation is needed, implement it in `gjp-admin-api-boot`.

---

## ADR-002 — No Authentication on Any Endpoint

**Decision:** All endpoints in this API are unauthenticated. There is no Spring Security, no JWT, no session management.

**Why:** This is a public content delivery API. Adding authentication would break the use case (public website, mobile app without login).

**Consequence:** Do not add `@PreAuthorize`, `SecurityConfig`, or any auth dependency. If a protected endpoint is needed, it belongs in the admin API.

---

## ADR-003 — Shared Database with Admin API

**Decision:** This project reads from the same MySQL database and the same tables as `gjp-admin-api-boot`. There is no separate read replica or data sync.

**Why:** Simplicity. A separate read database would add operational overhead (replication lag, schema sync) for no current benefit at this scale.

**Consequence:** Schema changes must be coordinated between both projects. The admin API owns the schema — do not run DDL migrations from this project in production (`ddl-auto: none` in prod).

---

## ADR-004 — Only `isPublic = true` App Settings Exposed

**Decision:** `GET /v1/app-settings` returns only records where `is_public = true`. Private/system settings are never included in public API responses.

**Why:** The `master_app_settings` table contains a mix of public UI configuration (site title, footer text) and private/system settings (internal flags, infrastructure config). Exposing all settings would leak internal configuration.

**Consequence:** When creating settings in the admin API, set `is_public = true` only for settings intended for public consumption.

---

## ADR-005 — Feature-Based Package Structure

**Decision:** Code is organised by feature (`cms/article/`, `cms/video/`) rather than by layer (`controller/`, `service/`, `repository/`).

**Why:** Feature-based structure keeps all related code co-located. See matching decision in admin API (ADR-007 there).

**Consequence:** Each feature package contains its entity, repository, service, controller, response DTO, and upload properties. Shared infrastructure lives in `core/`.

---

## ADR-006 — `displayOrder ASC` as Default Sort

**Decision:** All list endpoints default to sorting by `display_order ASC` rather than `created_at DESC`.

**Why:** This API serves a public-facing website where content ordering is editorially controlled via `display_order`. Defaulting to creation date would require clients to re-sort on every request.

**Consequence:** The admin API must keep `display_order` values meaningful. Do not change the default sort direction to `DESC` without editorial review.

---

## ADR-007 — No `@Data` on Entities

**Decision:** JPA entities use individual Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@ToString`, etc.) instead of `@Data`.

**Why:** `@Data` generates `hashCode()` based on all fields, which breaks Hibernate lazy-loading proxies and can cause infinite recursion in bidirectional relationships. Individual annotations give precise control.

**Consequence:** Always write `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` on entities. Never use `@Data` on an entity class.

---

## ADR-008 — `CmsUtil` as the Single Utility Class

**Decision:** All CMS cross-cutting utilities (language parsing, filename validation, pageable building, URL joining, MIME type detection) live in a single `CmsUtil` class.

**Why:** Keeps utilities discoverable and prevents duplicate implementations scattered across modules.

**Consequence:** Before writing a new utility method, check `CmsUtil` first. Add new utilities there rather than creating per-module helpers.

Key methods:
- `CmsUtil.parseLanguage(String, Class<E>)` — null-safe enum parse
- `CmsUtil.buildPageable(page, size, sort, direction)` — validated Pageable
- `CmsUtil.validateFilename(String)` — path traversal prevention
- `CmsUtil.determineContentType(String)` — MIME type from extension

---

## ADR-009 — 90%+ Test Coverage Enforced by JaCoCo

**Decision:** JaCoCo is configured with a minimum line coverage threshold of 90%. Builds fail below this threshold.

**Why:** This is a public-facing API. Regressions on public endpoints directly affect users. High coverage is non-negotiable.

**Consequence:** Every new feature must include tests. Do not lower the JaCoCo threshold. Use `@WebMvcTest` for controllers and `@ExtendWith(MockitoExtension.class)` for services.

---

## ADR-010 — Per-Module Upload Properties Classes

**Decision:** Each media module has its own `*UploadProperties` class (`ImageUploadProperties`, `VideoUploadProperties`, etc.) bound to its own `@ConfigurationProperties` prefix.

**Why:** Different media types have fundamentally different upload requirements (image max size 10 MB, video max size 500 MB, logo resize to 256px). A single shared properties class would be cluttered and hard to reason about.

**Consequence:** When adding a new media module, create a dedicated `*UploadProperties` class. Do not reuse another module's properties class.

---

*Last Updated: 2026-04-04*
