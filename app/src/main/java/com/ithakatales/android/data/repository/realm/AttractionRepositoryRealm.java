package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;

/**
 * @author Farhan Ali
 */
public class AttractionRepositoryRealm extends BaseRepositoryRealm<Attraction> implements AttractionRepository {

    public AttractionRepositoryRealm() {
        super(Attraction.class);
    }

    @Override
    public void updatePreviewPath(long id, String path) {
        realm.beginTransaction();
        find(id).setPreviewAudioPath(path);
        realm.commitTransaction();
    }

    @Override
    public void updateBlueprintPath(long id, String path) {
        realm.beginTransaction();
        find(id).setBluePrintPath(path);
        realm.commitTransaction();
    }

    @Override
    public void updateFeaturedImagePath(long id, String path) {
        realm.beginTransaction();
        find(id).setFeaturedImagePath(path);
        realm.commitTransaction();
    }

}
