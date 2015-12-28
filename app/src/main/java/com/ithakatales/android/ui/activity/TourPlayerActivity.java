package com.ithakatales.android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.map.MapView;
import com.ithakatales.android.map.Marker;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;
import com.ithakatales.android.ui.adapter.PlayListRecyclerAdapter;
import com.ithakatales.android.ui.adapter.RecyclerItemClickListener;
import com.ithakatales.android.ui.custom.VerticalSpaceItemDecoration;
import com.ithakatales.android.util.Bakery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Farhan Ali
 */
public class TourPlayerActivity extends BaseActivity implements RecyclerItemClickListener<Audio> {

    @Inject Bakery bakery;
    @Inject ImageRepository imageRepo;
    @Inject AudioRepository audioRepo;
    @Inject AttractionRepository attractionRepo;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.gallery_view_pager) ViewPager galleryPager;
    @Bind(R.id.map_view) MapView mapView;
    @Bind(R.id.recycler_audio_list) RecyclerView recyclerAudioList;

    @Bind(R.id.text_poi_name) TextView textPoiName;
    @Bind(R.id.text_poi_progress) TextView textPoiProgress;

    // player controls
    @Bind(R.id.button_play_pause) Button buttonPlayPause;
    @Bind(R.id.button_rewind) Button buttonRewind;
    @Bind(R.id.button_skip) Button buttonSkip;
    @Bind(R.id.button_playlist) Button buttonPlayList;

    private Menu menu;
    private Attraction attraction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_player);
        injectDependencies();

        attraction = attractionRepo.find(1);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(attraction.getName());
        }

        mapView.setVisibility(View.GONE);
        recyclerAudioList.setVisibility(View.GONE);

        // initialize mapView
        mapView.setMarkerDrawable(R.drawable.img_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.img_map_marker_selected);
        loadPoiMap();

        // initialize gallery
        List<Image> images = imageRepo.readAll();
        GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(images);
        galleryPager.setAdapter(galleryPagerAdapter);

        // initialize audio list
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this));
        recyclerAudioList.addItemDecoration(new VerticalSpaceItemDecoration(5));
        PlayListRecyclerAdapter playlistAdapter = new PlayListRecyclerAdapter(audioRepo.readAll(), this);
        recyclerAudioList.setAdapter(playlistAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_tour_player, menu);

        // hide gallery option initially
        menu.findItem(R.id.action_gallery).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                bakery.toastShort("camera clicked");
                break;
            case R.id.action_map:
                toggleMapAndGalleryOptionVisibility();
                break;
            case R.id.action_gallery:
                toggleMapAndGalleryOptionVisibility();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public void onItemClick(Audio item) {
        recyclerAudioList.setVisibility(View.GONE);
    }

    @OnClick(R.id.button_playlist)
    void onButtonPlaylistClick() {
        recyclerAudioList.setVisibility(View.VISIBLE);
    }

    private void toggleMapAndGalleryOptionVisibility() {
        MenuItem mapItem = menu.findItem(R.id.action_map);
        MenuItem galleryItem = menu.findItem(R.id.action_gallery);
        mapItem.setVisible( ! mapItem.isVisible());
        galleryItem.setVisible( ! galleryItem.isVisible());

        mapView.setVisibility(galleryItem.isVisible() ? View.VISIBLE : View.GONE);
        galleryPager.setVisibility(mapItem.isVisible() ? View.VISIBLE : View.GONE);
    }

    // declared here because it is specific to method under this, target instance got garbage collected,
    // that's why it should be an instance variable
    private Target mapViewPicassoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mapView.recycle();

            if (bitmap == null || bitmap.isRecycled()) {
                loadPoiMap();
                return;
            }

            mapView.setImage(ImageSource.bitmap(bitmap));

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            int index = 1;
            for (Poi poi : attraction.getPois()) {
                float x = (float) (poi.getxPercent() * bitmapWidth / 100);
                float y = (float) (poi.getyPercent() * bitmapHeight / 100);
                mapView.addMarker(new Marker(index, x, y, poi.getName(), "5 Min"));
                index ++;
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            bakery.toastShort("Blueprint loading failed");
            mapView.setVisibility(View.GONE);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void loadPoiMap() {
        Picasso.with(this)
                .load(new File(attraction.getBluePrintPath()))
                .into(mapViewPicassoTarget);
    }

}
