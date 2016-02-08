package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;

public class TourDownloadRetryAction extends TourAction {

    private TourDetailPresenter presenter;

    public TourDownloadRetryAction(Button button, TourDetailPresenter presenter) {
        super(button);
        this.presenter = presenter;
        text = "RETRY DOWNLOAD";
        enable = true;
        init();
    }

    @Override
    public void perform(Attraction attraction) {
        presenter.retryDownloadAttraction(attraction);
    }

}