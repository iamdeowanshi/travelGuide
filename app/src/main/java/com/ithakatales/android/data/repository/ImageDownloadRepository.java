package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.ImageDownload;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface ImageDownloadRepository extends BaseRepository<ImageDownload> {

    int getTotalProgressByTour(long tourId);

    List<ImageDownload> readByTourAndStatus(long tourId, String status);

    List<ImageDownload> readByTourId(long tourId);

    void updateProgressAndStatus(long downloadId, int progress, String status);

}
