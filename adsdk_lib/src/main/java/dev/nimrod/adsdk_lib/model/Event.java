package dev.nimrod.adsdk_lib.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Event {
    @SerializedName("adId")
    private String adId;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("eventDetails")
    private EventDetails eventDetails;


    public Event() {
        this.eventDetails = new EventDetails();

        // Initialize with defaults
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.timestamp = isoFormat.format(new Date());
    }

    public String getAdId() {
        return adId;
    }

    public Event setAdId(String adId) {
        this.adId = adId;
        return this;
    }

    public EventDetails getEventDetails() {
        return eventDetails;
    }

    public Event setEventDetails(EventDetails eventDetails) {
        this.eventDetails = eventDetails;
        return this;
    }

    public Event setPackageName(String packageName) {
        this.eventDetails.packageName = packageName;
        return this;
    }

    public Event setEventType(String eventType) {
        this.eventDetails.eventType = eventType;
        return this;
    }

    public Event setWatchDuration(float watchDuration) {
        this.eventDetails.watchDuration = watchDuration;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "adId='" + adId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", eventDetails=" + eventDetails +
                '}';
    }

    public static class EventDetails {
        @SerializedName("packageName")
        private String packageName;

        @SerializedName("eventType")
        private String eventType;

        @SerializedName("watchDuration")
        private float watchDuration;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public float getWatchDuration() {
            return watchDuration;
        }

        public void setWatchDuration(float watchDuration) {
            this.watchDuration = watchDuration;
        }

        @Override
        public String toString() {
            return "EventDetails{" +
                    "packageName='" + packageName + '\'' +
                    ", eventType='" + eventType + '\'' +
                    ", watchDuration=" + watchDuration +
                    '}';
        }
    }
}