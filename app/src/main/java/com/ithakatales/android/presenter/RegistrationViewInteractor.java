package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.NetworkViewInteractor;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface RegistrationViewInteractor extends NetworkViewInteractor {

    void onRegistrationSuccess(User user);

}
