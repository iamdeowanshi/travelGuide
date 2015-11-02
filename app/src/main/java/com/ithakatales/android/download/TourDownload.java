package com.ithakatales.android.download;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Farhan Ali
 */
public class TourDownload extends RealmObject {

    @PrimaryKey
    private long id;
    private long attractionId;
    private int progress;
    private String status;
    private RealmList<AudioDownload> audioDownloads;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RealmList<AudioDownload> getAudioDownloads() {
        return audioDownloads;
    }

    public void setAudioDownloads(RealmList<AudioDownload> audioDownloads) {
        this.audioDownloads = audioDownloads;
    }

}
