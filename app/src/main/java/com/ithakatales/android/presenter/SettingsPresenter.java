package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

import java.io.File;

/**
 * @author Farhan Ali
 */
public interface SettingsPresenter extends Presenter<SettingsViewInteractor> {

    void uploadProfilePic(User user, File file);

    void updateProfile(User user);

}
