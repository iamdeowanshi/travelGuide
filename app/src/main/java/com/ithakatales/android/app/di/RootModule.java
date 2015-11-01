package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.app.IthakaApplication;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.ui.activity.ApiTestActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Include all other modules, provide Context dependency. All activity, fragment, presenter and
 * any classes that are going to use dependency injection should be registered here.
 *
 * @author Farhan Ali
 */
@Module(
        includes = {
                PresenterModule.class,
                ApiModule.class,
                OrmModule.class
                UtilModule.class
        },
        injects = {
                IthakaApplication.class,

                // View specific classes - activities, fragments, adapters etc
                ApiTestActivity.class,

                // Presenters
                SamplePresenterImpl.class
        }
)
public class RootModule {

    private Context context;

    public RootModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context;
    }

}
