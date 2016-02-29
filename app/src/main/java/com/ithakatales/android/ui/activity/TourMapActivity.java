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
import com.ithakatales.android.util.AttractionUtil;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.PreferenceUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class TourMapActivity extends BaseActivity implements MapView.MarkerClickListener {

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
        attraction = Parcels.unwrap(getIntent().getParcelableExtra("attraction"));
        tourAction = getIntent().getIntExtra("tourAction", TourAction.DOWNLOAD);
        injectDependencies();
        initialize();
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

    @Override
    public void onMarkerClicked(Marker marker) {
        switch (tourAction) {
            case TourAction.DOWNLOAD :
                bakery.toastShort("Download to Access the Narration");
                break;
            case TourAction.START:
                bakery.toastShort("Click Begin IthakaTale to Access the Narration");
                break;
        }
    }

    @Override
    public void onMarkerPopoverClicked(Marker marker) {}

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
        mapView.setMarkerClickListener(this);

        // initialize no network view
        loadPoiMap();
    }

    private Target mapViewPicassoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mapView.setImage(ImageSource.bitmap(bitmap));

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            // copy poi list to another list to detach from realm if it is loading from db, then sort based on audio priority
            List<Poi> pois = new ArrayList<>();
            pois.addAll(attraction.getPois());
            Collections.sort(pois, new Comparator<Poi>() {
                @Override
                public int compare(Poi lhs, Poi rhs) {
                    return lhs.getAudio().getPriority() - rhs.getAudio().getPriority();
                }
            });

            int index = 1;
            for (Poi poi : pois) {
                float x = (float) (poi.getxPercent() * bitmapWidth / 100);
                float y = (float) (poi.getyPercent() * bitmapHeight / 100);
                mapView.addMarker(new Marker(index, x, y, poi.getName(), AttractionUtil.attractionDurationToString(poi.getAudio().getDuration())));
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
