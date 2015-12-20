package com.ithakatales.android.download;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.download.model.TourDownloadProgress;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class TourDownloadProgressObserver extends Thread {

    @Inject TourDownloadProgressReader progressReader;

    private TourDownloadProgressListener listener;
    private long attractionId;

    private boolean stopWatching = false;

    public TourDownloadProgressObserver(long attractionId, TourDownloadProgressListener listener) {
        Injector.instance().inject(this);
        this.attractionId = attractionId;
        this.listener = listener;
    }

    @Override
    public void run() {
        while ( ! stopWatching) {
            TourDownloadProgress progress = progressReader.readProgress(attractionId);
            listener.onProgressChange(progress);
        }
    }

    public void startWatching() {
        start();
    }

    public void stopWatching() {
        stopWatching = true;
    }

}
