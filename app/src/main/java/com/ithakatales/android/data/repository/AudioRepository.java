package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Audio;

/**
 * @author Farhan Ali
 */
public interface AudioRepository extends BaseRepository<Audio> {

    void updatePath(long id, String path);

    void updateDownloadId(long audioId, long downloadId);

}
