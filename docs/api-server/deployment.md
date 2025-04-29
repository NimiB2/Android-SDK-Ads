```markdown
# Ad Server Deployment Guide

This guide explains how to deploy the AdSDK Flask API server to various hosting environments.

## Deployment Options

The Ad Server can be deployed in several ways:

1. **Vercel** - Simplest option for serverless deployment
2. **Heroku** - Easy deployment with good scaling options
3. **AWS** - More control and advanced configuration options
4. **Docker** - Containerized deployment for maximum flexibility

## Prerequisites

Before deploying, make sure you have:

- A MongoDB Atlas account (or other MongoDB hosting)
- The complete ad-server codebase
- Environment variables prepared

## Deployment to Vercel (Recommended)

Vercel provides a simple, free tier for hosting Flask applications.

### Step 1: Install Vercel CLI

```bash
npm install -g vercel
```

### Step 2: Prepare the Project

Make sure your project includes a `vercel.json` file:

```json
{
    "version": 2,
    "builds": [
        {
            "src": "app.py",
            "use": "@vercel/python"
        }
    ],
    "routes": [
        {
            "src": "/(.*)",
            "dest": "app.py"
        }
    ]
}
```

### Step 3: Configure Environment Variables

Either:

1. Add them through the Vercel CLI deployment process, or
2. Add them in the Vercel dashboard after deployment

Required environment variables:
- `DB_CONNECTION_STRING`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

### Step 4: Deploy

From your project directory:

```bash
vercel
```

Follow the CLI prompts to complete deployment.

## Deployment to Heroku

### Step 1: Install Heroku CLI

```bash
# On macOS
brew install heroku/brew/heroku

# On Windows
# Download installer from https://devcenter.heroku.com/articles/heroku-cli
```

### Step 2: Prepare the Project

Create a `Procfile` in your project root:

```
web: gunicorn app:app
```

Add `gunicorn` to your `requirements.txt`:

```
flask
flasgger
pymongo
python-dotenv
email-validator
gunicorn
```

### Step 3: Create and Deploy the App

```bash
# Login to Heroku
heroku login

# Create a new app
heroku create ad-server-app

# Set environment variables
heroku config:set DB_CONNECTION_STRING=yourcluster.mongodb.net
heroku config:set DB_NAME=adserver
heroku config:set DB_USERNAME=youruser
heroku config:set DB_PASSWORD=yourpassword

# Deploy the app
git push heroku main
```

### Step 4: Verify Deployment

```bash
heroku open
```

Navigate to `/apidocs/` to see the Swagger UI.

## Deployment with Docker

### Step 1: Create a Dockerfile

Create a `Dockerfile` in your project root:

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

ENV PORT=1993

EXPOSE 1993

CMD ["python", "app.py"]
```

### Step 2: Build and Run the Docker Image

```bash
# Build the image
docker build -t ad-server .

# Run the container
docker run -p 1993:1993 \
  -e DB_CONNECTION_STRING=yourcluster.mongodb.net \
  -e DB_NAME=adserver \
  -e DB_USERNAME=youruser \
  -e DB_PASSWORD=yourpassword \
  ad-server
```

### Step 3: Deploy to a Cloud Provider

After testing locally, you can push your Docker image to a registry and deploy to:

- AWS ECS
- Google Cloud Run
- Azure Container Instances
- DigitalOcean App Platform

## Deployment to AWS

### Option 1: AWS Elastic Beanstalk

1. Install the EB CLI:
   ```bash
   pip install awsebcli
   ```

2. Initialize your EB project:
   ```bash
   eb init
   ```

3. Create an environment:
   ```bash
   eb create ad-server-env
   ```

4. Set environment variables:
   ```bash
   eb setenv DB_CONNECTION_STRING=yourcluster.mongodb.net DB_NAME=adserver DB_USERNAME=youruser DB_PASSWORD=yourpassword
   ```

5. Deploy:
   ```bash
   eb deploy
   ```

### Option 2: AWS Lambda with API Gateway

Use the Zappa framework to deploy your Flask app as a serverless application:

1. Install Zappa:
   ```bash
   pip install zappa
   ```

2. Initialize Zappa:
   ```bash
   zappa init
   ```

3. Deploy:
   ```bash
   zappa deploy dev
   ```

## Continuous Deployment

### GitHub Actions

Create a `.github/workflows/deploy.yml` file:

```yaml
name: Deploy

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v20
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          vercel-args: '--prod'
```

## Post-Deployment Steps

1. **Test the API**:
   - Visit `/apidocs/` to check the Swagger UI
   - Try creating a test performer and ad
   - Test the `/ads/random` endpoint

2. **Update Android SDK**:
   - Update the `BASE_URL` in `AdController.java` to point to your deployed API
   - Re-compile and distribute the SDK

3. **Monitor the logs**:
   - Check deployment platform logs for errors
   - Set up proper logging and monitoring

## Troubleshooting

- **Database Connection Issues**: Verify network access to MongoDB and credentials
- **Server Errors**: Check logs for Python exceptions
- **CORS Issues**: Enable CORS if accessing from web clients
- **Memory/CPU Limits**: Upgrade your hosting plan if hitting resource limits

## Performance Tips

- **MongoDB Indexes**: Ensure indexes are created for frequently queried fields
- **Connection Pooling**: Configure proper connection pooling for MongoDB
- **Caching**: Implement caching for frequently accessed data
- **Rate Limiting**: Consider adding rate limiting for public endpoints
```
