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
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.UserPreference;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    @Inject UserPreference userPreference;
    @Inject PreferenceUtil preferenceUtil;
    @Inject DialogUtil dialogUtil;

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
    @Bind(R.id.expandable_text_before_you_go) ExpandableTextView expandableTextBeforeYouGo;
    @Bind(R.id.expandable_text_credits) ExpandableTextView expandableTextCredits;

    @Bind(R.id.view_tag_type_one) View viewTagTypeOne;
    @Bind(R.id.view_tag_type_two) View viewTagTypeTwo;
    @Bind(R.id.view_tag_type_three) View viewTagTypeThree;

    @Bind(R.id.button_tour_action) Button buttonTourActon;
    @Bind(R.id.view_loading) RelativeLayout viewLoading;

    @Bind(R.id.view_no_network) NoNetworkView viewNoNetwork;

    private Attraction attraction;
    private int tourAction;
    private int lastProgress;
    private boolean isMapShown = false;
    private boolean isPreviewPlaying;

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
        this.attraction = attraction;

        if ( ! isAttractionValid()) {
            showAttractionError();
            return;
        }

        this.tourAction = tourAction;
        showTourDetails();
        buttonTourActon.setTag(getTourAction(tourAction));

        if( userPreference.readUser() == null) {
            buttonTourActon.setTag(getTourAction(1));
        }
    }

    @Override
    public void onDownloadProgressChange(final TourDownloadProgress download) {
        if (download.getProgress() < lastProgress) return;

        buttonTourActon.setText(String.format("Downloading (%d%%)", download.getProgress()));
        Timber.d(String.format("total: %d | downloaded: %d | progress: %d | status: %d",
                download.getBytesTotal(), download.getBytesDownloaded(),
                download.getProgress(), download.getStatus()));

        lastProgress = download.getProgress();
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

    @OnClick(R.id.button_preview_player)
    void onPreviewPlayerClick() {
        if (isPreviewPlaying) {
            stopPreview();
            return;
        }

        playPreview();
    }

    @OnClick(R.id.text_view_full)
    void onMapClick() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("attraction", Parcels.wrap(attraction));
        startActivity(TourMapActivity.class, bundle);
    }

    @OnClick(R.id.button_tour_action)
    void onTourActionClick() {
        TourAction action = (TourAction) buttonTourActon.getTag();

        if (action == null) return;

        if (action instanceof TourStartAction) {
            stopPreview();
        }

        action.perform(attraction);
    }

    @OnClick(R.id.image_featured)
    void onFeaturedImageClick() {
        if (attraction == null) return;

        Bundle bundle = new Bundle();
        bundle.putParcelable("attraction", Parcels.wrap(attraction));
        boolean isLoadFromUrl = tourAction != TourAction.START;
        bundle.putBoolean("is_load_from_url", isLoadFromUrl);
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

    private void showTourDetails() {
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
        textDuration.setText(AttractionUtil.attractionDurationToString(attraction.getDuration()));

        // load text details
        textDescription.setText(attraction.getShortDescription());
        expandableTextKnowMore.setText(attraction.getLongDescription());
        expandableTextBeforeYouGo.setBackgroundColor(Color.TRANSPARENT);
        expandableTextBeforeYouGo.setText(attraction.getBeforeYouGo());
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
                return new TourStartAction(buttonTourActon);
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

    private boolean isAttractionValid() {
        for (Poi poi : attraction.getPois()) {
            if (poi.getAudio() == null) {
                return false;
            }
        }
        return true;
    }

    private void showAttractionError() {
        dialogUtil.setTitle("Data Error")
                .setMessage("Opps ! Seems we had hiccup, but things will return to normal soon! Hang in there !")
                .setPositiveButtonText("Ok")
                .setDialogClickListener(new DialogUtil.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        finish();
                    }

                    @Override
                    public void onNegativeClick() {}
                }).show(this);
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
                isPreviewPlaying = true;
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
            isPreviewPlaying = false;
        }

        togglePreviewPlayerButton();
    }

    private void togglePreviewPlayerButton() {
         int buttonId = (previewPlayer != null && previewPlayer.isPlaying())
                ? R.drawable.btn_stop_preview
                : R.drawable.btn_play_preview;
        buttonPreviewPlayer.setBackgroundResource(buttonId);
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
        //TODO:  Custom share message
        String message = "Check this out\n" + Config.SHARE_TOUR_URL_BASE + attraction.getId();

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Ithaka Tales - " + attraction.getName());
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, "Share Ithaka - " + attraction.getName()));
    }

}
