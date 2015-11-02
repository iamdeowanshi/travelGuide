package com.ithakatales.android.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AudioDownloadRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.manager.DownloadProgressListener;
import com.ithakatales.android.download.manager.DownloadStatusListener;
import com.ithakatales.android.download.manager.Downloadable;
import com.ithakatales.android.download.manager.Downloader;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class TourDownloadService extends Service implements DownloadProgressListener, DownloadStatusListener {

    @Inject Downloader downloader;
    @Inject DownloadBuilder downloadBuilder;

    @Inject TourDownloadRepository tourDownloadRepo;
    @Inject AudioDownloadRepository audioDownloadRepo;

    private final IBinder downloadServiceBinder = new DownloadServiceBinder();

    private Map<Long, TourDownloadObserver> tourDownloadObserverMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.instance().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return downloadServiceBinder;
    }

    public TourDownload download(Attraction attraction) {
        TourDownload tourDownload = downloadBuilder.createTourDownload(attraction);
        tourDownloadRepo.save(tourDownload);
        startDownload(tourDownload);

        return tourDownload;
    }

    public void subscribe(long attractionId, TourDownloadObserver observer) {
        tourDownloadObserverMap.put(attractionId, observer);
    }

    public void unSubscribe(long attractionId) {
        tourDownloadObserverMap.remove(attractionId);
    }

    private void startDownload(TourDownload tourDownload) {
        downloader.setProgressListener(this);
        downloader.setStatusListener(this);
        downloader.download(downloadBuilder.getDownloadablesFromTour(tourDownload));
    }

    @Override
    public void progressUpdated(Downloadable downloadable, int progress) {
        AudioDownload audioDownload = downloadBuilder.getAudioDownloadFromMap(downloadable);
        audioDownload.setStatus(DownloadStatus.DOWNLOADING);
        audioDownload.setProgress(progress);
        audioDownloadRepo.save(audioDownload);

        updateTourDownloadAndNotify(audioDownload.getTourId(), audioDownload.getStatus());
    }

    @Override
    public void success(Downloadable downloadable) {

    }

    @Override
    public void failed(Downloadable downloadable, String message) {

    }

    @Override
    public void cancelled(Downloadable downloadable, String message) {

    }

    @Override
    public void interrupted(Downloadable downloadable, String message) {

    }

    private void updateTourDownloadAndNotify(long tourId, String status) {
        TourDownload tourDownload = tourDownloadRepo.find(tourId);

        TourDownload tourDownloadUpdate = new TourDownload();
        tourDownloadUpdate.setId(tourDownload.getId());
        tourDownloadUpdate.setProgress(audioDownloadRepo.getTourDownloadProgress(tourDownload.getId()));
        tourDownloadUpdate.setStatus(status);

        tourDownloadRepo.save(tourDownloadUpdate);

        tourDownloadObserverMap.get(tourDownload.getAttractionId()).downloadStatusChanged(tourDownload);
    }

    // Service binder class
    public class DownloadServiceBinder extends Binder {

        public TourDownloadService getService() {
            return TourDownloadService.this;
        }

    }

}
