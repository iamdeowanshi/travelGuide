package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.RegistrationPresenter;
import com.ithakatales.android.presenter.RegistrationViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class RegistrationPresenterImpl extends BaseNetworkPresenter<RegistrationViewInteractor> implements RegistrationPresenter {

    @Inject IthakaApi ithakaApi;

    public RegistrationPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void register(User user) {
        viewInteractor.showProgress();

        Observable<User> observable = ithakaApi.register(user);
        subscribeForNetwork(observable, new ApiObserver<User>() {
            @Override
            public void onResult(User result) {
                viewInteractor.onRegistrationSuccess(result);
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
