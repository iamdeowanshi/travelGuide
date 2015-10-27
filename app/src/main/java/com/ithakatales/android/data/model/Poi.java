package com.ithakatales.android.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
public class Poi extends RealmObject {

    @PrimaryKey
    private int id;
    private int attractionId;
    private String name;
    private String description;
    private float xPercent;
    private float yPercent;
    private boolean isPublished;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    private RealmList<TagType> tagTypes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(int attractionId) {
        this.attractionId = attractionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getxPercent() {
        return xPercent;
    }

    public void setxPercent(float xPercent) {
        this.xPercent = xPercent;
    }

    public float getyPercent() {
        return yPercent;
    }

    public void setyPercent(float yPercent) {
        this.yPercent = yPercent;
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

    public RealmList<TagType> getTagTypes() {
        return tagTypes;
    }

    public void setTagTypes(RealmList<TagType> tagTypes) {
        this.tagTypes = tagTypes;
    }

}

