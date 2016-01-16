package com.ithakatales.android.data.api;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.data.model.User;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public interface IthakaApi {

    // user/auth related apis

    @POST(Config.API_SIGNUP)
    Observable<User> register(@Body User user);

    @POST(Config.API_EMAIL_VERIFY)
    Observable<User> verifyEmail(@Body ApiModels.EmailVerificationRequest body);

    @POST(Config.API_NORMAL_LOGIN)
    Observable<User> loginNormal(@Body User user);

    @POST(Config.API_SOCIAL_LOGIN)
    Observable<User> loginSocial(@Body User user);

    @POST(Config.API_FORGOT_PASSWORD)
    Observable<Response> forgotPassword(@Body ApiModels.ForgotPasswordRequest body);

    @POST(Config.API_RESET_PASSWORD)
    Observable<User> resetPassword(@Body ApiModels.ResetPasswordRequest body);

    @Multipart
    @POST(Config.API_AVATAR_UPLOAD)
    Observable<ApiModels.ProfilePicUploadResponse> uploadProfilePic(@Header("Access-Token") String authorization, @Part("image") TypedFile profilePic);

    @PUT(Config.API_PROFILE_UPDATE)
    Observable<User> updateProfile(@Header("Access-Token") String authorization, @Path("user_id") long userId, @Body User user);

    // attraction related apis

    @GET(Config.API_CITIES)
    Observable<List<City>> getCities();

    @GET(Config.API_ATTRACTIONS)
    Observable<List<Attraction>> getAttractions(@Query("city_id") long cityId);

    @GET(Config.API_ATTRACTION_INFO)
    Observable<Attraction> getAttraction(@Path("attraction_id") long attractionId);

    @POST(Config.API_DOWNLOADS_SAVE)
    Observable<Response> attractionDownloaded(
            @Header("Access-Token") String authorization, @Body ApiModels.AttractionDownloadedRequest body);

    @GET(Config.API_DOWNLOADS_UPDATED)
    Observable<List<AttractionUpdate>> getAttractionUpdates(
            @Header("Access-Token") String authorization, @Query("user_id") long userId);

    @POST(Config.API_VIEWS_CREATE)
    Observable<Response> attractionViewed(
            @Header("Access-Token") String authorization, @Body ApiModels.AttractionViewedRequest body);

    @POST(Config.API_RATINGS_CREATE)
    Observable<Response> rateAttraction(
            @Header("Access-Token") String authorization, @Body ApiModels.AttractionRatingRequest body);

}
