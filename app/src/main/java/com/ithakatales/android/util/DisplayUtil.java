package com.ithakatales.android.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.ithakatales.android.app.di.Injector;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class DisplayUtil {

    @Inject Context context;

    public DisplayUtil() {
        Injector.instance().inject(this);
    }

    public int getWidth() {
        return getSize().x;
    }

    public int getHeight() {
        return getSize().y;
    }

    public Point getSize() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

}
