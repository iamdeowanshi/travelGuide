package com.ithakatales.android.data.model;

/**
 * @author Farhan Ali
 */
public class User {

    private long id;
    private String accessToken;

    public User() {
    }

    public User(long id, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;
    }

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

    public static User dummy() {
        return new User(1, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

}
