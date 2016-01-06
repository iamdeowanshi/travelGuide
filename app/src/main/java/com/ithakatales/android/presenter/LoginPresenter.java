package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;
import com.ithakatales.android.data.model.User;

/**
 * @author Farhan Ali
 */
public interface LoginPresenter extends Presenter<LoginViewInteractor> {

    void loginNormal(User user);

    void loginSocial(User user);

}
