---
layout: default
title: Getting Started
nav_order: 2
---
# Getting Started with AdSDK

This guide provides a quick introduction to installing and using the AdSDK in your Android application.

## Installation

### 1. Add JitPack Repository

Make sure you have the JitPack repository added in your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add the Dependency

Add the AdSDK to your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.NimiB2:Android-SDK-Ads:1.0.0")
}
```

### 3. Sync Project

Click "Sync Now" in Android Studio to download the library.

## Basic Usage

### 1. Initialize the SDK

In your main activity or application class, initialize the SDK:

```java
// Initialize AdSdk
AdSdk.init(this, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        Log.d(TAG, "Ad is available: " + ad.getId());
        // Enable your ad button here
    }

    @Override
    public void onAdFinished() {
        Log.d(TAG, "Ad playback finished");
        // Give reward to the user
    }

    @Override
    public void onNoAvailable(Ad ad) {
        Log.d(TAG, "No ads available");
        // Handle no ads case
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        // Handle error case
    }
});
```

### 2. Add Internet Permissions

Ensure your app has internet permissions in the `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. Show an Ad

When you want to display an ad (e.g., when a user clicks a button):

```java
if (AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
} else {
    Toast.makeText(this, "Loading ad...", Toast.LENGTH_SHORT).show();
}
```

### 4. Reward Users

In your `onAdFinished()` callback, provide rewards to users:

```java
@Override
public void onAdFinished() {
    // Give reward
    coinsCount += 100;
    updateStats();
    Toast.makeText(this, "+100 coins for watching ad!", Toast.LENGTH_SHORT).show();
}
```

## Example Implementation

Here's a minimal example of an activity that implements ad functionality:

```java
public class MainActivity extends AppCompatActivity implements AdCallback {
    private static final String TAG = "MainActivity";
    
    private Button watchAdButton;
    private TextView coinsText;
    private int coinsCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Find views
        watchAdButton = findViewById(R.id.watchAdButton);
        coinsText = findViewById(R.id.coinsText);
        
        // Update UI
        updateCoinsDisplay();
        
        // Initialize AdSdk
        AdSdk.init(this, this);
        
        // Set up button
        watchAdButton.setOnClickListener(v -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
            } else {
                Toast.makeText(this, "Ad not ready yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateCoinsDisplay() {
        coinsText.setText("Coins: " + coinsCount);
    }
    
    // AdCallback implementation
    @Override
    public void onAdAvailable(Ad ad) {
        Log.d(TAG, "Ad available: " + ad.getId());
        watchAdButton.setEnabled(true);
        watchAdButton.setText("Watch Ad for Reward");
    }
    
    @Override
    public void onAdFinished() {
        Log.d(TAG, "Ad finished");
        // Give reward
        coinsCount += 100;
        updateCoinsDisplay();
        Toast.makeText(this, "+100 coins!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        Log.d(TAG, "No ads available");
        watchAdButton.setText("No Ads Available");
    }
    
    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        watchAdButton.setText("Retry Ad");
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
```

## Next Steps

For a more detailed explanation of the SDK, check out:

- [Integration Guide](integration-guide.md) - Comprehensive integration instructions
- [Callbacks](callbacks.md) - Detailed information about the AdCallback interface
- [Ad Lifecycle](ad-lifecycle.md) - How ads are loaded, displayed, and tracked

## Troubleshooting

### Common Issues

1. **Ad Not Loading**
   - Check your internet connection
   - Verify your app has internet permissions in the manifest
   - Ensure the ad server is running and accessible

2. **Ad Shows But No Callback**
   - Verify you've implemented all callback methods
   - Check for exceptions in your callback implementation

3. **App Crashes When Showing Ad**
   - Ensure you're calling `showAd()` with a valid, non-null Activity
   - Check that you're calling `isAdReady()` before attempting to show an ad

For more troubleshooting information, see [Troubleshooting](troubleshooting.md).
