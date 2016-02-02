package com.ithakatales.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Sibi on 22/01/16.
 */
public class PlayPreviewButton extends ImageView {

    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public PlayPreviewButton(Context context) {
        super(context);
    }

    public PlayPreviewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayPreviewButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
