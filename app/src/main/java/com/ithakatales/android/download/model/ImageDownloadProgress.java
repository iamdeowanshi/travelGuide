package com.ithakatales.android.download.model;

/**
 * @author farhanali
 */
public class ImageDownloadProgress extends DownloadProgress<ImageDownloadProgress> {

    private long imageId;
    private String imageName;
    private String url;
    private String path;

    public long getImageId() {
        return imageId;
    }

    public ImageDownloadProgress setImageId(long imageId) {
        this.imageId = imageId;

        return this;
    }

    public String getImageName() {
        return imageName;
    }

    public ImageDownloadProgress setImageName(String imageName) {
        this.imageName = imageName;

        return this;
    }


    public String getUrl() {
        return url;
    }

    public ImageDownloadProgress setUrl(String url) {
        this.url = url;

        return this;
    }

    public String getPath() {
        return path;
    }

    public ImageDownloadProgress setPath(String path) {
        this.path = path;

        return this;
    }

}