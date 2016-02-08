package com.ithakatales.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;

import com.ithakatales.android.R;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.TourFinishPresenter;
import com.ithakatales.android.presenter.TourFinishViewInteractor;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author farhanali
 */
public class TourFinishActivity extends BaseActivity implements TourFinishViewInteractor {

    @Inject TourFinishPresenter presenter;
    @Inject UserPreference preference;
    @Inject Bakery bakery;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.rating_bar) RatingBar ratingBar;

    private long attractionId;
    private String attractionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_finish);
        injectDependencies();

        presenter.setViewInteractor(this);

        attractionName = getIntent().getStringExtra("attraction_name");
        attractionId = getIntent().getLongExtra("attraction_id", 0);

        // setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(attractionName);
        }
    }

    @Override
    public void onTourRated() {
        bakery.toastShort("Rating Done..");
    }

    @OnClick(R.id.button_share)
    void onShareClick() {
        shareAttraction();
    }

    @OnClick(R.id.text_submit)
    void onSubmitClick() {
        rateAttraction();
    }

    @OnClick(R.id.text_skip)
    void onSkipClick() {
        startActivityClearTop(HomeActivity.class, null);
    }

    private void rateAttraction() {
        // TODO: 08/01/16
        User user = preference.readUser();

        if (user == null) {
            bakery.toastShort("You should be logged in for rating..");
            return;
        }

        presenter.rateTour(user, attractionId, (int) ratingBar.getRating());
    }

    private void shareAttraction() {
        if (attractionName == null) {
            bakery.toastShort("Nothing to share !");
            return;
        }

        // TODO:  Custom share message
        String message = "I have rated " + attractionName + " with a " + (int) ratingBar.getRating() + " on 5.\nCheck this out - " + Config.SHARE_TOUR_URL_BASE + attractionId;

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Ithaka Tales - " + attractionName);
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, "Share Ithaka - " + attractionName));
    }

}
