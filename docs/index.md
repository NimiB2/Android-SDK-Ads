---
layout: default
title: Home
nav_order: 1
---

# AdSDK Documentation

## Overview

AdSDK is an Android library that enables developers to easily integrate video advertisements into their applications. This SDK handles ad loading, display, and event tracking with a simple interface.

---

### Project Architecture

The Ad SDK system consists of several interconnected components that work together to deliver ads to mobile applications:


<div align="center">
 <img src="https://raw.githubusercontent.com/NimiB2/video-ad-sdk-android/main/docs/assets/architecture-diagram.jpg"
       alt="Project Architecture Diagram" width="600"/>
</div>


---

## Documentation Sections

- [Getting Started](getting-started.md) - Quick installation and initialization
- [Integration Guide](integration-guide.md) - Step-by-step integration instructions
- [Callbacks](callbacks.md) - Working with AdCallback events
- [Ad Lifecycle](ad-lifecycle.md) - How ads are loaded, displayed, and tracked
- [Backend Connection](backend-connection.md) - How the SDK communicates with the ad server
- [Troubleshooting](troubleshooting.md) - Common issues and solutions

## Core Components

The AdSDK consists of these primary components:

### AdSdk Class
The main entry point for developers integrating the library. Located in `dev.nimrod.adsdk_lib.AdSdk`, it provides static methods for:
- Initializing the SDK with `init(Context, AdCallback)`
- Displaying ads with `showAd(Activity)`
- Checking if ads are ready with `isAdReady()`
- Getting the current ad with `getCurrentAd()`

### AdCallback Interface
Found in `dev.nimrod.adsdk_lib.callback.AdCallback`, this interface contains callbacks for ad events:
- `onAdAvailable(Ad)` - Called when an ad is available
- `onAdFinished()` - Called when ad playback completes
- `onNoAvailable(Ad)` - Called when no ads are available
- `onError(String)` - Called when an error occurs

### Ad Model
Located in `dev.nimrod.adsdk_lib.model.Ad`, this class represents an advertisement with properties:
- ID
- Performer name
- Ad name
- Video URL
- Target URL
- Skip and exit timing

### AdPlayerActivity
The `dev.nimrod.adsdk_lib.ui.AdPlayerActivity` handles video playback and user interactions.

## Related Documentation

For the server-side components that power this SDK, see the [Flask Ad Server Documentation](https://nimib2.github.io/video-ad-server/).
For the management interface and campaign analytics, see the [Ad Portal Documentation](https://nimib2.github.io/video-ad-portal/).

