package com.ithakatales.android.ui.activity.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.data.model.ImageDownload;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioDownloadRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageDownloadRepository;
import com.ithakatales.android.data.repository.ImageRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.TourDownloadObserver;
import com.ithakatales.android.download.TourDownloadService;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DownloadTourTestActivity extends BaseActivity implements TourDownloadObserver, ServiceConnection {

    private static final String BTN_TEXT_DOWNLOAD       = "Download Tour";
    private static final String BTN_TEXT_DOWNLOADING    = "Downloading";
    private static final String BTN_TEXT_START          = "Start Tour";
    private static final String BTN_TEXT_NO_CONNECTION  = "No Connection ! Retry";
    private static final String BTN_TEXT_FAILED         = "Download Failed! Retry";

    @Inject IthakaApi ithakaApi;

    @Inject AttractionRepository attractionRepo;
    @Inject TourDownloadRepository tourDownloadRepo;
    @Inject AudioRepository audioRepo;
    @Inject AudioDownloadRepository audioDownloadRepo;
    @Inject ImageRepository imageRepo;
    @Inject ImageDownloadRepository imageDownloadRepo;

    @Inject Bakery bakery;
    @Inject ConnectivityUtil connectivityUtil;

    @Bind(R.id.btn_download_tour) Button btnDownload;
    @Bind(R.id.txt_tour_name) TextView txtTourName;
    @Bind(R.id.txt_tour_description) TextView txtDescription;
    @Bind(R.id.txt_toure_progress) TextView txtTourProgress;
    @Bind(R.id.txt_message) TextView txtMessage;
    @Bind(R.id.txt_info) TextView txtInfo;
    @Bind(R.id.progress) ProgressBar progressBar;

    private TourDownloadService tourDownloadService;
    private boolean isTourDownloadServiceBound = false;

    private Attraction attraction;
    private TourDownload tourDownload;

    private long attractionId;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_download_tour);

        injectDependencies();

        attractionId = 1;

        startDownloadService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindDownloadService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindDownloadService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxUtil.unsubscribe(subscriptions);
    }

    @OnClick(R.id.btn_download_tour)
    void downloadTour() {
        switch (btnDownload.getText().toString()) {
            case BTN_TEXT_DOWNLOAD:
                if (attraction == null || ! connectivityUtil.isConnected()) {
                    setConnectivityAction();
                    break;
                }
                setButtonAction(BTN_TEXT_DOWNLOADING, false);
                attractionRepo.save(attraction);
                tourDownload = tourDownloadService.download(attraction);
                tourDownloadService.subscribe(attraction.getId(), this);
                break;
            case BTN_TEXT_NO_CONNECTION:
                loadAttraction(attractionId);
                break;
            case BTN_TEXT_START:
                bakery.snackShort(getContentView(), "Under construction !");
                break;
            case BTN_TEXT_FAILED:
                bakery.snackShort(getContentView(), "Under construction !");
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        tourDownloadService = ((TourDownloadService.DownloadServiceBinder) binder).getService();
        isTourDownloadServiceBound = true;
        serviceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        tourDownloadService = null;
        isTourDownloadServiceBound = false;
    }

    @Override
    public void downloadStatusChanged(TourDownload download) {
        tourDownload = download;
        if (tourDownload.getProgress() % 5 == 0) {
            updateTourDetails();
        }
    }

    private void startDownloadService() {
        Intent intent = new Intent(this, TourDownloadService.class);
        startService(intent);
    }

    // ISSUE: call bindService on application context is MUST instead of activity context
    // Refer: http://stackoverflow.com/questions/7875131/onserviceconnected-not-called
    private void bindDownloadService() {
        if ( ! isTourDownloadServiceBound) {
            Intent intent = new Intent(this, TourDownloadService.class);
            isTourDownloadServiceBound = getApplicationContext().bindService(
                    intent, this, BIND_AUTO_CREATE);
        }
    }

    private void unbindDownloadService() {
        getApplicationContext().unbindService(this);
        isTourDownloadServiceBound = false;
    }

    private void serviceConnected() {
        loadAttraction(attractionId);
    }

    private void loadAttraction(long attractionId) {
        loadAttractionFromDb(attractionId);

        if (isAttractionExist()) {
            loadAttractionOffline();
            setAttractionInfo();
            return;
        }

        setConnectivityAction();
        loadAttractionOnline(attractionId);
    }

    private void loadAttractionOffline() {
        hideProgress();
        updateAttractionDetails();
        loadTourDownloadFromDb(attraction.getId());
        updateTourDetails();
    }

    private void loadAttractionOnline(long attractionId) {
        showProgress();
        RxUtil.subscribeForNetwork(subscriptions, ithakaApi.getAttraction(attractionId), new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction attraction) {
                hideProgress();
                setAttraction(attraction);
                setAttractionInfo();
                updateAttractionDetails();
                setDownloadTourAction();
            }

            @Override
            public void onError(Throwable e) {
                hideProgress();
                Timber.e(e, e.getMessage());
                bakery.snackShort(getContentView(), e.getMessage());
                setConnectivityAction();
            }
        });
    }

    private void loadAttractionFromDb(long attractionId) {
        attraction = attractionRepo.find(attractionId);
    }

    private boolean isAttractionExist() {
        return attraction != null;
    }

    private void loadTourDownloadFromDb(long attractionId) {
        tourDownload = tourDownloadRepo.findByAttractionId(attractionId);
    }

    private boolean isDownloadExist() {
        return tourDownload != null;
    }

    private void updateAttractionDetails() {
        txtTourName.setText(attraction.getName());
        txtDescription.setText(attraction.getShortDescription());
    }

    private void updateTourDetails() {
        if( ! isDownloadExist()) {
            setDownloadTourAction();
            return;
        }

        updateTourDownloadProgress();
        updateProgressMessage();

        if (isDownloadCompleted()) {
            setStartTourAction();
            return;
        }

        tourDownloadService.subscribe(attraction.getId(), this);
        setDownloadingTourAction();
    }

    private void updateTourDownloadProgress() {
        if (tourDownload == null) return;

        txtTourProgress.setText(tourDownload.getProgress() + " %");
    }

    private void updateProgressMessage() {
        String audioProgressMessage = getAudioProgressMessage();
        String imageProgressMessage = getImageProgressMessage();
        String line = "\n------------------------------\n";

        int totalAudioProgress = audioDownloadRepo.getTotalProgressByTour(tourDownload.getId());
        int totalImageProgress = imageDownloadRepo.getTotalProgressByTour(tourDownload.getId());

        String audiosTitle = "\n Audios (" + totalAudioProgress + "%)";
        String imagesTitle = "\n Images (" + totalImageProgress + "%)";

        String message = audiosTitle + line + audioProgressMessage + "\n"
                + imagesTitle + line + imageProgressMessage;
        txtMessage.setText(message);
    }

    private String getAudioProgressMessage() {
        if (tourDownload == null) return "";

        List<AudioDownload> audioDownloads = audioDownloadRepo.readByTourId(tourDownload.getId());

        String message = "";
        for (AudioDownload audioDownload : audioDownloads) {
            Audio audio = audioRepo.find(audioDownload.getAudioId());
            message += audio.getName() + " : " + audioDownload.getProgress() + "% \n";
        }

        return message;
    }

    private String getImageProgressMessage() {
        if (tourDownload == null) return "";

        List<ImageDownload> imageDownloads = imageDownloadRepo.readByTourId(tourDownload.getId());

        String message = "";
        for (ImageDownload imageDownload : imageDownloads) {
            Image image = imageRepo.find(imageDownload.getImageId());
            message += image.getName() + " : " + imageDownload.getProgress() + "% \n";
        }

        return message;
    }

    private void setAttractionInfo() {
        if (attraction == null) return;

        String nl = "\n";
        String info = "\nTour Basic Info\n------------------------------\n";;
        info += "Name: " + attraction.getName() + nl;
        info += "Description: " + attraction.getShortDescription() + nl;
        info += "Type: " + attraction.getType().getName() + nl;
        info += "City: " + attraction.getCity().getName() + nl;
        info += "Address Line 1: " + attraction.getAddressLine1() + nl;
        info += "Duration: " + attraction.getDuration() + nl;
        info += "Before you go: " + attraction.getBeforeYouGo() + nl;
        info += "Credits: " + attraction.getCredits() + nl;

        txtInfo.setText(info);
    }

    private void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    private void setDownloadTourAction() {
        setButtonAction(BTN_TEXT_DOWNLOAD, true);
    }

    private void setStartTourAction() {
        setButtonAction(BTN_TEXT_START, true);
    }

    private void setDownloadingTourAction() {
        setButtonAction(BTN_TEXT_DOWNLOADING, false);
    }

    private void setConnectivityAction() {
        if (connectivityUtil.isConnected()) {
            setButtonAction(BTN_TEXT_DOWNLOAD, true);
            return;
        }

        setButtonAction(BTN_TEXT_NO_CONNECTION, true);
    }

    private void setDownloadFailedAction() {
        setButtonAction(BTN_TEXT_FAILED, true);
    }

    private boolean isDownloadCompleted() {
        return tourDownload != null && tourDownload.getProgress() == 100;
    }

    private void setButtonAction(String message, boolean isEnable) {
        btnDownload.setText(message);
        btnDownload.setEnabled(isEnable);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

}
