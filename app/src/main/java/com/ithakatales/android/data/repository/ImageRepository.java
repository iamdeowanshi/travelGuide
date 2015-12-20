package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Image;

/**
 * @author Farhan Ali
 */
public interface ImageRepository extends BaseRepository<Image> {

    void updatePath(long id, String path);

    void updateDownloadId(long imageId, long downloadId);

}
