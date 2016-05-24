package com.ithakatales.android.ui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.UserPreference;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.packagename",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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
