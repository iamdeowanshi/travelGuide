package com.ithakatales.android.data.model.ParcelConverter;

import android.os.Parcel;

import com.ithakatales.android.data.model.TagType;

import org.parceler.Parcels;

/**
 * Created by Sibi on 28/01/16.
 */
public class TagTypeParcelConverter extends RealmListParcelConverter<TagType>{


    @Override public void itemToParcel(TagType input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public TagType itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(TagType.class.getClassLoader()));
    }
}
