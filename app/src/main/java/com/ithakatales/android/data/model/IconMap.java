package com.ithakatales.android.data.model;

import android.util.LongSparseArray;

import com.ithakatales.android.R;

/**
 * @author Farhan Ali
 */
public class IconMap {

    public static final LongSparseArray<Integer> tourTypeLight = new LongSparseArray<Integer>(){{
        put(AttractionType.WALKING, R.drawable.icon_walk_light);
        put(AttractionType.MONUMENT, R.drawable.icon_view_light);
    }};

    public static final LongSparseArray<Integer> tourTypeDark = new LongSparseArray<Integer>(){{
        put(AttractionType.WALKING, R.drawable.icon_walk_dark);
        put(AttractionType.MONUMENT, R.drawable.icon_view_dark);
    }};

    public static final LongSparseArray<Integer> tourTags = new LongSparseArray<Integer>(){{
        put(Tag.WALKING, R.drawable.icon_walking);
        put(Tag.HIKING, R.drawable.icon_hiking);
        put(Tag.SHOPPING, R.drawable.icon_shopping);
        put(Tag.PHOTOGRAPHY, R.drawable.icon_photography);
        put(Tag.FAMILY_FRIENDLY, R.drawable.icon_family);
        put(Tag.PET_FRIENDLY, R.drawable.icon_pet);

        put(Tag.MONUMENT, R.drawable.icon_monument);
        put(Tag.RELIGIOUS_LANDMARK, R.drawable.icon_religious_landmark);
        put(Tag.MUSEUM, R.drawable.icon_museum);
        put(Tag.NEIGHBOURHOOD, R.drawable.icon_neighborhood);

        put(Tag.FOOD, R.drawable.icon_food);
        put(Tag.HISTORY, R.drawable.icon_history);
        put(Tag.NATURE, R.drawable.icon_nature);
        put(Tag.VIEWS, R.drawable.icon_views);
        put(Tag.ARCHITECTURE, R.drawable.icon_architecture);
        put(Tag.ART, R.drawable.icon_art);
        put(Tag.BAZAR, R.drawable.icon_bazar);
        put(Tag.NIGHTLIFE, R.drawable.icon_nightlife);
        put(Tag.MUSIC, R.drawable.icon_music);
    }};

}
