package com.ithakatales.android.download.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Farhan Ali
 */
public class DownloadNotificationClickReceiver extends BroadcastReceiver {

    private DownloadableProvider downloadableProvider;

    public DownloadNotificationClickReceiver(DownloadableProvider downloadableProvider) {
        this.downloadableProvider = (downloadableProvider != null)
                ? downloadableProvider
                : DownloadableProvider.DEFAULT;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] ids = intent.getLongArrayExtra(android.app.DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
        for (long id : ids) {
            Downloadable downloadable = downloadableProvider.getDownloadableById(id);
            if (downloadable != null) {
                context.sendBroadcast(createIntentWithExtras(downloadable));
                return;
            }
        }
    }

    private Intent createIntentWithExtras(Downloadable downloadable) {
        Intent intent = new Intent(Downloader.ACTION_NOTIFICATION_CLICKED);

        intent.putExtra(Downloader.EXTRA_ID, downloadable.getId());
        intent.putExtra(Downloader.EXTRA_URL, downloadable.getUrl());
        intent.putExtra(Downloader.EXTRA_TITLE, downloadable.getTitle());
        intent.putExtra(Downloader.EXTRA_DESCRIPTION, downloadable.getDescription());
        intent.putExtra(Downloader.EXTRA_DESTINATION, downloadable.getDestination());

        return intent;
    }

}
