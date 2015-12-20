package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface TourListPresenter extends Presenter<TourListViewInteractor> {

    void loadAttractions(long cityId);

    void loadAttractionUpdates(User user);

}
