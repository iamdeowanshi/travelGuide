package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.download.model.TourDownloadProgress;

/**
 * @author Farhan Ali
 */
public interface TourDetailViewInteractor extends NetworkViewInteractor {

    void onAttractionLoaded(Attraction attraction, int tourAction);

    void onDownloadProgressChange(TourDownloadProgress downloadProgress);

    void onDownloadComplete(long attractionId);

    void onNoNetwork();

}
