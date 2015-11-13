package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.TourDownloadRepository;

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
    public void updateProgressAndStatus(long tourId, int progress, String status) {
        realm.beginTransaction();
        TourDownload tourDownload = find(tourId);
        tourDownload.setProgress(progress);
        tourDownload.setStatus(status);
        realm.commitTransaction();
    }

}
