package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiModels;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.presenter.PasswordForgotPresenter;
import com.ithakatales.android.presenter.PasswordForgotViewInteractor;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public class PasswordForgotPresenterImpl extends BaseNetworkPresenter<PasswordForgotViewInteractor> implements PasswordForgotPresenter {

    @Inject IthakaApi ithakaApi;

    public PasswordForgotPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void requestPasswordForgot(String email) {
        viewInteractor.showProgress();

        ApiModels.ForgotPasswordRequest request = new ApiModels.ForgotPasswordRequest();
        request.email = email;

        Observable<Response> observable = ithakaApi.forgotPassword(request);
        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response result) {
                viewInteractor.hideProgress();
                viewInteractor.onPasswordForgotRequested();
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.hideProgress();
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
