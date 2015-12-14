package com.ithakatales.android.download.rework;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.download.DownloadStatus;
import com.ithakatales.android.download.TourStorage;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class TourDownloader {

    @Inject DownloadManager downloadManager;
    @Inject TourStorage tourStorage;

    @Inject AttractionRepository attractionRepo;
    @Inject AudioRepository audioRepo;
    @Inject ImageRepository imageRepo;

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

    public String getAttractionStatus(Attraction attraction) {
        // get all download id's - images and audios - check status for each
            // if failed : return failed
            // if downloading : return downloading
            // if completed - verify checksum
            // if checksum failed return failed
            // continue loop

        return DownloadStatus.SUCCESS;
    }

    public void listenForProgress(Attraction attraction, TourDownloadProgressListener listener) {

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
