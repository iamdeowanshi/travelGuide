package com.ithakatales.android.app.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ithakatales.android.R;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.IthakaApplication;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.ui.activity.LoginActivity;
import com.ithakatales.android.ui.activity.SettingsActivity;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Provides some basic operations, all activities should extend this class.
 *
 * @author Farhan Ali
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject Bakery bakery;
    @Inject UserPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();

        /*if (Config.ORIENTATION_PORTRAIT_ONLY) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        trackToGoogleAnalytics();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bindViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (preference.readUser() != null) {
            menu.findItem(R.id.action_login).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(SettingsActivity.class, null);
                break;
            case R.id.action_send_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Config.FEEDBACK_EMAIL_TO));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, Config.FEEDBACK_SUBJECT);
                startActivity(Intent.createChooser(emailIntent, "Share Ithakatales Feedback"));
                break;
            case R.id.action_login:
                startActivityClearTop(LoginActivity.class, null);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Injects all of dependencies.
     */
    protected void injectDependencies() {
        Injector.instance().inject(this);
    }

    /**
     * Binds the ViewInteractor objects within the activity.
     */
    protected void bindViews() {
        ButterKnife.bind(this);
    }

    /**
     * Starts an activity with a bundle set to the intent.
     *
     * @param activityClass Class<? extends Activity>
     * @param bundle Bundle
     */
    protected void startActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);

        if (bundle != null) intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * Start an activity by clearing all previous activities.
     *
     * @param activityClass Class<? extends Activity>
     * @param bundle Bundle
     */
    protected void startActivityClearTop(Class<? extends Activity> activityClass, Bundle bundle) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (bundle != null) intent.putExtras(bundle);

        startActivity(intent);
        //finish();
    }

    /**
     * Restarts an activity
     */
    protected void restartActivity() {
        finish();
        startActivity(getIntent());
    }

    /**
     * Get the content view of an activity.
     *
     * @return
     */
    protected View getContentView() {
        return findViewById(android.R.id.content);
    }

    /**
     * Google analytics tracker
     */
    private void trackToGoogleAnalytics() {
        Tracker tracker = ((IthakaApplication)getApplication()).getGoogleAnalyticsTracker();
        tracker.setScreenName(getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
