package com.ithakatales.android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.map.MapView;
import com.ithakatales.android.map.Marker;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.PreferenceUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;

public class TourMapActivity extends BaseActivity {

    @Inject Bakery bakery;
    @Inject PreferenceUtil preferenceUtil;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.map_view) MapView mapView;

    private Attraction attraction;
    private int tourAction;
    private boolean isMapShown = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_map);
        //attraction = (Attraction) preferenceUtil.read("attraction", Attraction.class);
        attraction = Parcels.unwrap(this.getIntent().getParcelableExtra("attraction"));
        injectDependencies();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    // required for handling map view interaction
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }


    private void initialize() {
        // initialize toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(attraction.getName());
        }


        // initialize mapView
        mapView.setMarkerDrawable(R.drawable.img_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.img_map_marker_selected);

        // initialize no network view
        loadPoiMap();

    }

    private Target mapViewPicassoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mapView.setImage(ImageSource.bitmap(bitmap));

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            int index = 1;
            for (Poi poi : attraction.getPois()) {
                float x = (float) (poi.getxPercent() * bitmapWidth / 100);
                float y = (float) (poi.getyPercent() * bitmapHeight / 100);
                long durationInMinutes = poi.getAudio() != null ? poi.getAudio().getDuration() / 60 : 0;
                mapView.addMarker(new Marker(index, x, y, poi.getName(), String.format("%d Min", durationInMinutes)));
                index++;
            }

            isMapShown = true;
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

    private void  loadPoiMap() {
        if (!isMapShown) {
            RequestCreator requestCreator = (attraction.getBluePrintPath() != null && tourAction == TourAction.START)
                    ? Picasso.with(this).load(new File(attraction.getBluePrintPath()))
                    : Picasso.with(this).load(attraction.getBlueprintUrl());
            requestCreator.into(mapViewPicassoTarget);
        }
    }

}
