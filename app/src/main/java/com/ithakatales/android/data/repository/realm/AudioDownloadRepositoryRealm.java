package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.repository.AudioDownloadRepository;

import java.util.List;

import io.realm.RealmResults;

/**
 * @author Farhan Ali
 */
public class AudioDownloadRepositoryRealm extends BaseRepositoryRealm<AudioDownload> implements AudioDownloadRepository {

    public AudioDownloadRepositoryRealm() {
        super(AudioDownload.class);
    }

    @Override
    public int getTotalProgressByTour(long tourId) {
        RealmResults<AudioDownload> audioDownloads = (RealmResults<AudioDownload>) readByTourId(tourId);

        int progressAggregate = audioDownloads.sum("progress").intValue();

        return progressAggregate / audioDownloads.size();
    }

    @Override
    public List<AudioDownload> readByTourAndStatus(long tourId, String status) {
        return realm.where(modelType)
                .equalTo("tourId", tourId)
                .equalTo("status", status)
                .findAll();
    }

    @Override
    public List<AudioDownload> readByTourId(long tourId) {
        return realm.where(modelType)
                .equalTo("tourId", tourId)
                .findAll();
    }

    @Override
    public void updateProgressAndStatus(long downloadId, int progress, String status) {
        realm.beginTransaction();
        AudioDownload audioDownload = find(downloadId);
        audioDownload.setProgress(progress);
        audioDownload.setStatus(status);
        realm.commitTransaction();
    }

}
