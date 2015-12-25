package com.ithakatales.android.ui.actions;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.DialogUtil;

import javax.inject.Inject;

public class TourStartAction extends TourAction {

    @Inject Bakery bakery;

    public TourStartAction(Button button) {
        super(button);
        Injector.instance().inject(this);

        text = "Start Tour";
        enable = true;

        init();
    }

    @Override
    public void perform(Attraction attraction) {
        // start tour here
        bakery.toastShort("Under Development !");
    }

}