package com.ithakatales.android.download.model;

import android.app.DownloadManager;

import com.ithakatales.android.util.Checksum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Farhan Ali
 */
public class TourDownloadProgress extends DownloadProgress<TourDownloadProgress> {

    private long attractionId;
    private int audioProgress;
    private long audioBytesTotal;
    private long audioBytesDownloaded;
    private int imageProgress;
    private int imageBytesTotal;
    private int imageBytesDownloaded;

    private List<AudioDownloadProgress> audioDownloadProgresses = new ArrayList<>();
    private List<ImageDownloadProgress> imageDownloadProgresses = new ArrayList<>();

    public TourDownloadProgress() {
        // assume status as success initially, will change if any one is RUNNING or FAILED
        status = DownloadManager.STATUS_SUCCESSFUL;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public TourDownloadProgress setAttractionId(long attractionId) {
        this.attractionId = attractionId;

        return this;
    }

    public List<AudioDownloadProgress> getAudioDownloadProgresses() {
        return audioDownloadProgresses;
    }

    public TourDownloadProgress setAudioDownloadProgresses(List<AudioDownloadProgress> audioDownloadProgresses) {
        this.audioDownloadProgresses = audioDownloadProgresses;

        return this;
    }

    public List<ImageDownloadProgress> getImageDownloadProgresses() {
        return imageDownloadProgresses;
    }

    public TourDownloadProgress setImageDownloadProgresses(List<ImageDownloadProgress> imageDownloadProgresses) {
        this.imageDownloadProgresses = imageDownloadProgresses;

        return this;
    }

    public int getAudioProgress() {
        return audioProgress;
    }

    public long getAudioBytesTotal() {
        return audioBytesTotal;
    }

    public long getAudioBytesDownloaded() {
        return audioBytesDownloaded;
    }

    public int getImageProgress() {
        return imageProgress;
    }

    public int getImageBytesTotal() {
        return imageBytesTotal;
    }

    public int getImageBytesDownloaded() {
        return imageBytesDownloaded;
    }

    public void updateProgressInfo() {
        for (AudioDownloadProgress audioDownload :  audioDownloadProgresses) {
            updateStatus(audioDownload, audioDownload.getUrl(), audioDownload.getPath());

            if (audioDownload.getStatus() == DownloadManager.STATUS_FAILED) continue;

            audioBytesTotal += audioDownload.getBytesTotal();
            audioBytesDownloaded += audioDownload.getBytesDownloaded();
        }

        for (ImageDownloadProgress imageDownload : imageDownloadProgresses) {
            updateStatus(imageDownload, imageDownload.getUrl(), imageDownload.getPath());

            if (imageDownload.getStatus() == DownloadManager.STATUS_FAILED) continue;

            imageBytesTotal += imageDownload.getBytesTotal();
            imageBytesDownloaded += imageDownload.getBytesDownloaded();
        }

        try {
            audioProgress = (int) ((audioBytesDownloaded * 100l) / audioBytesTotal);
            imageProgress = (int) ((imageBytesDownloaded * 100l) / imageBytesTotal);

            bytesTotal = imageBytesTotal + audioBytesTotal;
            bytesDownloaded = imageBytesDownloaded + audioBytesDownloaded;
            progress = (int) ((bytesDownloaded * 100l) / bytesTotal);
            progress = progress > 100 ? 100 : progress;
        } catch (Exception e) {
            // Some arithmetic exception when initial stage of download - nothing todo
        }
    }

    public int getDownloadedImageCount() {
        int count = 0;

        for (ImageDownloadProgress download : imageDownloadProgresses) {
            count += download.getProgress() == 100 ? 1 : 0;
        }

        return count;
    }

    // TODO: 15/12/15 code readability should be improved
    private void updateStatus(DownloadProgress downloadProgress, String url, String filePath) {
        // don't change if already running or failed
        if (status == DownloadManager.STATUS_RUNNING  || status == DownloadManager.STATUS_FAILED) return;

        switch (downloadProgress.getStatus()) {
            case DownloadManager.STATUS_RUNNING:
            case DownloadManager.STATUS_PAUSED:
            case DownloadManager.STATUS_PENDING:
                status = DownloadManager.STATUS_RUNNING;
                break;
            case DownloadManager.STATUS_FAILED:
                status = DownloadManager.STATUS_FAILED;
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                //status = DownloadManager.STATUS_SUCCESSFUL;
                status = getStatusAfterChecksumVerification(url, filePath);
                downloadProgress.setStatus(status);
                break;
        }
    }

    private int getStatusAfterChecksumVerification(String url, String filePath) {
        return verifyCheckSum(url, filePath)
                ? DownloadManager.STATUS_SUCCESSFUL
                : DownloadManager.STATUS_FAILED;
    }

    private boolean verifyCheckSum(String url, String filePath) {
        String checkSum = getCheckSumFromUrl(url);

        return (checkSum != null) && Checksum.checkSHA1(checkSum, new File(filePath));
    }

    private String getCheckSumFromUrl(String url) {
        try {
            return url.split("-")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

}

