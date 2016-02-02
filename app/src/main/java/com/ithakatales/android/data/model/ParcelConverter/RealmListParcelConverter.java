package com.ithakatales.android.data.model.ParcelConverter;

import org.parceler.converter.CollectionParcelConverter;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Sibi on 28/01/16.
 */
public abstract class RealmListParcelConverter<T extends RealmObject> extends CollectionParcelConverter<T, RealmList<T>> {
    @Override
    public RealmList<T> createCollection() {
        return new RealmList<T>();
    }
}