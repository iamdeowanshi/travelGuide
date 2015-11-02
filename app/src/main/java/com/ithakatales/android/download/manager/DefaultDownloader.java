package com.ithakatales.android.download.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import com.ithakatales.android.app.di.Injector;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class DefaultDownloader implements Downloader {

    @Inject Context context;

    private DownloadStatusListener statusListener       = DownloadStatusListener.DEFAULT_STATUS_LISTENER;
    private DownloadProgressListener progressListener   = DownloadProgressListener.DEFAULT__PROGRESS_LISTENER;

    private Map<Long, Downloadable> runningDownloadsMap = new HashMap<>();
    private Map<Downloadable, DownloadProgressObserver> progressObserverMap = new HashMap<>();

    private android.app.DownloadManager downloadManager;
    private BroadcastReceiver downloadStatusReceiver;
    private BroadcastReceiver notificationClickReceiver;

    public DefaultDownloader() {
        Injector.instance().inject(this);
        downloadManager = (android.app.DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        registerNotificationClickReciever();
        registerDownloadStatusReceiver();
    }

    @Override
    public void download(Downloadable downloadable) {
        long downloadId = downloadManager.enqueue(createRequest(downloadable));
        downloadable.setId(downloadId);
        runningDownloadsMap.put(downloadId, downloadable);
        registerProgressObserver(downloadable);
    }

    @Override
    public void download(List<Downloadable> downloadableList) {
        for (Downloadable downloadable : downloadableList) {
            download(downloadable);
        }
    }

    @Override
    public void cancel(Downloadable downloadable) {
        unregisterProgressObserver(downloadable);
        downloadManager.remove(downloadable.getId());
        runningDownloadsMap.remove(downloadable.getId());
    }

    @Override
    public void cancel(List<Downloadable> downloadableList) {
        for (Downloadable downloadable : downloadableList) {
            cancel(downloadable);
        }
    }

    @Override
    public void cancelAll() {
        // TODO: 15/10/15 CancelAll implementation
    }

    @Override
    public void setStatusListener(DownloadStatusListener listener) {
        statusListener = listener;
    }

    @Override
    public void removeStatusListener() {
        statusListener = DownloadStatusListener.DEFAULT_STATUS_LISTENER;
    }

    @Override
    public void setProgressListener(DownloadProgressListener listener) {
        progressListener = listener;
    }

    @Override
    public void removeProgressListener() {
        progressListener = DownloadProgressListener.DEFAULT__PROGRESS_LISTENER;
    }

    private android.app.DownloadManager.Request createRequest(Downloadable downloadable) {
        return new android.app.DownloadManager.Request(Uri.parse(downloadable.getUrl()))
                .setTitle(downloadable.getTitle())
                .setDescription(downloadable.getDescription())
                .setDestinationUri(Uri.fromFile(new File(downloadable.getDestination())))
                .setVisibleInDownloadsUi(true)
                .setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_WIFI
                        | android.app.DownloadManager.Request.NETWORK_MOBILE);
    }

    private void registerProgressObserver(Downloadable downloadable) {
        Uri myDownloads = Uri.parse( "content://downloads/my_downloads/" + downloadable.getId());
        DownloadProgressObserver observer = new DownloadProgressObserver(downloadable, downloadManager, progressListener);
        context.getContentResolver().registerContentObserver(myDownloads, true, observer);
        progressObserverMap.put(downloadable, observer);
    }

    private void unregisterProgressObserver(Downloadable downloadable) {
        DownloadProgressObserver observer = progressObserverMap.get(downloadable);

        if (observer != null) {
            context.getContentResolver().unregisterContentObserver(observer);
        }
    }

    // TODO: 15/10/15 Refactor

    private void registerNotificationClickReciever() {
        // filter for notifications - only acts on notification while download busy
        IntentFilter filter = new IntentFilter(android.app.DownloadManager
                .ACTION_NOTIFICATION_CLICKED);

        notificationClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long[] references = intent.getLongArrayExtra(android.app.DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                for (long reference : references) {
                    Downloadable downloadable = runningDownloadsMap.get(reference);
                    if (downloadable != null) {
                        // do something with the downloaded onclick
                    }
                }
            }
        };
        context.registerReceiver(notificationClickReceiver, filter);
    }

    private void registerDownloadStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        downloadStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                Downloadable downloadable = runningDownloadsMap.get(reference);

                if (downloadable != null) {
                    // do something with the download file
                    android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();
                    //        get the status of the download
                    int columnIndex = cursor.getColumnIndex(android.app.DownloadManager
                            .COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);

                    int fileNameIndex = cursor.getColumnIndex(android.app.DownloadManager
                            .COLUMN_LOCAL_FILENAME);
                    String savedFilePath = cursor.getString(fileNameIndex);

                    //        get the reason - more detail on the status
                    int columnReason = cursor.getColumnIndex(android.app.DownloadManager
                            .COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);

                    switch (status) {
                        case android.app.DownloadManager.STATUS_SUCCESSFUL:
                            statusListener.success(downloadable);
                            break;
                        case android.app.DownloadManager.STATUS_FAILED:
                            statusListener.failed(downloadable, "Failed");
                            break;
                        case android.app.DownloadManager.STATUS_PAUSED:
                            statusListener.interrupted(downloadable, "Paused");
                            break;
                        case android.app.DownloadManager.STATUS_PENDING:
                            statusListener.interrupted(downloadable, "Pending");
                            break;
                        case android.app.DownloadManager.STATUS_RUNNING:
                            progressListener.progressUpdated(downloadable, 20);
                            break;
                    }
                    cursor.close();
                }
            }
        };
        context.registerReceiver(downloadStatusReceiver, intentFilter);
    }

}
