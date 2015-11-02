package com.ithakatales.android.data.repository;

import com.ithakatales.android.download.AudioDownload;
import com.ithakatales.android.download.TourDownload;

/**
 * @author Farhan Ali
 */
public interface AudioDownloadRepository extends BaseRepository<AudioDownload> {

    int getTourDownloadProgress(long tourId);

}
