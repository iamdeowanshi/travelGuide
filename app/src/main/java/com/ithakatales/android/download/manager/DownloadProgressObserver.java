package com.ithakatales.android.download.manager;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class DownloadProgressObserver extends ContentObserver {

    private Downloadable downloadable;
    private DownloadProgressListener progressListener;
    private DownloadManager downloadManager;

    public DownloadProgressObserver(
            Downloadable downloadable, DownloadManager manager,
            DownloadProgressListener listener) {
        super(new Handler());
        this.downloadable = downloadable;
        this.downloadManager = manager;
        this.progressListener = listener;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadable.getId());

        Cursor cursor = downloadManager.query(query);

        try {
            cursor.moveToFirst();

            int bytesDownloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            final int progress = (int) ((bytesDownloaded * 100l) / bytesTotal);

            downloadable.setProgress(progress);
            progressListener.progressUpdated(downloadable, progress);
        } catch (Exception e) {
            Timber.e(e.getMessage(), e);
        } finally {
            cursor.close();
        }
    }

}