package com.ithakatales.android.download.model;

/**
 * @author farhanali
 */
public class AudioDownloadProgress extends DownloadProgress<AudioDownloadProgress> {

    private long audioId;
    private String audioName;
    private String url;
    private String path;

    public long getAudioId() {
        return audioId;
    }

    public AudioDownloadProgress setAudioId(long audioId) {
        this.audioId = audioId;

        return this;
    }

    public String getAudioName() {
        return audioName;
    }

    public AudioDownloadProgress setAudioName(String audioName) {
        this.audioName = audioName;

        return this;
    }

    public String getUrl() {
        return url;
    }

    public AudioDownloadProgress setUrl(String url) {
        this.url = url;

        return this;
    }

    public String getPath() {
        return path;
    }

    public AudioDownloadProgress setPath(String path) {
        this.path = path;

        return this;
    }

}