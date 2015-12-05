package com.ithakatales.android.app.di;

import com.ithakatales.android.presenter.NavigationDrawerPresenter;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourListPresenter;
import com.ithakatales.android.presenter.concrete.NavigationDrawerPresenterImpl;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.presenter.concrete.TourDetailPresenterImpl;
import com.ithakatales.android.presenter.concrete.TourListPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class PresenterModule {

    @Provides
    public SamplePresenter provideSamplePresenter() {
        return new SamplePresenterImpl();
    }

    @Provides
    public NavigationDrawerPresenter provideNavigationDrawerPresenter() {
        return new NavigationDrawerPresenterImpl();
    }

    @Provides
    public TourListPresenter provideTourListPresenter() {
        return new TourListPresenterImpl();
    }

    @Provides
    public TourDetailPresenter provideTourDetailPresenter() {
        return new TourDetailPresenterImpl();
    }

}
