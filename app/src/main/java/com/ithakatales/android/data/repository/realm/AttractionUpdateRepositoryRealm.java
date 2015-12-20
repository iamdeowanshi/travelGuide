package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.repository.AttractionUpdateRepository;

/**
 * @author Farhan Ali
 */
public class AttractionUpdateRepositoryRealm extends BaseRepositoryRealm<AttractionUpdate> implements AttractionUpdateRepository {

    public AttractionUpdateRepositoryRealm() {
        super(AttractionUpdate.class);
    }

    @Override
    public AttractionUpdate find(long attractionId) {
        return realm.where(modelType).equalTo("attractionId", attractionId).findFirst();
    }

}
