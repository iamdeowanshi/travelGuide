package com.ithakatales.android.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.ui.activity.TourPlayerActivity;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

public class TourStartAction extends TourAction {

    @Inject Bakery bakery;

    private Context activityContext;

    public TourStartAction(Button button) {
        super(button);
        Injector.instance().inject(this);

        activityContext = button.getContext();

        text = "Start Tour";
        enable = true;

        init();
    }

    @Override
    public void perform(Attraction attraction) {
        activityContext.startActivity(new Intent(activityContext, TourPlayerActivity.class));
    }

}