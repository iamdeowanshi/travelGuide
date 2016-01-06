package com.ithakatales.android.app.di;

import com.ithakatales.android.presenter.LoginPresenter;
import com.ithakatales.android.presenter.NavigationDrawerPresenter;
import com.ithakatales.android.presenter.PasswordForgotPresenter;
import com.ithakatales.android.presenter.PasswordResetPresenter;
import com.ithakatales.android.presenter.RegistrationPresenter;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourListPresenter;
import com.ithakatales.android.presenter.VerifyAccountPresenter;
import com.ithakatales.android.presenter.concrete.LoginPresenterImpl;
import com.ithakatales.android.presenter.concrete.NavigationDrawerPresenterImpl;
import com.ithakatales.android.presenter.concrete.PasswordForgotPresenterImpl;
import com.ithakatales.android.presenter.concrete.PasswordResetPresenterImpl;
import com.ithakatales.android.presenter.concrete.RegistrationPresenterImpl;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.presenter.concrete.TourDetailPresenterImpl;
import com.ithakatales.android.presenter.concrete.TourListPresenterImpl;
import com.ithakatales.android.presenter.concrete.VerifyAccountPresenterImpl;

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
    public LoginPresenter provideLoginPresenter() {
        return new LoginPresenterImpl();
    }

    @Provides
    public RegistrationPresenter provideRegistrationPresenter() {
        return new RegistrationPresenterImpl();
    }

    @Provides
    public PasswordForgotPresenter providePasswordForgotPresenter() {
        return new PasswordForgotPresenterImpl();
    }

    @Provides
    public PasswordResetPresenter providePasswordResetPresenter() {
        return new PasswordResetPresenterImpl();
    }

    @Provides
    public VerifyAccountPresenter provideVerifyAccountPresenter() {
        return new VerifyAccountPresenterImpl();
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
