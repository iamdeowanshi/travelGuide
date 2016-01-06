package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiModels;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.PasswordResetPresenter;
import com.ithakatales.android.presenter.PasswordResetViewInteractor;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class PasswordResetPresenterImpl extends BaseNetworkPresenter<PasswordResetViewInteractor> implements PasswordResetPresenter {

    @Inject IthakaApi ithakaApi;

    public PasswordResetPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void resetPassword(String email, String tempPassword, String newPassword) {
        viewInteractor.showProgress();

        ApiModels.ResetPasswordRequest request = new ApiModels.ResetPasswordRequest();
        request.email = email;
        request.otp = tempPassword;
        request.newPassword = newPassword;

        Observable<User> observable = ithakaApi.resetPassword(request);
        subscribeForNetwork(observable, new ApiObserver<User>() {
            @Override
            public void onResult(User result) {
                viewInteractor.hideProgress();
                viewInteractor.onPasswordResetSuccess(result);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.hideProgress();
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
