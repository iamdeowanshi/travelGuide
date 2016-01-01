package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Image;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface ImageRepository extends BaseRepository<Image> {

    void updatePath(long id, String path);

    void updateDownloadId(long imageId, long downloadId);

    List<Image> readByAttractionId(long attractionId);

}
