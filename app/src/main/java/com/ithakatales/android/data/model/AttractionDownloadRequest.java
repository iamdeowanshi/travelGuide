package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Farhan Ali
 */
public class AttractionDownloadRequest {

    @SerializedName("userId")
    private long userId;
    @SerializedName("attractionId")
    private long attractionId;

    public AttractionDownloadRequest() {
    }

    public AttractionDownloadRequest(long userId, long attractionId) {
        this.userId = userId;
        this.attractionId = attractionId;
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

}
