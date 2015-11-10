package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Attraction;

/**
 * @author Farhan Ali
 */
public interface AttractionRepository extends BaseRepository<Attraction> {

    void updatePreviewPath(long id, String path);

    void updateBlueprintPath(long id, String path);

    void updateFeaturedImagePath(long id, String path);

}
