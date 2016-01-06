package com.ithakatales.android.presenter;

import com.ithakatales.android.app.base.Presenter;

/**
 * @author Farhan Ali
 */
public interface PasswordResetPresenter extends Presenter<PasswordResetViewInteractor> {

    void resetPassword(String email, String tempPassword, String newPassword);

}
