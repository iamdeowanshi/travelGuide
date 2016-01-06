package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.LoginPresenter;
import com.ithakatales.android.presenter.LoginViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class LoginPresenterImpl extends BaseNetworkPresenter<LoginViewInteractor> implements LoginPresenter {

    @Inject IthakaApi ithakaApi;

    private ApiObserver<User> loginObserver = new ApiObserver<User>() {
        @Override
        public void onResult(User result) {
            viewInteractor.onLoginSuccess(result);
            viewInteractor.hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            viewInteractor.onNetworkError(e);
            viewInteractor.hideProgress();
        }
    };

    public LoginPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void loginNormal(User user) {
        viewInteractor.showProgress();
        Observable<User> observable = ithakaApi.loginNormal(user);
        subscribeForNetwork(observable, loginObserver);
    }

    @Override
    public void loginSocial(User user) {
        viewInteractor.showProgress();
        Observable<User> observable = ithakaApi.loginSocial(user);
        subscribeForNetwork(observable, loginObserver);
    }

}
