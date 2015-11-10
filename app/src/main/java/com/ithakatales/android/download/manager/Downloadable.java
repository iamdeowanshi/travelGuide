package com.ithakatales.android.download.manager;

/**
 * @author Farhan Ali
 */
public class Downloadable {

    private long id;
    private String url;
    private String title;
    private String description;
    private String destination;

    public Downloadable() {
    }

    public Downloadable( String title,  String description, String url, String destination) {
        this.destination = destination;
        this.description = description;
        this.title = title;
        this.url = url;
    }

    public Downloadable(long id, String title, String description,  String url, String destination) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.destination = destination;
    }

    public long getId() {
        return id;
    }

    public Downloadable setId(long id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Downloadable setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Downloadable setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Downloadable setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public Downloadable setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Downloadable) {
            Downloadable downloadable = (Downloadable) object;
            return (id == downloadable.getId()) && url.equals(downloadable.getUrl());
        }

        return false;
    }

}
