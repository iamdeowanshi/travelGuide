package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface TourFinishPresenter extends Presenter<TourFinishViewInteractor> {

    void rateTour(User user, long attractionId, int rating);

}
