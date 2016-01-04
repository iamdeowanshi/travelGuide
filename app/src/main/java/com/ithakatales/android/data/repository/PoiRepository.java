package com.ithakatales.android.data.repository;

import com.ithakatales.android.data.model.Poi;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface PoiRepository extends BaseRepository<Poi> {

    List<Poi> readByAttractionId(long attractionId);

}
