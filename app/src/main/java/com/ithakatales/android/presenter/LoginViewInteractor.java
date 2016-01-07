package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface LoginViewInteractor extends NetworkViewInteractor {

    void onLoginSuccess(User user);

}
