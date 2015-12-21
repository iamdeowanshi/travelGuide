package com.ithakatales.android.ui.actions;

import android.widget.Button;
import android.widget.Toast;

import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.Attraction;

public class TourStartAction extends TourAction {

    private BaseActivity activity;

    public TourStartAction(Button button, BaseActivity activity) {
        super(button);
        this.activity = activity;
        text = "Start Tour";
        enable = true;

        init();
    }

    @Override
    public void perform(Attraction attraction) {
        // start tour here
        Toast.makeText(activity, "Under Development !", Toast.LENGTH_SHORT).show();
    }

}