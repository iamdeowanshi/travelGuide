package com.ithakatales.android.download;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.ImageDownload;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.download.manager.Downloadable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.RealmList;

/**
 * @author Farhan Ali
 */
public class DownloadBuilder {

    @Inject AttractionRepository attractionRepo;
    @Inject AudioRepository audioRepo;
    @Inject ImageRepository imageRepo;

    @Inject TourStorage tourStorage;

    private Map<Downloadable, AudioDownload> audioDownloadableMap = new HashMap<>();
    private Map<Downloadable, ImageDownload> imageDownloadableMap = new HashMap<>();

    public DownloadBuilder() {
        Injector.instance().inject(this);
    }

    public TourDownload createTourDownload(Attraction attraction) {
        TourDownload tourDownload = new TourDownload();

        tourDownload.setId(attraction.getId());
        tourDownload.setAttractionId(attraction.getId());
        tourDownload.setStatus(DownloadStatus.WAITING_TO_START);
        tourDownload.setAudioDownloads(createAudioDownloads(attraction, tourDownload));
        tourDownload.setImageDownloads(createImageDownloads(attraction, tourDownload));
        tourDownload.setProgress(0);

        return tourDownload;
    }

    public AudioDownload getAudioDownloadFromMap(Downloadable downloadable) {
        return audioDownloadableMap.get(downloadable);
    }

    public ImageDownload getImageDownloadFromMap(Downloadable downloadable) {
        return imageDownloadableMap.get(downloadable);
    }

    public boolean removeFromMap(Downloadable downloadable) {
        return audioDownloadableMap.remove(downloadable) != null
                || imageDownloadableMap.remove(downloadable) != null;
    }

    public List<Downloadable> getDownloadablesForTourAudios(TourDownload tourDownload) {
        List<Downloadable> downloadables = new ArrayList<>();

        for (AudioDownload audioDownload : tourDownload.getAudioDownloads()) {
            Audio audio = audioRepo.find(audioDownload.getAudioId());

            Downloadable downloadable = new Downloadable();
            downloadable.setTitle(audio.getName());
            downloadable.setDestination(getAudioPath(audio, tourDownload.getId()));
            downloadable.setUrl(audio.getEncUrl());

            downloadables.add(downloadable);
            audioDownloadableMap.put(downloadable, audioDownload);

            audioRepo.updatePath(audio.getId(), downloadable.getDestination());
        }

        return downloadables;
    }

    public List<Downloadable> getDownloadablesForTourImages(TourDownload tourDownload) {
        List<Downloadable> downloadables = new ArrayList<>();

        for (ImageDownload imageDownload : tourDownload.getImageDownloads()) {
            Image image = imageRepo.find(imageDownload.getImageId());

            Downloadable downloadable = new Downloadable();
            downloadable.setTitle(image.getName());
            downloadable.setDestination(getImagePath(image, tourDownload.getId()));
            downloadable.setUrl(image.getUrl());

            downloadables.add(downloadable);
            imageDownloadableMap.put(downloadable, imageDownload);

            imageRepo.updatePath(image.getId(), downloadable.getDestination());
        }

        return downloadables;
    }

    public Downloadable getFeaturedImageDownloadable(Attraction attraction) {
        Downloadable downloadable = new Downloadable();
        downloadable.setUrl(attraction.getFeaturedImageUrl());
        downloadable.setTitle(attraction.getName() + " featured");
        String fileName = "/featured" + getExtensionFromUrl(attraction.getFeaturedImageUrl());
        downloadable.setDestination(tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName);

        attractionRepo.updateFeaturedImagePath(attraction.getId(), downloadable.getDestination());

        return downloadable;
    }

    public Downloadable getBluePrintImageDownloadable(Attraction attraction) {
        Downloadable downloadable = new Downloadable();
        downloadable.setUrl(attraction.getBlueprintUrl());
        downloadable.setTitle(attraction.getName() + " blueprint");
        String fileName = "/blueprint" + getExtensionFromUrl(attraction.getBlueprintUrl());
        downloadable.setDestination(tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName);

        attractionRepo.updateBlueprintPath(attraction.getId(), downloadable.getDestination());

        return downloadable;
    }

    public Downloadable getPreviewAudioDownloadable(Attraction attraction) {
        Downloadable downloadable = new Downloadable();
        downloadable.setUrl(attraction.getPreviewAudioUrl());
        downloadable.setTitle(attraction.getName() + " preview");
        String fileName = "/preview" + getExtensionFromUrl(attraction.getPreviewAudioUrl());
        downloadable.setDestination(tourStorage.getTourDir(attraction.getId()).getAbsolutePath() + fileName);

        attractionRepo.updatePreviewPath(attraction.getId(), downloadable.getDestination());

        return downloadable;
    }

    private RealmList<AudioDownload> createAudioDownloads(Attraction attraction, TourDownload tourDownload) {
        RealmList<AudioDownload> audioDownloads = new RealmList<>();

        // creating attraction audio downloads
        for (Audio audio : attraction.getAudios()) {
            audioDownloads.add(createAudioDownload(audio, tourDownload));
        }

        // creating poi audio downloads
        for (Poi poi : attraction.getPois()) {
            audioDownloads.add(createAudioDownload(poi.getAudio(), tourDownload));
        }

        return audioDownloads;
    }

    private AudioDownload createAudioDownload(Audio audio, TourDownload tourDownload) {
        AudioDownload audioDownload = new AudioDownload();

        audioDownload.setId(audio.getId());
        audioDownload.setAudioId(audio.getId());
        audioDownload.setTourId(tourDownload.getId());
        audioDownload.setStatus(DownloadStatus.WAITING_TO_START);
        audioDownload.setProgress(0);

        return audioDownload;
    }

    private RealmList<ImageDownload> createImageDownloads(Attraction attraction, TourDownload tourDownload) {
        RealmList<ImageDownload> imageDownloads = new RealmList<>();

        // creating attraction image downloads
        imageDownloads.addAll(createImageDownloads(attraction.getImages(), tourDownload));

        // creating attraction audio image downloads
        for (Audio audio : attraction.getAudios()) {
            imageDownloads.addAll(createImageDownloads(audio.getImages(), tourDownload));
        }

        // creating poi image & audio image downloads
        for (Poi poi : attraction.getPois()) {
            imageDownloads.addAll(createImageDownloads(poi.getImages(), tourDownload));
            imageDownloads.addAll(createImageDownloads(poi.getAudio().getImages(), tourDownload));
        }

        return imageDownloads;
    }

    private RealmList<ImageDownload> createImageDownloads(List<Image> images, TourDownload tourDownload) {
        RealmList<ImageDownload> imageDownloads = new RealmList<>();

        for (Image image : images) {
            imageDownloads.add(createImageDownload(image, tourDownload));
        }

        return imageDownloads;
    }

    private ImageDownload createImageDownload(Image image, TourDownload tourDownload) {
        ImageDownload imageDownload = new ImageDownload();

        imageDownload.setId(image.getId());
        imageDownload.setImageId(image.getId());
        imageDownload.setTourId(tourDownload.getId());
        imageDownload.setStatus(DownloadStatus.WAITING_TO_START);
        imageDownload.setProgress(0);

        return imageDownload;
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
            return url.substring(url.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

}
