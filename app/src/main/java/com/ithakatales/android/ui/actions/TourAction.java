package com.ithakatales.android.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.ui.activity.TourPlayerActivity;

/**
 * @author Farhan Ali
 */
public abstract class TourAction {

    public static final int DOWNLOAD      = 1;
    public static final int DOWNLOADING   = 2;
    public static final int START         = 3;
    public static final int RETRY         = 4;
    public static final int UPDATE        = 5;
    public static final int DELETE        = 6;

    public String text;
    public boolean enable;

    protected Button button;
    protected Context activityContext;

    public TourAction(Button button) {
        this.button = button;
        activityContext = button.getContext();
    }

    public abstract void perform(Attraction attraction);

    public void init() {
        button.setText(text);
        button.setEnabled(enable);
    }

    protected void startTour(Attraction attraction) {
        Intent intent = new Intent(activityContext, TourPlayerActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("attraction_id", attraction.getId());
        intent.putExtras(bundle);
        activityContext.startActivity(intent);
    }

}
