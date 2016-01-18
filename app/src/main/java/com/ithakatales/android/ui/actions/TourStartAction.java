package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

public class TourStartAction extends TourAction {

    @Inject Bakery bakery;

    public TourStartAction(Button button) {
        super(button);
        Injector.instance().inject(this);

        text = "Begin IthakaTale";
        enable = true;

        init();
    }

    @Override
    public void perform(Attraction attraction) {
        startTour(attraction);
    }

}