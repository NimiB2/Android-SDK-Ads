```markdown
# AdSDK Customization Guide

This guide explains how to customize the AdSDK library for your specific needs. While the default configuration works for most use cases, you may want to modify certain aspects of the SDK's behavior or appearance.

## Changing the API Server URL

By default, the SDK connects to the production API server at `https://ad-server-kappa.vercel.app/`. If you're running your own server instance, you'll need to modify this.

1. Open `AdController.java` in the `dev.nimrod.adsdk_lib.controller` package
2. Locate the `BASE_URL` constant:

```java
private static final String BASE_URL = "https://ad-server-kappa.vercel.app/";
```

3. Change it to your server URL:

```java
private static final String BASE_URL = "https://your-server-domain.com/";
```

4. Rebuild the SDK

## Customizing the Ad Player UI

### Modifying the Ad Player Layout

The ad player UI is defined in `activity_ad_player.xml`. To customize it:

1. Create a copy of this layout file in your app's resources
2. Modify the layout as needed
3. Create a custom `AdPlayerActivity` that uses your layout
4. Update `AdManager` to use your custom activity

### Customizing the End Card

The end card shown after an ad completes is defined in `adsdk_end_card_view.xml`. To customize it:

1. Create your own end card layout in your app
2. Create a custom `AdPlayerActivity` that uses your layout
3. Update the `showEndCard()` method to use your custom end card

### Styling the Buttons

To change the appearance of the skip and exit buttons:

1. Override the button styles in your app's theme
2. Or create a custom `AdPlayerActivity` with different button styles

## Customizing Ad Behavior

### Modifying Skip and Exit Times

Skip and exit times are determined by the ad configuration returned from the server. To override these:

1. In `AdPlayerActivity.java`, find the `setupButtons()` method
2. Modify the skip and exit time calculations:

```java
// Original code
long skipTimeMs = Math.max(1000, (long) (ad.getSkipTime() * 1000));
long exitTimeMs = Math.max(1000, (long) (ad.getExitTime() * 1000));

// Custom code
long skipTimeMs = 5000; // Always show skip button after 5 seconds
long exitTimeMs = 10000; // Always show exit button after 10 seconds
```

### Changing Event Tracking Behavior

To modify how events are tracked:

1. In `AdManager.java`, find the `createEvent()` method
2. Add your custom tracking logic:

```java
public void createEvent(EventEnum eventType) {
    if (currentAd == null || currentAd.getId() == null) {
        Log.e(TAG, "Cannot create event: no current ad or invalid ad ID");
        return;
    }

    float duration = getWatchDuration();
    
    // Add your custom tracking here
    MyAnalytics.trackEvent(eventType.getValue(), duration);
    
    // Original event sending
    adController.sendAdEvent(
            currentAd.getId(),
            packageName,
            eventType.getValue(),
            duration
    );
}
```

## Advanced Customization

### Custom Ad Loading Logic

To implement your own ad loading logic:

1. Create a custom `AdController` class that extends or replaces the default one
2. Override the `initRandomAd()` method to implement your logic
3. Update `AdManager` to use your custom controller

### Custom Reward Logic

To implement custom reward logic:

1. Create a `RewardManager` class in your app
2. When initializing the SDK, pass your reward manager:

```java
// In your app code
RewardManager rewardManager = new RewardManager();

AdSdk.init(context, new AdCallback() {
    @Override
    public void onAdFinished() {
        // Use your custom reward logic
        rewardManager.giveReward("ad_view", 100);
    }
    
    // Other callback methods...
});
```

### Implementing Custom Analytics

To add your own analytics alongside the default event tracking:

1. Create an `AnalyticsManager` class in your app
2. Integrate it with the SDK callbacks:

```java
AnalyticsManager analytics = new AnalyticsManager();

AdSdk.init(context, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) {
        analytics.trackEvent("ad_loaded", ad.getId());
    }

    @Override
    public void onAdFinished() {
        analytics.trackEvent("ad_completed", null);
    }

    // Other callback methods...
});
```

## Creating a Custom Fork

For extensive customization, you may want to fork the AdSDK repository:

1. Fork the repository on GitHub
2. Clone your fork locally
3. Make your modifications
4. Push your changes to your fork
5. Use JitPack to build your custom version:

```gradle
dependencies {
    implementation 'com.github.YourUsername:Android-SDK-Ads:YourBranchOrTag'
}
```

## Best Practices

- **Minimal Changes**: Only modify what you need to
- **Test Thoroughly**: Test all customizations on multiple devices
- **Maintain Compatibility**: Ensure compatibility with the API server
- **Documentation**: Document your changes for future reference
- **Version Control**: Use proper version control for your modifications
- **Consider Upstream**: Consider contributing your improvements back to the main project

## Known Limitations

- Extensive UI customization may break functionality
- Some behaviors are tightly coupled with the server API
- Custom event tracking may duplicate events in analytics

## Need Help?

If you need assistance with customization beyond what's covered here, consider:

1. Opening an issue on the GitHub repository
2. Contacting the project maintainer
3. Hiring a developer familiar with Android SDK development
```
