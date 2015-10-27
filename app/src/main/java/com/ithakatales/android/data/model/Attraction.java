package com.ithakatales.android.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
public class Attraction extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private long duration;
    private String bluePrintUrl;
    private String bluePrintPath;
    private String featuredImageUrl;
    private String featuredImagePath;
    private String previewAudioUrl;
    private String previewAudioPath;
    private double latitude;
    private double longitude;
    private String shortDescription;
    private String longDescription;
    private String beforeYouGo;
    private String credits;
    private double price;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private boolean isPublished;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    private AttractionType type;
    private City city;
    private RealmList<TagType> tagTypes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getBluePrintUrl() {
        return bluePrintUrl;
    }

    public void setBluePrintUrl(String bluePrintUrl) {
        this.bluePrintUrl = bluePrintUrl;
    }

    public String getBluePrintPath() {
        return bluePrintPath;
    }

    public void setBluePrintPath(String bluePrintPath) {
        this.bluePrintPath = bluePrintPath;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public String getFeaturedImagePath() {
        return featuredImagePath;
    }

    public void setFeaturedImagePath(String featuredImagePath) {
        this.featuredImagePath = featuredImagePath;
    }

    public String getPreviewAudioUrl() {
        return previewAudioUrl;
    }

    public void setPreviewAudioUrl(String previewAudioUrl) {
        this.previewAudioUrl = previewAudioUrl;
    }

    public String getPreviewAudioPath() {
        return previewAudioPath;
    }

    public void setPreviewAudioPath(String previewAudioPath) {
        this.previewAudioPath = previewAudioPath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getBeforeYouGo() {
        return beforeYouGo;
    }

    public void setBeforeYouGo(String beforeYouGo) {
        this.beforeYouGo = beforeYouGo;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
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

    public AttractionType getType() {
        return type;
    }

    public void setType(AttractionType type) {
        this.type = type;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public RealmList<TagType> getTagTypes() {
        return tagTypes;
    }

    public void setTagTypes(RealmList<TagType> tagTypes) {
        this.tagTypes = tagTypes;
    }

}
