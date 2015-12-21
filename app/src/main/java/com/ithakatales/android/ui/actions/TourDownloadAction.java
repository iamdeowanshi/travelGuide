package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;

public class TourDownloadAction extends TourAction {

    TourDetailPresenter presenter;

    public TourDownloadAction(Button button, TourDetailPresenter presenter) {
        super(button);
        this.presenter = presenter;
        text = "Download Tour";
        enable = true;
        init();
    }

    @Override
    public void perform(Attraction attraction) {
        presenter.downloadAttraction(attraction);
    }

}