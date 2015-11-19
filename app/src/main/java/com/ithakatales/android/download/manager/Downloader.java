package com.ithakatales.android.download.manager;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface Downloader {

    String ACTION_NOTIFICATION_CLICKED = "com.tecsol.android.download.NOTIFICATION_CLICKED_ACTION";

    String EXTRA_ID             = "download_id";
    String EXTRA_URL            = "download_url";
    String EXTRA_TITLE          = "download_title";
    String EXTRA_DESCRIPTION    = "download_description";
    String EXTRA_DESTINATION    = "download_destination";

    void download(Downloadable downloadable);

    void download(List<Downloadable> downloadableList);

    void cancel(Downloadable downloadable);

    void cancel(List<Downloadable> downloadableList);

    void cancelAll();

    void setStatusListener(DownloadStatusListener listener);

    void removeStatusListener();

    void unregisterProgressObserver(Downloadable downloadable);

    void setProgressListener(DownloadProgressListener listener);

    void removeProgressListener();

}
