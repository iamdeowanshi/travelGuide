package com.ithakatales.android.download.manager;

public interface DownloadableProvider {

    DownloadableProvider DEFAULT = new DownloadableProvider() {
        @Override
        public Downloadable getDownloadableById(long downloadId) {
            return null;
        }
    };

    Downloadable getDownloadableById(long downloadId);

}