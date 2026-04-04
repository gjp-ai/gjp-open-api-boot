# TODO & Known Gaps

Track planned work and known limitations. Update this file when items are completed or new ones are discovered.

---

## In Progress

*(nothing currently in progress)*

---

## Planned Work

### P1 — High Priority

- [ ] **Complete test coverage to 90%+** — JaCoCo threshold is set but coverage may not yet meet 90% across all modules. Run `./tooling/script/coverage.sh` to check current status. Modules to prioritise:
  - All `*ControllerTest` classes (verify `@WebMvcTest` setup for each module)
  - All `*ServiceTest` classes (verify mock repository coverage)
  - `CmsUtil` edge cases

- [ ] **Article image serving endpoints** — Verify `GET /v1/articles/cover-images/{filename}` and `GET /v1/articles/content-images/{filename}` are implemented and serving files correctly from the upload directory.

### P2 — Medium Priority

- [ ] **Design documentation** — `tooling/docs/` currently only has `guide/CODING_STANDARDS.md`. Add design docs similar to admin API:
  - `tooling/docs/design/` — per-module API reference and database schema
  - At minimum: a CMS overview and API reference doc

- [ ] **Postman collection** — A collection exists in `tooling/postman/` but may be incomplete. Verify all endpoints are covered and the collection is importable.

- [ ] **Article content image endpoint** — Confirm there is a `GET /v1/articles/content-images/{filename}` endpoint for serving article body images, separate from cover images.

- [ ] **Website logo serving** — `Website.logoUrl` stores a URL string. Verify whether this is an external URL or an internal path, and document accordingly.

### P3 — Low Priority

- [ ] **OpenAPI / Swagger** — No API documentation UI is configured. Consider adding `springdoc-openapi` for interactive docs.

- [ ] **Caching** — Public read-only endpoints are good candidates for HTTP caching (`Cache-Control` headers) or Spring `@Cacheable`. Not implemented yet.

- [ ] **Rate limiting** — No rate limiting on public endpoints. May be handled at the reverse proxy/CDN layer but not in the app itself.

---

## Known Limitations

| Limitation | Details |
|---|---|
| No authentication | Intentional — all endpoints are public (see ADR-002) |
| No mutations | Intentional — write ops belong in admin API (see ADR-001) |
| Shared database | Reads from same DB as admin API — no read replica (see ADR-003) |
| No caching | Responses are not cached — every request hits the database |
| `isPublic` flag not per-language | A setting with `is_public=true` for EN will not automatically expose the ZH variant |
| Media files served by admin API | Public API constructs URLs pointing to admin API file-serving endpoints — if admin API is down, media URLs will be broken |

---

## Decided Against (Do Not Re-propose)

| Idea | Reason |
|---|---|
| Authentication on any endpoint | Public API by design — see ADR-002 |
| POST/PUT/DELETE endpoints | Write ops belong in admin API — see ADR-001 |
| `@Data` on entities | Breaks Hibernate proxies — see ADR-007 |
| Shared utility methods across modules (other than CmsUtil) | All CMS utilities belong in `CmsUtil` — see ADR-008 |
| Separate `*UploadProperties` per module | Already done — do not consolidate into one class (see ADR-010) |
| Lowering JaCoCo 90% threshold | Non-negotiable — see ADR-009 |

---

*Last Updated: 2026-04-04*
