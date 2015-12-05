package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
public class Attraction extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("caption")
    private String caption;
    @SerializedName("duration")
    private long duration;
    @SerializedName("blueprintUrl")
    private String blueprintUrl;
    private String bluePrintPath;
    @SerializedName("shortDescription")
    private String shortDescription;
    @SerializedName("longDescription")
    private String longDescription;
    @SerializedName("beforeYouGo")
    private String beforeYouGo;
    @SerializedName("credits")
    private String credits;
    @SerializedName("price")
    private long price;
    @SerializedName("addressLine1")
    private String addressLine1;
    @SerializedName("addressLine2")
    private String addressLine2;
    @SerializedName("addressLine3")
    private String addressLine3;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("isPublished")
    private boolean isPublished;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("featuredImage")
    private Image featuredImage;
    @SerializedName("previewAudio")
    private Audio previewAudio;
    @SerializedName("city")
    private City city;
    @SerializedName("type")
    private AttractionType type;
    @SerializedName("tagTypes")
    private RealmList<TagType> tagTypes;
    @SerializedName("pois")
    private RealmList<Poi> pois;
    @SerializedName("audios")
    private RealmList<Audio> audios;
    @SerializedName("images")
    private RealmList<Image> images;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public String getBlueprintUrl() {
        return blueprintUrl;
    }

    public void setBlueprintUrl(String blueprintUrl) {
        this.blueprintUrl = blueprintUrl;
    }

    public String getBluePrintPath() {
        return bluePrintPath;
    }

    public void setBluePrintPath(String bluePrintPath) {
        this.bluePrintPath = bluePrintPath;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
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

    public Image getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(Image featuredImage) {
        this.featuredImage = featuredImage;
    }

    public Audio getPreviewAudio() {
        return previewAudio;
    }

    public void setPreviewAudio(Audio previewAudio) {
        this.previewAudio = previewAudio;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public AttractionType getType() {
        return type;
    }

    public void setType(AttractionType type) {
        this.type = type;
    }

    public RealmList<TagType> getTagTypes() {
        return tagTypes;
    }

    public void setTagTypes(RealmList<TagType> tagTypes) {
        this.tagTypes = tagTypes;
    }

    public RealmList<Poi> getPois() {
        return pois;
    }

    public void setPois(RealmList<Poi> pois) {
        this.pois = pois;
    }

    public RealmList<Audio> getAudios() {
        return audios;
    }

    public void setAudios(RealmList<Audio> audios) {
        this.audios = audios;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

}
