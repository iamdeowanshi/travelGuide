package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;
import com.ithakatales.android.data.model.ParcelConverter.TagParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.TagTypeRealmProxy;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
@Parcel(value = Parcel.Serialization.BEAN,
        analyze = { TagType.class }
        ,implementations = { TagTypeRealmProxy.class}
)
public class TagType extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("tags")
    private RealmList<Tag> tags;

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

    public RealmList<Tag> getTags() {
        return tags;
    }

    @ParcelPropertyConverter(TagParcelConverter.class)
    public void setTags(RealmList<Tag> tags) {
        this.tags = tags;
    }

}
