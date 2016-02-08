package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ithakatales.android.R;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.RegistrationPresenter;
import com.ithakatales.android.presenter.RegistrationViewInteractor;
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.UserPreference;
import com.ithakatales.android.util.ValidationUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class RegistrationActivity extends SocialLoginEnabledActivity implements RegistrationViewInteractor {

    @Inject RegistrationPresenter registrationPresenter;
    @Inject AwesomeValidation validator;
    @Inject ConnectivityUtil connectivityUtil;
    @Inject UserPreference userPreference;
    @Inject Bakery bakery;

    @Bind(R.id.input_first_name) EditText inputFirstName;
    @Bind(R.id.input_last_name) EditText inputLastName;
    @Bind(R.id.input_email) EditText inputEmail;
    @Bind(R.id.input_password) EditText inputPassword;
    @Bind(R.id.progress) ProgressBar progress;
    @Bind(R.id.view_no_network) NoNetworkView viewNoNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        injectDependencies();
        registrationPresenter.setViewInteractor(this);
        loginPresenter.setViewInteractor(this);

        validator.addValidation(inputFirstName, ValidationUtil.PERSON_NAME_REGEX, "Invalid First Name");
        validator.addValidation(inputLastName, ValidationUtil.PERSON_NAME_REGEX, "Invalid Last Name");
        validator.addValidation(inputEmail, Patterns.EMAIL_ADDRESS, "Please enter valid email address");
        validator.addValidation(inputPassword, ValidationUtil.PASSWORD_REGEX, "Invalid Password");

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
        bakery.toastShort("This email address is an existing member of Ithaka, please select 'Login Now' link to access the Login page");
    }

    @Override
    public void onRegistrationSuccess(User user) {
        userPreference.saveUser(user);
        bakery.toastLong("Please verify your account with code sent to your email");
        startActivityClearTop(VerifyAccountActivity.class, null);
    }

    @OnClick(R.id.button_signup)
    void onSignupClick() {
        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network connection !");
            return;
        }

        if ( ! validator.validate()) {
            bakery.toastShort("Please enter the required fields to complete the membership process");
            return;
        }

        User user = new User();
        user.setFirstName(inputFirstName.getText().toString());
        user.setLastName(inputLastName.getText().toString());
        user.setEmail(inputEmail.getText().toString());
        user.setPassword(inputPassword.getText().toString());

        registrationPresenter.register(user);
    }

    @OnClick(R.id.text_login)
    void onLoginTextClick() {
        finish();
    }

}
