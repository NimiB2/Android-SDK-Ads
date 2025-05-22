---
layout: default
title: Integration Guide
nav_order: 3
---
# AdSDK Integration Guide

This guide explains how to integrate the AdSDK into your Android application.

## Prerequisites

- Android Studio
- Minimum SDK version: 26 (Android 8.0)
- Compile SDK version: 35
- Java 11 or higher

## Installation

Add the AdSDK to your project by including the library dependency in your app's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.NimiB2:video-ad-sdk-android:1.0.11")
}
```

Make sure you have the JitPack repository added in your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url = uri("https://jitpack.io") }
    }
}
```

## Basic Integration

### 1. Initialize the SDK

Initialize the SDK in your main activity or application class:

```java
AdSdk.init(this, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        // Ad is ready to be shown
        Log.d(TAG, "Ad is available: " + ad.getId());
        // Enable your ad button here
    }

    @Override
    public void onAdFinished() {
        // User finished watching the ad
        Log.d(TAG, "Ad playback finished");
        // Give reward to the user
    }

    @Override
    public void onAdSkipped() {
        Log.d(TAG, "Ad skipped");
        Toast.makeText(this, "Ad skipped - no reward", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onAdExited() {
        Log.d(TAG, "Ad exited");
        Toast.makeText(this, "Ad exited early", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable(Ad ad) {
        // No ads available
        Log.d(TAG, "No ads available");
        // Update UI to reflect no ads
    }

    @Override
    public void onError(String message) {
        // Error loading ad
        Log.e(TAG, "Error: " + message);
        // Handle error, perhaps retry
    }
});
```

### 2. Check Ad Availability

Before showing an ad, check if one is available:

```java
if (AdSdk.isAdReady()) {
    // Show ad button or proceed to show ad
}
```

### 3. Show an Ad

When you want to display an ad (e.g., when user clicks a button):

```java
AdSdk.showAd(activity);
```

### 4. Handle Rewards

In your `AdCallback` implementation, provide rewards when users finish watching ads:

```java
@Override
public void onAdFinished() {
    // Give user reward (coins, gems, lives, etc.)
    coinsCount += 100;
    updateStats();
    Toast.makeText(this, "+100 coins for watching ad!", Toast.LENGTH_SHORT).show();
    
    // Prepare for next ad if needed
    loadAd();
}
```

## Complete Example

Here's how integration looks in a complete activity:

```java
public class MainActivity extends AppCompatActivity implements AdCallback {
    private static final String TAG = "MainActivity";
    
    // UI elements
    private MaterialButton showAdButton;
    private TextView coinsDisplay;
    
    // Game state
    private int coinsCount = 750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Find views
        showAdButton = findViewById(R.id.show_ad_button);
        coinsDisplay = findViewById(R.id.coins_display);
        
        // Initialize AdSdk
        AdSdk.init(this, this);
        
        // Setup button
        showAdButton.setOnClickListener(view -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
            } else {
                Toast.makeText(this, "Loading ad...", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Update UI
        updateStats();
    }
    
    private void updateStats() {
        coinsDisplay.setText(String.valueOf(coinsCount));
    }
    
    @Override
    public void onAdAvailable(Ad ad) {
        Log.d(TAG, "Ad is available: " + ad.getId());
        showAdButton.setEnabled(true);
        showAdButton.setText("WATCH AD FOR 100 COINS");
    }

    @Override
    public void onAdFinished() {
        Log.d(TAG, "Ad playback finished");
        // Give reward
        coinsCount += 100;
        updateStats();
        Toast.makeText(this, "+100 coins for watching ad!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable(Ad ad) {
        Log.d(TAG, "No ads available");
        showAdButton.setEnabled(true);
        showAdButton.setText("NO ADS AVAILABLE");
        Toast.makeText(this, "No ads available right now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        showAdButton.setEnabled(true);
        showAdButton.setText("RETRY AD");
        Toast.makeText(this, "Error loading ad: " + message, Toast.LENGTH_SHORT).show();
    }
}
```

## Internet Permissions

Ensure your app has internet permissions in the `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Advanced Topics

See the [Callbacks](callbacks.md) documentation for detailed information on working with the AdCallback interface and the [Ad Lifecycle](ad-lifecycle.md) guide for understanding how ads are managed by the SDK.
