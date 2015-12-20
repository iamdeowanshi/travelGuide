package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.data.model.Attraction;

/**
 * @author Farhan Ali
 */
public abstract class TourAction {

    public static final int DOWNLOAD      = 1;
    public static final int START         = 3;
    public static final int RETRY         = 4;
    public static final int UPDATE        = 5;
    public static final int DELETE        = 6;

    public String text;
    public boolean enable;

    protected Button button;

    public TourAction(Button button) {
        this.button = button;
    }

    public abstract void perform(Attraction attraction);

    public void init() {
        button.setText(text);
        button.setEnabled(enable);
    }

}
