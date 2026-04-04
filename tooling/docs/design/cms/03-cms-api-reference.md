# CMS Module — API Reference

> **Base URL:** `http://localhost:8084/ai` (dev) | `https://www.ganjianping.com/ai` (prod)
> **Authentication:** None — all endpoints are public.
> **Methods:** GET only.

---

## Response Envelope

All endpoints wrap responses in the same envelope:

```json
{
  "status": {
    "code": 200,
    "message": "Articles retrieved",
    "errors": null
  },
  "data": { ... },
  "meta": {
    "serverDateTime": "2026-04-04 10:30:00"
  }
}
```

**Paginated list response** (`data` field):
```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

**Error response:**
```json
{
  "status": {
    "code": 404,
    "message": "Article not found",
    "errors": null
  },
  "data": null,
  "meta": {
    "serverDateTime": "2026-04-04 10:30:00"
  }
}
```

---

## Common List Query Parameters

All list endpoints share these pagination/filter parameters:

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `page` | `int` | No | `0` | Page number (0-based) |
| `size` | `int` | No | `20` | Page size |
| `sort` | `string` | No | `displayOrder` | Sort field |
| `direction` | `string` | No | `asc` | Sort direction: `asc` or `desc` |
| `lang` | `string` | No | — | Filter by language: `EN` or `ZH` |
| `isActive` | `boolean` | No | — | Filter by active status |
| `tags` | `string` | No | — | Filter by tag (partial match) |

---

## Articles

### GET /v1/articles

Returns a paginated list of articles.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `title` | `string` | No | — | Filter by title (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Articles retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "title": "Introduction to Machine Learning",
        "summary": "A beginner's guide to ML concepts",
        "originalUrl": "https://source.example.com/article",
        "sourceName": "AI Weekly",
        "coverImageOriginalUrl": "https://source.example.com/image.jpg",
        "coverImageUrl": "https://www.ganjianping.com/ai/v1/articles/cover-images/abc.jpg",
        "tags": "AI,ML,beginner",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 42,
    "totalPages": 3
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/articles/{id}

Returns full article detail including `content` (HTML/Markdown body).

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | `string` | Article UUID |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Article retrieved", "errors": null },
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Introduction to Machine Learning",
    "summary": "A beginner's guide to ML concepts",
    "content": "<h1>Introduction</h1><p>Machine learning is...</p>",
    "originalUrl": "https://source.example.com/article",
    "sourceName": "AI Weekly",
    "coverImageOriginalUrl": "https://source.example.com/image.jpg",
    "coverImageUrl": "https://www.ganjianping.com/ai/v1/articles/cover-images/abc.jpg",
    "tags": "AI,ML,beginner",
    "lang": "EN",
    "displayOrder": 1,
    "updatedAt": "2026-04-01T08:00:00.000Z"
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

> The `content` field is only present in the detail endpoint, not in the list endpoint.

---

## Audio

### GET /v1/audios

Returns a paginated list of audio records.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Audios retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "title": "Deep Learning Podcast Episode 1",
        "description": "Introduction to neural networks",
        "subtitle": null,
        "artist": "AI Learning Team",
        "url": "https://www.ganjianping.com/ai/v1/audios/view/episode1.mp3",
        "coverImageUrl": "https://www.ganjianping.com/ai/v1/audios/cover-images/cover1.jpg",
        "tags": "podcast,deep-learning",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 15,
    "totalPages": 1
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/audios/{id}

Returns a single audio record by ID.

---

### GET /v1/audios/view/{filename}

Streams an audio file. Supports HTTP Range requests for seeking.

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `filename` | `string` | Audio filename (e.g. `episode1.mp3`) |

**Request headers:**

| Header | Required | Description |
|---|---|---|
| `Range` | No | Byte range (e.g. `bytes=0-1048575`) for partial content |

**Full file response (200 OK):**
```
Content-Type: audio/mpeg
Content-Disposition: inline; filename="episode1.mp3"
Content-Length: 5242880
```

**Range response (206 Partial Content):**
```
HTTP/1.1 206 Partial Content
Accept-Ranges: bytes
Content-Range: bytes 0-1048575/5242880
Content-Type: audio/mpeg
Content-Length: 1048576
```

Returns `404 Not Found` if filename is invalid or file not found.

---

### GET /v1/audios/cover-images/{filename}

Serves an audio cover image inline.

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `filename` | `string` | Cover image filename (e.g. `cover1.jpg`) |

**Response headers:**
```
Content-Type: image/jpeg
Content-Disposition: inline; filename="cover1.jpg"
```

---

## Files

### GET /v1/files

Returns a paginated list of file records.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Files retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "name": "Machine Learning Cheatsheet",
        "url": "https://www.ganjianping.com/ai/v1/files/view/ml-cheatsheet.pdf",
        "originalUrl": null,
        "tags": "ML,cheatsheet,PDF",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 8,
    "totalPages": 1
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/files/{id}

Returns a single file record by ID.

---

### GET /v1/files/view/{filename}

Downloads a file. Returns the file as a forced download (`Content-Disposition: attachment`).

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `filename` | `string` | File filename (e.g. `ml-cheatsheet.pdf`) |

**Response headers:**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="ml-cheatsheet.pdf"
Content-Length: 204800
```

Returns `404 Not Found` if filename is invalid or file not found.

---

## Images

### GET /v1/images

Returns a paginated list of images.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Images retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440003",
        "name": "Neural Network Diagram",
        "altText": "Diagram showing layers of a neural network",
        "url": "https://www.ganjianping.com/ai/v1/images/view/nn-diagram.png",
        "thumbnailUrl": "https://www.ganjianping.com/ai/v1/images/view/nn-diagram-thumb.png",
        "originalUrl": null,
        "tags": "diagram,neural-network",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 30,
    "totalPages": 2
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/images/{id}

Returns a single image record by ID.

---

### GET /v1/images/view/{filename}

Serves an image file inline.

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `filename` | `string` | Image filename (e.g. `nn-diagram.png`) |

**Response headers:**
```
Content-Type: image/png
Content-Disposition: inline; filename="nn-diagram.png"
```

Returns `404 Not Found` if filename is invalid or file not found.

---

## Logos

### GET /v1/logos

Returns a paginated list of logos.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Logos retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440004",
        "name": "GJP Logo",
        "url": "https://www.ganjianping.com/ai/v1/logos/view/gjp-logo.png",
        "thumbnailUrl": null,
        "tags": "brand,logo",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 5,
    "totalPages": 1
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/logos/{id}

Returns a single logo record by ID.

---

### GET /v1/logos/view/{filename}

Serves a logo file inline.

**Path parameters:**

| Parameter | Type | Description |
|---|---|---|
| `filename` | `string` | Logo filename (e.g. `gjp-logo.png`) |

**Response headers:**
```
Content-Type: image/png
Content-Disposition: inline; filename="gjp-logo.png"
```

Returns `404 Not Found` if filename is invalid or file not found.

---

## Questions

### GET /v1/questions

Returns a paginated list of Q&A records.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `question` | `string` | No | — | Filter by question text (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Questions retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440005",
        "question": "What is machine learning?",
        "answer": "Machine learning is a subset of AI that enables systems to learn from data.",
        "tags": "AI,ML,basics",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 25,
    "totalPages": 2
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/questions/{id}

Returns a single question/answer record by ID.

---

## Videos

### GET /v1/videos

Returns a paginated list of video records. The `url` field links to the admin API's video-serving endpoint.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Videos retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440006",
        "title": "Neural Networks Explained",
        "description": "Visual explanation of how neural networks work",
        "url": "https://www.ganjianping.com/ai/v1/videos/lecture1.mp4",
        "coverImageUrl": "https://www.ganjianping.com/ai/v1/videos/cover-images/lecture1-cover.jpg",
        "tags": "neural-network,video,lecture",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 12,
    "totalPages": 1
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

> Video files do not have a dedicated streaming endpoint in the Open API. The `url` field points to the admin API's streaming endpoint.

---

### GET /v1/videos/{id}

Returns a single video record by ID.

---

## Websites

### GET /v1/websites

Returns a paginated list of website directory entries.

**Additional query parameters:**

| Parameter | Type | Required | Default | Description |
|---|---|---|---|---|
| `name` | `string` | No | — | Filter by name (partial match) |

**Example response:**
```json
{
  "status": { "code": 200, "message": "Websites retrieved", "errors": null },
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440007",
        "name": "Hugging Face",
        "description": "The AI community building the future",
        "url": "https://huggingface.co",
        "logoUrl": "https://huggingface.co/favicon.ico",
        "tags": "AI,models,datasets",
        "lang": "EN",
        "displayOrder": 1,
        "updatedAt": "2026-04-01T08:00:00.000Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 20,
    "totalPages": 1
  },
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

### GET /v1/websites/{id}

Returns a single website entry by ID.

---

## Error Responses

| HTTP Status | `status.code` | Cause |
|---|---|---|
| `400 Bad Request` | `400` | Invalid `lang` parameter (not `EN` or `ZH`) |
| `404 Not Found` | `404` | Resource not found by ID, or file not found on disk |
| `500 Internal Server Error` | `500` | Unexpected server error (e.g. file read failure) |

**Example 400 error:**
```json
{
  "status": { "code": 400, "message": "Invalid lang", "errors": null },
  "data": null,
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

**Example 404 error:**
```json
{
  "status": { "code": 404, "message": "Article not found", "errors": null },
  "data": null,
  "meta": { "serverDateTime": "2026-04-04 10:30:00" }
}
```

---

## Endpoint Summary

| Method | Path | Description |
|---|---|---|
| GET | `/v1/articles` | List articles (paginated) |
| GET | `/v1/articles/{id}` | Get article detail with content |
| GET | `/v1/audios` | List audio records (paginated) |
| GET | `/v1/audios/{id}` | Get audio record |
| GET | `/v1/audios/view/{filename}` | Stream audio file (Range supported) |
| GET | `/v1/audios/cover-images/{filename}` | Serve audio cover image |
| GET | `/v1/files` | List file records (paginated) |
| GET | `/v1/files/{id}` | Get file record |
| GET | `/v1/files/view/{filename}` | Download file (attachment) |
| GET | `/v1/images` | List images (paginated) |
| GET | `/v1/images/{id}` | Get image record |
| GET | `/v1/images/view/{filename}` | Serve image inline |
| GET | `/v1/logos` | List logos (paginated) |
| GET | `/v1/logos/{id}` | Get logo record |
| GET | `/v1/logos/view/{filename}` | Serve logo inline |
| GET | `/v1/questions` | List Q&A records (paginated) |
| GET | `/v1/questions/{id}` | Get Q&A record |
| GET | `/v1/videos` | List video records (paginated) |
| GET | `/v1/videos/{id}` | Get video record |
| GET | `/v1/websites` | List website entries (paginated) |
| GET | `/v1/websites/{id}` | Get website entry |

---

*Last Updated: 2026-04-04*
