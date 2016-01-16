package com.ithakatales.android.download;

import android.content.Context;
import android.os.Environment;

import com.ithakatales.android.app.Config;

import java.io.File;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class TourStorage {

    @Inject Context context;

    // return ithaka/tours dir
    public File getTourDir() {
        File ithakaDir = getIthakaDir();
        File toursDir = new File(ithakaDir.getAbsolutePath() + "/tours");

        return makeIfNotExist(toursDir);
    }

    // return ithaka/tours/{tourId} dir
    public File getTourDir(long tourId) {
        File toursDir = getTourDir();
        File tourDir = new File(toursDir.getAbsolutePath() + "/" + tourId);

        return makeIfNotExist(tourDir);
    }

    // return ithaka/tours/{tourId}/audios dir
    public File getAudioDir(long tourId) {
        File tourDir = getTourDir(tourId);
        File audioDir = new File(tourDir.getAbsolutePath() + "/audios");

        return makeIfNotExist(audioDir);
    }

    // return ithaka/tours/{tourId}/images dir
    public File getImagesDir(long tourId) {
        File tourDir = getTourDir(tourId);
        File imagesDir = new File(tourDir.getAbsolutePath() + "/images");

        return makeIfNotExist(imagesDir);
    }

    public File getIthakaDir() {
        if (Config.STORAGE_USE_INTERNAL) {
            return getIthakaInternalDir();
        }

        return getIthakaExternalDir();
    }

    public File getIthakaCapturedImageDir() {
        File dir = new File(getIthakaDir().getAbsolutePath() + "/Captured");

        return makeIfNotExist(dir);
    }

    public void removeTour(long tourId) {
        delete(getTourDir(tourId));
    }

    public void removeAllTours() {
        delete(getIthakaDir());
    }

    private File getIthakaInternalDir() {
        File internal = context.getFilesDir();

        return getIthakaDir(internal);
    }

    private File getIthakaExternalDir() {
        File sdCard = Environment.getExternalStorageDirectory();

        return getIthakaDir(sdCard);
    }

    private File getIthakaDir(File srcDir) {
        String dirPath = Config.STORAGE_HIDE_FILES
                ? "." + Config.STORAGE_DATA_FOLDER
                : Config.STORAGE_DATA_FOLDER;
        File dir = new File (srcDir.getAbsolutePath() + "/" + dirPath);

        return makeIfNotExist(dir);
    }

    private File makeIfNotExist(File dir) {
        if ( ! dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }

    private void delete(File file) {
        if ( ! file.exists()) return;

        if (file.isDirectory() && file.list().length > 0) {
            for (File child : file.listFiles()) {
                delete(child);
            }
        }

        file.delete();
    }

}
