package com.kwakdevs.kwak123.mybakingapp.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public final class Step {

    @SerializedName("id")
    private Integer id;

    @SerializedName("shortDescription")
    private String shortDescription;

    @SerializedName("description")
    private String description;

    @SerializedName("videoURL")
    private String videoUrl;

    @SerializedName("thumbnailURL")
    private String thumbnailUrl;

    public Step(Integer id, String shortDescription, String description, String videoUrl,
                String thumbnailUrl) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getId() {
        return this.id;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getDescription() {
        return this.description;
    }

    @NonNull
    public String getVideoUrl() {
        if (videoUrl == null) return "";
        return this.videoUrl;
    }

    @NonNull
    public String getThumbnailUrl() {
        if (thumbnailUrl == null) return "";
        return this.thumbnailUrl;
    }
}
