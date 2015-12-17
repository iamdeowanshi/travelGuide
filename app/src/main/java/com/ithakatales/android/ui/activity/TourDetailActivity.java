package com.ithakatales.android.ui.activity;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
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
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.download.TourDownloadProgressListener;
import com.ithakatales.android.download.TourDownloadProgressReader;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.map.MapView;
import com.ithakatales.android.map.Marker;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;
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
public class TourDetailActivity extends BaseActivity implements TourDetailViewInteractor, TourDownloadProgressListener {

    private static final int TOUR_ACTION_DOWNLOAD       = 1;
    private static final int TOUR_ACTION_DOWNLOADING    = 2;
    private static final int TOUR_ACTION_START          = 3;
    private static final int TOUR_ACTION_RETRY          = 4;

    @Inject TourDownloader tourDownloader;
    @Inject TourDownloadProgressReader progressReader;

    @Inject AttractionRepository attractionRepo;
    @Inject TourDetailPresenter presenter;
    @Inject Bakery bakery;

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
    private long attractionId;
    private int tourAction;
    private TourDownloadProgress lastDownloadProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        injectDependencies();
        initialize();

        loadAttraction();
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
    protected void onPause() {
        super.onPause();
        presenter.pause();
        tourDownloader.stopProgressListening(attractionId);
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
    public void onAttractionLoadSuccess(Attraction attraction) {
        this.attraction = attraction;
        showAttractionDetails();
        setTourAction(TOUR_ACTION_DOWNLOAD);
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

    @Override
    public void onProgressChange(final TourDownloadProgress download) {
        if (download == null) return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // if (lastDownloadProgress != null && lastDownloadProgress.getProgress() > download.getProgress()) return;

                buttonTourActon.setText(String.format("Downloading (%d%%)", download.getProgress()));
                Timber.d(String.format("total: %d | downloaded: %d | progress: %d | status: %d",
                        download.getBytesTotal(), download.getBytesDownloaded(),
                        download.getProgress(), download.getStatus()));

                if (download.getProgress() == 100) {
                    tourDownloader.stopProgressListening(attractionId);
                    setTourAction(download);
                }

                lastDownloadProgress = download;
            }
        });
    }

    @OnClick(R.id.button_preview_player)
    void onPreviewPlayerClick() {
        bakery.snackShort(getContentView(), "Under Development !");
    }

    @OnClick(R.id.button_tour_action)
    void onTourActionClick() {
        switch (tourAction) {
            case TOUR_ACTION_DOWNLOAD:
                tourDownloader.download(attraction);
                setTourAction(TOUR_ACTION_DOWNLOADING);
                break;
            case TOUR_ACTION_START:
                bakery.snackShort(getContentView(), "Under Development !");
                break;
            case TOUR_ACTION_RETRY:
                tourDownloader.download(attraction);
                setTourAction(TOUR_ACTION_DOWNLOADING);
                break;
        }
    }

    private void initialize() {
        presenter.setViewInteractor(this);

        initActivityTransitions();
        initToolbar();
        initMapView();
        initTagViews();
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void initMapView() {
        mapView.setMarkerDrawable(R.drawable.icon_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.icon_map_marker_selected);
    }

    private void initTagViews() {
        viewTagTypeOne.setVisibility(View.GONE);
        viewTagTypeTwo.setVisibility(View.GONE);
        viewTagTypeTwo.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        viewTagTypeThree.setVisibility(View.GONE);
    }

    private void loadAttraction() {
        attractionId = getIntent().getLongExtra("attraction_id", 0);
        attraction = attractionRepo.find(attractionId);

        // if not in db, load from api using presenter
        if (attraction == null) {
            presenter.loadAttraction(attractionId);
            return;
        }

        showAttractionDetails();
        setTourAction(progressReader.readProgress(attractionId));
    }

    private void showAttractionDetails() {
        if (attraction == null) return;

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

    private void setTourAction(TourDownloadProgress download) {
        switch (download.getStatus()) {
            case DownloadManager.STATUS_FAILED:
                setTourAction(TOUR_ACTION_RETRY);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                setTourAction(TOUR_ACTION_START);
                break;
            case DownloadManager.STATUS_RUNNING:
                setTourAction(TOUR_ACTION_DOWNLOADING);
                break;
        }
    }

    private void setTourAction(int tourAction) {
        this.tourAction = tourAction;
        buttonTourActon.setEnabled(true);
        buttonTourActon.setClickable(true);

        switch (tourAction) {
            case TOUR_ACTION_DOWNLOAD:
                buttonTourActon.setText("Download Tour");
                break;
            case TOUR_ACTION_DOWNLOADING:
                buttonTourActon.setEnabled(false);
                buttonTourActon.setClickable(false);
                // TODO: 17/12/15 code to move - start progress listening here is confusing
                tourDownloader.startProgressListening(attractionId, this);
                break;
            case TOUR_ACTION_START:
                buttonTourActon.setText("Start Tour");
                break;
            case TOUR_ACTION_RETRY:
                buttonTourActon.setText("Retry Download");
                break;
        }
    }

    private void loadFeaturedImage() {
        RequestCreator requestCreator = null;
        if (attraction.getFeaturedImage().getPath() != null) {
            requestCreator = Picasso.with(this).load(new File(attraction.getFeaturedImage().getPath()));
        } else {
            requestCreator = Picasso.with(this).load(attraction.getFeaturedImage().getUrl());
        }

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
                    public void onError() {}
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

    private void applyPalette(Palette palette) {
        int primaryDark = ContextCompat.getColor(this, R.color.primary_dark);
        int primary = ContextCompat.getColor(this, R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
    }

}
