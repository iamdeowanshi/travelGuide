package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.TourDownload;

/**
 * @author Farhan Ali
 */
public interface TourDownloadRepository extends BaseRepository<TourDownload> {

    TourDownload findByAttractionId(long attractionId);

    void updateProgressAndStatus(long tourId, int progress, String status);

}
