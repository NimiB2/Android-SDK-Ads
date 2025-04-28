# AdSDK - Android Video Ad Library

## 🖼️ Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4532ea8f-8649-4407-9acf-2eff2a21c572" width="180"/><br/>
      <sub><b>Ad&nbsp;Integration&nbsp;Flow</b></sub>
    </td>
    <td width="25"></td>  <!-- spacer -->
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4f8bf291-5716-49d8-92ef-102c9d977545" width="180"/><br/>
      <sub><b>SDK&nbsp;User&nbsp;Interface</b></sub>
    </td>
    <td width="25"></td>  <!-- spacer -->
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0cceb809-3828-4f93-8983-4f6e6eebfff9" width="180"/><br/>
      <sub><b>End-Card&nbsp;Screen</b></sub>
    </td>
  </tr>
</table>

## Overview
AdSDK is a powerful Android library designed to seamlessly integrate video advertisements into mobile applications with minimal development overhead.

## Key Features
- 🎥 Smooth Video Ad Playback
- 🔄 Intelligent Ad Preloading
- 📊 Comprehensive Event Tracking
- 🏆 Flexible Reward System Integration
- 🌐 Easy Backend Communication

## Quick Integration

### 1. Dependency Installation
Add to your app's `build.gradle`:
```groovy
dependencies {
    implementation 'dev.nimrod:adsdk-lib:1.0.0'
}
```

### 2. SDK Initialization
In your main Activity or Application class:
```java
public class YourMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize AdSDK with callback
        AdSdk.init(this, new AdCallback() {
            @Override
            public void onAdAvailable(Ad ad) {
                // Ad is ready to display
                // Enable ad-related UI elements
            }

            @Override
            public void onAdFinished() {
                // Ad completed
                // Give user reward or continue game flow
                giveUserReward();
            }

            @Override
            public void onNoAvailable(Ad ad) {
                // No ads currently available
                // Disable ad-related UI or show alternative
            }

            @Override
            public void onError(String message) {
                // Handle initialization errors
                Log.e("AdSDK", "Initialization error: " + message);
            }
        });
    }
}
```

### 3. Showing an Ad
```java
// Check if ad is ready
if (AdSdk.isAdReady()) {
    // Show ad in current activity
    AdSdk.showAd(this);
} else {
    // Optionally, trigger manual ad load
    Toast.makeText(this, "Ad loading...", Toast.LENGTH_SHORT).show();
}
```

### 4. Reward Handling
```java
private void giveUserReward() {
    // Example reward mechanism
    gameCoins += 100;
    updateUserInterface();
    
    // Log or track reward
    Log.d("Reward", "User earned 100 coins from ad");
}
```

## Advanced Usage

### Checking Ad Status
```java
// Check if an ad is currently available
boolean adReady = AdSdk.isAdReady();

// Get current ad details
Ad currentAd = AdSdk.getCurrentAd();
```

## Requirements
- Minimum Android 5.0 (API 21)
- Internet Permission
- JSON parsing support

## Documentation
📄 Full Documentation: [Link to Detailed Documentation]

## License

### AdSDK Library License
Copyright (c) 2024 Nimrod Bar

Permission is hereby granted to any person obtaining a copy of this software and associated documentation files (the "Software"), to use, copy, modify, merge, publish, distribute, and/or sublicense the Software, subject to the following conditions:

1. **Personal and Commercial Use**: The Software may be used for personal, academic, and commercial purposes.

2. **Attribution**: You must retain the original copyright notice in all copies or substantial portions of the Software.

3. **Restrictions**:
   - You may NOT create a competing advertising SDK or ad-serving platform without prior written permission.
   - You may NOT remove or alter the original copyright and license notices.

4. **Derivative Works**: Modifications are allowed, but you must clearly indicate changes made to the original Software.

5. **No Warranty**: The Software is provided "AS IS", without warranty of any kind, express or implied.

6. **Liability**: The authors are not liable for any damages arising from the use of this Software.

By using this Software, you agree to these terms.
