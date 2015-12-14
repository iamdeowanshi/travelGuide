package com.ithakatales.android.download;

import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Farhan Ali
 */
public class TourDownloadProgress {

    private long attractionId;
    private int totalProgress;
    private int totalAudioProgress;
    private int totalImageProgress;

    private Map<Audio, Integer> audioProgressMap = new HashMap<>();
    private Map<Image, Integer> imageProgressMap = new HashMap<>();

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    public int getTotalAudioProgress() {
        return totalAudioProgress;
    }

    public void setTotalAudioProgress(int totalAudioProgress) {
        this.totalAudioProgress = totalAudioProgress;
    }

    public int getTotalImageProgress() {
        return totalImageProgress;
    }

    public void setTotalImageProgress(int totalImageProgress) {
        this.totalImageProgress = totalImageProgress;
    }

    public Map<Audio, Integer> getAudioProgressMap() {
        return audioProgressMap;
    }

    public Map<Image, Integer> getImageProgressMap() {
        return imageProgressMap;
    }

    public void addToAudioProgress(Audio audio, int progress) {
        audioProgressMap.put(audio, progress);
    }

    public void addToImageProgress(Image image, int progress) {
        imageProgressMap.put(image, progress);
    }

}
