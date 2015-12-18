package com.ithakatales.android.data.model;

/**
 * @author Farhan Ali
 */
public class User {

    private long id;
    private String accessToken;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
}
