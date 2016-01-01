package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.repository.AudioRepository;

import java.util.List;

/**
 * @author Farhan Ali
 */
public class AudioRepositoryRealm extends BaseRepositoryRealm<Audio> implements AudioRepository {

    public AudioRepositoryRealm() {
        super(Audio.class);
    }

    @Override
    public void updatePath(long id, String path) {
        realm.beginTransaction();
        find(id).setPath(path);
        realm.commitTransaction();
    }

    @Override
    public void updateDownloadId(long audioId, long downloadId) {
        realm.beginTransaction();
        find(audioId).setDownloadId(downloadId);
        realm.commitTransaction();
    }

    @Override
    public List<Audio> readByAttractionId(long attractionId) {
        return realm.where(modelType).equalTo("attraction_id", attractionId).findAll();
    }

}
