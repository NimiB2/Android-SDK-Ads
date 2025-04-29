```markdown
# AdSDK API Reference

This document provides detailed information about the classes, methods, and interfaces available in the AdSDK library.

## Core Classes

### AdSdk

The main entry point for the AdSDK functionality.

#### Methods

| Method | Description |
| ------ | ----------- |
| `init(Context context, AdCallback callback)` | Initializes the SDK with the application context and callback interface. |
| `showAd(Activity activity)` | Displays an ad if one is available. |
| `isAdReady()` | Returns `true` if an ad is ready to be displayed. |
| `getCurrentAd()` | Returns the currently loaded ad object. |

#### Example

```java
// Initialize the SDK
AdSdk.init(context, callback);

// Check if an ad is ready and show it
if (AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
}
```

### Ad

Represents an advertisement with all its properties.

#### Properties

| Property | Type | Description |
| -------- | ---- | ----------- |
| `id` | String | Unique identifier for the ad. |
| `performerName` | String | Name of the advertiser. |
| `adName` | String | Name of the ad campaign. |
| `adDetails` | AdDetails | Contains detailed ad configuration. |

#### Methods

| Method | Return Type | Description |
| ------ | ----------- | ----------- |
| `getId()` | String | Returns the ad ID. |
| `getPerformerName()` | String | Returns the advertiser name. |
| `getAdName()` | String | Returns the ad campaign name. |
| `getTargetUrl()` | String | Returns the URL to open when the ad is clicked. |
| `getVideoUrl()` | String | Returns the URL of the video to play. |
| `getSkipTime()` | long | Returns the time (in seconds) after which the skip button appears. |
| `getExitTime()` | long | Returns the time (in seconds) after which the exit button appears. |

### AdDetails

Contains detailed configuration for an ad.

#### Properties

| Property | Type | Description |
| -------- | ---- | ----------- |
| `videoUrl` | String | URL of the video to play. |
| `targetUrl` | String | URL to open when the ad is clicked. |
| `budget` | String | Budget level of the ad (low, medium, high). |
| `skipTime` | double | Time (in seconds) after which the skip button appears. |
| `exitTime` | double | Time (in seconds) after which the exit button appears. |

## Interfaces

### AdCallback

Interface for handling ad-related events.

#### Methods

| Method | Parameters | Description |
| ------ | ---------- | ----------- |
| `onAdAvailable(Ad ad)` | Ad | Called when an ad is successfully loaded and ready to be displayed. |
| `onAdFinished()` | - | Called when the user has finished watching an ad. This is where you should reward the user. |
| `onNoAvailable(Ad ad)` | Ad | Called when no ads are available. |
| `onError(String message)` | String | Called when an error occurs while loading or displaying an ad. |

#### Example

```java
AdSdk.init(context, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        // Ad ready to display
        // Enable your "Watch Ad" button
    }

    @Override
    public void onAdFinished() {
        // User finished watching ad
        // Reward the user here
    }

    @Override
    public void onNoAvailable(Ad ad) {
        // No ads available
        // Disable your "Watch Ad" button
    }

    @Override
    public void onError(String message) {
        // Error occurred
        Log.e("AdSDK", "Error: " + message);
    }
});
```

## Activities

### AdPlayerActivity

Handles the display of video ads and user interactions.

This activity is started automatically when you call `AdSdk.showAd(activity)`. You don't need to interact with it directly.

#### Features

- Plays the ad video
- Displays skip and exit buttons based on configured times
- Shows an end card after the ad completes
- Tracks user interactions (views, clicks, skips, exits)
- Handles orientation changes and lifecycle events

## Managers

### AdManager

Manages ad loading, display, and event tracking.

This class is used internally by the SDK. You don't need to interact with it directly.

#### Responsibilities

- Loading and caching ads
- Tracking watch time
- Creating and sending events
- Managing callbacks

### AdPreloadManager

Handles background preloading of ads for instant display.

This class is used internally by the SDK. You don't need to interact with it directly.

#### Responsibilities

- Preloading ads in the background
- Caching ads for quick access
- Auto-refreshing the ad cache

## Event Tracking

The SDK automatically tracks the following events:

| Event | Description |
| ----- | ----------- |
| `view` | User watched the ad to completion |
| `click` | User clicked on the ad or end card |
| `skip` | User skipped the ad before completion |
| `exit` | User exited the ad before completion |

These events are sent to the Ad Server for analytics purposes.

## Error Handling

The SDK reports errors through the `onError(String message)` callback method. Common errors include:

- Network connectivity issues
- Invalid ad data
- Video playback failures

## Permissions

The SDK requires the following permissions, which are automatically added to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Advanced Configuration

For advanced configuration, such as changing the API server URL, you'll need to modify the SDK source code. See the [Customization Guide](customization.md) for details.
```
