package com.ithakatales.android.download.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * @author Farhan Ali
 */
public class DownloadStatusReceiver extends BroadcastReceiver {

    private DownloadManager downloadManager;
    private DownloadableProvider downloadableProvider;
    private DownloadStatusListener statusListener = DownloadStatusListener.DEFAULT_STATUS_LISTENER;

    public DownloadStatusReceiver(DownloadManager downloadManager, DownloadableProvider downloadableProvider) {
        this.downloadManager = downloadManager;
        this.downloadableProvider = (downloadableProvider != null)
                ? downloadableProvider
                : DownloadableProvider.DEFAULT;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        Downloadable downloadable = downloadableProvider.getDownloadableById(downloadId);

        if (downloadable == null) return;

        Status status = getStatus(downloadId);
        status.notifyListener(statusListener, downloadable);
    }

    public void setStatusListener(DownloadStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    private Status getStatus(long downloadId) {
        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(android.app.DownloadManager.COLUMN_STATUS);
        int statusCode = cursor.getInt(columnIndex);
        int columnReason = cursor.getColumnIndex(android.app.DownloadManager.COLUMN_REASON);
        int reasonCode = cursor.getInt(columnReason);

        cursor.close();

        return new Status(statusCode, reasonCode);
    }

    static class Status {

        int statusCode;
        int reasonCode;

        public Status(int statusCode, int reasonCod) {
            this.statusCode = statusCode;
            this.reasonCode = reasonCod;
        }

        void notifyListener(DownloadStatusListener listener, Downloadable downloadable) {
            String reason = getReason();
            switch (statusCode) {
                case android.app.DownloadManager.STATUS_SUCCESSFUL:
                    listener.success(downloadable);
                    break;
                case android.app.DownloadManager.STATUS_FAILED:
                    listener.failed(downloadable, reason);
                    break;
                case android.app.DownloadManager.STATUS_PAUSED:
                    listener.paused(downloadable, reason);
                    break;
            }
        }

        private String getReason() {
            switch (reasonCode) {
                case DownloadManager.ERROR_CANNOT_RESUME:
                    return DownloadStatusListener.ERROR_CANNOT_RESUME;
                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                    return DownloadStatusListener.ERROR_HTTP_DATA_ERROR;
                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                    return DownloadStatusListener.ERROR_INSUFFICIENT_SPACE;
                case DownloadManager.ERROR_UNKNOWN:
                    return DownloadStatusListener.ERROR_UNKNOWN;
                case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                    return DownloadStatusListener.PAUSED_WAITING_FOR_NETWORK;
                case DownloadManager.PAUSED_WAITING_TO_RETRY:
                    return DownloadStatusListener.PAUSED_WAITING_TO_RETRY;
                case DownloadManager.PAUSED_UNKNOWN:
                    return DownloadStatusListener.PAUSED_UNKNOWN;
                default:return DownloadStatusListener.UNKNOWN;
            }
        }

    }

    static interface DownloadableProvider {

        DownloadableProvider DEFAULT = new DownloadableProvider() {
            @Override
            public Downloadable getDownloadableById(long downloadId) {
                return null;
            }
        };

        Downloadable getDownloadableById(long downloadId);

    }

}
