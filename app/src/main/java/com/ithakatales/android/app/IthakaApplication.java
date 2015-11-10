package com.ithakatales.android.app;

import android.app.Application;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.app.di.RootModule;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class IthakaApplication extends Application {

    @Inject Bakery bakery;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create module to make it ready for the injection
        Injector.instance().createModule(new RootModule(this));

        Injector.instance().inject(this);

        // Plant Timber tree for Logging
        Timber.plant(new Timber.DebugTree());
    }

}
