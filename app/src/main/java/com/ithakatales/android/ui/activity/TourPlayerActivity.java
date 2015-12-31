package com.ithakatales.android.ui.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import com.ithakatales.android.player.EncryptedAudioHttpServer;
import com.ithakatales.android.player.PlayerDurationUtil;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;
import com.ithakatales.android.ui.adapter.PlayListRecyclerAdapter;
import com.ithakatales.android.ui.adapter.PlaylistItemClickListener;
import com.ithakatales.android.ui.custom.VerticalSpaceItemDecoration;
import com.ithakatales.android.util.Bakery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourPlayerActivity extends BaseActivity implements PlaylistItemClickListener, MediaPlayer.OnCompletionListener {

    public static final String URL_FORMAT = "http://127.0.0.1:%d%s?key=%s&iv=%s";
    public static final String KEY = "tecsolsoftwarepvtltdbangalorekar";
    public static final String IV = "tecsolbangalorek";

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
    @Bind(R.id.progress) ProgressBar progressBar;
    @Bind(R.id.text_duration_played) TextView textDurationPlayed;
    @Bind(R.id.text_duration_total) TextView textDurationTotal;
    @Bind(R.id.text_audio_name) TextView textAudioName;
    @Bind(R.id.button_play_pause) Button buttonPlayPause;
    @Bind(R.id.button_rewind) Button buttonRewind;
    @Bind(R.id.button_skip) Button buttonSkip;
    @Bind(R.id.button_playlist) Button buttonPlayList;

    private Menu menu;
    private Attraction attraction;
    private List<Audio> audios;
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
        audios = audioRepo.readAll();
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(this));
        recyclerAudioList.addItemDecoration(new VerticalSpaceItemDecoration(5));
        PlayListRecyclerAdapter playlistAdapter = new PlayListRecyclerAdapter(audios, this);
        recyclerAudioList.setAdapter(playlistAdapter);

        // By default play first song
        if (audios.size() > 0) {
            playSong(0);
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
    protected void onDestroy() {
        super.onDestroy();
        cancelNotification();
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

    //----------------------------------------------------------------------------------
    // Audio player related methods
    //----------------------------------------------------------------------------------

    @Override
    public void onAudioItemClick(int position) {
        togglePlayListButtonVisibility();
        currentAudioIndex = position;
        playSong(currentAudioIndex);
    }

    @OnClick(R.id.button_playlist)
    void onButtonPlaylistClick() {
        // animation on visibility change
        /*int animateFrom = recyclerAudioList.isShown() ? recyclerAudioList.getHeight() : 0;
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, animateFrom);
        animate.setDuration(300);
        animate.setFillAfter(true);
        recyclerAudioList.startAnimation(animate);*/
        togglePlayListButtonVisibility();
    }

    @OnClick(R.id.button_play_pause)
    void onButtonPlayPauseClick() {
        // check for already playing
        if (mediaPlayer == null) {
            playSong(currentAudioIndex);
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
            playSong(currentAudioIndex + 1);
            currentAudioIndex = currentAudioIndex + 1;
        } else {
            // play first song
            playSong(0);
            currentAudioIndex = 0;
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
        if (currentAudioIndex < (audios.size() - 1)) {
            playSong(currentAudioIndex + 1);
            currentAudioIndex ++;
        } else {
            // play first song
            playSong(0);
            currentAudioIndex = 0;
        }
    }

    private void playSong(int audioIndex) {
        if (audioIndex >= audios.size()) return;

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        EncryptedAudioHttpServer server = new EncryptedAudioHttpServer();
        server.start();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        Audio audio = audios.get(audioIndex);

        showNotification(audio);

        try {
            String filePath = audio.getPath();
            String url = String.format(URL_FORMAT, EncryptedAudioHttpServer.SERVER_PORT, filePath, KEY, IV);
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
    }

    /**
     * Update timer on progress bar
     */
    private void updateProgressBar() {
        progressHandler.postDelayed(progressUpdateTask, 100);
    }

    private void togglePlayListButtonVisibility() {
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
        notificationManager.notify(999, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(999);
    }

}
