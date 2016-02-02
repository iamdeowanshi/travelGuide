package com.ithakatales.android.data.model.ParcelConverter;

import android.os.Parcel;

import com.ithakatales.android.data.model.Tag;

import org.parceler.Parcels;

/**
 * Created by Sibi on 28/01/16.
 */
public class TagParcelConverter extends RealmListParcelConverter<Tag> {
    @Override public void itemToParcel(Tag input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public Tag itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Tag.class.getClassLoader()));
    }
}
