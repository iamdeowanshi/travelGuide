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

        sortByPriority(images);

        return images;
    }

    public static List<Image> getAllImagesExceptFeatured(Attraction attraction) {
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

        sortByPriority(images);

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

    public static String attractionDurationToString(long duration) {
        if (duration == 0) {
            return "0 sec";
        }

        long minutes = duration / 60;
        long seconds = duration % 60;

        if (minutes != 0 && seconds != 0) {
            return String.format("%d min %d sec", minutes, seconds);
        }

        if (minutes == 0 && seconds != 0) {
            return String.format("%d sec", seconds);
        }

        if (minutes != 0) {
            return String.format("%d min", minutes);
        }

        return null;
    }

    public static String audioDurationToString(long duration) {
        if (duration == 0) {
            return "0:0";
        }

        long minutes = duration / 60;
        long seconds = duration % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private static void sortByPriority(List<Image> images) {
        Collections.sort(images, new Comparator<Image>() {
            @Override
            public int compare(Image lhs, Image rhs) {
                return lhs.getPriority() - rhs.getPriority();
            }
        });
    }

}
