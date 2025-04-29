```markdown
# Getting Started with AdSDK for Android

This guide will help you integrate the AdSDK library into your Android application.

## Installation

### Add the JitPack Repository

Add the JitPack repository to your project-level `build.gradle` or `settings.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Add the Dependency

Add the AdSDK dependency to your app-level `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.NimiB2:Android-SDK-Ads:1.0.0'
}
```

## Basic Integration

### 1. Initialize the SDK

In your Application class or main Activity's `onCreate` method:

```java
AdSdk.init(context, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        // Ad is ready to be shown
        // Enable your "Watch Ad" button
    }

    @Override
    public void onAdFinished() {
        // User has completed watching the ad
        // Grant rewards here (coins, lives, etc.)
    }

    @Override
    public void onNoAvailable(Ad ad) {
        // No ads are currently available
        // Disable your "Watch Ad" button
    }

    @Override
    public void onError(String message) {
        // An error occurred while loading the ad
        Log.e("AdSDK", "Error: " + message);
    }
});
```

### 2. Show an Ad

When the user clicks your "Watch Ad" button:

```java
if (AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
}
```

### 3. Reward the User

In your `onAdFinished()` callback, grant rewards to the user:

```java
@Override
public void onAdFinished() {
    // Example: Give user 100 coins
    userCoins += 100;
    updateCoinsDisplay();
    
    // Show a message
    Toast.makeText(this, "+100 coins for watching the ad!", Toast.LENGTH_SHORT).show();
}
```

## Next Steps

- See the [Integration Examples](examples.md) for more implementation details
- Learn about [API Reference](api-reference.md) for advanced usage
- Set up the [Ad Server](../api-server/setup.md) for backend support

## Common Issues

- **No ads available**: Make sure the Ad Server is properly configured and running
- **Network errors**: Check internet connectivity and server status
- **Black screen**: Verify that video URLs in the ad server are valid and accessible
```
