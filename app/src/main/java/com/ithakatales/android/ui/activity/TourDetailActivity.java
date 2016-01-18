package com.ithakatales.android.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.ithakatales.android.R;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.IconMap;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
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
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.util.AttractionUtil;
import com.ithakatales.android.util.Bakery;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourDetailActivity extends BaseActivity implements TourDetailViewInteractor {

    private static final String PREVIEW_BUTTON_TEXT_PLAY = "Play Preview";
    private static final String PREVIEW_BUTTON_TEXT_STOP = "Stop Preview";

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
    @Bind(R.id.button_preview_player) Button buttonPreviewPlayer;

    @Bind(R.id.map_view) MapView mapView;

    @Bind(R.id.expandable_text_know_more) ExpandableTextView expandableTextKnowMore;
    @Bind(R.id.web_view_before_you_go) WebView webViewBeforeYouGo;
    @Bind(R.id.expandable_text_credits) ExpandableTextView expandableTextCredits;

    @Bind(R.id.view_tag_type_one) View viewTagTypeOne;
    @Bind(R.id.view_tag_type_two) View viewTagTypeTwo;
    @Bind(R.id.view_tag_type_three) View viewTagTypeThree;

    @Bind(R.id.button_tour_action) Button buttonTourActon;
    @Bind(R.id.view_loading) RelativeLayout viewLoading;

    @Bind(R.id.view_no_network) NoNetworkView viewNoNetwork;

    private Attraction attraction;
    private int tourAction;
    private boolean isMapShown = false;

    private MediaPlayer previewPlayer;

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
    protected void onResume() {
        super.onResume();
        presenter.resume();
        togglePreviewPlayerButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tour_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        stopPreview();

        switch (item.getItemId()) {
            case R.id.action_share:
                shareAttraction();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopPreview();
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

    // view interactor method implementations

    @Override
    public void onAttractionLoaded(Attraction attraction, int tourAction) {
        this.tourAction = tourAction;
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
    public void onDownloadComplete(long attractionId) {
        presenter.loadAttraction(attractionId);
    }

    @Override
    public void onNoNetwork() {
        bakery.toastShort("No network !");
        viewNoNetwork.show();
    }

    @Override
    public void showProgress() {
        viewLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        viewLoading.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(Throwable e) {
        bakery.toastShort(e.getMessage());
        Timber.e(e.getMessage(), e);
        viewNoNetwork.show();
    }

    @OnClick(R.id.button_preview_player) void onPreviewPlayerClick() {
        String buttonText = buttonPreviewPlayer.getText().toString();

        switch (buttonText) {
            case PREVIEW_BUTTON_TEXT_PLAY:
                playPreview();
                break;
            case PREVIEW_BUTTON_TEXT_STOP:
                stopPreview();
                break;
        }
    }

    @OnClick(R.id.button_tour_action) void onTourActionClick() {
        TourAction action = (TourAction) buttonTourActon.getTag();

        if (action == null) return;

        if (action instanceof TourStartAction) {
            stopPreview();
        }

        action.perform(attraction);
    }

    @OnClick(R.id.image_featured) void onFeaturedImageClick() {
        if (attraction == null) return;

        HashMap<String, String> imageCaptionMap = new HashMap<>();
        boolean isLoadFromUrl = tourAction != TourAction.START;
        List<Image> images = AttractionUtil.getAllImages(attraction);

        for (Image image : images) {
            String url = isLoadFromUrl ? image.getUrl() : image.getPath();
            imageCaptionMap.put(url, image.getCaption());
        }

        Bundle bundle = new Bundle();
        bundle.putString("attraction_name", attraction.getName());
        bundle.putBoolean("is_load_from_url", isLoadFromUrl);
        bundle.putSerializable("image_caption_map", imageCaptionMap);
        startActivity(TourGalleryActivity.class, bundle);
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
        mapView.setMarkerDrawable(R.drawable.img_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.img_map_marker_selected);

        // initialize no network view
        viewNoNetwork.hide();
        viewNoNetwork.setNetworkRetryListener(new NoNetworkView.NetworkRetryListener() {
            @Override
            public void onNetworkAvailable() {
                restartActivity();
            }

            @Override
            public void onNetworkNotAvailable() {
            }
        });
    }

    private void showTourDetails(Attraction attraction) {
        this.attraction = attraction;

        loadFeaturedImage();
        loadPoiMap();
        loadTagTypes();
        togglePreviewPlayerButton();

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

    // declared here because it is specific to method under this
    private Callback featuredImageLoadCallback = new Callback() {
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
    };

    private void loadFeaturedImage() {
        RequestCreator requestCreator = (attraction.getBluePrintPath() != null && tourAction != TourAction.DOWNLOADING)
                ? Picasso.with(this).load(new File(attraction.getFeaturedImage().getPath()))
                : Picasso.with(this).load(attraction.getFeaturedImage().getUrl());

        requestCreator.placeholder(R.drawable.placeholder_ratio_1_1)
                .error(R.drawable.placeholder_ratio_1_1)
                .resize(600, 600)
                .into(imageFeatured, featuredImageLoadCallback);
    }

    // declared here because it is specific to method under this, target instance got garbage collected,
    // that's why it should be an instance variable
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

    private void loadPoiMap() {
        if (!isMapShown) {
            RequestCreator requestCreator = (attraction.getBluePrintPath() != null && tourAction == TourAction.START)
                    ? Picasso.with(this).load(new File(attraction.getBluePrintPath()))
                    : Picasso.with(this).load(attraction.getBlueprintUrl());
            requestCreator.into(mapViewPicassoTarget);
        }
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
            case TourAction.START:
                //TODo : If audio is not present toast an error message.
                return ( validAudio()) ? new TourStartAction(buttonTourActon) : null;
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
//TODo : checking each poi for audio.
    private boolean validAudio() {
        for (Poi poi : attraction.getPois()) {
            if (poi.getAudio() == null) {
                bakery.toastShort("Data error");
                return false;
            }
        }
        return true;
    }

    private void playPreview() {
        previewPlayer = new MediaPlayer();

        String dataSource = attraction.getPreviewAudio().getPath();

        if (dataSource == null || !new File(dataSource).exists()) {
            previewPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            dataSource = attraction.getPreviewAudio().getEncUrl();
        }

        try {
            previewPlayer.setDataSource(dataSource);
            previewPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // preview can be playing from url, that is why prepareAsync and OnPreparedListener
        previewPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                previewPlayer.start();
                togglePreviewPlayerButton();
            }
        });

        previewPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPreview();
            }
        });
    }

    private void stopPreview() {
        if (previewPlayer != null && previewPlayer.isPlaying()) {
            previewPlayer.stop();
            previewPlayer = null;
        }

        togglePreviewPlayerButton();
    }

    private void togglePreviewPlayerButton() {
        String buttonText = (previewPlayer != null && previewPlayer.isPlaying())
                ? PREVIEW_BUTTON_TEXT_STOP
                : PREVIEW_BUTTON_TEXT_PLAY;
        buttonPreviewPlayer.setText(buttonText);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = ContextCompat.getColor(this, R.color.primary_dark);
        int primary = ContextCompat.getColor(this, R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
    }

    private void shareAttraction() {
        if (attraction == null) {
            bakery.toastShort("Nothing to share !");
            return;
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Ithaka Tales - " + attraction.getName());
        i.putExtra(Intent.EXTRA_TEXT, Config.SHARE_TOUR_URL_BASE + attraction.getId());
        startActivity(Intent.createChooser(i, "Share Ithaka - " + attraction.getName()));
    }

}
