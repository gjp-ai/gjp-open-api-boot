# CMS Module — Database Schema

> **Schema is owned and managed by `gjp-admin-api-boot`.**
> This project uses `ddl-auto: none` — never run DDL migrations from this project.
> All tables are read via JPA entities only.

---

## cms_article

Stores blog/news articles. One row per language per article.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `title` | `VARCHAR(255)` | NO | — | Article title |
| `summary` | `VARCHAR(500)` | YES | NULL | Short summary/excerpt |
| `content` | `LONGTEXT` | YES | NULL | Full HTML/Markdown content |
| `original_url` | `VARCHAR(500)` | YES | NULL | Source article URL |
| `source_name` | `VARCHAR(255)` | YES | NULL | Source publication name |
| `cover_image_filename` | `VARCHAR(500)` | YES | NULL | Filename of cover image (stored by admin API) |
| `cover_image_original_url` | `VARCHAR(500)` | YES | NULL | Original URL of cover image |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `created_at` | `TIMESTAMP` | NO | — | Creation timestamp |
| `updated_at` | `TIMESTAMP` | NO | — | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |

**Primary Key:** `id`

---

## cms_article_image

Images associated with a specific article. One row per image per language.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `article_id` | `CHAR(36)` | NO | — | Reference to `cms_article.id` (no FK constraint) |
| `article_title` | `VARCHAR(500)` | YES | NULL | Denormalised article title |
| `filename` | `VARCHAR(255)` | NO | — | Stored filename |
| `original_url` | `VARCHAR(500)` | YES | NULL | Original source URL |
| `width` | `INT` | YES | NULL | Image width in pixels |
| `height` | `INT` | YES | NULL | Image height in pixels |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of parent article |
| `display_order` | `INT` | NO | `0` | Sort order within article |
| `created_at` | `TIMESTAMP` | NO | — | Creation timestamp |
| `updated_at` | `TIMESTAMP` | NO | — | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |

**Primary Key:** `id`
> No foreign key constraint on `article_id` — referential integrity is enforced at the application layer in the admin API.

---

## cms_audio

Audio files with optional cover image, subtitle, and artist metadata.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(255)` | NO | — | Display name |
| `filename` | `VARCHAR(255)` | NO | — | Stored audio filename |
| `size_bytes` | `BIGINT` | YES | NULL | File size in bytes |
| `cover_image_filename` | `VARCHAR(500)` | YES | NULL | Cover art filename |
| `original_url` | `VARCHAR(500)` | YES | NULL | Original download URL |
| `source_name` | `VARCHAR(255)` | YES | NULL | Source name |
| `description` | `VARCHAR(500)` | YES | NULL | Short description |
| `subtitle` | `TEXT` | YES | NULL | Full subtitle/lyrics text |
| `artist` | `VARCHAR(255)` | YES | NULL | Artist name |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `created_at` | `TIMESTAMP` | NO | — | Creation timestamp |
| `updated_at` | `TIMESTAMP` | NO | — | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |

**Primary Key:** `id`

---

## cms_file

Generic uploaded files (PDFs, documents, archives, etc.).

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(255)` | YES | NULL | Display name |
| `original_url` | `VARCHAR(255)` | YES | NULL | Original source URL |
| `source_name` | `VARCHAR(255)` | YES | NULL | Source name |
| `filename` | `VARCHAR(255)` | YES | NULL | Stored filename |
| `size_bytes` | `BIGINT` | YES | NULL | File size in bytes |
| `extension` | `VARCHAR(255)` | YES | NULL | File extension (e.g. `pdf`) |
| `mime_type` | `VARCHAR(255)` | YES | NULL | MIME type |
| `tags` | `VARCHAR(255)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | YES | `0` | Editorial sort order |
| `created_at` | `TIMESTAMP` | YES | NULL | Creation timestamp |
| `updated_at` | `TIMESTAMP` | YES | NULL | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | YES | `1` | Visibility flag |

**Primary Key:** `id`

---

## cms_image

Images with thumbnail support and alt text for accessibility.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(255)` | NO | — | Display name |
| `original_url` | `VARCHAR(500)` | YES | NULL | Original source URL |
| `source_name` | `VARCHAR(255)` | YES | NULL | Source name |
| `filename` | `VARCHAR(255)` | NO | — | Stored full-size filename |
| `thumbnail_filename` | `VARCHAR(255)` | YES | NULL | Stored thumbnail filename (400px) |
| `extension` | `VARCHAR(10)` | NO | — | File extension |
| `mime_type` | `VARCHAR(100)` | YES | NULL | MIME type |
| `size_bytes` | `BIGINT` | YES | NULL | File size in bytes |
| `width` | `INT` | YES | NULL | Image width in pixels |
| `height` | `INT` | YES | NULL | Image height in pixels |
| `alt_text` | `VARCHAR(500)` | YES | NULL | Accessibility alt text |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `created_at` | `TIMESTAMP` | NO | — | Creation timestamp |
| `updated_at` | `TIMESTAMP` | NO | — | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |

**Primary Key:** `id`

---

## cms_logo

Brand/site logos. Lightweight — no audit trail beyond `updated_at`.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(255)` | NO | — | Display name |
| `original_url` | `VARCHAR(500)` | YES | NULL | Original source URL |
| `filename` | `VARCHAR(255)` | NO | — | Stored filename |
| `extension` | `VARCHAR(16)` | NO | — | File extension (svg, png, etc.) |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |
| `updated_at` | `DATETIME` | NO | — | Last update timestamp |

**Primary Key:** `id`

---

## cms_question

FAQ-style question and answer pairs.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `question` | `VARCHAR(255)` | NO | — | Question text |
| `answer` | `VARCHAR(2000)` | YES | NULL | Answer text |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |
| `updated_at` | `DATETIME` | NO | — | Last update timestamp |

**Primary Key:** `id`

---

## cms_video

Video file metadata. Actual video streaming is handled by the admin API.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(255)` | NO | — | Display name |
| `filename` | `VARCHAR(255)` | NO | — | Stored video filename |
| `size_bytes` | `BIGINT` | YES | NULL | File size in bytes |
| `cover_image_filename` | `VARCHAR(500)` | YES | NULL | Cover image filename |
| `original_url` | `VARCHAR(500)` | YES | NULL | Original download URL |
| `source_name` | `VARCHAR(255)` | YES | NULL | Source name |
| `description` | `VARCHAR(500)` | YES | NULL | Short description |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `created_at` | `TIMESTAMP` | NO | — | Creation timestamp |
| `updated_at` | `TIMESTAMP` | NO | — | Last update timestamp |
| `created_by` | `CHAR(36)` | YES | NULL | User ID who created |
| `updated_by` | `CHAR(36)` | YES | NULL | User ID who last updated |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |

**Primary Key:** `id`

---

## cms_website

Website directory — links to external sites with logo and description.

| Column | Type | Nullable | Default | Description |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NO | — | UUID primary key |
| `name` | `VARCHAR(128)` | NO | — | Website display name |
| `url` | `VARCHAR(500)` | NO | — | Website URL |
| `logo_url` | `VARCHAR(500)` | YES | NULL | Logo image URL |
| `description` | `VARCHAR(1000)` | YES | NULL | Short description |
| `tags` | `VARCHAR(500)` | YES | NULL | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NO | `'EN'` | Language of this row |
| `display_order` | `INT` | NO | `0` | Editorial sort order |
| `is_active` | `TINYINT(1)` | NO | `1` | Visibility flag |
| `updated_at` | `DATETIME` | NO | — | Last update timestamp |

**Primary Key:** `id`

---

*Last Updated: 2026-04-04*
