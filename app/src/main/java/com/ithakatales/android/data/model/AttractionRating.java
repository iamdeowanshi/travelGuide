package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Farhan Ali
 */
public class AttractionRating {

    @SerializedName("userId")
    private long userId;
    @SerializedName("attractionId")
    private long attractionId;
    @SerializedName("value")
    private int rating;

    public AttractionRating() {
    }

    public AttractionRating(long userId, long attractionId, int rating) {
        this.userId = userId;
        this.attractionId = attractionId;
        this.rating = rating;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}
