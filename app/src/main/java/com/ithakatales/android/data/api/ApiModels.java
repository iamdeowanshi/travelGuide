package com.ithakatales.android.data.api;

import com.google.gson.annotations.SerializedName;

/**
 * Holds static classes only using for api request and response
 *
 * @author Farhan Ali
 */
public class ApiModels {

    public static class EmailVerificationRequest {
        @SerializedName("email")
        public String email;
        @SerializedName("otp")
        public String otp;
    }

    public static class ForgotPasswordRequest {
        @SerializedName("email")
        public String email;
    }

    public static class ResetPasswordRequest {
        @SerializedName("email")
        public String email;
        @SerializedName("newPassword")
        public String newPassword;
        @SerializedName("otp")
        public String otp;
    }

    private static class AttractionAccess {
        @SerializedName("userId")
        public long userId;
        @SerializedName("attractionId")
        public long attractionId;
    }

    public static class AttractionViewedRequest extends AttractionAccess {}

    public static class AttractionDownloadedRequest extends AttractionAccess {}

    public static class AttractionRatingRequest extends AttractionAccess {
        @SerializedName("value")
        public int rating;
    }

    public static class ProfilePicUploadResponse {
        @SerializedName("url")
        public String url;
        @SerializedName("size")
        public long size;
    }

}