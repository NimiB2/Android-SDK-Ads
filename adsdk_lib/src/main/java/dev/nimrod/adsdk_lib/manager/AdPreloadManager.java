package dev.nimrod.adsdk_lib.manager;

import android.util.Log;

import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.controller.AdController;
import dev.nimrod.adsdk_lib.model.Ad;

public class AdPreloadManager {
    private static final String TAG = "AdPreloadManager";
    private static AdPreloadManager instance;
    private final AdController adController;

    private Ad preloadedAd = null;
    private boolean isLoading = false;
    private String packageName;

    private AdPreloadManager() {
        adController = AdController.getInstance();
    }

    public static synchronized AdPreloadManager getInstance() {
        if (instance == null) {
            instance = new AdPreloadManager();
        }
        return instance;
    }


    public void initialize(String packageName) {
        this.packageName = packageName;
        preloadNextAd();
    }

    public boolean hasPreloadedAd() {
        return preloadedAd != null;
    }


    public Ad getPreloadedAd() {
        Ad ad = preloadedAd;
        preloadedAd = null;
        preloadNextAd();
        return ad;
    }

    public void preloadNextAd() {
        // Skip if already loading or package name not set
        if (isLoading || packageName == null) {
            Log.d(TAG, "Skip preloading because already loading or no package name");
            return;
        }

        isLoading = true;
        Log.d(TAG, "Preloading next ad");

        adController.initRandomAd(packageName, new AdCallback() {
            @Override
            public void onAdAvailable(Ad ad) {
                Log.d(TAG, "Ad successfully preloaded: " + ad.getId());
                preloadedAd = ad;
                isLoading = false;
            }

            @Override
            public void onAdFinished() {
                isLoading = false;
            }

            @Override
            public void onAdSkipped() {
                isLoading = false;
            }

            @Override
            public void onAdExited() {
                isLoading = false;
            }

            @Override
            public void onNoAvailable(Ad ad) {
                Log.d(TAG, "No ad available for preloading");
                isLoading = false;

                new android.os.Handler().postDelayed(() -> {
                    preloadNextAd();
                }, 2000);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Error preloading ad: " + message);
                isLoading = false;

                new android.os.Handler().postDelayed(() -> {
                    preloadNextAd();
                }, 5000);
            }
        });
    }
}