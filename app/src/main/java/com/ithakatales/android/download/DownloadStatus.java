package com.ithakatales.android.download;

/**
 * @author Farhan Ali
 */

public class DownloadStatus {

    public static final String DOWNLOADING  = "Downloading";
    public static final String PAUSED       = "Paused";
    public static final String SUCCESS      = "Success";
    public static final String FAILED       = "Failed";

}

// NO ENUM SUPPORT IN REALM
/*public enum DownloadStatus {

    WAITING_TO_START(0, "Waiting to start"),
    WAITING_FOR_NETWORK(1, "Waiting for network"),
    DOWNLOADING(2, "Downloading"),
    PAUSED(3, "Paused"),
    SUCCESS(4, "Success"),
    FAILED(5, "Failed"),
    CANCELLED(6, "Cancelled");

    public int code;
    public String message;

    DownloadStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}*/
