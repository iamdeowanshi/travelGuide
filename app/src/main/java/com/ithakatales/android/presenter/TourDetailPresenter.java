package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface TourDetailPresenter extends Presenter<TourDetailViewInteractor> {

    void loadAttraction(long attractionId);

    void updateAttractionView(User user, long attractionId);

    void updateAttractionDownload(User user, long attractionId);

}
