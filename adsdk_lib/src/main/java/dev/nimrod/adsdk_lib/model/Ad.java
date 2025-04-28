package dev.nimrod.adsdk_lib.model;

import com.google.gson.annotations.SerializedName;

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

    public Ad() {
        this.adDetails = new Ad.AdDetails();
    }

    public String getId() {
        return id;
    }

    public Ad setId(String id) {
        this.id = id;
        return this;
    }

    public String getPerformerName() {
        return performerName;
    }

    public Ad setPerformerName(String performerName) {
        this.performerName = performerName;
        return this;
    }

    public String getAdName() {
        return adName;
    }

    public Ad setAdName(String adName) {
        this.adName = adName;
        return this;
    }

    public String getPerformerEmail() {
        return performerEmail;
    }

    public Ad setPerformerEmail(String performerEmail) {
        this.performerEmail = performerEmail;
        return this;
    }

    public AdDetails getAdDetails() {
        return adDetails;
    }

    public Ad setAdDetails(AdDetails adDetails) {
        this.adDetails = adDetails;
        return this;
    }

    public String getTargetUrl() {
        return adDetails != null ? adDetails.getTargetUrl() : null;
    }

    public Ad setTargetUrl(String targetUrl) {
        ensureDetails().setTargetUrl(targetUrl);
        return this;
    }

    public long getExitTime() {
        return adDetails != null ? (long) adDetails.getExitTime() : 0;
    }

    public Ad setExitTime(long exitTime) {
        ensureDetails().setExitTime(exitTime);
        return this;
    }

    public long getSkipTime() {
        return adDetails != null ? (long) adDetails.getSkipTime() : 0;
    }

    public Ad setSkipTime(long skipTime) {
        ensureDetails().setSkipTime(skipTime);
        return this;
    }

    public String getVideoUrl() {
        return adDetails != null ? adDetails.getVideoUrl() : null;
    }

    public Ad setVideoUrl(String videoUrl) {
        ensureDetails().setVideoUrl(videoUrl);
        return this;
    }

    private AdDetails ensureDetails() {
        if (adDetails == null) adDetails = new AdDetails();
        return adDetails;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", performerName='" + performerName + '\'' +
                ", adName='" + adName + '\'' +
                ", performerEmail='" + performerEmail + '\'' +
                ", adDetails=" + adDetails +
                '}';
    }

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

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getTargetUrl() {
            return targetUrl;
        }

        public void setTargetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
        }

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public double getSkipTime() {
            return skipTime;
        }

        public void setSkipTime(double skipTime) {
            this.skipTime = skipTime;
        }

        public double getExitTime() {
            return exitTime;
        }

        public void setExitTime(double exitTime) {
            this.exitTime = exitTime;
        }

        @Override
        public String toString() {
            return "AdDetails{" +
                    "videoUrl='" + videoUrl + '\'' +
                    ", targetUrl='" + targetUrl + '\'' +
                    ", budget='" + budget + '\'' +
                    ", skipTime=" + skipTime +
                    ", exitTime=" + exitTime +
                    '}';
        }
    }
}
