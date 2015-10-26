package com.ithakatales.android.app.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ithakatales.android.app.di.Injector;

import butterknife.ButterKnife;

/**
 * Provides some basic operations, all activities should extend this class.
 *
 * @author Farhan Ali
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bindViews();
    }

    /**
     * Injects all of dependencies.
     */
    protected void injectDependencies() {
        Injector.instance().inject(this);
    }

    /**
     * Binds the viewInteractor objects within the activity.
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
     * Shows a LENGTH_SHORT toast message.
     *
     * @param message
     */
    protected void toastShort(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Shows a LENGTH_LONG toast message.
     *
     * @param message
     */
    protected void toastLong(String message) {
        toast(message, Toast.LENGTH_LONG);
    }

    /**
     * Shows a toast message.
     *
     * @param message
     * @param length
     */
    protected void toast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    /**
     * Return class name as TAG.
     *
     * @return String tag
     */
    public String tag() {
        return getClass().getName();
    }

}
