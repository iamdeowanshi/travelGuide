package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Audio;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface AudioRepository extends BaseRepository<Audio> {

    void updatePath(long id, String path);

    void updateDownloadId(long audioId, long downloadId);

    List<Audio> readByAttractionId(long attractionId);

}
