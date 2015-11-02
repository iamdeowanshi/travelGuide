package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.download.DownloadBuilder;
import com.ithakatales.android.download.manager.DefaultDownloader;
import com.ithakatales.android.download.manager.Downloader;

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
public class DownloadModule {

    @Provides
    @Singleton
    public Downloader provideDownloader() {
        return new DefaultDownloader();
    }

    @Provides
    @Singleton
    public DownloadBuilder provideDownloadBuilder() {
        return new DownloadBuilder();
    }

}
