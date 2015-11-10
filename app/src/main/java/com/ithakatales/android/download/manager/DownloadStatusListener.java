package com.ithakatales.android.download.manager;

/**
 * @author Farhan Ali
 */
public interface DownloadStatusListener {

    void success(Downloadable downloadable);

    void failed(Downloadable downloadable, String message);

    void cancelled(Downloadable downloadable, String message);

    void interrupted(Downloadable downloadable, String message);

    DownloadStatusListener DEFAULT_STATUS_LISTENER = new DownloadStatusListener() {
        @Override
        public void success(Downloadable downloadable) {}

        @Override
        public void failed(Downloadable downloadable, String message) {}

        @Override
        public void cancelled(Downloadable downloadable, String message) {}

        @Override
        public void interrupted(Downloadable downloadable, String message) {}
    };

}
