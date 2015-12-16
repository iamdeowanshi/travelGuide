package com.ithakatales.android.download.model;

import com.ithakatales.android.data.model.Audio;

/**
 * @author farhanali
 */
public class AudioDownloadProgress extends DownloadProgress<AudioDownloadProgress> {

    private Audio audio;

    public Audio getAudio() {
        return audio;
    }

    public AudioDownloadProgress setAudio(Audio audio) {
        this.audio = audio;

        return this;
    }

}