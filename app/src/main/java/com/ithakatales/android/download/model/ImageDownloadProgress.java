package com.ithakatales.android.download.model;

import com.ithakatales.android.data.model.Image;

/**
 * @author farhanali
 */
public class ImageDownloadProgress extends DownloadProgress<ImageDownloadProgress> {

    private Image image;

    public Image getImage() {
        return image;
    }

    public ImageDownloadProgress setImage(Image image) {
        this.image = image;

        return this;
    }

}