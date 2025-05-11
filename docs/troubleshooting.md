---
layout: default
title: Troubleshooting
nav_order: 7
---
# Troubleshooting

This guide addresses common issues you might encounter when integrating and using the AdSDK, along with their solutions.

## Ad Loading Issues

### Problem: No Ads Available

**Symptoms:**
- `onNoAvailable()` callback is triggered
- Log message: "No ad available, response code: 204"

**Possible Causes:**
1. The ad server doesn't have any ads in the database
2. The server is not accessible
3. The package name isn't recognized

**Solutions:**
1. Check the ad server to ensure ads are available
2. Verify the server URL is correct and accessible
3. Ensure the package name is correctly set during initialization
4. Implement a retry mechanism:

```java
@Override
public void onNoAvailable(Ad ad) {
    Log.d(TAG, "No ads available");
    // Schedule a retry
    new Handler().postDelayed(() -> {
        AdSdk.init(context, this);
    }, 30000); // Retry after 30 seconds
}
```

### Problem: Network Errors

**Symptoms:**
- `onError()` callback is triggered with network error messages
- Log message: "Ad request failed" with connection errors

**Possible Causes:**
1. No internet connection
2. Incorrect server URL
3. Firewall or network restrictions

**Solutions:**
1. Check the device's internet connection
2. Verify the BASE_URL in `AdController.java` is correct
3. Add network state monitoring:

```java
private boolean isNetworkAvailable(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
}
```

## Video Playback Issues

### Problem: Video Fails to Play

**Symptoms:**
- Ad activity shows but video doesn't play
- Error in video player

**Possible Causes:**
1. Invalid video URL
2. Unsupported video format
3. Network issues during playback

**Solutions:**
1. Check the video URL in the ad response
2. Ensure the device supports the video format
3. Implement error handling in the video player:

```java
videoView.setOnErrorListener((mp, what, extra) -> {
    loadingProgressBar.setVisibility(View.GONE);
    Log.e(TAG, "Video playback error: " + what + ", " + extra);
    Toast.makeText(AdPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
    finish();
    return true;
});
```

### Problem: Skip Button Not Appearing

**Symptoms:**
- Skip button remains invisible throughout the ad

**Possible Causes:**
1. `skipTime` value in ad is too large
2. UI visibility issues

**Solutions:**
1. Check the `skipTime` value in the ad response
2. Verify the timing mechanism:

```java
// In AdPlayerActivity.java
long skipTimeMs = Math.max(1000, (long) (ad.getSkipTime() * 1000));
skipButton.setVisibility(View.INVISIBLE);

skipButton.postDelayed(() -> {
    skipButton.setVisibility(View.VISIBLE);
    Log.d(TAG, "Skip button should now be visible");
}, skipTimeMs);
```

## Event Tracking Issues

### Problem: Events Not Being Recorded

**Symptoms:**
- Events are sent but not appearing in server logs
- No analytics data available

**Possible Causes:**
1. Event sending failures
2. Incorrect event format
3. Server issues

**Solutions:**
1. Add additional logging to event sending:

```java
Log.d(TAG, "Sending event: " + new Gson().toJson(event));

call.enqueue(new Callback<Void>() {
    @Override
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            Log.d(TAG, "Event sent successfully: " + eventType);
        } else {
            Log.e(TAG, "Error sending event: " + response.code());
            // Log the error body
            if (response.errorBody() != null) {
                try {
                    Log.e(TAG, "Error body: " + response.errorBody().string());
                } catch (Exception e) {
                    Log.e(TAG, "Error reading error body", e);
                }
            }
        }
    }
});
```

2. Verify event formatting matches server expectations
3. Implement offline event caching and retry:

```java
// Cache events locally when sending fails
private void cacheEventForRetry(Event event) {
    // Store event in SharedPreferences or local database
    // Implement retry mechanism on next app start
}
```

## Integration Issues

### Problem: AdSdk.isAdReady() Always Returns False

**Symptoms:**
- `isAdReady()` always returns false even after initialization
- Ads never load

**Possible Causes:**
1. Initialization failed
2. Callbacks not properly implemented
3. Ad loading errors not being handled

**Solutions:**
1. Ensure proper initialization sequence:

```java
// Check if initialization completed successfully
boolean initialized = false;

@Override
public void onAdAvailable(Ad ad) {
    initialized = true;
    // Rest of implementation
}

// Then check both conditions
if (initialized && AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
}
```

2. Implement proper error handling and logging in all callbacks

### Problem: Crash When Showing Ad

**Symptoms:**
- App crashes when `AdSdk.showAd()` is called
- NullPointerException in AdManager or AdPlayerActivity

**Possible Causes:**
1. Calling `showAd()` before checking `isAdReady()`
2. Activity is finishing or already destroyed
3. Ad object is corrupted or nullified

**Solutions:**
1. Always check readiness first:

```java
if (AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
} else {
    // Wait for ad to load or reload
    AdSdk.init(context, callback);
}
```

2. Check activity state:

```java
if (!activity.isFinishing() && AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
}
```

## Lifecycle Issues

### Problem: Rewards Given Multiple Times

**Symptoms:**
- User receives rewards multiple times for watching one ad
- `onAdFinished()` called more than once

**Possible Causes:**
1. Multiple callback registrations
2. Lifecycle issues with activity recreation

**Solutions:**
1. Use a flag to track reward status:

```java
private boolean rewardGiven = false;

@Override
public void onAdFinished() {
    if (!rewardGiven) {
        // Give reward
        coinsCount += 100;
        updateStats();
        Toast.makeText(this, "+100 coins!", Toast.LENGTH_SHORT).show();
        rewardGiven = true;
    }
}

@Override
protected void onResume() {
    super.onResume();
    // Reset flag when appropriate (e.g., when a new ad is loaded)
    if (AdSdk.isAdReady()) {
        rewardGiven = false;
    }
}
```

### Problem: Memory Leaks

**Symptoms:**
- Memory usage increases over time
- Activity references not being released

**Possible Causes:**
1. Activity references held by static AdCallback
2. VideoView not being properly released

**Solutions:**
1. Use WeakReference for contexts:

```java
private static WeakReference<Context> contextRef;

public static void init(Context context, AdCallback callback) {
    contextRef = new WeakReference<>(context.getApplicationContext());
    // Rest of implementation
}
```

2. Ensure proper cleanup in onDestroy:

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    
    // Clean up video view
    if (videoView != null) {
        videoView.stopPlayback();
        videoView = null;
    }
}
```

## Debugging Tips

### Enable Verbose Logging

Add a debug flag to enable detailed logging:

```java
public static boolean DEBUG = BuildConfig.DEBUG;

private static void log(String message) {
    if (DEBUG) {
        Log.d(TAG, message);
    }
}
```

### Monitor Network Requests

Use a network inspection tool like [Flipper](https://fbflipper.com/) or [Charles Proxy](https://www.charlesproxy.com/) to monitor API requests and responses.

### Check Server Logs

If you have access to the server, check its logs for:
- Received requests
- Error responses
- Event recording issues

### Check AdController State

Monitor the state of the AdController to ensure ads are being properly managed:

```java
public static void debugAdState() {
    Ad currentAd = AdSdk.getCurrentAd();
    Log.d(TAG, "Current Ad: " + (currentAd != null ? currentAd.getId() : "null"));
    Log.d(TAG, "Is Ad Ready: " + AdSdk.isAdReady());
}
```

## Getting Help

If you're still experiencing issues after trying these solutions:

1. Check the [GitHub issues](https://github.com/YourUsername/AdSDK/issues) for similar problems
2. Review the [API Reference](https://github.com/YourUsername/ad-server-repo/docs/api-reference.md) for correct endpoint usage
3. Contact support with:
   - Detailed error logs
   - Steps to reproduce the issue
   - Device information (model, Android version)
   - SDK version
