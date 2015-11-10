package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.AudioDownload;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface AudioDownloadRepository extends BaseRepository<AudioDownload> {

    int getTotalProgressByTour(long tourId);

    List<AudioDownload> readByTourAndStatus(long tourId, String status);

    List<AudioDownload> readByTourId(long tourId);

}
