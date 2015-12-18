package com.ithakatales.android.download;

import android.app.DownloadManager;
import android.net.Uri;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.download.model.AudioDownloadProgress;
import com.ithakatales.android.download.model.ImageDownloadProgress;
import com.ithakatales.android.download.model.TourDownloadProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourDownloader {

    @Inject DownloadManager downloadManager;
    @Inject TourDownloadProgressReader progressReader;
    @Inject TourStorage tourStorage;

    @Inject AttractionRepository attractionRepo;
    @Inject AudioRepository audioRepo;
    @Inject ImageRepository imageRepo;

    private Map<Long, TourDownloadProgressObserver> progressObserverMap = new HashMap<>();

    public TourDownloader() {
        Injector.instance().inject(this);
    }

    public void download(Attraction attraction) {
        attractionRepo.save(attraction);

        downloadBlueprint(attraction);
        downloadFeaturedImage(attraction);
        downloadPreviewAudio(attraction);

        downloadImages(attraction.getImages(), attraction.getId());
        downloadAudios(attraction.getAudios(), attraction.getId());

        for (Poi poi : attraction.getPois()) {
            downloadAudio(poi.getAudio(), attraction.getId());
            downloadImages(poi.getImages(), attraction.getId());
        }
    }

    public void retryDownload(Attraction attraction) {
        TourDownloadProgress downloadProgress = readProgress(attraction.getId());

        for (AudioDownloadProgress audioDownload: downloadProgress.getAudioDownloadProgresses()) {
            if (audioDownload.getStatus() == DownloadManager.STATUS_FAILED) {
                long newDownloadId = enqueueDownload(audioDownload.getAudioName(), audioDownload.getUrl(), audioDownload.getPath());
                audioRepo.updateDownloadId(audioDownload.getAudioId(), newDownloadId);
                Timber.d(String.format("Retrying download: %d | %s | %s", newDownloadId, audioDownload.getAudioName(), audioDownload.getUrl()));
            }
        }

        for (ImageDownloadProgress imageDownload: downloadProgress.getImageDownloadProgresses()) {
            if (imageDownload.getStatus() == DownloadManager.STATUS_FAILED) {
                long newDownloadId = enqueueDownload(imageDownload.getImageName(), imageDownload.getUrl(), imageDownload.getPath());
                imageRepo.updateDownloadId(imageDownload.getImageId(), newDownloadId);
                Timber.d(String.format("Retrying download: %d | %s | %s", newDownloadId, imageDownload.getImageName(), imageDownload.getUrl()));
            }
        }
    }

    public void update(Attraction attraction) {
        // get updated and download again

    }

    public TourDownloadProgress readProgress(long attractionId) {
        return progressReader.readProgress(attractionId);
    }

    public void startProgressListening(final long attractionId, final TourDownloadProgressListener listener) {
        TourDownloadProgressObserver observer = new TourDownloadProgressObserver(attractionId, listener);
        progressObserverMap.put(attractionId, observer);
        observer.startWatching();
    }

    public void stopProgressListening(long attractionId) {
        TourDownloadProgressObserver observer = progressObserverMap.get(attractionId);

        if (observer != null) {
            observer.stopWatching();
            progressObserverMap.remove(attractionId);
        }
    }

    private void downloadBlueprint(Attraction attraction) {
        String title = attraction.getName() + " blueprint";
        String fileName = "/blueprint" + getExtensionFromUrl(attraction.getBlueprintUrl());
        String destination = tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName;
        enqueueDownload(title, attraction.getBlueprintUrl(), destination);

        attractionRepo.updateBlueprintPath(attraction.getId(), destination);
    }

    private void downloadFeaturedImage(Attraction attraction) {
        Image featuredImage = attraction.getFeaturedImage();

        if (featuredImage == null) return;

        String fileName = "/featured" + getExtensionFromUrl(featuredImage.getUrl());
        String destination = tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName;
        long downloadId = enqueueDownload(featuredImage.getName(), featuredImage.getUrl(), destination);

        updateImageDownloadInfo(featuredImage.getId(), downloadId, destination);
    }

    private void downloadPreviewAudio(Attraction attraction) {
        Audio previewAudio = attraction.getPreviewAudio();

        if (previewAudio == null) return;

        String fileName = "/preview" + getExtensionFromUrl(previewAudio.getEncUrl());
        String destination = tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName;
        long downloadId = enqueueDownload(previewAudio.getName(), previewAudio.getEncUrl(), destination);

        updateAudioDownloadInfo(previewAudio.getId(), downloadId, destination);
    }

    private void downloadAudios(List<Audio> audios, long tourId) {
        if (audios == null) return;

        for (Audio audio : audios) {
            downloadAudio(audio, tourId);
        }
    }

    private void downloadAudio(Audio audio, long tourId) {
        if (audio == null) return;

        // download audio
        String destination = getAudioPath(audio, tourId);
        long downloadId = enqueueDownload(audio.getName(), audio.getEncUrl(), destination);

        updateAudioDownloadInfo(audio.getId(), downloadId, destination);

        // download images within an audio
        downloadImages(audio.getImages(), tourId);
    }

    private void downloadImages(List<Image> images, long tourId) {
        if (images == null) return;

        for (Image image : images) {
            downloadImage(image, tourId);
        }
    }

    private void downloadImage(Image image, long tourId) {
        if (image == null) return;

        String destination = getImagePath(image, tourId);
        long downloadId = enqueueDownload(image.getName(), image.getUrl(), destination);

        updateImageDownloadInfo(image.getId(), downloadId, destination);
    }

    private long enqueueDownload(String title, String url, String destination) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDestinationUri(Uri.fromFile(new File(destination)))
                .setVisibleInDownloadsUi(false)
                .setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI
                        | android.app.DownloadManager.Request.NETWORK_MOBILE);

        return downloadManager.enqueue(request);
    }

    private String getAudioPath(Audio audio, long tourId) {
        String audioName = audio.getName().replace(" ", "_");
        return tourStorage.getAudioDir(tourId)
                .getAbsolutePath() + "/" + audio.getId() + "_" + audioName;
    }

    private String getImagePath(Image image, long tourId) {
        String imageName = image.getName().replace(" ", "_");
        return tourStorage.getImagesDir(tourId)
                .getAbsolutePath() + "/" + image.getId() + "_" + imageName + getExtensionFromUrl(image.getUrl());
    }

    private String getExtensionFromUrl(String url) {
        try {
            String extension = url.substring(url.lastIndexOf("."));
            int ampersandIndex = extension.indexOf("&");

            return ampersandIndex != -1
                    ? extension.substring(0, ampersandIndex)
                    : extension;
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    private void updateAudioDownloadInfo(long audioId, long downloadId, String path) {
        audioRepo.updateDownloadId(audioId, downloadId);
        audioRepo.updatePath(audioId, path);
    }

    private void updateImageDownloadInfo(long imageId, long downloadId, String path) {
        imageRepo.updateDownloadId(imageId, downloadId);
        imageRepo.updatePath(imageId, path);
    }

}
