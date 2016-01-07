package com.ithakatales.android.util.social;

/**
 * @author farhanali
 */
public interface SocialAuthCallback {

    void onAuthSuccess(AuthResult result);

    void onAuthCancel();

    void onAuthError(Throwable throwable);

}
