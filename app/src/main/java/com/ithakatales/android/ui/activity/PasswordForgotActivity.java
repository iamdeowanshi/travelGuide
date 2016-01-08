package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.presenter.PasswordForgotPresenter;
import com.ithakatales.android.presenter.PasswordForgotViewInteractor;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class PasswordForgotActivity extends BaseActivity implements PasswordForgotViewInteractor {

    @Inject PasswordForgotPresenter presenter;
    @Inject AwesomeValidation validator;
    @Inject UserPreference userPreference;
    @Inject ConnectivityUtil connectivityUtil;
    @Inject Bakery bakery;

    @Bind(R.id.input_email) EditText inputEmail;
    @Bind(R.id.progress) ProgressBar progress;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        injectDependencies();
        presenter.setViewInteractor(this);

        validator.addValidation(inputEmail, Patterns.EMAIL_ADDRESS, "Email not valid !");
    }

    @Override
    public void onPasswordForgotRequested() {
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        startActivity(PasswordResetActivity.class, bundle);
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
        bakery.toastShort("Request failed !, Confirm email is correct");
    }

    @OnClick(R.id.button_continue)
    void onContinueClick() {
        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network connection !");
            return;
        }

        if ( ! validator.validate()) return;

        email = inputEmail.getText().toString();
        presenter.requestPasswordForgot(email);
    }

}
