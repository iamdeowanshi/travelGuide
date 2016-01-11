package com.ithakatales.android.util;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Farhan Ali
 */
public class AttractionUtil {

    public static List<Image> getAllImages(Attraction attraction) {
        List<Image> images = getAllImagesExceptFeatured(attraction);
        images.add(attraction.getFeaturedImage());

        return images;
    }

    public static List<Image> getAllImagesExceptFeatured(Attraction attraction) {
        List<Image> images = new ArrayList<>();

        images.add(attraction.getFeaturedImage());
        images.addAll(attraction.getImages());

        for (Audio audio : attraction.getAudios()) {
            images.addAll(audio.getImages());
        }

        for (Poi  poi : attraction.getPois()) {
            images.addAll(poi.getImages());

            if (poi.getAudio() == null) continue;

            images.addAll(poi.getAudio().getImages());
        }

        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image lhs, Image rhs) {
                return lhs.getPriority() - rhs.getPriority();
            }
        });

        return images;
    }

    public static List<Audio> getAllAudios(Attraction attraction) {
        List<Audio> audios = getAllAudioExceptPreview(attraction);
        audios.add(attraction.getPreviewAudio());

        return audios;
    }

    public static List<Audio> getAllAudioExceptPreview(Attraction attraction) {
        List<Audio> audios = new ArrayList<>();

        audios.addAll(attraction.getAudios());

        for (Poi poi : attraction.getPois()) {
            if (poi.getAudio() == null) continue;

            audios.add(poi.getAudio());
        }

        Collections.sort(audios, new Comparator<Audio>() {
            @Override
            public int compare(Audio lhs, Audio rhs) {
                return lhs.getPriority() - rhs.getPriority();
            }
        });

        return audios;
    }

}
