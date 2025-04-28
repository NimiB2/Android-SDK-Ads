package dev.nimrod.adsdk_lib.callback;

import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.model.Event;

public interface AdCallback {
    void onAdAvailable(Ad ad);
    void onAdFinished();
    void onNoAvailable(Ad ad);
    void onError(String message);

}
