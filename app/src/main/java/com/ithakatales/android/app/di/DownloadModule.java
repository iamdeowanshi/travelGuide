package com.ithakatales.android.app.di;

import com.ithakatales.android.download.DownloadBuilder;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.TourStorage;
import com.ithakatales.android.download.manager.DefaultDownloader;
import com.ithakatales.android.download.manager.Downloader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides
    @Singleton
    public TourDownloader provideTourDownloader() {
        return new TourDownloader();
    }

    @Provides
    @Singleton
    public TourStorage provideTourStorage() {
        return new TourStorage();
    }

}
