package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class TourDetailPresenterImpl extends BaseNetworkPresenter<TourDetailViewInteractor>
        implements TourDetailPresenter {

    @Inject IthakaApi api;

    public TourDetailPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void loadAttraction(long attractionId) {
        viewInteractor.showProgress();

        Observable<Attraction> observable = api.getAttraction(attractionId);

        subscribeForNetwork(observable, new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction result) {
                viewInteractor.onAttractionLoadSuccess(result);
                viewInteractor.hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
                viewInteractor.hideProgress();
            }
        });
    }

}
