package com.ithakatales.android.download.rework;

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
    private Map<Long, Integer> progressMap = new HashMap<>();

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

    public Map<Long, Integer> getProgressMap() {
        return progressMap;
    }

    public void addProgressToMap(long downloadId, int progress) {
        progressMap.put(downloadId, progress);
    }

}
