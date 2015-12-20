package com.ithakatales.android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.IconMap;
import com.ithakatales.android.data.model.TagType;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.map.MapView;
import com.ithakatales.android.map.Marker;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.ui.actions.TourDeleteAction;
import com.ithakatales.android.ui.actions.TourDownloadAction;
import com.ithakatales.android.ui.actions.TourDownloadRetryAction;
import com.ithakatales.android.ui.actions.TourStartAction;
import com.ithakatales.android.ui.actions.TourUpdateAction;
import com.ithakatales.android.ui.adapter.TagGridAdapter;
import com.ithakatales.android.util.Bakery;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourDetailActivity extends BaseActivity implements TourDetailViewInteractor {

    @Inject TourDetailPresenter presenter;
    @Inject Bakery bakery;

    @Bind(R.id.layout_coordinator) CoordinatorLayout layoutCoordinator;
    @Bind(R.id.image_featured) ImageView imageFeatured;

    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.text_title) TextView textTitle;
    @Bind(R.id.text_description) TextView textDescription;
    @Bind(R.id.icon_type) ImageView iconType;
    @Bind(R.id.text_duration) TextView textDuration;

    @Bind(R.id.map_view) MapView mapView;

    @Bind(R.id.expandable_text_know_more) ExpandableTextView expandableTextKnowMore;
    @Bind(R.id.web_view_before_you_go) WebView webViewBeforeYouGo;
    @Bind(R.id.expandable_text_credits) ExpandableTextView expandableTextCredits;

    @Bind(R.id.view_tag_type_one) View viewTagTypeOne;
    @Bind(R.id.view_tag_type_two) View viewTagTypeTwo;
    @Bind(R.id.view_tag_type_three) View viewTagTypeThree;

    @Bind(R.id.button_tour_action) Button buttonTourActon;
    @Bind(R.id.progress) ProgressBar progress;

    private Attraction attraction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        injectDependencies();
        initialize();

        presenter.setViewInteractor(this);
        presenter.loadAttraction(getIntent().getLongExtra("attraction_id", 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tour_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                bakery.toastShort("share clicked");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    // required for handling collapsing toolbar & coordinator layout interaction
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    // view interactor method implementations

    @Override
    public void onAttractionLoaded(Attraction attraction, int tourAction) {
        showTourDetails(attraction);
        buttonTourActon.setTag(getTourAction(tourAction));
    }

    @Override
    public void onDownloadProgressChange(final TourDownloadProgress download) {
        buttonTourActon.setEnabled(false);
        buttonTourActon.setText(String.format("Downloading (%d%%)", download.getProgress()));
        Timber.d(String.format("total: %d | downloaded: %d | progress: %d | status: %d",
                download.getBytesTotal(), download.getBytesDownloaded(),
                download.getProgress(), download.getStatus()));
    }

    @Override
    public void onNoNetwork() {
        bakery.toastShort("No network !");
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onNetworkError(Throwable e) {
        bakery.toastShort(e.getMessage());
        Timber.e(e.getMessage(), e);
    }

    @OnClick(R.id.button_preview_player)
    void onPreviewPlayerClick() {
        bakery.snackShort(getContentView(), "Under Development !");
    }

    @OnClick(R.id.button_tour_action)
    void onTourActionClick() {
        TourAction action = (TourAction) buttonTourActon.getTag();

        if (action != null) {
            action.perform(attraction);
        }
    }

    private void initialize() {
        // initialize toolbar
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // initialize tag type views
        viewTagTypeOne.setVisibility(View.GONE);
        viewTagTypeTwo.setVisibility(View.GONE);
        viewTagTypeThree.setVisibility(View.GONE);
        viewTagTypeTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));

        // initialize mapView
        mapView.setMarkerDrawable(R.drawable.icon_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.icon_map_marker_selected);
    }

    private void showTourDetails(Attraction attraction) {
        this.attraction = attraction;

        loadFeaturedImage();
        loadPoiMap();
        loadTagTypes();

        // load titles
        collapsingToolbarLayout.setTitle(attraction.getName());
        textTitle.setText(attraction.getName());

        // load type
        iconType.setImageResource(IconMap.tourTypeDark.get(attraction.getType().getId()));

        // load duration
        int durationInMinutes = (int) (attraction.getDuration() / 60);
        textDuration.setText(String.format("%d Mins", durationInMinutes));

        // load text details
        textDescription.setText(attraction.getShortDescription());
        expandableTextKnowMore.setText(attraction.getLongDescription());
        webViewBeforeYouGo.setBackgroundColor(Color.TRANSPARENT);
        webViewBeforeYouGo.loadData(attraction.getBeforeYouGo(), "text/html", "UTF-8");
        expandableTextCredits.setText(attraction.getCredits());
    }

    private void loadFeaturedImage() {
        RequestCreator requestCreator = attraction.getFeaturedImage().getPath() != null
                ? Picasso.with(this).load(new File(attraction.getFeaturedImage().getPath()))
                : Picasso.with(this).load(attraction.getFeaturedImage().getUrl());

        requestCreator.placeholder(R.drawable.placeholder_ratio_1_1)
                .error(R.drawable.placeholder_ratio_1_1)
                .resize(600, 600)
                .into(imageFeatured, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageFeatured.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    // TODO: 09/12/15 load actual data
    private void loadPoiMap() {
        mapView.setImage(ImageSource.asset("map_sample.jpg"));

        mapView.addMarker(new Marker(1, 175f, 445f, "Poi 1", "76 min"));
        mapView.addMarker(new Marker(2, 400f, 200f, "Poi 2", "42 min"));
        mapView.addMarker(new Marker(3, 500f, 420f, "Poi 2", "18 min"));
        mapView.addMarker(new Marker(4, 895f, 555f, "Poi 4", "24 min"));
        mapView.addMarker(new Marker(5, 1100f, 300f, "Poi 5", "35 min"));
    }

    private void loadTagTypes() {
        int index = 0;
        for (TagType tagType : attraction.getTagTypes()) {
            loadTags(tagType, index);
            index++;
        }
    }

    private void loadTags(TagType tagType, int index) {
        View tagTypeView = getTagTypeView(index);
        tagTypeView.setVisibility(View.VISIBLE);

        TextView textTagTypeName = (TextView) tagTypeView.findViewById(R.id.text_tag_type_name);
        GridView gridTags = (GridView) tagTypeView.findViewById(R.id.grid_tags);

        textTagTypeName.setText(tagType.getName());
        TagGridAdapter adapter = new TagGridAdapter(tagType.getTags());
        gridTags.setAdapter(adapter);
    }

    private View getTagTypeView(int index) {
        switch (index) {
            case 0:
                return viewTagTypeOne;
            case 1:
                return viewTagTypeTwo;
            case 2:
                return viewTagTypeThree;
        }

        return viewTagTypeOne;
    }

    private TourAction getTourAction(int tourAction) {
        switch (tourAction) {
            case TourAction.DOWNLOAD:
                return new TourDownloadAction(buttonTourActon, presenter);
            /*case TourAction.DOWNLOADING:
                return new TourDownloadingAction(buttonTourActon);*/
            case TourAction.START:
                return new TourStartAction(buttonTourActon, this);
            case TourAction.RETRY:
                return new TourDownloadRetryAction(buttonTourActon, presenter);
            case TourAction.UPDATE:
                return new TourUpdateAction(buttonTourActon, presenter);
            case TourAction.DELETE:
                return new TourDeleteAction(buttonTourActon, presenter);
            default:
                return null;
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = ContextCompat.getColor(this, R.color.primary_dark);
        int primary = ContextCompat.getColor(this, R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
    }

}
