package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface TourListViewInteractor extends NetworkViewInteractor {

    void attractionsLoaded(List<Attraction> attractions);

}
