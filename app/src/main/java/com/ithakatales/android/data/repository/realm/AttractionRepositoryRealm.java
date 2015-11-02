package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.BaseRepository;

/**
 * @author Farhan Ali
 */
public class AttractionRepositoryRealm extends BaseRepositoryRealm<Attraction> implements AttractionRepository {

    public AttractionRepositoryRealm() {
        super(Attraction.class);
    }

}
