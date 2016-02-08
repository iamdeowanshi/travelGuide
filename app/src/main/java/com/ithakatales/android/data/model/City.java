package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.CityRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
@Parcel(value = Parcel.Serialization.BEAN,
        analyze = { City.class }
        ,implementations = { CityRealmProxy.class}
)
public class City extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("featuredImageUrl")
    private String featuredImageUrl;
    @SerializedName("isPublished")
    private Boolean isPublished;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("deletedAt")
    private String deletedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
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
