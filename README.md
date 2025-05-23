# AdSDK – Android Video Advertisement Library

![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)
![JitPack](https://img.shields.io/badge/JitPack-1.0.11-red.svg)
![API](https://img.shields.io/badge/API-26%2B-blue.svg)
![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Smart%20Rate%20Us%20Dialog-brightgreen.svg)
![Repo Size](https://img.shields.io/badge/repo%20size-2%20MiB-blue.svg)

A professional Android SDK for seamless video advertisement integration with automatic preloading, user interaction tracking, and reward system support.

## 📚 Documentation & Demo

**[📖 Complete Documentation](https://nimib2.github.io/video-ad-sdk-android/)** | **[🎥 Live Demo](https://NimiB2.github.io/video-ad-server/demo.html)**

<a href="https://NimiB2.github.io/video-ad-server/demo.html">
 <img src="https://i.imgur.com/BarqWRo.png" alt="AdSDK Demo" width="200"/>
</a>

---

## ✨ What's New

### Version 1.0.11
- Enhanced ad preloading with background management
- Improved video scaling and full-screen display
- Added comprehensive event tracking (view, click, skip, exit)
- Optimized lifecycle management for pause/resume scenarios
- Built-in reward system integration with callback support
- Professional end-card display with call-to-action buttons

---

## 🚀 Quick Start

### Installation

Add JitPack repository to your `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add dependency to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.github.NimiB2:Android-SDK-Ads:1.0.11")
}
```

### Basic Integration

```java
// 1. Initialize SDK
AdSdk.init(this, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        showAdButton.setEnabled(true);
    }
    
    @Override
    public void onAdFinished() {
        // Reward user for watching complete ad
        giveUserReward(100);
    }
    
    @Override
    public void onError(String message) {
        Log.e(TAG, "Ad error: " + message);
    }
});

// 2. Show ad when ready
if (AdSdk.isAdReady()) {
    AdSdk.showAd(this);
}
```

### Required Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## 📱 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4532ea8f-8649-4407-9acf-2eff2a21c572" width="200"/><br/>
      <sub><b>Integration Flow</b></sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4f8bf291-5716-49d8-92ef-102c9d977545" width="200"/><br/>
      <sub><b>Video Player Interface</b></sub>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0cceb809-3828-4f93-8983-4f6e6eebfff9" width="200"/><br/>
      <sub><b>End Card with CTA</b></sub>
    </td>
  </tr>
</table>

---

## 🔧 Core Features

| Feature | Description |
|---------|-------------|
| **Preloading** | Background ad loading for instant availability |
| **Video Playback** | Full-screen video with automatic scaling |
| **User Controls** | Configurable skip/exit button timing |
| **Event Tracking** | Comprehensive interaction analytics |
| **Reward Integration** | Built-in callback system for user rewards |
| **Lifecycle Management** | Automatic pause/resume handling |

### Technical Specifications

- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **Dependencies**: Retrofit 2.11.0, Gson 2.10.1
- **Architecture**: Singleton pattern with preload management
- **Library Size**: ~200KB

---

## 📋 API Reference

### Main Interface

| Method | Description | Return Type |
|--------|-------------|-------------|
| `AdSdk.init(Context, AdCallback)` | Initialize SDK with callback | `void` |
| `AdSdk.showAd(Activity)` | Display available advertisement | `void` |
| `AdSdk.isAdReady()` | Check if ad is ready for display | `boolean` |
| `AdSdk.getCurrentAd()` | Get current ad object | `Ad` |

### AdCallback Interface

| Callback | When Called | Use Case |
|----------|-------------|----------|
| `onAdAvailable(Ad)` | Ad loaded successfully | Enable ad buttons |
| `onAdFinished()` | User completed viewing | Grant full reward |
| `onAdSkipped()` | User skipped before completion | No reward |
| `onAdExited()` | User used exit button | Optional partial reward |
| `onNoAvailable(Ad)` | No ads available | Update UI state |
| `onError(String)` | Error occurred | Handle failures |

---

## 🏗️ Architecture

### Core Components

```
AdSdk (Main Interface)
├── AdManager (Lifecycle Management)
├── AdPreloadManager (Background Loading)
├── AdController (API Communication)
├── AdPlayerActivity (Video Display)
└── Event System (Analytics Tracking)
```

### Data Flow
1. **Initialization** → SDK requests ad from server
2. **Preloading** → Next ad loads in background
3. **Display** → Full-screen video with user controls
4. **Tracking** → Events sent to analytics backend
5. **Completion** → Reward callbacks triggered

---

## 💡 Implementation Examples

### Complete Activity Example

```java
public class MainActivity extends AppCompatActivity implements AdCallback {
    private MaterialButton adButton;
    private TextView coinsDisplay;
    private int coins = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize SDK
        AdSdk.init(this, this);
        
        // Setup UI
        adButton = findViewById(R.id.watch_ad_button);
        adButton.setOnClickListener(v -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
            }
        });
    }
    
    @Override
    public void onAdFinished() {
        coins += 100;
        coinsDisplay.setText(String.valueOf(coins));
        Toast.makeText(this, "+100 coins!", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onAdAvailable(Ad ad) {
        adButton.setEnabled(true);
        adButton.setText("Watch Ad for 100 Coins");
    }
}
```

---

## 🔗 Integration with Backend

The SDK integrates with the [Flask Ad Server](https://nimib2.github.io/video-ad-server/) for:

- **Ad Delivery**: `/ads/random` endpoint
- **Event Tracking**: `/ad_event` endpoint  
- **Campaign Management**: Via [Ad Portal](https://nimib2.github.io/video-ad-portal/)

---

## 📖 Additional Resources

- **[Integration Guide](https://nimib2.github.io/video-ad-sdk-android/integration-guide.html)** - Detailed setup instructions
- **[Callbacks Documentation](https://nimib2.github.io/video-ad-sdk-android/callbacks.html)** - Event handling guide
- **[Troubleshooting](https://nimib2.github.io/video-ad-sdk-android/troubleshooting.html)** - Common issues and solutions
- **[Ad Lifecycle](https://nimib2.github.io/video-ad-sdk-android/ad-lifecycle.html)** - Understanding ad flow

---

## 🙏 Credits

### Core Technologies
- **[Retrofit](https://square.github.io/retrofit/)** by Square - Network communication
- **[Gson](https://github.com/google/gson)** by Google - JSON serialization
- **[Android Jetpack](https://developer.android.com/jetpack)** by Google - Core Android components

### Development Tools
- **[JitPack](https://jitpack.io/)** - Library distribution platform
- **[Material Design](https://material.io/)** by Google - UI design system

---

## 📄 License

```
MIT License

Copyright (c) 2025 Nimrod Bar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

**This is an educational project created for learning purposes.**
