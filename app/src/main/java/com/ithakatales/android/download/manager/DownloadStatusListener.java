package com.ithakatales.android.download.manager;

/**
 * @author Farhan Ali
 */
public interface DownloadStatusListener {

    // Ref: http://developer.android.com/reference/android/app/DownloadManager.html
    String ERROR_CANNOT_RESUME          = "Some possibly transient error occurred but can't resume the download";
    String ERROR_HTTP_DATA_ERROR        = "Error receiving or processing data occurred at the HTTP level";
    String ERROR_INSUFFICIENT_SPACE     = "Insufficient storage space";
    String ERROR_UNKNOWN                = "Download has completed with an error that doesn't fit under any other error code";

    String PAUSED_WAITING_FOR_NETWORK   = "Download is waiting for network connectivity to proceed";
    String PAUSED_WAITING_TO_RETRY      = "Download is paused because some network error occurred and the download manager is waiting before retrying the request";
    String PAUSED_UNKNOWN               = "Download is paused for some unknown reason";

    String UNKNOWN                      = "Unknown reason";

    void success(Downloadable downloadable);

    void failed(Downloadable downloadable, String message);

    void cancelled(Downloadable downloadable, String message);

    void paused(Downloadable downloadable, String message);

    DownloadStatusListener DEFAULT_STATUS_LISTENER = new DownloadStatusListener() {
        @Override
        public void success(Downloadable downloadable) {}

        @Override
        public void failed(Downloadable downloadable, String message) {}

        @Override
        public void cancelled(Downloadable downloadable, String message) {}

        @Override
        public void paused(Downloadable downloadable, String message) {}
    };

}
