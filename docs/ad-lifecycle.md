# Ad Lifecycle

This document explains how ads are managed throughout their lifecycle in the AdSDK, from initialization to display and event tracking.

## Lifecycle Overview

The ad lifecycle consists of these main phases:

1. **Initialization** - SDK setup and initial ad loading
2. **Preloading** - Background loading of ads for instant access
3. **Display** - Showing the ad to the user
4. **Tracking** - Recording user interactions with the ad
5. **Completion** - Handling ad completion and rewards

## Detailed Lifecycle

### 1. Initialization

When your app calls `AdSdk.init()`, the following process occurs:

```java
AdSdk.init(context, callback);
```

Internally, this:

1. Sets the package name for tracking via `AdManager.getInstance().setPackageName()`
2. Initializes the `AdPreloadManager` to begin background ad loading
3. Attempts to load an ad either from the preload cache or directly from the server
4. Calls `onAdAvailable()` when an ad is ready or appropriate error callbacks

```java
// AdSdk.java
public static void init(Context context, AdCallback callback) {
    AdManager.getInstance().setPackageName(context.getPackageName());
    AdManager.getInstance().initAd(context, callback);
}
```

### 2. Preloading

The `AdPreloadManager` handles loading ads in the background to ensure they're ready when needed:

```java
// AdManager.java - initAd() method checks preload first
if (preloadManager.hasPreloadedAd()) {
    Log.d(TAG, "Using preloaded ad");
    currentAd = preloadManager.getPreloadedAd();
    if (callback != null) {
        callback.onAdAvailable(currentAd);
    }
    return;
}
```

The preload manager:
- Maintains a singleton instance
- Loads ads in the background without blocking the UI
- Provides ads immediately when requested
- Automatically loads the next ad after one is consumed

### 3. Display

When your app calls `AdSdk.showAd()`, the ad display process begins:

```java
AdSdk.showAd(activity);
```

This triggers:

1. `AdManager.checkAdDisplay()` to verify an ad is available
2. Creation of an intent to launch `AdPlayerActivity`
3. Video loading and preparation 
4. Display of the ad in a specialized full-screen activity

```java
// AdSdk.java
public static void showAd(Activity activity) {
    AdManager.getInstance().checkAdDisplay(activity);
}

// AdManager.java
public void checkAdDisplay(Activity activity) {
    if (currentAd != null) {
        startAdDisplay(activity);
    } else if(preloadManager.hasPreloadedAd()){
        currentAd = preloadManager.getPreloadedAd();
        startAdDisplay(activity);
        preloadManager.preloadNextAd();
    } else {
        Log.e(TAG, "Cannot display ad: no ad loaded");
    }
}
```

The `AdPlayerActivity` handles:
- Video playback
- Skip and exit button timing based on ad configuration
- End card display after video completion
- Click handling to open the advertiser's target URL

### 4. Tracking

Throughout the ad display process, events are tracked using the `EventEnum` types:

- `VIEW` - User watched the ad to completion
- `CLICK` - User clicked to visit the advertiser's URL
- `SKIP` - User skipped the ad before completion
- `EXIT` - User exited the ad

These events are sent to the ad server:

```java
// AdManager.java
public void createEvent(EventEnum eventType) {
    if (currentAd == null || currentAd.getId() == null) {
        Log.e(TAG, "Cannot create event: no current ad or invalid ad ID");
        return;
    }

    float duration = getWatchDuration();
    adController.sendAdEvent(
            currentAd.getId(),
            packageName,
            eventType.getValue(),
            duration
    );
}
```

Watch time is tracked precisely:
- Starts when video begins playing
- Pauses when activity is paused
- Resumes when activity is resumed
- Captures total duration in seconds

### 5. Completion

When an ad completes (through any end state), the SDK:

1. Sends the appropriate event to the server
2. Calls `onAdFinished()` to notify your app
3. Releases resources

```java
// AdManager.java
public void notifyAdFinished() {
    if (userCallback != null) {
        userCallback.onAdFinished();
    }
    currentAd = null;
}
```

This is when your app should provide rewards to the user, as demonstrated in the example app:

```java
// MainActivity.java
@Override
public void onAdFinished() {
    Log.d(TAG, "Ad playback finished");
    // Give reward when ad finishes
    giveReward();
    // Preload next ad
    isAdReady = false;
    loadAd();
}

private void giveReward() {
    coinsCount += 100;
    updateStats();
    Toast.makeText(this, "+100 coins for watching ad!", Toast.LENGTH_SHORT).show();
}
```

## Class Relationships

The key components in the ad lifecycle and their relationships:

- **AdSdk** - Public interface for developers
- **AdManager** - Manages the ad state and lifecycle
- **AdPreloadManager** - Handles background loading of ads
- **AdController** - Communicates with the ad server API
- **AdPlayerActivity** - Displays ads to users
- **Ad** - Data model representing an advertisement
- **Event** - Data model for tracking ad interactions

## Architecture Diagram

```
┌─────────────┐          ┌─────────────┐          ┌─────────────┐
│    AdSdk    │          │  AdManager  │          │AdPreloadMgr │
│  (Public)   │◄─────────┤ (Singleton) ├─────────►│ (Singleton) │
└──────┬──────┘          └──────┬──────┘          └──────┬──────┘
       │                        │                        │
       │                        │                        │
       │                  ┌─────▼─────┐                  │
       │                  │   Event   │                  │
       │                  │ Tracking  │                  │
       │                  └─────┬─────┘                  │
       │                        │                        │
┌──────▼──────┐          ┌──────▼──────┐          ┌──────▼──────┐
│AdPlayerActiv│          │ AdController │          │    API      │
│(UI Display) │◄─────────┤ (API Client) ├─────────►│Communication│
└─────────────┘          └─────────────┘          └─────────────┘
```

## State Transitions

The ad lifecycle involves these primary state transitions:

1. **Not Loaded → Loading**: When SDK is initialized or a new ad is requested
2. **Loading → Available**: When an ad is successfully loaded from the server
3. **Available → Displaying**: When `showAd()` is called
4. **Displaying → Complete**: When the ad finishes (view, click, skip, or exit)
5. **Complete → Not Loaded**: After completion, the cycle begins again

In parallel, the preload manager maintains a separate loading cycle to ensure ads are ready when needed.

## Best Practices

- Initialize the SDK early in your app lifecycle (like in the main activity's `onCreate()`)
- Check ad availability with `AdSdk.isAdReady()` before showing UI elements related to ads
- Always provide rewards in the `onAdFinished()` callback, not earlier
- Handle errors gracefully in the `onError()` callback
- Consider implementing retry logic for failed ad loads
