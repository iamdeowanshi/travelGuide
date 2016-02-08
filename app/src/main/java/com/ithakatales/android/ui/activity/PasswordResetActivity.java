package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.PasswordResetPresenter;
import com.ithakatales.android.presenter.PasswordResetViewInteractor;
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
public class PasswordResetActivity extends BaseActivity implements PasswordResetViewInteractor {

    @Inject PasswordResetPresenter presenter;
    @Inject AwesomeValidation validator;
    @Inject UserPreference userPreference;
    @Inject ConnectivityUtil connectivityUtil;
    @Inject Bakery bakery;

    @Bind(R.id.input_temp_password) EditText inputTempPassword;
    @Bind(R.id.input_new_password) EditText inputNewPassword;
    @Bind(R.id.input_confirm_password) EditText inputConfirmPassword;
    @Bind(R.id.progress) ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        injectDependencies();
        presenter.setViewInteractor(this);

        validator.addValidation(inputTempPassword, ValidationUtil.VERIFY_CODE_REGEX, "Invalid code !");
        validator.addValidation(inputNewPassword, ValidationUtil.PASSWORD_REGEX, "Invalid password !");
        validator.addValidation(inputConfirmPassword, ValidationUtil.PASSWORD_REGEX, "Invalid password !");
    }

    @Override
    public void onPasswordResetSuccess(User user) {
        userPreference.saveUser(user);
        bakery.toastShort("Your password has been successfully reset");
        startActivityClearTop(HomeActivity.class, null);
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
        bakery.toastShort("Passwords do not match, please re-enter the passwords");
    }

    @OnClick(R.id.button_reset)
    void onResetClick() {
        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network connection !");
            return;
        }

        if ( ! validator.validate()) return;

        if ( ! inputNewPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
            inputConfirmPassword.setError("Password not matching !");
            return;
        }

        String email = getIntent().getExtras().getString("email");
        presenter.resetPassword(email, inputTempPassword.getText().toString(), inputNewPassword.getText().toString());
    }

    @OnClick(R.id.text_back)
    void onBackClick() {
        startActivityClearTop(LoginActivity.class, null);
    }

}
