package com.ithakatales.android.ui.activity.test;

import android.os.Bundle;

import com.google.gson.Gson;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.AttractionAccess;
import com.ithakatales.android.data.model.AttractionRating;
import com.ithakatales.android.util.JsonUtil;
import com.ithakatales.android.util.RxUtil;

import javax.inject.Inject;

import butterknife.OnClick;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ApiTestActivity extends BaseActivity {

    private static final String AUTHORIZATION = "Baerer ASDFhbs348SDF82masdfiu32ADF8w3nsd";

    @Inject IthakaApi ithakaApi;
    @Inject Gson gson;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private ApiObserver observer = new ApiObserver() {
        @Override
        public void onResult(Object result) {
            String response = gson.toJson(result);
            startResponseActivity(JsonUtil.formatJson(response));
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "RETROFIT ERROR");
            startResponseActivity("ERROR: \n---------------------\n" + e.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_api);

        // call to inject dependencies
        injectDependencies();
    }

    @OnClick(R.id.btn_list_cities)
    void listCities() {
        subscribeForNetwork(ithakaApi.getCities(), observer);
    }

    @OnClick(R.id.btn_list_attractions)
    void listAttractions() {
        subscribeForNetwork(ithakaApi.getAttractions(1), observer);
    }

    @OnClick(R.id.btn_view_attraction)
    void viewAttraction() {
        subscribeForNetwork(ithakaApi.getAttraction(1), observer);
    }

    @OnClick(R.id.btn_list_updates)
    void listUpdates() {
        subscribeForNetwork(ithakaApi.getAttractionUpdates(AUTHORIZATION, 1), observer);
    }

    @OnClick(R.id.btn_create_download)
    void createAttractionDownload() {
        AttractionAccess body = new AttractionAccess(1, 1);
        subscribeForNetwork(ithakaApi.attractionDownloaded(AUTHORIZATION, body), observer);
    }

    @OnClick(R.id.btn_create_view)
    void createAttractionView() {
        AttractionAccess body = new AttractionAccess(1, 1);
        subscribeForNetwork(ithakaApi.attractionViewed(AUTHORIZATION, body), observer);
    }

    @OnClick(R.id.btn_rate_attraction)
    void rateAttraction() {
        AttractionRating body = new AttractionRating(1, 1, 4);
        subscribeForNetwork(ithakaApi.rateAttraction(AUTHORIZATION, body), observer);
    }

    private void startResponseActivity(String response) {
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        startActivity(ApiTestResultActivity.class, bundle);
    }

    private void subscribeForNetwork(Observable resultObservable, ApiObserver apiObserver) {
        RxUtil.subscribeForNetwork(subscriptions, resultObservable, apiObserver);
    }

}
