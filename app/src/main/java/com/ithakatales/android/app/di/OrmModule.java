package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioDownloadRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.data.repository.realm.AttractionRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioDownloadRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioRepositoryRealm;
import com.ithakatales.android.data.repository.realm.TourDownloadRepositoryRealm;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

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
        return Realm.getInstance(context);
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
    public TourDownloadRepository provideTourDownloadRepository() {
        return new TourDownloadRepositoryRealm();
    }

    @Provides
    @Singleton
    public AudioDownloadRepository provideAudioDownloadRepository() {
        return new AudioDownloadRepositoryRealm();
    }

}
