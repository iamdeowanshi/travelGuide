package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionAccess;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;

import javax.inject.Inject;

import retrofit.client.Response;
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

    @Override
    public void updateAttractionView(User user, long attractionId) {
        AttractionAccess attractionAccess = new AttractionAccess(user.getId(), attractionId);
        Observable<Response> observable = api.attractionViewed(user.getAccessToken(), attractionAccess);

        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response response) {}

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
            }
        });
    }

    @Override
    public void updateAttractionDownload(User user, long attractionId) {
        AttractionAccess attractionAccess = new AttractionAccess(user.getId(), attractionId);

        Observable<Response> observable = api.attractionDownloaded(user.getAccessToken(), attractionAccess);

        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response response) {}

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
