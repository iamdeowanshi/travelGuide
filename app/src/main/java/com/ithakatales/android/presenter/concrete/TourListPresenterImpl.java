package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.data.repository.AttractionUpdateRepository;
import com.ithakatales.android.presenter.TourListPresenter;
import com.ithakatales.android.presenter.TourListViewInteractor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class TourListPresenterImpl extends BaseNetworkPresenter<TourListViewInteractor>
        implements TourListPresenter {

    @Inject IthakaApi api;
    @Inject AttractionUpdateRepository attractionUpdateRepo;

    public TourListPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void loadAttractions(long cityId) {
        viewInteractor.showProgress();

        Observable<List<Attraction>> observable = api.getAttractions(cityId);

        subscribeForNetwork(observable, new ApiObserver<List<Attraction>>() {
            @Override
            public void onResult(List<Attraction> result) {
                viewInteractor.attractionsLoaded(result);
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
    public void loadAttractionUpdates(User user) {
        Observable<List<AttractionUpdate>> observable = api.getAttractionUpdates(user.getAccessToken(), user.getId());

        subscribeForNetwork(observable, new ApiObserver<List<AttractionUpdate>>() {
            @Override
            public void onResult(List<AttractionUpdate> result) {
                attractionUpdateRepo.save(result);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
