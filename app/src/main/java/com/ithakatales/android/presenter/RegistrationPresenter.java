package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface RegistrationPresenter extends Presenter<RegistrationViewInteractor> {

    void register(User user);

}
