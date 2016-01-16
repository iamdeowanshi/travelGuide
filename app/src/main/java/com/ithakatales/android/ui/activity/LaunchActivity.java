package com.ithakatales.android.ui.activity;

import android.os.Bundle;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

import butterknife.OnClick;

/**
 * @author Farhan Ali
 */
public class LaunchActivity extends BaseActivity {

    @Inject PreferenceUtil preference;
    @Inject UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();

        if (! preference.readBoolean(PreferenceUtil.FIRST_LAUNCH_DONE, false)) {
            preference.save(PreferenceUtil.FIRST_LAUNCH_DONE, true);
            startActivity(UserOnBoardActivity.class, null);
            finish();
            return;
        }

        User user = userPreference.readUser();
        if (user != null && user.isVerified()) {
            startActivity(HomeActivity.class, null);
            finish();
            return;
        }

        setContentView(R.layout.activity_launch);
    }

    @OnClick(R.id.button_login)
    void onLoginClick() {
        startActivity(LoginActivity.class, null);
    }

    @OnClick(R.id.button_sneak_peak)
    void onSneakPeakClick() {
        startActivity(HomeActivity.class, null);
    }

}
