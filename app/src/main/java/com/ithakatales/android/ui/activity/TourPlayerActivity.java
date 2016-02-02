package com.ithakatales.android.ui.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.ithakatales.android.R;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.Poi;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.download.TourStorage;
import com.ithakatales.android.map.MapView;
import com.ithakatales.android.map.Marker;
import com.ithakatales.android.player.EncryptedAudioHttpServer;
import com.ithakatales.android.player.PlayerDurationUtil;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;
import com.ithakatales.android.ui.adapter.PlayListRecyclerAdapter;
import com.ithakatales.android.ui.adapter.PlaylistItemClickListener;
import com.ithakatales.android.ui.custom.VerticalSpaceItemDecoration;
import com.ithakatales.android.util.AttractionUtil;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.DialogUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourPlayerActivity extends BaseActivity implements PlaylistItemClickListener,
        MediaPlayer.OnCompletionListener, MapView.MarkerClickListener, GalleryPagerAdapter.NavigationClickListener {

    public static final String URL_FORMAT = "http://127.0.0.1:%d%s?key=%s&iv=%s";

    private static final int NOTIFICATION_ID = 999;
    private static final int REQUEST_CAMERA  = 100;

    @Inject Bakery bakery;
    @Inject ImageRepository imageRepo;
    @Inject AttractionRepository attractionRepo;
    @Inject TourStorage tourStorage;
    @Inject DialogUtil dialogUtil;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.gallery_view_pager) ViewPager galleryPager;
    @Bind(R.id.map_view) MapView mapView;
    @Bind(R.id.recycler_audio_list) RecyclerView recyclerAudioList;

    @Bind(R.id.layout_poi_info) RelativeLayout layoutPoiInfo;
    @Bind(R.id.text_poi_name) TextView textPoiName;
    @Bind(R.id.text_poi_progress) TextView textPoiProgress;

    // player controls
    @Bind(R.id.progress) ProgressBar progressBar;
    @Bind(R.id.text_duration_played) TextView textDurationPlayed;
    @Bind(R.id.text_duration_total) TextView textDurationTotal;
    @Bind(R.id.text_audio_name) TextView textAudioName;
    @Bind(R.id.button_play_pause) Button buttonPlayPause;
    @Bind(R.id.button_rewind) Button buttonRewind;
    @Bind(R.id.button_skip) Button buttonSkip;
    @Bind(R.id.button_playlist) Button buttonPlayList;

    private Menu menu;

    private Map<Marker, Poi> markerPoiMap = new HashMap<>();
    private List<Poi> pois = new ArrayList<>();
    private List<Audio> audios = new ArrayList<>();
    private List<Image> images = new ArrayList<>();

    private GalleryPagerAdapter galleryPagerAdapter;
    private PlayListRecyclerAdapter playlistAdapter;

    private Attraction attraction;
    private int currentAudioIndex = 0;
    private int seekBackwardTime = 10000; // milliseconds

    private MediaPlayer mediaPlayer;
    private Handler progressHandler = new Handler();
    private Runnable progressUpdateTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            textDurationTotal.setText(String.format("%s", PlayerDurationUtil.milliSecondsToTimer(totalDuration)));
            textDurationPlayed.setText(String.format("%s", PlayerDurationUtil.milliSecondsToTimer(currentDuration)));

            // Updating progress bar
            int progress = (int) (PlayerDurationUtil.getProgressPercentage(currentDuration, totalDuration));
            progressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            progressHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_player);
        injectDependencies();

        attraction = attractionRepo.find(getIntent().getLongExtra("attraction_id", 0));
        if (attraction == null) {
            finish();
            return;
        }

        // setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // hide mapView and audio list initially, show gallery
        mapView.setVisibility(View.GONE);
        recyclerAudioList.setVisibility(View.GONE);
        galleryPager.setVisibility(View.VISIBLE);

        // initialize mapView
        mapView.setMarkerDrawable(R.drawable.img_map_marker);
        mapView.setMarkerSelectedDrawable(R.drawable.img_map_marker_selected);
        mapView.setMarkerClickListener(this);
        loadPoiMap();

        // initialize audio list
        audios = AttractionUtil.getAllAudioExceptPreview(attraction);
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this));
        recyclerAudioList.addItemDecoration(new VerticalSpaceItemDecoration(5));
        playlistAdapter = new PlayListRecyclerAdapter(audios, this);
        recyclerAudioList.setAdapter(playlistAdapter);

        // initialize gallery
        galleryPagerAdapter = new GalleryPagerAdapter(images, false);
        galleryPagerAdapter.setNavigationClickListener(this);
        galleryPager.setAdapter(galleryPagerAdapter);

        // By default play first song
        if (audios.size() > 0) {
            playAudio(currentAudioIndex);
        }
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
                startCameraIntent();
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

    @Override public void onBackPressed() {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override public void onPositiveClick() {
                cancelNotification();
                stopPlayer();
                finish();
            }
            @Override public void onNegativeClick() {
            }
        });
        dialogUtil.setTitle("Exit Player")
                .setMessage("Exiting this page will stop the narration, are you sure you want to continue.")
                .setPositiveButtonText("Yes")
                .setNegativeButtonText("No")
                .show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelNotification();
        stopPlayer();
    }

    @Override
    public void onMarkerClicked(Marker marker) {}

    @Override
    public void onMarkerPopoverClicked(Marker marker) {
        int index = 0;
        for (Audio audio : audios) {
            if (audio.getId() == markerPoiMap.get(marker).getAudio().getId()) {
                playAudio(index);
                break;
            }
            index ++;
        }
    }

    @Override
    public void onPreviousClick(int position) {
        galleryPager.setCurrentItem(position - 1);
    }

    @Override
    public void onNextClicked(int position) {
        galleryPager.setCurrentItem(position + 1);
    }

    private void toggleMapAndGalleryOptionVisibility() {
        MenuItem mapItem = menu.findItem(R.id.action_map);
        MenuItem galleryItem = menu.findItem(R.id.action_gallery);
        mapItem.setVisible( ! mapItem.isVisible());
        galleryItem.setVisible( ! galleryItem.isVisible());

        mapView.setVisibility(galleryItem.isVisible() ? View.VISIBLE : View.GONE);
        galleryPager.setVisibility(mapItem.isVisible() ? View.VISIBLE : View.GONE);

        // hid if audio list is shown
        if (recyclerAudioList.isShown()) {
            togglePlayListVisibility();
        }
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

            // copy poi list to another list to detach from realm, then sort based on audio priority

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
                long durationInMinutes = poi.getAudio() != null ? poi.getAudio().getDuration() / 60 : 0;
                Marker marker = new Marker(index, x, y, poi.getName(), String.format("%d Min", durationInMinutes));
                mapView.addMarker(marker);
                mapView.addMarker(marker);
                markerPoiMap.put(marker, poi);
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

    private void updateMapMarkerSelection(Audio audio) {
        for (Marker marker : markerPoiMap.keySet()) {
            if (markerPoiMap.get(marker).getAudio().getId() == audio.getId()) {
                mapView.setSelectedMarker(marker);
                break;
            }
        }
    }

    private void updateGallery(Audio audio) {
        images.clear();
        images.addAll(imageRepo.readByAudioId(audio.getId()));
        galleryPagerAdapter.notifyDataSetChanged();
    }

    private void updateHeaderTexts(Audio audio, int index) {
        if (audio.getPoiId() <= 0) {
            layoutPoiInfo.setVisibility(View.GONE);
            return;
        }

        int poiPosition = 0;
        for (Poi poi : pois) {
            poiPosition ++;

            if (poi.getAudio().getId() != audio.getId()) continue;

            layoutPoiInfo.setVisibility(View.VISIBLE);
            textPoiName.setText(poi.getName());
            textPoiProgress.setText(String.format("%d of %d", poiPosition, pois.size()));
            break;
        }
    }

    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String imageName = "ITK_" + dateFormat.format(new Date()) + ".jpg";
        Uri imageUri = Uri.fromFile(new File(tourStorage.getIthakaCapturedImageDir(), imageName));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //----------------------------------------------------------------------------------
    // Audio player related methods
    //----------------------------------------------------------------------------------

    @Override
    public void onAudioItemClick(int position) {
      //  togglePlayListVisibility();
        playAudio(position);
    }

    @OnClick(R.id.button_playlist)
    void onButtonPlaylistClick() {
        togglePlayListVisibility();
    }

    @OnClick(R.id.button_play_pause)
    void onButtonPlayPauseClick() {
        // check for already playing
        if (mediaPlayer == null) {
            playAudio(currentAudioIndex);
            return;
        }

        // pause if playing
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayPause.setBackground(ContextCompat.getDrawable(this, R.drawable.player_button_play));
            return;
        }

        // resume if paused
        mediaPlayer.start();
        buttonPlayPause.setBackground(ContextCompat.getDrawable(this, R.drawable.player_button_pause));
    }

    @OnClick(R.id.button_skip)
    void onButtonSkipClick() {
        // check if next song is there or not
        if (currentAudioIndex < (audios.size() - 1)) {
            playAudio(currentAudioIndex + 1);
        } else if (currentAudioIndex == audios.size() - 1) {
            //TODo : Complete Player if last audio
            stopPlayer();
            onCompletion(null);
        }
    }

    @OnClick(R.id.button_rewind)
    void onButtonRewindClick() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - seekBackwardTime >= 0) {
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            mediaPlayer.seekTo(0);
        }
    }

    // TODO: 04/01/16 to remove - only for development purpose
    @OnClick(R.id.button_forward)
    void onButtonForwardClick() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition + 10000 <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosition + 10000);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    // @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentAudioIndex == audios.size() - 1) {
            Bundle bundle = new Bundle();
            bundle.putLong("attraction_id", attraction.getId());
            bundle.putString("attraction_name", attraction.getName());
            //TODo : to remove notification
            cancelNotification();
            startActivity(TourFinishActivity.class, bundle);
            return;
        }

        playAudio(currentAudioIndex + 1);
    }

    private void playAudio(int audioIndex) {
        if (audioIndex >= audios.size()) return;

        stopPlayer();

        EncryptedAudioHttpServer server = new EncryptedAudioHttpServer();
        server.start();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        Audio audio = audios.get(audioIndex);

        showNotification(audio);
        updateGallery(audio);
        updateHeaderTexts(audio, audioIndex);
        updateMapMarkerSelection(audio);
        playlistAdapter.setSelectedItemPosition(audioIndex);

        try {
            String filePath = audio.getPath();
            String url = String.format(URL_FORMAT, EncryptedAudioHttpServer.SERVER_PORT, filePath, Config.ENCRYPTION_KEY, Config.ENCRYPTION_IV);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            textAudioName.setText(audio.getName());
            buttonPlayPause.setBackground(ContextCompat.getDrawable(this, R.drawable.player_button_pause));

            // set Progress bar values
            progressBar.setProgress(0);
            progressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Timber.e(e.getMessage(), e);
        }

        currentAudioIndex = audioIndex;
    }

    private void stopPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            progressHandler.removeCallbacks(progressUpdateTask);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Update timer on progress bar
     */
    private void updateProgressBar() {
        progressHandler.postDelayed(progressUpdateTask, 100);
    }

    private void togglePlayListVisibility() {
        recyclerAudioList.setVisibility(recyclerAudioList.isShown() ? View.GONE : View.VISIBLE);
        Drawable playlistButtonBackground = recyclerAudioList.isShown()
                ? ContextCompat.getDrawable(this, R.drawable.player_button_playlist_inset_pressed)
                : ContextCompat.getDrawable(this, R.drawable.player_button_playlist_inset_normal);
        buttonPlayList.setBackground(playlistButtonBackground);
    }

    private void showNotification(Audio audio) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle(attraction.getName())
                        .setContentText(audio.getName() + " is playing");

        Intent toLaunch = new Intent(getApplicationContext(), TourPlayerActivity.class);
        toLaunch.setAction("android.intent.action.MAIN");
        toLaunch.addCategory("android.intent.category.LAUNCHER");

        PendingIntent intentBack = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intentBack);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
