package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiModels;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.TourFinishPresenter;
import com.ithakatales.android.presenter.TourFinishViewInteractor;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public class TourFinishPresenterImpl extends BaseNetworkPresenter<TourFinishViewInteractor> implements TourFinishPresenter {

    @Inject IthakaApi ithakaApi;

    public TourFinishPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void rateTour(User user, long attractionId, int rating) {
        ApiModels.AttractionRatingRequest request = new ApiModels.AttractionRatingRequest();
        request.userId = user.getId();
        request.attractionId = attractionId;
        request.rating = rating;

        Observable<Response> observable = ithakaApi.rateAttraction(user.getAccessToken(), request);
        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response result) {
                viewInteractor.onTourRated();
            }

            @Override
            public void onError(Throwable e) {}
        });
    }

}
