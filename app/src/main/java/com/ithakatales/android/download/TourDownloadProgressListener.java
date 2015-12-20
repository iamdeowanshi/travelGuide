package com.ithakatales.android.download;

import com.ithakatales.android.download.model.TourDownloadProgress;

/**
 * @author Farhan Ali
 */
public interface TourDownloadProgressListener {

    void onProgressChange(TourDownloadProgress tourDownloadProgress);

}
