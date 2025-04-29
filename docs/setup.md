```markdown
# Ad Server Setup Guide

This guide explains how to set up and configure the Flask-based Ad Server that powers the AdSDK backend.

## Prerequisites

- Python 3.9 or higher
- MongoDB (local instance or MongoDB Atlas)
- Basic knowledge of Flask applications

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/YourUsername/ad-server.git
cd ad-server
```

### 2. Create a Virtual Environment

```bash
# On Windows
python -m venv venv
venv\Scripts\activate

# On macOS/Linux
python -m venv venv
source venv/bin/activate
```

### 3. Install Dependencies

```bash
pip install -r requirements.txt
```

### 4. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```
DB_CONNECTION_STRING=yourcluster.mongodb.net
DB_NAME=adserver
DB_USERNAME=youruser
DB_PASSWORD=yourpassword
```

For MongoDB Atlas, your connection string will look like `cluster0.abc123.mongodb.net`.

## Running the Server

Start the Flask application:

```bash
python app.py
```

The server will run on port 1993 by default (http://localhost:1993).

You can access the Swagger API documentation at http://localhost:1993/apidocs/.

## Server Endpoints

### Ad Management

- `POST /performers` - Create a new advertiser
- `POST /ads` - Create a new ad campaign
- `GET /ads` - Get all ads
- `GET /ads/<ad_id>` - Get a specific ad
- `PUT /ads/<ad_id>` - Update an ad
- `DELETE /ads/<ad_id>` - Delete an ad

### Ad Serving

- `GET /ads/random?packageName=com.example.app` - Get a random ad for an app

### Event Tracking

- `POST /ad_event` - Log ad events (view, click, skip, exit)

### Analytics

- `GET /ads/<ad_id>/stats` - Get statistics for a specific ad
- `GET /performers/<performer_id>/stats` - Get statistics for a performer

## Deployment

### Deploying to Vercel

1. Install Vercel CLI:
   ```bash
   npm install -g vercel
   ```

2. Deploy:
   ```bash
   vercel
   ```

3. Follow the prompts and set the environment variables.

## Connecting to the Android SDK

The Android SDK is configured by default to use the production API at:
```
https://ad-server-kappa.vercel.app/
```

If you're running a local server for development, update the `BASE_URL` in the `AdController.java` file:

```java
private static final String BASE_URL = "http://your-local-ip:1993/";
```

## Troubleshooting

- **Database Connection Issues**: Verify MongoDB credentials and network access
- **CORS Errors**: If testing from a web client, you may need to enable CORS
- **Missing Dependencies**: Ensure all packages in requirements.txt are installed
```
