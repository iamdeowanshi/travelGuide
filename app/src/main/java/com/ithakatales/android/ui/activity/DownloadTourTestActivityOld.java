package com.ithakatales.android.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.TourDownloadRepository;
import com.ithakatales.android.download.TourDownloadObserver;
import com.ithakatales.android.download.TourDownloadService;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.RxUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DownloadTourTestActivityOld extends BaseActivity implements TourDownloadObserver {

    private static final String BTN_TEXT_DOWNLOAD       = "Download Tour";
    private static final String BTN_TEXT_DOWNLOADING    = "Downloading";
    private static final String BTN_TEXT_START          = "Start Tour";

    @Inject IthakaApi ithakaApi;
    @Inject Gson gson;
    @Inject AttractionRepository attractionRepo;
    @Inject TourDownloadRepository tourDownloadRepo;
    @Inject Bakery bakery;

    @Bind(R.id.btn_download_tour) Button btnDownload;
    @Bind(R.id.txt_tour_name) TextView txtTourName;
    @Bind(R.id.txt_tour_description) TextView txtDescription;
    @Bind(R.id.txt_toure_progress) TextView txtTourProgress;
    @Bind(R.id.txt_message) TextView txtMessage;
    @Bind(R.id.progress) ProgressBar progressBar;

    private TourDownloadService tourDownloadService;
    private boolean isTourDownloadServiceBound = false;

    private Attraction attraction;
    private TourDownload tourDownload;

    private ServiceConnection downloadServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            tourDownloadService = ((TourDownloadService.DownloadServiceBinder) binder).getService();
            isTourDownloadServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isTourDownloadServiceBound = false;
        }
    };

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_tour_test);

        injectDependencies();

        bindDownloadService();

        loadTourData(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        tourDownloadService.unSubscribe(attraction.getId());
    }

    @OnClick(R.id.btn_download_tour)
    void downloadTour() {
        switch (btnDownload.getText().toString()) {
            case BTN_TEXT_DOWNLOAD:
                setDownloadButton(BTN_TEXT_DOWNLOADING, false);
                TourDownload tourDownload = tourDownloadService.download(attraction);
                tourDownloadService.subscribe(attraction.getId(), this);
                break;
            case BTN_TEXT_START:
                bakery.snackLong(getContentView(), "Under construction !");
                break;
        }
    }

    @Override
    public void downloadStatusChanged(TourDownload download) {
        //String json = gson.toJson(download);
        //txtMessage.setText(JsonUtil.formatJson(json));
        txtTourProgress.setText(download.getProgress() + " %");
        if (download.getProgress() == 100) {
            setDownloadButton(BTN_TEXT_START, true);
        }
    }

    void loadTourData(long attractionId) {
        showProgress();

        tourDownload = tourDownloadRepo.findByAttractionId(attractionId);
        if (tourDownload != null) {
            attraction = attractionRepo.find(tourDownload.getId());
            setAttractionDetails(attraction);
            hideProgress();
            return;
        }

        loadTourFromApi(attractionId);
    }

    void loadTourFromApi(long attractionId) {
        RxUtil.subscribeForNetwork(subscriptions, ithakaApi.getAttraction(attractionId), new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction attraction) {
                hideProgress();
                setAttractionDetails(attraction);
                attractionRepo.save(attraction);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, e.getMessage());
                bakery.snackShort(getContentView(), e.getMessage());
                hideProgress();
            }
        });
    }

    private void bindDownloadService() {
        Intent intent = new Intent(this, TourDownloadService.class);
        // ISSUE: call bindService on application context is MUST instead of activity context
        // Refer: http://stackoverflow.com/questions/7875131/onserviceconnected-not-called
        getApplicationContext().bindService(intent, downloadServiceConnection, BIND_AUTO_CREATE);
    }

    private void setAttractionDetails(Attraction attraction) {
        this.attraction = attraction;
        txtTourName.setText(attraction.getName());
        txtDescription.setText(attraction.getShortDescription());
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setDownloadButton(String message, boolean isEnable) {
        btnDownload.setText(message);
        btnDownload.setEnabled(isEnable);
    }

}
