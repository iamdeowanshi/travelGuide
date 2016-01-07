package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiModels;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.VerifyAccountPresenter;
import com.ithakatales.android.presenter.VerifyAccountViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class VerifyAccountPresenterImpl extends BaseNetworkPresenter<VerifyAccountViewInteractor> implements VerifyAccountPresenter {

    @Inject IthakaApi ithakaApi;

    public VerifyAccountPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void verifyAccount(String email, String verificationCode) {
        viewInteractor.showProgress();

        ApiModels.EmailVerificationRequest request = new ApiModels.EmailVerificationRequest();
        request.email = email;
        request.otp = verificationCode;

        Observable<User> observable = ithakaApi.verifyEmail(request);
        subscribeForNetwork(observable, new ApiObserver<User>() {
            @Override
            public void onResult(User result) {
                viewInteractor.hideProgress();
                viewInteractor.onAccountVerificationSuccess(result);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.hideProgress();
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
