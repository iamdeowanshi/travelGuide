package com.ithakatales.android.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.app.di.RootModule;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class IthakaApplication extends Application {

    @Inject Bakery bakery;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));

        Injector.instance().inject(this);

        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
