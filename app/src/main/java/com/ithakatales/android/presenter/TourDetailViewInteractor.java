package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.Attraction;

/**
 * @author Farhan Ali
 */
public interface TourDetailViewInteractor extends NetworkViewInteractor {

    void onAttractionLoadSuccess(Attraction attraction);

}
