package com.ithakatales.android.ui.custom;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ithakatales.android.R;

/**
 * @author Farhan Ali
 */
public class LoadingView extends RelativeLayout {

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private void init() {
        if (isInEditMode()) return;

        inflate(getContext(), R.layout.view_loading, this);
        TextView textLoading = (TextView) findViewById(R.id.text_loading);

        ObjectAnimator textColorAnim = ObjectAnimator.ofInt(textLoading, "textColor", Color.DKGRAY, Color.TRANSPARENT);
        textColorAnim.setDuration(800);
        textColorAnim.setEvaluator(new ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim.start();
    }

}
