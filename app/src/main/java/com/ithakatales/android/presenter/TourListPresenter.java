package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;

/**
 * @author Farhan Ali
 */
public interface TourListPresenter extends Presenter<TourListViewInteractor> {

    void loadAttractions(long cityId);

}
