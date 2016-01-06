package com.ithakatales.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.LoginPresenter;
import com.ithakatales.android.presenter.LoginViewInteractor;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.UserPreference;
import com.ithakatales.android.util.social.AuthResult;
import com.ithakatales.android.util.social.SocialAuth;
import com.ithakatales.android.util.social.SocialAuthCallback;

import javax.inject.Inject;

import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public abstract class SocialLoginEnabledActivity extends BaseActivity implements SocialAuthCallback, LoginViewInteractor {

    @Inject LoginPresenter loginPresenter;
    @Inject UserPreference userPreference;
    @Inject Bakery bakery;

    protected SocialAuth socialAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectDependencies();
        loginPresenter.setViewInteractor(this);

        socialAuth = new SocialAuth(this);
        socialAuth.setCallback(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialAuth.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthSuccess(AuthResult result) {
        hideProgress();
        loginPresenter.loginSocial(new User(result));
    }

    @Override
    public void onAuthCancel() {
        hideProgress();
    }

    @Override
    public void onAuthError(Throwable e) {
        hideProgress();
        Timber.e(e.getMessage(), e);
        bakery.snackShort(getContentView(), "Authentication Failed");
    }

    @Override
    public void onLoginSuccess(User user) {
        userPreference.saveUser(user);
        Class<? extends Activity> activityClass = user.isVerified()
                ? HomeActivity.class
                : VerifyAccountActivity.class;
        startActivityClearTop(activityClass, null);
    }

    @OnClick(R.id.button_fb_login)
    void onFacebookLoginClick() {
        showProgress();
        socialAuth.login(SocialAuth.SocialType.FACEBOOK);
    }

    @OnClick(R.id.button_google_login)
    void onGoogleLoginClick() {
        showProgress();
        socialAuth.login(SocialAuth.SocialType.GOOGLE);
    }

}
