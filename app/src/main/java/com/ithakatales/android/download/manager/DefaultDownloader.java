package com.ithakatales.android.download.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
public class DefaultDownloader implements Downloader, DownloadStatusReceiver.DownloadableProvider {

    @Inject Context context;

    private DownloadProgressListener progressListener   = DownloadProgressListener.DEFAULT__PROGRESS_LISTENER;

    private Map<Long, Downloadable> runningDownloadsMap = new HashMap<>();
    private Map<Downloadable, DownloadProgressObserver> progressObserverMap = new HashMap<>();

    private android.app.DownloadManager downloadManager;
    private DownloadStatusReceiver downloadStatusReceiver;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatusListener(DownloadStatusListener listener) {
        downloadStatusReceiver.setStatusListener(listener);
    }

    @Override
    public void removeStatusListener() {
        downloadStatusReceiver.setStatusListener(DownloadStatusListener.DEFAULT_STATUS_LISTENER);
    }

    @Override
    public void setProgressListener(DownloadProgressListener listener) {
        progressListener = listener;
    }

    @Override
    public void removeProgressListener() {
        progressListener = DownloadProgressListener.DEFAULT__PROGRESS_LISTENER;
    }

    @Override
    public void unregisterProgressObserver(Downloadable downloadable) {
        DownloadProgressObserver observer = progressObserverMap.get(downloadable);

        if (observer != null) {
            context.getContentResolver().unregisterContentObserver(observer);
        }
    }

    @Override
    public Downloadable getDownloadableById(long downloadId) {
        return runningDownloadsMap.get(downloadId);
    }

    // TODO: 15/10/15 Refactor
    public void registerNotificationClickReciever() {
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
        downloadStatusReceiver = new DownloadStatusReceiver(downloadManager, this);
        context.registerReceiver(downloadStatusReceiver, intentFilter);
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
        Uri myDownloads = Uri.parse("content://downloads/my_downloads/" + downloadable.getId());
        DownloadProgressObserver observer = new DownloadProgressObserver(downloadable, downloadManager, progressListener);
        context.getContentResolver().registerContentObserver(myDownloads, true, observer);
        progressObserverMap.put(downloadable, observer);
    }

}
