package dev.nimrod.adsdk_lib.api;

import dev.nimrod.adsdk_lib.model.Ad;
import dev.nimrod.adsdk_lib.model.Event;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdApiService {
    @GET("ads/random")
    Call<Ad> loadRandomAd(@Query("packageName") String packageName);

    @POST("ad_event")
    Call<Void> sendAdEvent(@Body Event event);
}