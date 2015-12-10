package com.ithakatales.android.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.ui.activity.test.TestActivity;

import butterknife.OnClick;

/**
 * @author Farhan Ali
 */
public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @OnClick(R.id.button_login)
    void launchLogin() {
        launchActivity(TestActivity.class);
    }

    @OnClick(R.id.button_sneak_peak)
    void launchHomeAsGuest() {
        launchActivity(HomeActivity.class);
    }

    private void launchActivity(Class<? extends Activity> activityClass) {
        startActivity(activityClass, null);
        //finish();
    }

}
