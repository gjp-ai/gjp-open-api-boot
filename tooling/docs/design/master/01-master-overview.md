# Master Module — Overview

> Application-wide key/value settings readable by public clients.
> All endpoints are public — no authentication required.
> Read-only. All writes are handled exclusively by `gjp-admin-api-boot`.

---

## Purpose

The Master module exposes application settings to web, mobile, and other public clients. These settings drive UI configuration, feature flags, and site-wide metadata (e.g. site title, footer text, social links, feature toggles).

Only records flagged `is_public = true` are returned. Private and system settings are never exposed through this API.

---

## Entity — AppSetting

**Table:** `master_app_settings`

| Java Field | DB Column | Type | Nullable | Default | Description |
|---|---|---|---|---|---|
| `id` | `id` | `VARCHAR(36)` | NO | — | UUID primary key |
| `name` | `name` | `VARCHAR(50)` | NO | — | Setting key name |
| `value` | `value` | `VARCHAR(500)` | YES | NULL | Setting value |
| `lang` | `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `isPublic` | `is_public` | `TINYINT(1)` | NO | `0` | Whether this setting is exposed publicly |

> Unlike CMS entities, `AppSetting` has no `display_order`, `is_active`, or audit fields (`created_at`, `updated_at`, etc.).

---

## Response DTO — AppSettingDto

The API does **not** expose the raw entity. It maps to `AppSettingDto`:

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Setting key name |
| `value` | `String` | Setting value |
| `lang` | `String` | Language (`"EN"` or `"ZH"`) |

The `id` and `isPublic` fields are intentionally omitted from the response.

---

## isPublic Flag

The filter `isPublic = true` is applied at the repository/service layer — the controller never receives private settings. This is enforced by design so the API cannot accidentally leak system or private configuration even if parameters are tampered with.

| `is_public` | Returned by Open API? |
|---|---|
| `true` | Yes |
| `false` | No |

---

## Language Support

Per-language rows: separate database rows exist for each language. A setting named `site_title` may appear twice — once with `lang = EN` and once with `lang = ZH`. The single GET endpoint returns **all public settings for all languages** — clients filter client-side by language.

---

## Use Cases

| Setting Name (example) | Purpose |
|---|---|
| `site_title` | Page title shown in browser tab |
| `site_description` | Meta description for SEO |
| `footer_text` | Footer copyright or tagline |
| `contact_email` | Public contact email |
| `social_github` | GitHub profile URL |
| `social_linkedin` | LinkedIn profile URL |
| `feature_blog_enabled` | Feature flag to show/hide blog section |
| `feature_video_enabled` | Feature flag to show/hide video section |

Setting names are arbitrary strings defined in the admin UI. No naming convention is enforced at the API level.

---

## Key Constraints

| Rule | Detail |
|---|---|
| Read-only | GET only — never add POST/PUT/PATCH/DELETE |
| isPublic filter | Only `is_public = true` records are returned |
| No authentication | No `@PreAuthorize`, no JWT |
| No pagination | Single endpoint returns all public settings as a flat list |
| All languages returned | The single endpoint returns all languages; clients filter client-side |
| Writes via admin API | `gjp-admin-api-boot` owns all writes to `master_app_settings` |

---

*Last Updated: 2026-04-04*
