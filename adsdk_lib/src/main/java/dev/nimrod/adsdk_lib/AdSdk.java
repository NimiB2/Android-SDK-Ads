package dev.nimrod.adsdk_lib;

import android.app.Activity;
import android.content.Context;

import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.manager.AdManager;
import dev.nimrod.adsdk_lib.manager.AdPreloadManager;
import dev.nimrod.adsdk_lib.model.Ad;

public class AdSdk {

    public static void init(Context context,AdCallback callback) {
        AdManager.getInstance().setPackageName(context.getPackageName());
        AdManager.getInstance().initAd(context, callback);

    }

    public static void showAd(Activity activity) {
        AdManager.getInstance().checkAdDisplay(activity);
    }

    public static boolean isAdReady() {
        return AdManager.getInstance().getCurrentAd() != null ||
                AdPreloadManager.getInstance().hasPreloadedAd();
    }

    public static Ad getCurrentAd() {
        return AdManager.getInstance().getCurrentAd();
    }
}