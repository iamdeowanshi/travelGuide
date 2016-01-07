package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * @author farhanali
 */
public class TourGalleryActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private List<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_gallery);

        injectDependencies();

        Map<String, String> imageCaptionMap = (HashMap<String, String>) getIntent().getSerializableExtra("image_caption_map");
        boolean isLoadFromUrl = getIntent().getBooleanExtra("is_load_from_url", false);
        String attractionName = getIntent().getStringExtra("attraction_name");

        // setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(attractionName);
        }


        for (Map.Entry<String, String> entry : imageCaptionMap.entrySet()) {
            Image image = new Image();
            image.setCaption(entry.getValue());

            if (isLoadFromUrl) {
                image.setUrl(entry.getKey());
            } else {
                image.setPath(entry.getKey());
            }

            images.add(image);
        }

        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(images, isLoadFromUrl);
        viewPager.setAdapter(galleryPagerAdapter);
    }

}
