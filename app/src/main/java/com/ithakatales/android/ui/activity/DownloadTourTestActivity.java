package com.ithakatales.android.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.download.TourDownload;
import com.ithakatales.android.download.TourDownloadObserver;
import com.ithakatales.android.download.TourDownloadService;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.JsonUtil;
import com.ithakatales.android.util.RxUtil;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DownloadTourTestActivity extends BaseActivity implements TourDownloadObserver {

    @Inject IthakaApi ithakaApi;
    @Inject Gson gson;
    @Inject AttractionRepository attractionRepo;
    @Inject Bakery bakery;

    @Bind(R.id.btn_download_tour) Button btnDownload;
    @Bind(R.id.txt_response) TextView txtResponse;

    private TourDownloadService tourDownloadService;
    private boolean isTourDownloadServiceBound = false;

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
    }

    @OnClick(R.id.btn_download_tour)
    void downloadTour() {
        RxUtil.subscribeForNetwork(subscriptions, ithakaApi.getAttraction(1), new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction attraction) {
                btnDownload.setText("Tour Downloaded: " + attraction.getName());
                bakery.snackShort(getContentView(), attraction.getName());
                attractionRepo.save(attraction);
                TourDownload tourDownload = tourDownloadService.download(attraction);
                tourDownloadService.subscribe(attraction.getId(), DownloadTourTestActivity.this);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, e.getMessage());
            }
        });
    }

    private void bindDownloadService() {
        Intent intent = new Intent(this, TourDownloadService.class);
        // ISSUE: call bindService on application context is MUST instead of activity context
        // Refer: http://stackoverflow.com/questions/7875131/onserviceconnected-not-called
        getApplicationContext().bindService(intent, downloadServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void downloadStatusChanged(TourDownload download) {
        String json = gson.toJson(download);
        txtResponse.setText(JsonUtil.formatJson(json));
    }

}
