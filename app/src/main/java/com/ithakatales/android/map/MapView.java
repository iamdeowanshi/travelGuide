package com.ithakatales.android.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author farhanali
 */
public class MapView extends SubsamplingScaleImageView {

    private MarkerClickListener markerClickListener = MarkerClickListener.DEFAULT;

    private int markerDrawable;
    private int markerSelectedDrawable;

    private List<Marker> markers = new ArrayList<>();
    private Marker selectedMarker;
    private Marker selectedPopoverMarker;

    private Bitmap markerBitmap;
    private Bitmap markerSelectedBitmap;

    private Paint paint;

    public MapView(Context context) {
        this(context, null);

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public MapView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ( ! isReady()) {
            return;
        }

        drawMarkers(canvas);
        drawPopovers(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return super.onTouchEvent(event);

        PointF viewCoordinate = viewToSourceCoord(event.getX(), event.getY());
        boolean markerTouch = false;
        boolean popoverTouch = notifyIfMarkerPopoverTouch((int) viewCoordinate.x, (int) viewCoordinate.y);
        if ( ! popoverTouch) {
            markerTouch = notifyIfMarkerTouch((int) viewCoordinate.x, (int) viewCoordinate.y);
        }

        return super.onTouchEvent(event);
    }

    public void addMarker(Marker marker) {
        markers.add(marker);
        reDraw();
    }

    public void setMarkerClickListener(MarkerClickListener markerClickListener) {
        this.markerClickListener = markerClickListener;
    }

    public void setMarkerDrawable(int markerDrawable) {
        this.markerDrawable = markerDrawable;
    }

    public void setMarkerSelectedDrawable(int markerSelectedDrawable) {
        this.markerSelectedDrawable = markerSelectedDrawable;
    }

    public void reDraw() {
        scaleMarkerBitmaps();
        invalidate();
    }

    public void setSelectedMarker(Marker marker) {
        // remove existing selection
        if (selectedMarker != null) {
            selectedMarker.setSelected(false);
        }

        marker.setSelected(true);
        selectedMarker = marker;
        selectedPopoverMarker = marker;
        reDraw();
    }

    private void scaleMarkerBitmaps() {
        markerBitmap = BitmapFactory.decodeResource(this.getResources(), markerDrawable);
        markerSelectedBitmap = BitmapFactory.decodeResource(this.getResources(), markerSelectedDrawable);

        float density = getResources().getDisplayMetrics().densityDpi;

        float w = (density / 350f) * markerBitmap.getWidth();
        float h = (density / 350f) * markerBitmap.getHeight();
        markerBitmap = Bitmap.createScaledBitmap(markerBitmap, (int) w, (int) h, true);

        w = (density / 350f) * markerSelectedBitmap.getWidth();
        h = (density / 350f) * markerSelectedBitmap.getHeight();
        markerSelectedBitmap = Bitmap.createScaledBitmap(markerSelectedBitmap, (int) w, (int) h, true);
    }

    private void drawMarkers(Canvas canvas) {
        for (Marker marker : markers) {
            PointF viewCoordinate = sourceToViewCoord(marker.getX(), marker.getY());

            Bitmap bitmap = marker.isSelected() ? markerSelectedBitmap : markerBitmap;

            float xCoordinate = viewCoordinate.x - (bitmap.getWidth() / 2);
            float yCoordinate = viewCoordinate.y - (bitmap.getHeight() / 2);
            canvas.drawBitmap(bitmap, xCoordinate, yCoordinate, paint);

            // drawing id/number on marker
            resetPaint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(28);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(marker.getId() + "", xCoordinate + 24, yCoordinate + 35, paint);
        }
    }

    private void drawPopovers(Canvas canvas) {
        if (selectedMarker == null || ! selectedMarker.isSelected()) return;

        Rect rect = getPopoverRectFromMarker(selectedMarker);

        // draw fill
        resetPaint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(rect), 10, 10, paint);

        // draw stroke
        resetPaint();
        int strokeColor = selectedPopoverMarker != null && selectedMarker.equals(selectedPopoverMarker)
                ? Color.parseColor("#37abb7") : Color.DKGRAY;
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawRoundRect(new RectF(rect), 10, 10, paint);

        // draw title
        resetPaint();
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(32);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(selectedMarker.getTitle(), rect.left + rect.width()/2, rect.top + 50, paint);

        // draw duration
        resetPaint();
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(selectedMarker.getDuration(), rect.left + rect.width()/2, rect.top + 80, paint);
    }

    // Marker touch related

    private boolean notifyIfMarkerTouch(int x, int y) {
        PointF touchCoordinate = sourceToViewCoord(x, y);

        for (Marker marker : markers) {
            if (notifyIfMarkerTouch(marker, touchCoordinate)) return true;
        }

        return false;
    }

    private boolean notifyIfMarkerTouch(Marker marker, PointF touchCoordinate) {
        if ( ! isMarkerTouch(marker, touchCoordinate)) return false;

        toggleMarkerSelection(marker);
        markerClickListener.onMarkerClicked(marker);
        reDraw();

        return true;
    }

    private boolean isMarkerTouch(Marker marker, PointF touchCoordinate) {
        Region region = new Region(getRectFromMarker(marker));

        return region.contains((int)touchCoordinate.x, (int)touchCoordinate.y);
    }

    private void toggleMarkerSelection(Marker marker) {
        // remove existing selection
        if (selectedMarker != null) {
            selectedMarker.setSelected(false);
        }

        // make existing selection null if selected again
        if (selectedMarker != null && selectedMarker.equals(marker)) {
            selectedMarker = null;
            return;
        }

        // make current selection
        marker.setSelected(true);
        selectedMarker = marker;
    }

    // Marker popover touch related

    private boolean notifyIfMarkerPopoverTouch(int x, int y) {
        PointF touchCoordinate = sourceToViewCoord(x, y);

        for (Marker marker : markers) {
            if (notifyIfMarkerPopoverTouch(marker, touchCoordinate)) return true;
        }

        return false;
    }

    private boolean notifyIfMarkerPopoverTouch(Marker marker, PointF touchCoordinate) {
        if ( ! isMarkerPopoverTouch(marker, touchCoordinate)) return false;

        marker.setSelected(true);
        selectedMarker = marker;
        markerClickListener.onMarkerPopoverClicked(marker);
        selectedPopoverMarker = marker;
        reDraw();

        return true;
    }

    private boolean isMarkerPopoverTouch(Marker marker, PointF touchCoordinate) {
        if (marker != selectedMarker) return false;

        Region region = new Region(getPopoverRectFromMarker(marker));

        return region.contains((int)touchCoordinate.x, (int)touchCoordinate.y);
    }

    private Rect getRectFromMarker(Marker marker) {
        PointF markerCoordinate = sourceToViewCoord(marker.getX(), marker.getY());

        int left    = (int) (markerCoordinate.x - (markerBitmap.getWidth()/2)) - 10;
        int top     = (int) (markerCoordinate.y - (markerBitmap.getHeight()/2)) - 10;
        int right   = left + markerBitmap.getWidth() + 10;
        int bottom  = top + markerBitmap.getHeight() + 10;

        return new Rect(left, top, right, bottom);
    }

    private Rect getPopoverRectFromMarker(Marker marker) {
        PointF markerCoordinate = sourceToViewCoord(marker.getX(), marker.getY());

        resetPaint();
        Rect textBounds = new Rect();
        paint.setTextSize(32);
        paint.getTextBounds(marker.getTitle(), 0, marker.getTitle().length(), textBounds);

        int textHeight = textBounds.height();
        int textWidth = textBounds.width();

        int minMargin = 150;
        int rightMargin = minMargin;
        int leftMargin = minMargin;

        if ((textWidth/2 + 20) > minMargin) {
            leftMargin = textWidth/2 + 20;
            rightMargin = textWidth/2 + 20;
        }

        int left    = (int) markerCoordinate.x - leftMargin;
        int top     = (int) markerCoordinate.y - 150;
        int right   = (int) markerCoordinate.x + rightMargin;
        int bottom  = (int) markerCoordinate.y - (markerBitmap.getHeight()/2 + 10);

        return fitIntoMapView(new Rect(left, top, right, bottom));
    }

    private Rect fitIntoMapView(Rect rect) {
        int imageHeight = getHeight();
        int imageWidth = getWidth();

        int popoverWidth = rect.right - rect.left;
        int popoverHeight = rect.bottom - rect.top;

        if (rect.left < 0) {
            rect.left = 10;
            rect.right = rect.left + popoverWidth;
        }

        if (rect.right > imageWidth) {
            rect.right = imageWidth - 10;
            rect.left = rect.right - popoverWidth;
        }

        if (rect.top < 0) {
            rect.top = 10;
            rect.bottom = rect.top + popoverHeight;
        }

        if (rect.bottom > imageHeight) {
            rect.bottom = imageHeight - 10;
            rect.top = rect.bottom - popoverHeight;
        }

        return rect;
    }

    private void resetPaint() {
        if (paint == null) {
            paint = new Paint();
        };

        paint.reset();
        paint.setAntiAlias(true);
    }

    private void log(String message) {
        Log.d("MAP_VIEW", message);
    }

    public static interface MarkerClickListener {
        MarkerClickListener DEFAULT = new MarkerClickListener() {
            @Override
            public void onMarkerClicked(Marker marker) {}

            @Override
            public void onMarkerPopoverClicked(Marker marker) {}
        };

        void onMarkerClicked(Marker marker);

        void onMarkerPopoverClicked(Marker marker);
    }

}
