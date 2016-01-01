package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.PoiRepository;

import java.util.List;

/**
 * @author Farhan Ali
 */
public class PoiRepositoryRealm extends BaseRepositoryRealm<Poi> implements PoiRepository {

    public PoiRepositoryRealm() {
        super(Poi.class);
    }

    @Override
    public List<Poi> readByAttractionId(long attractionId) {
        return realm.where(modelType).equalTo("attractionId", attractionId).findAll();
    }

}
