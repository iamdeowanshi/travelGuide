package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.presenter.SettingsPresenter;
import com.ithakatales.android.presenter.SettingsViewInteractor;
import com.ithakatales.android.util.UserPreference;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Farhan Ali
 */
public class SettingsPresenterImpl extends BaseNetworkPresenter<SettingsViewInteractor> implements SettingsPresenter {

    @Inject IthakaApi ithakaApi;
    @Inject TourDownloader tourDownloader;
    @Inject UserPreference preference;

    public SettingsPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void uploadProfilePic(User user, File file) {
        // TODO: 06/01/16
    }

    @Override
    public void updateProfile(User user) {
        viewInteractor.showProgress();

        Observable<User> observable = ithakaApi.updateProfile(user.getAccessToken(), user.getId(), user);

        subscribeForNetwork(observable, new ApiObserver<User>() {
            @Override
            public void onResult(User result) {
                viewInteractor.hideProgress();
                viewInteractor.onProfileUpdateSuccess(result);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.hideProgress();
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
