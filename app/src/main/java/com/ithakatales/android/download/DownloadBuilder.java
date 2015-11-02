package com.ithakatales.android.download;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.download.manager.Downloadable;
import com.ithakatales.android.util.StorageUtil;

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

    @Inject AudioRepository audioRepo;
    @Inject StorageUtil storageUtil;

    private Map<Downloadable, AudioDownload> audioDownloadableMap = new HashMap<>();

    public DownloadBuilder() {
        Injector.instance().inject(this);
    }

    public List<Downloadable> getDownloadablesFromTour(TourDownload tourDownload) {
        List<Downloadable> downloadables = new ArrayList<>();

        for (AudioDownload audioDownload : tourDownload.getAudioDownloads()) {
            Downloadable downloadable = new Downloadable();

            Audio audio = audioRepo.find(audioDownload.getAudioId());
            downloadable.setTitle(audio.getName());
            downloadable.setDestination(
                    storageUtil.getIthakaFolderPath() + "/atr_" + tourDownload.getId() + "_aud_" + audio.getId());
            downloadable.setUrl(audio.getEncUrl());

            downloadables.add(downloadable);
            audioDownloadableMap.put(downloadable, audioDownload);
        }

        return downloadables;
    }

    public TourDownload createTourDownload(Attraction attraction) {
        TourDownload tourDownload = new TourDownload();

        tourDownload.setId(attraction.getId());
        tourDownload.setAttractionId(attraction.getId());
        tourDownload.setStatus(DownloadStatus.WAITING_TO_START);
        tourDownload.setAudioDownloads(createAudioDownloads(attraction, tourDownload));
        tourDownload.setProgress(0);

        return tourDownload;
    }

    public AudioDownload getAudioDownloadFromMap(Downloadable downloadable) {
        return audioDownloadableMap.get(downloadable);
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


}
