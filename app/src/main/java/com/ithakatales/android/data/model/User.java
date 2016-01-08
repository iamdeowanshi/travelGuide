package com.ithakatales.android.data.model;

import com.google.gson.annotations.SerializedName;
import com.ithakatales.android.util.social.AuthResult;
import com.ithakatales.android.util.social.AuthUser;

/**
 * @author Farhan Ali
 */
public class User {

    @SerializedName("id")
    private long id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("about")
    private String about;
    @SerializedName("profilePictureUrl")
    private String avatar;
    @SerializedName("gPlusId")
    private String googleId;
    @SerializedName("facebookId")
    private String facebookId;
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("isVerified")
    private boolean verified;

    public User() {
    }

    public User(AuthResult result) {
        AuthUser authUser = result.getAuthUser();
        this.setFirstName(authUser.getFirstName());
        this.setLastName(authUser.getLastName());
        this.setEmail(authUser.getEmail());
        this.setAvatar(authUser.getProfilePic());
        this.setAccessToken(authUser.getAccessToken());

        switch (result.getAuthType()) {
            case GOOGLE:
                this.setGoogleId(authUser.getSocialId());
                break;
            case FACEBOOK:
                this.setFacebookId(authUser.getSocialId());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getFullName() {
        String fName = firstName != null ? firstName : "";
        String lName = lastName != null ? lastName : "";

        return firstName + " " + lastName;
    }

    /**
     * Merge all non null data of passed user to this.
     *
     * @param user
     */
    public void merge(User user) {
        if (user == null) return;

        this.id = user.id != 0 ? user.id : this.id;
        this.firstName = user.firstName != null ? user.firstName : this.firstName;
        this.lastName = user.lastName != null ? user.lastName : this.lastName;
        this.email = user.email != null ? user.email : this.email;
        this.password = user.password != null ? user.password : this.password;
        this.about = user.about != null ? user.about : this.about;
        this.avatar = user.avatar != null ? user.avatar : this.avatar;
        this.googleId = user.googleId != null ? user.googleId : this.googleId;
        this.facebookId = user.facebookId != null ? user.facebookId : this.facebookId;
        this.accessToken = user.accessToken != null ? user.accessToken : this.accessToken;
    }

}
