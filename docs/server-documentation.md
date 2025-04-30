---
layout: default
title: Server Documentation
nav_order: 8
has_children: true
permalink: /server/
---
# Server Documentation

This section contains documentation related to the Flask Ad Server, which powers the backend for the AdSDK.

## Server Repository

The complete server code is available in the [Flask Ad Server Repository](https://github.com/NimiB2/Ad-Server).

## Server Overview

The Flask Ad Server is a RESTful backend that powers:
- Campaign management
- Ad delivery
- Event tracking
- Analytics

It exposes endpoints for advertisers to upload creatives and for the SDK to request ads and log events.

## Documentation Sections

- [Server Setup](server-setup.md) - Installation and deployment guide
- [API Reference](api-reference.md) - Complete endpoint documentation
- [Database Schema](database-schema.md) - MongoDB collections and structure
- [Event Tracking](event-tracking.md) - How ad events are processed
- [Analytics](analytics.md) - Statistics and reporting
- [SDK Support](sdk-support.md) - How the server supports the Android SDK
