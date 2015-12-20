package com.ithakatales.android.download.model;

/**
 * @author Farhan Ali
 */
public abstract class DownloadProgress<T extends DownloadProgress> {

    protected long downloadId;
    protected int progress;
    protected long bytesTotal;
    protected long bytesDownloaded;
    protected int status;

    public long getDownloadId() {
        return downloadId;
    }

    public T setDownloadId(long downloadId) {
        this.downloadId = downloadId;

        return (T) this;
    }

    public int getProgress() {
        return progress;
    }

    public T setProgress(int progress) {
        this.progress = progress;

        return (T) this;
    }

    public long getBytesTotal() {
        return bytesTotal;
    }

    public T setBytesTotal(long bytesTotal) {
        this.bytesTotal = bytesTotal;

        return (T) this;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public T setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;

        return (T) this;
    }

    public int getStatus() {
        return status;
    }

    public T setStatus(int status) {
        this.status = status;

        return (T) this;
    }

}
