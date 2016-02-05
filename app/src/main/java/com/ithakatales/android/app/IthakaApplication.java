package com.ithakatales.android.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.app.di.RootModule;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.util.UserPreference;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class IthakaApplication extends Application {

    private Tracker googleAnalyticsTracker;

    @Inject UserPreference userPreference;
    @Inject TourDownloader tourDownloader;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));

        Injector.instance().inject(this);

        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());

        // Universal image loader setup
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCache(new WeakMemoryCache()).build();

        ImageLoader.getInstance().init(config);

        // removing the tours if any if there is no user
        if (userPreference.readUser() == null && userPreference.readPreviousUser() == null) {
            tourDownloader.clearAll();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getGoogleAnalyticsTracker() {
        if (googleAnalyticsTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            googleAnalyticsTracker = analytics.newTracker(R.xml.global_tracker);
        }

        return googleAnalyticsTracker;
    }

}
