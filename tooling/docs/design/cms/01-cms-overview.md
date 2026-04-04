# CMS Module — Overview

> GJP Open API — read-only, unauthenticated public content delivery.
> All CMS endpoints are GET only. No mutations. No authentication required.

---

## Module Map

| Module | Package | Entity | Table | Description |
|---|---|---|---|---|
| Article | `cms.article` | `Article` | `cms_article` | Blog/news articles with cover image and content |
| Article Image | `cms.article.image` | `ArticleImage` | `cms_article_image` | Images belonging to an article |
| Audio | `cms.audio` | `Audio` | `cms_audio` | Audio files with streaming support |
| File | `cms.file` | `File` | `cms_file` | Generic file downloads |
| Image | `cms.image` | `Image` | `cms_image` | Images with thumbnail support |
| Logo | `cms.logo` | `Logo` | `cms_logo` | Brand/site logos |
| Question | `cms.question` | `Question` | `cms_question` | FAQ-style Q&A content |
| Video | `cms.video` | `Video` | `cms_video` | Video metadata |
| Website | `cms.website` | `Website` | `cms_website` | Website directory entries |

---

## Shared Patterns

### No BaseEntity
Unlike `gjp-admin-api-boot`, this project does **not** use a shared `BaseEntity`. Each entity declares its own audit fields (`created_at`, `updated_at`, `created_by`, `updated_by`) where applicable. Lightweight entities (Logo, Question, Website) only carry `updated_at`.

### Language Support
Every entity has a `lang ENUM('EN','ZH')` column. Separate database rows are stored per language — not separate columns. Clients filter by `?lang=EN` or `?lang=ZH`. Omitting `lang` returns all languages.

### Display Order
Every entity has `display_order INT NOT NULL DEFAULT 0`. All list endpoints default to `ORDER BY display_order ASC`. Editorial ordering is managed in the admin API.

### isActive Flag
Every entity has `is_active TINYINT(1) NOT NULL DEFAULT 1`. Clients may filter by `?isActive=true/false`. Inactive records are not hidden by default — the frontend decides whether to show or hide them.

### UUID Primary Keys
All IDs are `CHAR(36)` UUID strings. Never auto-increment.

### Entity Annotations
New entities must use `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor` — **never `@Data`** (prevents lazy-loading issues and equals/hashCode problems on JPA entities).

> Note: Some existing entities still use `@Data` — tracked as tech debt in `tooling/docs/context/todo.md`.

---

## Media File Strategy

Files (images, audio, video, logos) are **stored and uploaded exclusively by `gjp-admin-api-boot`**. This project never handles file uploads.

The Open API constructs media URLs using a configured `cms.base-url` property. Services map stored filenames to full URLs at response time:

| Media Type | URL Pattern |
|---|---|
| Article cover image | `{cms.base-url}/v1/articles/cover-images/{filename}` |
| Article content image | `{cms.base-url}/v1/articles/content-images/{filename}` |
| Audio stream | `{cms.base-url}/v1/audios/view/{filename}` |
| Audio cover image | `{cms.base-url}/v1/audios/cover-images/{filename}` |
| Image | `{cms.base-url}/v1/images/view/{filename}` |
| File download | `{cms.base-url}/v1/files/view/{filename}` |
| Logo | `{cms.base-url}/v1/logos/view/{filename}` |
| Video | `{cms.base-url}/v1/videos/{filename}` |

`cms.base-url` is environment-specific (set via `CMS_BASE_URL` env var):
- **Dev:** typically `http://localhost:8084/ai`
- **Prod:** `https://www.ganjianping.com/ai`

---

## Key Utilities — CmsUtil

`org.ganjp.api.cms.util.CmsUtil` is a stateless utility class. **Always use these methods — never reimplement.**

| Method | Purpose |
|---|---|
| `parseLanguage(lang, EnumClass)` | Parses `"EN"`/`"ZH"` string to enum; returns `null` if blank or invalid |
| `buildPageable(page, size, sort, direction)` | Builds `Pageable`; clamps page ≥ 0, size ≥ 1 |
| `validateFilename(filename)` | Blocks path traversal (`..`, `/`, `\`); throws `IllegalArgumentException` |
| `determineContentType(filename)` | Maps file extension to MIME type |
| `joinBaseAndPath(base, path)` | Joins base URL + filename; returns `null` if path is blank |
| `joinBasePathWithSegment(base, segment, path)` | Joins base + sub-path segment + filename |

---

## Streaming Support

Audio files support **HTTP Range requests** (RFC 7233), enabling HTML5 `<audio>` seeking:

- **Full file:** `GET /v1/audios/view/{filename}` — returns `200 OK`
- **Range request:** same endpoint with `Range: bytes=0-1048575` header — returns `206 Partial Content`

```
HTTP/1.1 206 Partial Content
Accept-Ranges: bytes
Content-Range: bytes 0-1048575/5242880
Content-Type: audio/mpeg
Content-Length: 1048576
```

Video files do **not** have a streaming endpoint in this API — video metadata is served via `/v1/videos`, and the `url` field in the response points to the admin API's file-serving endpoint.

Image and Logo serve endpoints (`/view/{filename}`) return the full file inline (no Range support). File serve endpoints (`/view/{filename}`) return files as `Content-Disposition: attachment` (forced download).

---

## Package Structure

```
org.ganjp.api/
├── core/
│   ├── config/CorsConfig.java
│   ├── exception/GlobalExceptionHandler.java
│   └── model/
│       ├── ApiResponse.java
│       └── PaginatedResponse.java
├── cms/
│   ├── util/CmsUtil.java           ← shared utilities
│   ├── article/
│   │   ├── image/                  ← ArticleImage sub-package
│   │   ├── Article.java
│   │   ├── ArticleController.java
│   │   ├── ArticleService.java
│   │   ├── ArticleRepository.java
│   │   ├── ArticleResponse.java    ← list DTO (no content field)
│   │   ├── ArticleDetailResponse.java ← detail DTO (includes content)
│   │   └── ArticleProperties.java
│   ├── audio/
│   ├── file/
│   ├── image/
│   ├── logo/
│   ├── question/
│   ├── video/
│   └── website/
└── master/
    └── setting/
```

---

## Key Constraints

| Rule | Detail |
|---|---|
| GET only | Never add POST/PUT/PATCH/DELETE |
| No authentication | No `@PreAuthorize`, no `SecurityConfig`, no JWT |
| Default sort | `displayOrder ASC` on all list endpoints |
| Pagination | Always use `CmsUtil.buildPageable()` |
| Language parsing | Always use `CmsUtil.parseLanguage()` |
| File serving | Always call `CmsUtil.validateFilename()` before serving |
| Test coverage | 90%+ enforced by JaCoCo — build fails below threshold |
| No `@Data` on new entities | Use `@Getter @Setter @Builder` separately |

---

*Last Updated: 2026-04-04*
