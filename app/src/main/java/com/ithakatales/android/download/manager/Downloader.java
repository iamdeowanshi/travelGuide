package com.ithakatales.android.download.manager;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface Downloader {

    void download(Downloadable downloadable);

    void download(List<Downloadable> downloadableList);

    void cancel(Downloadable downloadable);

    void cancel(List<Downloadable> downloadableList);

    void cancelAll();

    void setStatusListener(DownloadStatusListener listener);

    void removeStatusListener();

    void setProgressListener(DownloadProgressListener listener);

    void removeProgressListener();

}
