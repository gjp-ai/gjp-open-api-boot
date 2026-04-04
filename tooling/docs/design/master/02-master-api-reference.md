# Master Module — API Reference

> **Base URL:** `http://localhost:8084/ai` (dev) | `https://www.ganjianping.com/ai` (prod)
> **Authentication:** None — all endpoints are public.
> **Methods:** GET only.

---

## Response Envelope

```json
{
  "status": {
    "code": 200,
    "message": "Public app settings retrieved successfully",
    "errors": null
  },
  "data": [ ... ],
  "meta": {
    "serverDateTime": "2026-04-04 10:30:00"
  }
}
```

---

## Endpoints

### GET /v1/app-settings

Returns all application settings where `is_public = true`, across all languages.

**Query parameters:** None.

**Response:** `200 OK` — list of `AppSettingDto` objects.

**Example response:**
```json
{
  "status": {
    "code": 200,
    "message": "Public app settings retrieved successfully",
    "errors": null
  },
  "data": [
    {
      "name": "site_title",
      "value": "GJP AI Learning Platform",
      "lang": "EN"
    },
    {
      "name": "site_title",
      "value": "GJP AI 学习平台",
      "lang": "ZH"
    },
    {
      "name": "footer_text",
      "value": "© 2026 GJP. All rights reserved.",
      "lang": "EN"
    },
    {
      "name": "feature_blog_enabled",
      "value": "true",
      "lang": "EN"
    },
    {
      "name": "social_github",
      "value": "https://github.com/gjp-ai",
      "lang": "EN"
    }
  ],
  "meta": {
    "serverDateTime": "2026-04-04 10:30:00"
  }
}
```

**Notes:**
- Returns a flat list — no pagination.
- All languages (`EN`, `ZH`) are returned together; clients filter by language client-side.
- Settings with `is_public = false` are never included.
- The `id` and `is_public` fields are not included in the response.
- If no public settings exist, returns an empty list (`"data": []`).

---

## Error Responses

This endpoint has minimal error cases since it takes no parameters:

| HTTP Status | `status.code` | Cause |
|---|---|---|
| `500 Internal Server Error` | `500` | Unexpected server error (e.g. database connectivity) |

---

## Endpoint Summary

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/v1/app-settings` | None | Get all public app settings (all languages) |

---

*Last Updated: 2026-04-04*
