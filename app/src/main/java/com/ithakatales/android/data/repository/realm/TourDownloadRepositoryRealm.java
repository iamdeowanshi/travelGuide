package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.TourDownload;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * @author Farhan Ali
 */
public class TourDownloadRepositoryRealm extends BaseRepositoryRealm<TourDownload> implements TourDownloadRepository {

    public TourDownloadRepositoryRealm() {
        super(TourDownload.class);
    }

}
