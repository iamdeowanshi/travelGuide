package com.ithakatales.android.app.di;

import android.app.DownloadManager;
import android.content.Context;

import com.ithakatales.android.download.TourDownloadProgressReader;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.TourStorage;

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
    public DownloadManager provideDownloadManager(Context context) {
        return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Provides
    @Singleton
    public TourDownloader provideTourDownloader() {
        return new TourDownloader();
    }

    @Provides
    @Singleton
    public TourDownloadProgressReader provideTourDownloadProgressReader() {
        return new TourDownloadProgressReader();
    }

    @Provides
    @Singleton
    public TourStorage provideTourStorage() {
        return new TourStorage();
    }

}
