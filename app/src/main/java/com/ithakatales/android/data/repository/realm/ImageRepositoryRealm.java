package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.repository.ImageRepository;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Farhan Ali
 */
public class ImageRepositoryRealm extends BaseRepositoryRealm<Image> implements ImageRepository {

    public ImageRepositoryRealm() {
        super(Image.class);
    }

    @Override
    public void updatePath(long id, String path) {
        realm.beginTransaction();
        find(id).setPath(path);
        realm.commitTransaction();
    }

    @Override
    public void updateDownloadId(long imageId, long downloadId) {
        realm.beginTransaction();
        find(imageId).setDownloadId(downloadId);
        realm.commitTransaction();
    }

    @Override
    public List<Image> readByAttractionId(long attractionId) {
        RealmResults<Image> images = realm.where(modelType).equalTo("attractionId", attractionId).findAll();
        images.sort("priority");

        return images;
    }

    @Override
    public List<Image> readByPoiId(long poiId) {
        RealmResults<Image> images = realm.where(modelType).equalTo("poiId", poiId).findAll();
        images.sort("priority");

        return images;
    }

    @Override
    public List<Image> readByAudioId(long audioId) {
        RealmResults<Image> images = realm.where(modelType).equalTo("audioId", audioId).findAll();
        images.sort("priority");

        return images;
    }

}
