package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.repository.AudioDownloadRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.AudioDownload;
import com.ithakatales.android.download.TourDownload;

import io.realm.RealmResults;

/**
 * @author Farhan Ali
 */
public class AudioDownloadRepositoryRealm extends BaseRepositoryRealm<AudioDownload> implements AudioDownloadRepository {

    public AudioDownloadRepositoryRealm() {
        super(AudioDownload.class);
    }

    @Override
    public int getTourDownloadProgress(long tourId) {
        RealmResults<AudioDownload> audioDownloads = realm.where(modelType)
                .equalTo("tourId", tourId)
                .findAll();

        int progressAggregate = audioDownloads.sum("progress").intValue();

        return 100 * (progressAggregate / audioDownloads.size());
    }

}
