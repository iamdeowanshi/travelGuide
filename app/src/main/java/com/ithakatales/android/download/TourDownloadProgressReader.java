package com.ithakatales.android.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.download.model.AudioDownloadProgress;
import com.ithakatales.android.download.model.ImageDownloadProgress;
import com.ithakatales.android.download.model.TourDownloadProgress;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourDownloadProgressReader {

    @Inject RealmConfiguration realmConfig;
    @Inject DownloadManager downloadManager;

    public TourDownloadProgressReader() {
        Injector.instance().inject(this);
    }

    public TourDownloadProgress readProgress(long attractionId) {
        Attraction attraction = readAttractionFromRealm(attractionId);

        TourDownloadProgress downloadProgress = new TourDownloadProgress();

        downloadProgress.setAttractionId(attraction.getId());

        // featured image progress
        downloadProgress.getImageDownloadProgresses().add(readImageProgress(attraction.getFeaturedImage()));
        // preview audio progress
        downloadProgress.getAudioDownloadProgresses().add(readAudioProgress(attraction.getPreviewAudio()));
        // tour images progress
        downloadProgress.getImageDownloadProgresses().addAll(readImageProgresses(attraction.getImages()));

        // tour audios and it's images progress
        for (Audio audio : attraction.getAudios()) {
            downloadProgress.getAudioDownloadProgresses().add(readAudioProgress(audio));
            downloadProgress.getImageDownloadProgresses().addAll(readImageProgresses(audio.getImages()));
        }

        // poi images, audio & audio images progress
        for (Poi poi : attraction.getPois()) {
            downloadProgress.getAudioDownloadProgresses().add(readAudioProgress(poi.getAudio()));
            downloadProgress.getImageDownloadProgresses().addAll(readImageProgresses(poi.getAudio().getImages()));
            downloadProgress.getImageDownloadProgresses().addAll(readImageProgresses(poi.getImages()));
        }

        downloadProgress.updateProgressInfo();

        return downloadProgress;
    }

    private AudioDownloadProgress readAudioProgress(Audio audio) {
        ProgressInfo info = readInfo(audio.getDownloadId());

        return new AudioDownloadProgress()
                .setAudio(audio)
                .setProgress(info.progress)
                .setBytesDownloaded(info.bytesDownloaded)
                .setBytesTotal(info.bytesTotal)
                .setStatus(info.status);
    }

    private List<ImageDownloadProgress> readImageProgresses(List<Image> images) {
        List<ImageDownloadProgress> imageDownloadProgresses = new ArrayList<>();

        for (Image image : images) {
            imageDownloadProgresses.add(readImageProgress(image));
        }

        return imageDownloadProgresses;
    }

    private ImageDownloadProgress readImageProgress(Image image) {
        ProgressInfo info = readInfo(image.getDownloadId());

        return new ImageDownloadProgress()
                .setImage(image)
                .setProgress(info.progress)
                .setBytesDownloaded(info.bytesDownloaded)
                .setBytesTotal(info.bytesTotal)
                .setStatus(info.status);
    }

    private ProgressInfo readInfo(long downloadId) {
        ProgressInfo info = null;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        try {
            cursor.moveToFirst();
            // read bytes and progress
            info = new ProgressInfo();
            info.bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            info.bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            info.progress = (int) ((info.bytesDownloaded * 100l) / info.bytesTotal);
            // read status
            info.status = cursor.getInt(cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS));
        } catch (Exception e) {
            Timber.e(e.getMessage(), e);
        } finally {
            cursor.close();
        }

        return info;
    }

    /**
     * To read attraction from realm, since Progress reader is running in another thread - inside file observer
     * We cannot pass Attraction object directly or can't use repository
     *
     * @param attractionId
     * @return
     */
    private Attraction readAttractionFromRealm(long attractionId) {
        return Realm.getInstance(realmConfig)
                .where(Attraction.class)
                .equalTo("id", attractionId)
                .findFirst();
    }

    private class ProgressInfo {
        int progress;
        long bytesTotal;
        long bytesDownloaded;
        int status;
    }

}
