package com.ithakatales.android.data.model;

import android.util.LongSparseArray;

import com.ithakatales.android.R;

/**
 * @author Farhan Ali
 */
public class IconMap {

    public static final LongSparseArray<Integer> tourTypeLight = new LongSparseArray<Integer>(){{
        put(AttractionType.WALKING, R.drawable.ic_tagtype_walk_light);
        put(AttractionType.MONUMENT, R.drawable.ic_tagtype_view_light);
    }};

    public static final LongSparseArray<Integer> tourTypeDark = new LongSparseArray<Integer>(){{
        put(AttractionType.WALKING, R.drawable.ic_tagtype_walk_dark);
        put(AttractionType.MONUMENT, R.drawable.ic_tagtype_view_dark);
    }};

    public static final LongSparseArray<Integer> tourTags = new LongSparseArray<Integer>(){{
        put(Tag.WALKING, R.drawable.tag_icon_walking);
        put(Tag.HIKING, R.drawable.tag_icon_hiking);
        put(Tag.SHOPPING, R.drawable.tag_icon_shopping);
        put(Tag.PHOTOGRAPHY, R.drawable.tag_icon_photography);
        put(Tag.FAMILY_FRIENDLY, R.drawable.tag_icon_family);
        put(Tag.PET_FRIENDLY, R.drawable.tag_icon_pet);

        put(Tag.MONUMENT, R.drawable.tag_icon_monument);
        put(Tag.RELIGIOUS_LANDMARK, R.drawable.tag_icon_religious_landmark);
        put(Tag.MUSEUM, R.drawable.tag_icon_museum);
        put(Tag.NEIGHBOURHOOD, R.drawable.tag_icon_neighborhood);

        put(Tag.FOOD, R.drawable.tag_icon_food);
        put(Tag.HISTORY, R.drawable.tag_icon_history);
        put(Tag.NATURE, R.drawable.tag_icon_nature);
        put(Tag.VIEWS, R.drawable.tag_icon_views);
        put(Tag.ARCHITECTURE, R.drawable.tag_icon_architecture);
        put(Tag.ART, R.drawable.tag_icon_art);
        put(Tag.BAZAR, R.drawable.tag_icon_bazar);
        put(Tag.NIGHTLIFE, R.drawable.tag_icon_nightlife);
        put(Tag.MUSIC, R.drawable.tag_icon_music);
    }};

}
