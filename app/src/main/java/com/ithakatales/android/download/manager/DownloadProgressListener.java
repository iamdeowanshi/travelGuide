package com.ithakatales.android.download.manager;

/**
 * @author Farhan Ali
 */
public interface DownloadProgressListener {

    void progressUpdated(Downloadable downloadable, int progress);

    DownloadProgressListener DEFAULT__PROGRESS_LISTENER = new DownloadProgressListener() {
        @Override
        public void progressUpdated(Downloadable downloadable, int progress) {}
    };

}
