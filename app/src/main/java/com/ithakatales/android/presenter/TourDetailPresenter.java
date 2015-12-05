package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;

/**
 * @author Farhan Ali
 */
public interface TourDetailPresenter extends Presenter<TourDetailViewInteractor> {

    void loadAttraction(long attractionId);

}
