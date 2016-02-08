package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.AttractionTypeRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
@Parcel(value = Parcel.Serialization.BEAN,
        analyze = { AttractionType.class }
        ,implementations = { AttractionTypeRealmProxy.class}
)
public class AttractionType extends RealmObject {

    public static final long WALKING     = 1;
    public static final long MONUMENT    = 2;

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
