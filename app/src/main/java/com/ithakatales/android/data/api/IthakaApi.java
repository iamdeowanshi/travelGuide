package com.ithakatales.android.data.api;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionDownloadRequest;
import com.ithakatales.android.data.model.AttractionRatingRequest;
import com.ithakatales.android.data.model.AttractionUpdateResponse;
import com.ithakatales.android.data.model.AttractionViewRequest;
import com.ithakatales.android.data.model.City;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public interface IthakaApi {

    @GET(Config.API_CITIES)
    Observable<List<City>> getCities();

    @GET(Config.API_ATTRACTIONS)
    Observable<List<Attraction>> getAttractions(@Query("city_id") long cityId);

    @GET(Config.API_ATTRACTION_INFO)
    Observable<Attraction> getAttraction(@Path("attraction_id") long attractionId);

    @POST(Config.API_DOWNLOADS_SAVE)
    Observable<Response> attractionDownloaded(
            @Header("Authorization") String authorization,@Body AttractionDownloadRequest downloadBody);

    @GET(Config.API_DOWNLOADS_UPDATED)
    Observable<List<AttractionUpdateResponse>> getAttractionUpdates(
            @Header("Authorization") String authorization, @Query("user_id") long userId);

    @POST(Config.API_VIEWS_CREATE)
    Observable<Response> attractionViewed(
            @Header("Authorization") String authorization, @Body AttractionViewRequest viewBody);

    @POST(Config.API_RATINGS_CREATE)
    Observable<Response> rateAttraction(
            @Header("Authorization") String authorization, @Body AttractionRatingRequest ratingBody);

}
