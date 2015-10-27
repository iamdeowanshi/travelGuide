package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class OrmModule {

    @Provides
    @Singleton
    public Realm provideRealm(Context context) {
        /*RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .name(Config.DB_NAME)
                .schemaVersion(Config.DB_VERSION)
                .build();*/

        return Realm.getInstance(context);
    }

}
