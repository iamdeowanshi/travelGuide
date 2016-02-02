package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;
import com.ithakatales.android.data.model.ParcelConverter.ImageParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.AudioRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
@Parcel(value = Parcel.Serialization.BEAN,
        analyze = { Audio.class }
        ,implementations = { AudioRealmProxy.class}
)
public class Audio extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("attraction_id")
    private long attractionId;
    @SerializedName("poi_id")
    private long poiId;
    @SerializedName("priority")
    private int priority;
    @SerializedName("name")
    private String name;
    @SerializedName("encUrl")
    private String encUrl;
    @SerializedName("encDigest")
    private String encDigest;
    private String path;
    @SerializedName("size")
    private long size;
    @SerializedName("duration")
    private long duration;
    @SerializedName("isPublished")
    private boolean isPublished;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("deletedAt")
    private String deletedAt;
    @SerializedName("images")
    private RealmList<Image> images;
    private boolean isPlayed = false;
    // download related details
    private long downloadId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncUrl() {
        return encUrl;
    }

    public void setEncUrl(String encUrl) {
        this.encUrl = encUrl;
    }

    public String getEncDigest() {
        return encDigest;
    }

    public void setEncDigest(String encDigest) {
        this.encDigest = encDigest;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
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

    public RealmList<Image> getImages() {
        return images;
    }

    @ParcelPropertyConverter(ImageParcelConverter.class)
    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setIsPlayed(boolean isPlayed) {
        this.isPlayed = isPlayed;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

}
