package com.ithakatales.android.data.model.ParcelConverter;

import android.os.Parcel;

import com.ithakatales.android.data.model.Image;

import org.parceler.Parcels;

/**
 * Created by Sibi on 28/01/16.
 */
public class ImageParcelConverter extends RealmListParcelConverter<Image> {
    @Override public void itemToParcel(Image input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public Image itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Image.class.getClassLoader()));
    }
}
