package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.AttractionUpdateRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
@Parcel(value = Parcel.Serialization.BEAN,
        analyze = { AttractionUpdate.class }
        ,implementations = { AttractionUpdateRealmProxy.class}
)
public class AttractionUpdate extends RealmObject {

    @PrimaryKey
    @SerializedName("attraction_id")
    private long attractionId;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("deletedAt")
    private String deletedAt;

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

}
