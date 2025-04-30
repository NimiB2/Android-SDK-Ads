# Backend Connection

This document explains how the AdSDK communicates with the Flask Ad Server backend.

## Communication Overview

The AdSDK uses Retrofit to communicate with the backend API. All network communication is managed by the `AdController` class, which serves as the central client for API interactions.

## API Configuration

The API endpoint is configured in `AdController.java`:

```java
private static final String BASE_URL = "https://ad-server-kappa.vercel.app/";
```

The Retrofit client is initialized with:

```java
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
```

## API Interface

The API endpoints are defined in `AdApiService.java`:

```java
public interface AdApiService {
    @GET("ads/random")
    Call<Ad> loadRandomAd(@Query("packageName") String packageName);

    @POST("ad_event")
    Call<Void> sendAdEvent(@Body Event event);
}
```

## Ad Loading Process

When the SDK initializes or needs to load a new ad, it calls the backend API:

```java
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
```

This method:
1. Constructs a request with the app's package name
2. Sends an asynchronous request to the `/ads/random` endpoint
3. Processes the response in callbacks
4. Notifies the app through the `AdCallback` interface

## Event Tracking

When users interact with ads, the SDK sends event data to the backend:

```java
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
```

This method:
1. Creates an `Event` object with the required details
2. Formats the timestamp in ISO 8601 format
3. Sends an asynchronous POST request to the `/ad_event` endpoint
4. Logs the result but doesn't notify the app (events are "fire and forget")

## Data Models

### Ad Model

The `Ad` class maps to the JSON response from the backend:

```java
public class Ad {
    @SerializedName("_id")
    private String id;
    @SerializedName("performerName")
    private String performerName;
    @SerializedName("name")
    private String adName;
    @SerializedName("adDetails")
    private AdDetails adDetails;
    private String performerEmail;

    // getters, setters, and helper methods...

    public static class AdDetails {
        @SerializedName("videoUrl")
        private String videoUrl;
        @SerializedName("targetUrl")
        private String targetUrl;
        @SerializedName("budget")
        private String budget;
        @SerializedName("skipTime")
        private double skipTime;
        @SerializedName("exitTime")
        private double exitTime;

        // getters and setters...
    }
}
```

### Event Model

The `Event` class maps to the JSON request sent to the backend:

```java
public class Event {
    @SerializedName("adId")
    private String adId;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("eventDetails")
    private EventDetails eventDetails;

    // getters, setters, and helper methods...

    public static class EventDetails {
        @SerializedName("packageName")
        private String packageName;

        @SerializedName("eventType")
        private String eventType;

        @SerializedName("watchDuration")
        private float watchDuration;

        // getters and setters...
    }
}
```

## Error Handling

The SDK implements robust error handling for API interactions:

1. **Network Errors**: Captured in `onFailure()` callbacks and propagated to the app via `AdCallback.onError()`
2. **Server Errors**: HTTP error status codes are logged and treated as "no ad available"
3. **Malformed Responses**: Handled via JSON parsing exceptions
4. **Retry Logic**: The `AdPreloadManager` includes built-in retry for failed requests

Examples of error handling:

```java
// In AdController.java
@Override
public void onFailure(Call<Ad> call, Throwable throwable) {
    Log.e(TAG, "Ad request failed", throwable);
    if (adCallback != null) {
        adCallback.onError(throwable.getMessage());
    } else {
        Log.e(TAG, "adCallback is null in onFailure");
    }
}

// In AdPreloadManager.java
@Override
public void onError(String message) {
    Log.e(TAG, "Error preloading ad: " + message);
    isLoading = false;

    new android.os.Handler().postDelayed(() -> {
        preloadNextAd();
    }, 5000);  // Retry after 5 seconds
}
```

## Configuration

The backend URL is currently hardcoded in `AdController.java`. In a production environment, consider:

1. Making the URL configurable via the SDK initialization
2. Implementing fallback URLs
3. Adding CDN support for video content

## Related Documentation

For more details on the server endpoints and API structure, see the [Flask Ad Server API Reference](https://github.com/YourUsername/ad-server-repo/docs/api-reference.md).
