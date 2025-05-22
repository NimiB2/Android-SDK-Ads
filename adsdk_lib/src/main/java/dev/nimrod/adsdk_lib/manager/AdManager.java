package dev.nimrod.adsdk_lib.manager;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.controller.AdController;
import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.ui.AdPlayerActivity;
import dev.nimrod.adsdk_lib.util.EventEnum;

/**
 * Central manager for ad lifecycle, preloading, and watch time tracking.
 * Coordinates between ad loading, display, and event reporting.
 */
public class AdManager {
    private static final String TAG = "AdManager";
    private static AdManager instance;
    private String packageName;
    private Ad currentAd;
    private AdController adController;
    private AdPreloadManager preloadManager;
    private AdCallback userCallback;

    private long lastWatchTimeStart;
    private float totalWatchDuration;
    private boolean isWatchTimeTrackingActive = false;

    /**
     * Central manager for ad lifecycle, preloading, and watch time tracking.
     * Coordinates between ad loading, display, and event reporting.
     */
    private AdManager() {
        adController = AdController.getInstance();
        preloadManager = AdPreloadManager.getInstance();
    }

    public static synchronized AdManager getInstance() {
        if (instance == null) {
            instance = new AdManager();
        }
        return instance;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        // Initialize preload manager when package name is set
        preloadManager.initialize(packageName);
    }

    public String getPackageName() {
        return packageName;
    }

    /**
     * Initializes ad loading, preferring preloaded ads when available.
     *
     * @param context  The application context
     * @param callback Callback to handle ad loading results
     */
    public void initAd(Context context, AdCallback callback) {
        Log.d(TAG, "initAd called");
        this.userCallback = callback;
        preloadManager.setNotificationCallback(callback);

        // Check if a preloaded ad is available
        if (preloadManager.hasPreloadedAd()) {
            Log.d(TAG, "Using preloaded ad");
            currentAd = preloadManager.getPreloadedAd();
            if (callback != null) {
                callback.onAdAvailable(currentAd);
            }
            return;
        }

        // No preloaded ad available, load one directly
        Log.d(TAG, "No preloaded ad available, loading directly");
        adController.initRandomAd(packageName, new AdCallback() {
            @Override
            public void onAdAvailable(Ad ad) {
                Log.d(TAG, "Ad loaded directly: " + ad.getId());
                currentAd = ad;
                if (callback != null) {
                    callback.onAdAvailable(ad);
                }
            }

            @Override
            public void onAdExited() {
                if (callback != null) {
                    callback.onAdExited();
                }
            }

            @Override
            public void onAdFinished() {
                if (callback != null) {
                    callback.onAdFinished();
                }
            }

            @Override
            public void onAdSkipped() {
                if (callback != null) {
                    callback.onAdSkipped();
                }
            }

            @Override
            public void onNoAvailable(Ad ad) {
                Log.d(TAG, "No ad available");
                if (callback != null) {
                    callback.onNoAvailable(ad);
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error loading ad: " + message);
                if (callback != null) {
                    callback.onError(message);
                }
            }
        });
    }

    public Ad getCurrentAd() {
        return currentAd;
    }

    public void notifyAdSkipped() {
        if (userCallback != null) {
            userCallback.onAdSkipped();
        }
        currentAd = null;
    }

    public void notifyAdExited() {
        if (userCallback != null) {
            userCallback.onAdExited();
        }
        currentAd = null;
    }

    /**
     * Checks for available ads and initiates display if possible.
     * Uses preloaded ads when current ad is not available.
     *
     * @param activity The activity to launch the ad player from
     */
    public void checkAdDisplay(Activity activity) {
        if (currentAd != null) {
            startAdDisplay(activity);
        } else if (preloadManager.hasPreloadedAd()) {
            currentAd = preloadManager.getPreloadedAd();
            Log.d(TAG, "Using preloaded ad for display: " + (currentAd != null ? currentAd.getId() : "none"));
            startAdDisplay(activity);
            preloadManager.preloadNextAd();

        } else {
            Log.d(TAG, "No ad available, triggering preload");
            preloadManager.preloadNextAd();
        }
    }

    private void startAdDisplay(Activity activity) {
        Log.d(TAG, "Displaying ad: " + currentAd.getId());

        // Reset watch time tracking
        totalWatchDuration = 0;
        lastWatchTimeStart = 0;

        // Launch the ad player activity
        AdPlayerActivity.start(activity, this);
    }

    public void startWatchTimeTracking() {
        lastWatchTimeStart = SystemClock.elapsedRealtime();
        isWatchTimeTrackingActive = true;
    }

    public void pauseWatchTimeTracking() {
        if (isWatchTimeTrackingActive && lastWatchTimeStart > 0) {
            long currentTime = SystemClock.elapsedRealtime();
            totalWatchDuration += (currentTime - lastWatchTimeStart) / 1000f;
            lastWatchTimeStart = 0;
            isWatchTimeTrackingActive = false;
        }
    }

    public void resumeWatchTimeTracking() {
        if (!isWatchTimeTrackingActive) {
            lastWatchTimeStart = SystemClock.elapsedRealtime();
            isWatchTimeTrackingActive = true;
        }
    }

    /**
     * Calculates total watch duration including any currently active tracking session.
     *
     * @return Total watch duration in seconds
     */
    public float getWatchDuration() {
        if (isWatchTimeTrackingActive && lastWatchTimeStart > 0) {
            long currentTime = SystemClock.elapsedRealtime();
            return totalWatchDuration + ((currentTime - lastWatchTimeStart) / 1000f);
        }
        return totalWatchDuration;
    }

    /**
     * Creates and sends an ad interaction event to the server.
     *
     * @param eventType The type of interaction (view, click, skip, exit)
     */
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

    public void notifyAdFinished() {
        if (userCallback != null) {
            userCallback.onAdFinished();
        }
        currentAd = null;
    }
}