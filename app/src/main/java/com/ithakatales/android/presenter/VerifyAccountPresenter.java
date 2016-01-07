package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;

/**
 * @author Farhan Ali
 */
public interface VerifyAccountPresenter extends Presenter<VerifyAccountViewInteractor> {

    void verifyAccount(String email, String verificationCode);

}
