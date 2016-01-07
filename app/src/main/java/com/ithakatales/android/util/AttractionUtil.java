package com.ithakatales.android.util;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Farhan Ali
 */
public class AttractionUtil {

    public static List<Image> getAllImages(Attraction attraction) {
        List<Image> images = new ArrayList<>();

        images.addAll(attraction.getImages());

        for (Audio audio : attraction.getAudios()) {
            images.addAll(audio.getImages());
        }

        for (Poi  poi : attraction.getPois()) {
            images.addAll(poi.getImages());

            if (poi.getAudio() == null) continue;

            images.addAll(poi.getAudio().getImages());
        }

        return images;
    }

}
