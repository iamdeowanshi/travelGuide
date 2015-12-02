package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
public class Tag extends RealmObject {

    public static final long WALKING            = 1;
    public static final long HIKING             = 2;
    public static final long SHOPPING           = 3;
    public static final long PHOTOGRAPHY        = 4;
    public static final long FAMILY_FRIENDLY    = 5;
    public static final long PET_FRIENDLY       = 6;

    public static final long MONUMENT           = 7;
    public static final long RELIGIOUS_LANDMARK = 8;
    public static final long MUSEUM             = 9;
    public static final long NEIGHBOURHOOD      = 10;

    public static final long FOOD               = 11;
    public static final long HISTORY            = 12;
    public static final long NATURE             = 13;
    public static final long VIEWS              = 14;
    public static final long ARCHITECTURE       = 15;
    public static final long ART                = 16;
    public static final long BAZAR              = 17;
    public static final long NIGHTLIFE          = 18;
    public static final long MUSIC              = 19;

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;

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

}
