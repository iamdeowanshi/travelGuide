package com.ithakatales.android.download.manager;

/**
 * @author Farhan Ali
 */
public class DownloaderException extends RuntimeException {

    public DownloaderException() {
        super("Exception on downloader");
    }

    public DownloaderException(String detailMessage) {
        super(detailMessage);
    }

    public DownloaderException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DownloaderException(Throwable throwable) {
        super(throwable);
    }

}
