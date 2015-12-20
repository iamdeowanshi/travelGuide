package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;

public class TourDeleteAction extends TourAction {

    TourDetailPresenter presenter;

    public TourDeleteAction(Button button, TourDetailPresenter presenter) {
        super(button);

        this.presenter = presenter;

        text = "Delete Tour";
        enable = true;

        init();
    }

    @Override
    public void perform(Attraction attraction) {
        presenter.deleteAttraction(attraction);
    }

}