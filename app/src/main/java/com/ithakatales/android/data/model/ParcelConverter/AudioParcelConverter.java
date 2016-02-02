package com.ithakatales.android.data.model.ParcelConverter;

import android.os.Parcel;

import com.ithakatales.android.data.model.Audio;

import org.parceler.Parcels;

/**
 * Created by Sibi on 28/01/16.
 */
public class AudioParcelConverter extends RealmListParcelConverter<Audio> {
    @Override public void itemToParcel(Audio input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public Audio itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Audio.class.getClassLoader()));
    }
}
