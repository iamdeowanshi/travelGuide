package com.ithakatales.android.data.model.ParcelConverter;

import android.os.Parcel;

import com.ithakatales.android.data.model.Poi;

import org.parceler.Parcels;

/**
 * Created by Sibi on 28/01/16.
 */
public class PoiParcelConverter extends RealmListParcelConverter<Poi>{


    @Override public void itemToParcel(Poi input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public Poi itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Poi.class.getClassLoader()));
    }
}
