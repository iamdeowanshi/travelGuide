package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;
import com.ithakatales.android.util.AttractionUtil;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;

/**
 * @author farhanali
 */
public class TourGalleryActivity extends BaseActivity implements GalleryPagerAdapter.NavigationClickListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.view_pager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_gallery);

        injectDependencies();

        Attraction attraction = Parcels.unwrap(this.getIntent().getParcelableExtra("attraction"));
        boolean isLoadFromUrl = getIntent().getBooleanExtra("is_load_from_url", false);
        List<Image> images = AttractionUtil.getAllImages(attraction);

        // setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(attraction.getName());
        }

        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(images, isLoadFromUrl);
        galleryPagerAdapter.setNavigationClickListener(this);
        viewPager.setAdapter(galleryPagerAdapter);
    }

    @Override
    public void onPreviousClick(int position) {
        viewPager.setCurrentItem(position - 1);
    }

    @Override
    public void onNextClicked(int position) {
        viewPager.setCurrentItem(position + 1);
    }

}
