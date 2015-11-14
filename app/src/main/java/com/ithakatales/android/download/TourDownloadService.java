package com.ithakatales.android.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.model.ImageDownload;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.AudioDownloadRepository;
import com.ithakatales.android.data.repository.ImageDownloadRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.manager.DownloadProgressListener;
import com.ithakatales.android.download.manager.DownloadStatusListener;
import com.ithakatales.android.download.manager.Downloadable;
import com.ithakatales.android.download.manager.Downloader;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.Checksum;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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
    @Inject ImageDownloadRepository imageDownloadRepo;

    @Inject Bakery bakery;

    private final IBinder downloadServiceBinder = new DownloadServiceBinder();

    private Map<Long, TourDownloadObserver> tourDownloadObserverMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.instance().inject(this);

        downloader.setProgressListener(this);
        downloader.setStatusListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return downloadServiceBinder;
    }

    public void subscribe(long attractionId, TourDownloadObserver observer) {
        tourDownloadObserverMap.put(attractionId, observer);
    }

    public void unSubscribe(long attractionId) {
        tourDownloadObserverMap.remove(attractionId);
    }

    public TourDownload download(Attraction attraction) {
        TourDownload tourDownload = downloadBuilder.createTourDownload(attraction);
        tourDownloadRepo.save(tourDownload);

        List<Downloadable> downloadables = downloadBuilder.getDownloadablesForTourAudios(tourDownload);
        downloadables.addAll(downloadBuilder.getDownloadablesForTourImages(tourDownload));
        downloadables.add(downloadBuilder.getPreviewAudioDownloadable(tourDownload));
        downloadables.add(downloadBuilder.getFeaturedImageDownloadable(tourDownload));
        downloadables.add(downloadBuilder.getBluePrintImageDownloadable(attraction));

        downloader.download(downloadables);

        return tourDownload;
    }

    @Override
    public void progressUpdated(Downloadable downloadable) {
        // get status, if completed
        String status = (downloadable.getProgress() == 100)
                ? getStatusAfterChecksumVerification(downloadable)
                : DownloadStatus.DOWNLOADING;

        // update download progress & status
        long tourId = updateDownload(downloadable, status);
        updateTourDownloadAndNotify(tourId);
    }

    @Override
    public void success(Downloadable downloadable) {
        // updating success status is done in progress updated because some unexpected call-
        // is coming to progressUpdated method even after success of a download
        downloader.unregisterProgressObserver(downloadable);
    }

    @Override
    public void failed(Downloadable downloadable, String message) {
        updateDownload(downloadable, DownloadStatus.FAILED);
        downloader.unregisterProgressObserver(downloadable);
    }

    @Override
    public void cancelled(Downloadable downloadable, String message) {
        updateDownload(downloadable, DownloadStatus.FAILED);
        downloader.unregisterProgressObserver(downloadable);
    }

    @Override
    public void paused(Downloadable downloadable, String message) {
        updateDownload(downloadable, DownloadStatus.PAUSED);
    }

    private long updateDownload(Downloadable downloadable, String status) {
        // update progress and status if audio download
        AudioDownload audioDownload = downloadBuilder.getAudioDownloadFromMap(downloadable);
        if (audioDownload != null) {
            audioDownloadRepo.updateProgressAndStatus(audioDownload.getId(), downloadable.getProgress(), status);

            return audioDownload.getTourId();
        }

        // update progress and status if image download
        ImageDownload imageDownload = downloadBuilder.getImageDownloadFromMap(downloadable);
        if (imageDownload != null) {
            imageDownloadRepo.updateProgressAndStatus(imageDownload.getId(), downloadable.getProgress(), status);

            return imageDownload.getTourId();
        }

        return 0;
    }

    private void updateTourDownloadAndNotify(long tourId) {
        updateTourDownload(tourId);
        notifyTourDownloadStatusChange(tourId);
    }

    private void updateTourDownload(long tourId) {
        int totalAudioProgress  = audioDownloadRepo.getTotalProgressByTour(tourId);
        int totalImageProgress  = imageDownloadRepo.getTotalProgressByTour(tourId);
        int totalTourProgress   = (totalAudioProgress + totalImageProgress) / 2;

        String status = getTourDownloadStatus(tourId);

        tourDownloadRepo.updateProgressAndStatus(tourId, totalTourProgress, status);
    }

    private void notifyTourDownloadStatusChange(long tourId) {
        TourDownload tourDownload = tourDownloadRepo.find(tourId);
        TourDownloadObserver observer = tourDownloadObserverMap.get(tourDownload.getAttractionId());
        if (observer != null) {
            observer.downloadStatusChanged(tourDownload);
        }
    }

    private String getTourDownloadStatus(long tourId) {
        int audioFailedCount = audioDownloadRepo.readByTourAndStatus(tourId, DownloadStatus.FAILED).size();
        int imageFailedCount = imageDownloadRepo.readByTourAndStatus(tourId, DownloadStatus.FAILED).size();

        if (audioFailedCount > 0 || imageFailedCount > 0) {
            return DownloadStatus.FAILED;
        }

        int audioDownloadingCount = audioDownloadRepo.readByTourAndStatus(tourId, DownloadStatus.DOWNLOADING).size();
        int imageDownloadingCount = imageDownloadRepo.readByTourAndStatus(tourId, DownloadStatus.DOWNLOADING).size();

        if (audioDownloadingCount > 0 || imageDownloadingCount > 0) {
            return DownloadStatus.DOWNLOADING;
        }

        return DownloadStatus.SUCCESS;
    }

    private String getStatusAfterChecksumVerification(Downloadable downloadable) {
        return verifyCheckSum(downloadable)
                ? DownloadStatus.SUCCESS
                : DownloadStatus.FAILED;
    }

    private boolean verifyCheckSum(Downloadable downloadable) {
        String checkSum = getCheckSumFromUrl(downloadable.getUrl());

        return (checkSum != null) && Checksum.checkSHA1(checkSum, new File(downloadable.getDestination()));
    }

    private String getCheckSumFromUrl(String url) {
        try {
            return url.split("-")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Service binder class
     */
    public class DownloadServiceBinder extends Binder {

        public TourDownloadService getService() {
            return TourDownloadService.this;
        }

    }

}
