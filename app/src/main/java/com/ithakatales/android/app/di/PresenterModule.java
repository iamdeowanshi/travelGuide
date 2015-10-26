package com.ithakatales.android.app.di;

import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;

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

}
