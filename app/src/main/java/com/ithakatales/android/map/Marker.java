package com.ithakatales.android.map;

/**
 * @author Farhan Ali
 */
public class Marker {

    private int id;
    private float x;
    private float y;
    private String title;
    private String duration;
    private boolean selected;

    public Marker() {
    }

    public Marker(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public Marker(int id, float x, float y, boolean selected) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.selected = selected;
    }

    public Marker(int id, float x, float y, String title, String duration) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.title = title;
        this.duration = duration;
    }

    public Marker(int id, float x, float y, String title, String duration, boolean selected) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.title = title;
        this.duration = duration;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
