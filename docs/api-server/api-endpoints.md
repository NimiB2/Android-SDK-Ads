```markdown
# API Endpoints Reference

This document provides detailed information about all available API endpoints in the AdSDK server.

## Base URL

```
https://ad-server-kappa.vercel.app/
```

For local development:
```
http://localhost:1993/
```

## Authentication

Currently, the API does not require authentication. This may change in future versions.

## Advertiser Management

### Create Performer

Creates a new advertiser (performer) or returns an existing one with the same email.

**Endpoint:** `POST /performers`

**Request Body:**
```json
{
  "name": "Company Name",
  "email": "contact@example.com"
}
```

**Response (201 Created):**
```json
{
  "message": "Performer created",
  "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

**Response (200 OK - Existing Performer):**
```json
{
  "message": "Performer already exists",
  "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

## Ad Management

### Create Ad

Creates a new ad for a performer.

**Endpoint:** `POST /ads`

**Request Body:**
```json
{
  "adName": "Summer Promotion",
  "performerEmail": "contact@example.com",
  "adDetails": {
    "videoUrl": "https://example.com/videos/ad1.mp4",
    "targetUrl": "https://example.com/promo",
    "budget": "medium",
    "skipTime": 5.0,
    "exitTime": 15.0
  }
}
```

**Response (201 Created):**
```json
{
  "message": "Ad created successfully",
  "adId": "d290f1ee-6c54-4b01-90e6-d701748f0851"
}
```

### Get All Ads

Retrieves all ads in the system.

**Endpoint:** `GET /ads`

**Response (200 OK):**
```json
[
  {
    "_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
    "name": "Summer Promotion",
    "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "performerName": "Company Name",
    "adDetails": {
      "videoUrl": "https://example.com/videos/ad1.mp4",
      "targetUrl": "https://example.com/promo",
      "budget": "medium",
      "skipTime": 5.0,
      "exitTime": 15.0
    },
    "createdAt": "2025-04-15T10:30:00.000Z",
    "updatedAt": "2025-04-15T10:30:00.000Z"
  },
  // More ads...
]
```

### Get Ad by ID

Retrieves a specific ad by its ID.

**Endpoint:** `GET /ads/{ad_id}`

**Response (200 OK):**
```json
{
  "_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "name": "Summer Promotion",
  "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "performerName": "Company Name",
  "adDetails": {
    "videoUrl": "https://example.com/videos/ad1.mp4",
    "targetUrl": "https://example.com/promo",
    "budget": "medium",
    "skipTime": 5.0,
    "exitTime": 15.0
  },
  "createdAt": "2025-04-15T10:30:00.000Z",
  "updatedAt": "2025-04-15T10:30:00.000Z"
}
```

### Update Ad

Updates an existing ad by its ID.

**Endpoint:** `PUT /ads/{ad_id}`

**Request Body:**
```json
{
  "name": "Updated Promotion",
  "adDetails": {
    "skipTime": 3.0
  }
}
```

**Response (200 OK):**
```json
{
  "message": "Ad updated"
}
```

### Delete Ad

Deletes an ad by its ID.

**Endpoint:** `DELETE /ads/{ad_id}`

**Response (200 OK):**
```json
{
  "message": "Ad deleted"
}
```

## Ad Serving

### Get Random Ad

Returns a random ad for displaying in an app.

**Endpoint:** `GET /ads/random?packageName={packageName}`

**Response (200 OK):**
```json
{
  "_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "name": "Summer Promotion",
  "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "performerName": "Company Name",
  "adDetails": {
    "videoUrl": "https://example.com/videos/ad1.mp4",
    "targetUrl": "https://example.com/promo",
    "budget": "medium",
    "skipTime": 5.0,
    "exitTime": 15.0
  }
}
```

## Event Tracking

### Send Ad Event

Logs an ad event (view, click, skip, exit).

**Endpoint:** `POST /ad_event`

**Request Body:**
```json
{
  "adId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "timestamp": "2025-04-15T14:30:45.123Z",
  "eventDetails": {
    "packageName": "com.example.app",
    "eventType": "view",
    "watchDuration": 25.5
  }
}
```

**Response (201 Created):**
```json
{
  "message": "Event logged"
}
```

## Analytics

### Get Ad Statistics

Retrieves statistics for a specific ad.

**Endpoint:** `GET /ads/{ad_id}/stats?from=2025-04-01&to=2025-04-30`

**Response (200 OK):**
```json
{
  "adId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
  "dateRange": {
    "from": "2025-04-01",
    "to": "2025-04-30"
  },
  "adStats": {
    "views": 1250,
    "clicks": 87,
    "skips": 320,
    "avgWatchDuration": 18.75,
    "clickThroughRate": 6.96,
    "conversionRate": 3.48
  }
}
```

### Get Performer Statistics

Retrieves statistics for all ads of a performer.

**Endpoint:** `GET /performers/{performer_id}/stats`

**Response (200 OK):**
```json
{
  "performerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "adsStats": [
    {
      "adId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
      "views": 1250,
      "clicks": 87,
      "skips": 320,
      "exits": 843,
      "avgWatchDuration": 18.75,
      "clickThroughRate": 6.96
    },
    // More ads...
  ]
}
```

## Error Handling

All API endpoints return appropriate HTTP status codes:

- **200 OK**: Request succeeded
- **201 Created**: Resource created successfully
- **204 No Content**: No ads available
- **400 Bad Request**: Invalid request parameters
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

Error responses include a JSON object with an error message:

```json
{
  "error": "Invalid email format"
}
```

## Swagger Documentation

For interactive API documentation, visit the Swagger UI at:

```
https://ad-server-kappa.vercel.app/apidocs/
```

or locally at:

```
http://localhost:1993/apidocs/
```
```
