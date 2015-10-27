package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BasePresenter;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.SampleViewInteractor;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

/**
 * @author Farhan Ali
 */
public class SamplePresenterImpl extends BasePresenter<SampleViewInteractor>
        implements SamplePresenter {

    @Inject Realm realm;

    public SamplePresenterImpl() {
        injectDependencies();
    }

    @Override
    public void doSomething() {
        viewInteractor.showSomeMessage("Doing something..");
        City city = createCity();
        viewInteractor.showSomeMessage("Created : " + city);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    private City createCity() {
        int id = getCities().size() + 1;

        City city = new City();
        city.setId(id);
        city.setName("City " + id);

        realm.beginTransaction();
        city = realm.copyToRealmOrUpdate(city);
        realm.commitTransaction();

        return city;
    }

    private List<City> getCities() {
        return realm.where(City.class).findAll();
    }

}
