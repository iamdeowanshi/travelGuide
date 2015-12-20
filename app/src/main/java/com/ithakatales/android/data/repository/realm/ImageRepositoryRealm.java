package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.repository.ImageRepository;

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

}
