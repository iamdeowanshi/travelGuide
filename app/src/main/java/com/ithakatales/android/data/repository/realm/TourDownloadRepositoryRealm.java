package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.model.ImageDownload;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.TourDownloadRepository;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Farhan Ali
 */
public class TourDownloadRepositoryRealm extends BaseRepositoryRealm<TourDownload> implements TourDownloadRepository {

    public TourDownloadRepositoryRealm() {
        super(TourDownload.class);
    }

    @Override
    public TourDownload findByAttractionId(long attractionId) {
        return realm.where(modelType).equalTo("attractionId", attractionId).findFirst();
    }

    @Override
    public int getTotalProgress(long tourId) {
        RealmResults<AudioDownload> audioDownloads = realm.where(AudioDownload.class)
                .equalTo("tourId", tourId)
                .findAll();
        int audioDownloadProgressAggregate = audioDownloads.sum("progress").intValue();

        RealmResults<ImageDownload> imageDownloads = realm.where(ImageDownload.class)
                .equalTo("tourId", tourId)
                .findAll();
        int imageDownloadProgressAggregate = imageDownloads.sum("progress").intValue();

        int progressAggregate = audioDownloadProgressAggregate + imageDownloadProgressAggregate;
        int totalDownloadCount = audioDownloads.size() + imageDownloads.size();

        return progressAggregate / totalDownloadCount;
    }

    @Override
    public void updateProgressAndStatus(long tourId, int progress, String status) {
        realm.beginTransaction();
        TourDownload tourDownload = find(tourId);
        tourDownload.setProgress(progress);
        tourDownload.setStatus(status);
        realm.commitTransaction();
    }

}
