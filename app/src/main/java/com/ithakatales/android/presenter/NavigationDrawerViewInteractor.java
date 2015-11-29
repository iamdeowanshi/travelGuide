package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.City;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface NavigationDrawerViewInteractor extends NetworkViewInteractor {

    void citiesLoaded(List<City> cities);

}
