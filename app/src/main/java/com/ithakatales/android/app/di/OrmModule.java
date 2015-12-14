package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.data.repository.realm.AttractionRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioRepositoryRealm;
import com.ithakatales.android.data.repository.realm.ImageRepositoryRealm;

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
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .name(Config.DB_NAME)
                .schemaVersion(Config.DB_VERSION)
                .build();

        return Realm.getInstance(config);
    }

    @Provides
    @Singleton
    public AttractionRepository provideAttractionRepository() {
        return new AttractionRepositoryRealm();
    }

    @Provides
    @Singleton
    public AudioRepository provideAudioRepository() {
        return new AudioRepositoryRealm();
    }

    @Provides
    @Singleton
    public ImageRepository provideImageRepository() {
        return new ImageRepositoryRealm();
    }

}
