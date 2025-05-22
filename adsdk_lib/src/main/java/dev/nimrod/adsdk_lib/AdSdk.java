package dev.nimrod.adsdk_lib;

import android.app.Activity;
import android.content.Context;

import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.manager.AdManager;
import dev.nimrod.adsdk_lib.manager.AdPreloadManager;
import dev.nimrod.adsdk_lib.model.Ad;

/**
 * Main entry point for the AdSDK library.
 * Provides static methods for initializing the SDK, checking ad availability, and displaying ads.
 */
public class AdSdk {

    /**
     * Initializes the AdSDK with the provided context and callback.
     * This method should be called once during application startup.
     *
     * @param context  The application or activity context
     * @param callback The callback interface to handle ad events
     */
    public static void init(Context context, AdCallback callback) {
        AdManager.getInstance().setPackageName(context.getPackageName());
        AdManager.getInstance().initAd(context, callback);

    }

    /**
     * Displays an ad if one is available.
     * Check {@link #isAdReady()} before calling this method.
     *
     * @param activity The activity to launch the ad player from
     */
    public static void showAd(Activity activity) {
        AdManager.getInstance().checkAdDisplay(activity);
    }

    /**
     * Checks if an ad is ready to be displayed.
     * Returns true if either a current ad or preloaded ad is available.
     *
     * @return true if an ad is ready, false otherwise
     */
    public static boolean isAdReady() {
        return AdManager.getInstance().getCurrentAd() != null ||
                AdPreloadManager.getInstance().hasPreloadedAd();
    }

    /**
     * Gets the currently loaded ad.
     *
     * @return The current ad object, or null if none is loaded
     */
    public static Ad getCurrentAd() {
        return AdManager.getInstance().getCurrentAd();
    }
}