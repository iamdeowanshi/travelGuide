package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.Attraction;

/**
 * @author Farhan Ali
 */
public interface TourDetailPresenter extends Presenter<TourDetailViewInteractor> {

    void loadAttraction(long attractionId);

    void downloadAttraction(Attraction attraction);

    void retryDownloadAttraction(Attraction attraction);

    void updateAttraction(Attraction attraction);

    void deleteAttraction(Attraction attraction);

    void stopProgressTracking();

}
