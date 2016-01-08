package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ithakatales.android.R;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.ValidationUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class LoginActivity extends SocialLoginEnabledActivity {

    @Inject AwesomeValidation validator;
    @Inject Bakery bakery;
    @Inject ConnectivityUtil connectivityUtil;

    @Bind(R.id.input_email) EditText inputEmail;
    @Bind(R.id.input_password) EditText inputPassword;
    @Bind(R.id.view_no_network) NoNetworkView viewNoNetwork;
    @Bind(R.id.progress) ProgressBar progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        injectDependencies();
        loginPresenter.setViewInteractor(this);

        // add validations
        validator.addValidation(inputEmail, Patterns.EMAIL_ADDRESS, "Email not valid !");
        validator.addValidation(inputPassword, ValidationUtil.PASSWORD_REGEX, "Password not valid !");

        viewNoNetwork.setNetworkRetryListener(new NoNetworkView.NetworkRetryListener() {
            @Override
            public void onNetworkAvailable() {
                restartActivity();
            }

            @Override
            public void onNetworkNotAvailable() {}
        });
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(Throwable e) {
        Timber.e(e.getMessage(), e);
        bakery.toastShort("Login failed !, Verify email and password");
    }

    // onClick actions

    @OnClick(R.id.button_login)
    void onLoginClick() {
        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network connection !");
            return;
        }

        if ( ! validator.validate()) return;

        User user = new User();
        user.setEmail(inputEmail.getText().toString());
        user.setPassword(inputPassword.getText().toString());

        loginPresenter.loginNormal(user);
    }

    @OnClick(R.id.text_forgot_password)
    void onForgotPasswordClick() {
        startActivity(PasswordForgotActivity.class, null);
    }

    @OnClick(R.id.text_signup)
    void onSignupClick() {
        startActivity(RegistrationActivity.class, null);
    }

}
