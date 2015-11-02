package com.ithakatales.android.util;

import android.os.Environment;

import java.io.File;

/**
 * @author Farhan Ali
 */
public class StorageUtil {

    public String getIthakaFolderPath() {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Ithaka");

        if ( ! dir.exists()) dir.mkdirs();

        return dir.getAbsolutePath();
    }

}
