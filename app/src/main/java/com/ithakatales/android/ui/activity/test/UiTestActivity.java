package com.ithakatales.android.ui.activity.test;

import android.os.Bundle;
import android.widget.Button;

import com.ithakatales.android.ui.activity.ForgotPasswordActivity;
import com.ithakatales.android.R;
import com.ithakatales.android.ui.activity.RegisterActivity;
import com.ithakatales.android.ui.activity.ResetPasswordActivity;
import com.ithakatales.android.ui.activity.SettingsActivity;
import com.ithakatales.android.ui.activity.VerifyAccount;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.ui.activity.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UiTestActivity extends BaseActivity {

    @Bind(R.id.btn_ui_login) Button btnUiLogin;
    @Bind(R.id.btn_ui_register) Button btnUiRegister;
    @Bind(R.id.btn_ui_reset_password) Button btnUiResetPassword;
    @Bind(R.id.btn_ui_forgot_password) Button btnUiForgotPassword;
    @Bind(R.id.btn_ui_verify_account) Button btnUiVerifyAccount;
    @Bind(R.id.btn_ui_end_tour) Button btnUiEndTour;
    @Bind(R.id.btn_ui_settings) Button btnUiSettings;
    @Bind(R.id.btn_ui_user_boarding) Button btnUiUserBoarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_ui);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_ui_login)
    void setBtnUiLogin() {
        startActivity(LoginActivity.class, null);
    }

    @OnClick(R.id.btn_ui_register)
    void setBtnUiRegister() {
        startActivity(RegisterActivity.class, null);
    }

    @OnClick(R.id.btn_ui_reset_password)
    void setBtnUiResetPassword() {
        startActivity(ResetPasswordActivity.class, null);
    }

    @OnClick(R.id.btn_ui_forgot_password)
    void setBtnUiForgotPassword() {
        startActivity(ForgotPasswordActivity.class, null);
    }

    @OnClick(R.id.btn_ui_verify_account)
    void setBtnUiVerifyAccount() {
        startActivity(VerifyAccount.class, null);
    }

    @OnClick(R.id.btn_ui_settings)
    void setBtnUiSettings() {
        startActivity(SettingsActivity.class, null);
    }

}
