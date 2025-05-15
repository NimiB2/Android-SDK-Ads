---
layout: default
title: Callbacks
nav_order: 4
---
# AdCallback Interface

The `AdCallback` interface is the primary way your application receives notifications about ad events. This document explains each callback method and how to implement them effectively.

## Interface Definition

The `AdCallback` interface is defined in `dev.nimrod.adsdk_lib.callback.AdCallback`:

```java
public interface AdCallback {
    void onAdAvailable(Ad ad);
    void onAdFinished();
    void onAdSkipped();
    void onAdExited();
    void onNoAvailable(Ad ad);
    void onError(String message);
}
```

## Callback Methods

### onAdAvailable(Ad ad)

This callback is invoked when an ad is successfully loaded and ready to be displayed.

**When it's called:**
- After successful initialization with `AdSdk.init()`
- When a new ad finishes loading in the background

**Parameters:**
- `ad`: The Ad object that's ready to display

**Implementation example:**
```java
@Override
public void onAdAvailable(Ad ad) {
    Log.d(TAG, "Ad is available: " + ad.getId());
    
    // Enable "Watch Ad" button
    main_BTN_showAdButton.setEnabled(true);
    main_BTN_showAdButton.setText("WATCH AD FOR 100 COINS");
    
    // Update your app's state
    isAdReady = true;
}
```

**Best practices:**
- Update your UI to indicate ads are available
- Enable ad-related buttons or features
- Don't attempt to show the ad immediately; wait for user action

### onAdFinished()

This callback is invoked when the user finishes watching an ad completely (watched to the end).

**When it's called:**
- After an ad completes naturally (reaches the end)
- When the user interacts with the end card

**Implementation example:**
```java
@Override
public void onAdFinished() {
    Log.d(TAG, "Ad playback finished");

    // Give full reward when ad finishes
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

**Best practices:**
- Always provide full rewards in this callback
- Preload the next ad to ensure a smooth user experience
- Update your UI to reflect new rewards or state

### onAdSkipped()

This callback is invoked when the user clicks the skip button during ad playback.

**When it's called:**
- User clicks the skip button after it becomes available
- The ad was not watched to completion

**Implementation example:**
```java
@Override
public void onAdSkipped() {
    Log.d(TAG, "Ad was skipped");
    Toast.makeText(this, "Ad skipped - no reward", Toast.LENGTH_SHORT).show();
    
    // No reward given for skip
    isAdReady = false;
    loadAd();
}
```

**Best practices:**
- Don't provide rewards for skipped ads
- Consider implementing a cool-down period before the next ad
- Track skip rates for analytics
- Distinguish between skip and exit events

### onAdExited()

This callback is invoked when the user exits the ad using the exit button.

**When it's called:**
- User clicks the exit button after it becomes available
- The ad was partially watched but not completed
- User exits via back button or other means

**Implementation example:**
```java
@Override
public void onAdExited() {
    Log.d(TAG, "Ad was exited");
    Toast.makeText(this, "Ad exited - partial reward", Toast.LENGTH_SHORT).show();
    
    // Give partial reward for exit if desired
    coinsCount += 50; // Half reward
    updateStats();
    isAdReady = false;
    loadAd();
}
```

**Best practices:**
- Consider partial rewards for exit events
- Track exit rates to optimize ad timing
- Distinguish between exit and skip in your analytics
- Update UI to reflect any partial rewards

### onNoAvailable(Ad ad)

This callback is invoked when no ads are available to display.

**When it's called:**
- When the server returns no ads
- When there's no internet connection or server is unreachable

**Parameters:**
- `ad`: Either null or an incomplete ad object

**Implementation example:**
```java
@Override
public void onNoAvailable(Ad ad) {
    Log.d(TAG, "No ads available");
    
    // Update button state
    main_BTN_showAdButton.setEnabled(true);
    main_BTN_showAdButton.setText("NO ADS AVAILABLE");
    
    // Inform user
    Toast.makeText(this, "No ads available right now", Toast.LENGTH_SHORT).show();
}
```

**Best practices:**
- Update your UI to indicate ads are unavailable
- Provide an alternative path for users to get rewards or continue
- Consider implementing a retry mechanism after some delay

### onError(String message)

This callback is invoked when an error occurs during ad loading or display.

**When it's called:**
- Network errors during ad request
- Invalid server response
- Video playback errors
- Any other exceptional conditions

**Parameters:**
- `message`: Error description

**Implementation example:**
```java
@Override
public void onError(String message) {
    Log.e(TAG, "Error: " + message);
    
    // Update button state
    main_BTN_showAdButton.setEnabled(true);
    main_BTN_showAdButton.setText("RETRY AD");
    
    // Inform user
    Toast.makeText(this, "Error loading ad: " + message, Toast.LENGTH_SHORT).show();
}
```

**Best practices:**
- Log the error for debugging
- Provide user-friendly error messaging
- Enable retry functionality
- Consider fallback options if ads are critical to your app flow

## Implementation Patterns

### Activity Implementation

You can implement the `AdCallback` interface directly in your Activity:

```java
public class MainActivity extends AppCompatActivity implements AdCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize SDK with this Activity as the callback
        AdSdk.init(this, this);
        
        // Rest of your initialization code
    }
    
    // AdCallback implementation
    @Override
    public void onAdAvailable(Ad ad) {
        // Handle ad available
    }
    
    @Override
    public void onAdFinished() {
        // Handle ad finished
    }
    
    @Override
    public void onAdSkipped() {
        // Handle ad skipped
    }
    
    @Override
    public void onAdExited() {
        // Handle ad exited
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        // Handle no ad available
    }
    
    @Override
    public void onError(String message) {
        // Handle error
    }
}
```

### Anonymous Implementation

You can also use anonymous implementation for more flexibility:

```java
AdSdk.init(this, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        // Handle ad available
    }
    
    @Override
    public void onAdFinished() {
        // Handle ad finished
    }
    
    @Override
    public void onAdSkipped() {
        // Handle ad skipped
    }
    
    @Override
    public void onAdExited() {
        // Handle ad exited
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        // Handle no ad available
    }
    
    @Override
    public void onError(String message) {
        // Handle error
    }
});
```

### Singleton Manager Implementation

For complex apps, consider a dedicated manager class:

```java
public class AdManager implements AdCallback {
    private static AdManager instance;
    private Context context;
    private boolean isAdReady = false;
    
    private AdManager(Context context) {
        this.context = context.getApplicationContext();
        AdSdk.init(context, this);
    }
    
    public static synchronized AdManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdManager(context);
        }
        return instance;
    }
    
    public boolean isAdReady() {
        return isAdReady || AdSdk.isAdReady();
    }
    
    public void showAd(Activity activity) {
        if (isAdReady()) {
            AdSdk.showAd(activity);
        }
    }
    
    // AdCallback implementation
    @Override
    public void onAdAvailable(Ad ad) {
        isAdReady = true;
        // Additional handling
    }
    
    @Override
    public void onAdFinished() {
        isAdReady = false;
        // Additional handling - give full reward
    }
    
    @Override
    public void onAdSkipped() {
        isAdReady = false;
        // Additional handling - no reward
    }
    
    @Override
    public void onAdExited() {
        isAdReady = false;
        // Additional handling - partial reward
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        isAdReady = false;
        // Additional handling
    }
    
    @Override
    public void onError(String message) {
        isAdReady = false;
        // Additional handling
    }
}
```

## Thread Safety

Callbacks are delivered on the main thread, so it's safe to update UI directly from these methods. However, if you perform lengthy operations, consider moving them to a background thread to avoid blocking the UI.

## See Also

- [Integration Guide](integration-guide.md) for overall SDK setup
- [Ad Lifecycle](ad-lifecycle.md) for understanding the complete ad flow
