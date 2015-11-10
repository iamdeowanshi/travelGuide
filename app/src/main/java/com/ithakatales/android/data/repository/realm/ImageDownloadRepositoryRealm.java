package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.ImageDownload;
import com.ithakatales.android.data.repository.ImageDownloadRepository;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Farhan Ali
 */
public class ImageDownloadRepositoryRealm extends BaseRepositoryRealm<ImageDownload> implements ImageDownloadRepository {

    public ImageDownloadRepositoryRealm() {
        super(ImageDownload.class);
    }

    @Override
    public int getTotalProgressByTour(long tourId) {
        RealmResults<ImageDownload> imageDownloads = (RealmResults<ImageDownload>) readByTourId(tourId);

        int progressAggregate = imageDownloads.sum("progress").intValue();

        return progressAggregate / imageDownloads.size();
    }

    @Override
    public List<ImageDownload> readByTourAndStatus(long tourId, String status) {
        return realm.where(modelType)
                .equalTo("tourId", tourId)
                .equalTo("status", status)
                .findAll();
    }

    @Override
    public List<ImageDownload> readByTourId(long tourId) {
        return realm.where(modelType)
                .equalTo("tourId", tourId)
                .findAll();
    }

}
