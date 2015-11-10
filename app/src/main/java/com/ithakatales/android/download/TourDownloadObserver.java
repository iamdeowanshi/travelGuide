package com.ithakatales.android.download;

import com.ithakatales.android.data.model.TourDownload;

/**
 * @author Farhan Ali
 */
public interface TourDownloadObserver {

    void downloadStatusChanged(TourDownload download);

}
