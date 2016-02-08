package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.VerifyAccountPresenter;
import com.ithakatales.android.presenter.VerifyAccountViewInteractor;
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
public class VerifyAccountActivity extends BaseActivity implements VerifyAccountViewInteractor {

    @Inject VerifyAccountPresenter presenter;
    @Inject AwesomeValidation validator;
    @Inject UserPreference userPreference;
    @Inject ConnectivityUtil connectivityUtil;
    @Inject Bakery bakery;

    @Bind(R.id.input_verification_code) EditText inputVerificationCode;
    @Bind(R.id.progress) ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        injectDependencies();
        presenter.setViewInteractor(this);

        validator.addValidation(inputVerificationCode, ValidationUtil.VERIFY_CODE_REGEX, "Code is not valid !");
    }

    @Override
    public void onAccountVerificationSuccess(User user) {
        userPreference.saveUser(user);
        bakery.toastShort("Welcome to Ithaka, let us help you travel deeper!");
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
        bakery.toastShort("Verification code is invalid");
    }

    @OnClick(R.id.button_verify)
    void onVerifyClick() {
        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network connection !");
            return;
        }

        if ( ! validator.validate()) return;

        presenter.verifyAccount(userPreference.readUser().getEmail(), inputVerificationCode.getText().toString());
    }

}
