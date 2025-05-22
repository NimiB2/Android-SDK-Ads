package dev.nimrod.adsdk_lib.api;

import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.model.Event;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Retrofit service interface for communicating with the ad server API.
 * Defines endpoints for loading ads and sending tracking events.
 */
public interface AdApiService {

    /**
     * Loads a random ad from the server for the specified app package.
     *
     * @param packageName The package name of the requesting application
     * @return A Retrofit Call object containing the Ad response
     */
    @GET("ads/random")
    Call<Ad> loadRandomAd(@Query("packageName") String packageName);

    /**
     * Sends an ad interaction event to the server for analytics tracking.
     *
     * @param event The event data containing interaction details (view, click, skip, exit)
     * @return A Retrofit Call object with void response
     */
    @POST("ad_event")
    Call<Void> sendAdEvent(@Body Event event);
}