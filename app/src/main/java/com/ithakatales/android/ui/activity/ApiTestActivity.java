package com.ithakatales.android.ui.activity;

import android.os.Bundle;

import com.google.gson.Gson;
import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.AttractionDownload;
import com.ithakatales.android.data.model.AttractionRating;
import com.ithakatales.android.data.model.AttractionView;
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
            startResponseActivity(formatJson(response));
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
        setContentView(R.layout.activity_api_test);

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
        AttractionDownload body = new AttractionDownload(1, 1);
        subscribeForNetwork(ithakaApi.attractionDownloaded(AUTHORIZATION, body), observer);
    }

    @OnClick(R.id.btn_create_view)
    void createAttractionView() {
        AttractionView body = new AttractionView(1, 1);
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

    private String formatJson(String text){

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

}
