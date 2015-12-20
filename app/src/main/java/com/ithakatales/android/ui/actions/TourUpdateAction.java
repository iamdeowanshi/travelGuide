package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;

public class TourUpdateAction extends TourAction {

    TourDetailPresenter presenter;

    public TourUpdateAction(Button button, TourDetailPresenter presenter) {
        super(button);
        this.presenter = presenter;
        text = "Update Tour";
        enable = true;
        init();
    }

    @Override
    public void perform(Attraction attraction) {
        presenter.updateAttraction(attraction);
    }

}