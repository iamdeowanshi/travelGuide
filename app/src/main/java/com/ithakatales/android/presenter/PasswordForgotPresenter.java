package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;

/**
 * @author Farhan Ali
 */
public interface PasswordForgotPresenter extends Presenter<PasswordForgotViewInteractor> {

    void requestPasswordForgot(String email);

}
