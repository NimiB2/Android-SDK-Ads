package dev.nimrod.adsdk_lib.controller;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import dev.nimrod.adsdk_lib.api.AdApiService;
import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.model.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdController {
    private static final String TAG = "AdController";
    private static final String BASE_URL = "https://ad-server-kappa.vercel.app/";
    private Ad currentAd;

    private static AdController instance;

    private AdController() {
    }

    public static AdController getInstance() {
        if (instance == null) {
            instance = new AdController();
        }
        return instance;
    }

    public Ad getCurrentAd() {
        return this.currentAd;
    }

    private AdApiService getApiService() {
        Log.d(TAG, "Creating API service with base URL: " + BASE_URL);

        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(AdApiService.class);
        } catch (Exception e) {
            Log.e(TAG, "Error creating API service", e);
            throw e;
        }
    }

    public void initRandomAd(String packageName, AdCallback adCallback) {
        Log.d(TAG, "Requesting ad for package: " + packageName);

        try {
            AdApiService apiService = getApiService();
            Call<Ad> call = apiService.loadRandomAd(packageName);

            call.enqueue(new Callback<Ad>() {
                @Override
                public void onResponse(Call<Ad> call, Response<Ad> response) {
                    Log.d(TAG, "Ad response received: " + response.code());

                    if (adCallback == null) {
                        Log.e(TAG, "adCallback is null in onResponse");
                        return;
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        currentAd = response.body();
                        Log.d(TAG, "Ad available: " + currentAd.getId());
                        adCallback.onAdAvailable(currentAd);
                    } else {
                        Log.d(TAG, "No ad available, response code: " + response.code());
                        adCallback.onNoAvailable(null);
                    }
                }

                @Override
                public void onFailure(Call<Ad> call, Throwable throwable) {
                    Log.e(TAG, "Ad request failed", throwable);
                    if (adCallback != null) {
                        adCallback.onError(throwable.getMessage());
                    } else {
                        Log.e(TAG, "adCallback is null in onFailure");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ad request", e);
            if (adCallback != null) {
                adCallback.onError("Error initializing ad request: " + e.getMessage());
            }
        }
    }

    public void sendAdEvent(String adId, String packageName, String eventType, float watchDuration) {
        if (adId == null) {
            Log.e(TAG, "Cannot send event: ad ID is null");
            return;
        }

        try {
            Event event = new Event();
            event.setAdId(adId);
            // Set timestamp for current time in ISO 8601 format
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            event.setTimestamp(isoFormat.format(new Date()));

            Event.EventDetails details = new Event.EventDetails();
            details.setPackageName(packageName);
            details.setEventType(eventType);
            details.setWatchDuration(watchDuration);

            event.setEventDetails(details);

            Log.d(TAG, "Sending event: " + new Gson().toJson(event));

            AdApiService apiService = getApiService();
            Call<Void> call = apiService.sendAdEvent(event);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Event sent successfully: " + eventType);
                    } else {
                        Log.e(TAG, "Error sending event: " + response.code());
                        if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Failure sending event", t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending event", e);
        }
    }
}