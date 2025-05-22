package dev.nimrod.adsdk_lib.callback;

import android.app.Activity;
import android.content.Context;

import dev.nimrod.adsdk_lib.AdSdk;
import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.model.Event;

/**
 * Callback interface for handling ad lifecycle events.
 * Implement this interface to receive notifications about ad loading, display, and user interactions.
 */
public interface AdCallback {

    /**
     * Called when an ad has been successfully loaded and is ready to display.
     *
     * @param ad The loaded ad object containing video URL, target URL, and timing information
     */
    void onAdAvailable(Ad ad);

    /**
     * Called when the user has finished watching the ad completely.
     * This is the appropriate time to grant rewards to the user.
     */
    void onAdFinished();

    /**
     * Called when the user skips the ad before completion.
     * Typically no reward should be granted in this case.
     */
    void onAdSkipped();

    /**
     * Called when the user exits the ad using the exit button.
     * Consider granting partial rewards based on watch duration.
     */
    void onAdExited();

    /**
     * Called when no ads are available to display.
     *
     * @param ad May be null or contain partial ad data
     */
    void onNoAvailable(Ad ad);

    /**
     * Called when an error occurs during ad loading or display.
     *
     * @param message Error description for debugging purposes
     */
    void onError(String message);

}
