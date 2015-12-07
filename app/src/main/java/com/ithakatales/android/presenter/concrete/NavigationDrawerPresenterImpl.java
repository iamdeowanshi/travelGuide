package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.presenter.NavigationDrawerPresenter;
import com.ithakatales.android.presenter.NavigationDrawerViewInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class NavigationDrawerPresenterImpl extends BaseNetworkPresenter<NavigationDrawerViewInteractor>
        implements NavigationDrawerPresenter {

    @Inject IthakaApi api;

    public NavigationDrawerPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void loadCities() {
        viewInteractor.showProgress();

        if (Config.API_BASE_URL == Config.API_BASE_URL_MOCK) {

            Observable<List<City>> observable = api.getCities();

            subscribeForNetwork(observable, new ApiObserver<List<City>>() {
                @Override
                public void onResult(List<City> result) {
                    viewInteractor.citiesLoaded(result);
                    viewInteractor.hideProgress();
                }

                @Override
                public void onError(Throwable e) {
                    viewInteractor.onNetworkError(e);
                    viewInteractor.hideProgress();
                }
            });

            return;
        }

        List<City> cities = new ArrayList<>();
        City city = new City();
        city.setId(1);
        city.setName("Delhi");
        cities.add(city);

        viewInteractor.citiesLoaded(cities);
        viewInteractor.hideProgress();
    }

}
