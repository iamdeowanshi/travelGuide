package com.ithakatales.android.data.api;

import com.ithakatales.android.app.Config;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.data.model.AttractionDownload;
import com.ithakatales.android.data.model.AttractionRating;
import com.ithakatales.android.data.model.AttractionView;

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
            @Header("Authorization") String authorization,@Body AttractionDownload downloadBody);

    @GET(Config.API_DOWNLOADS_UPDATED)
    Observable<List<AttractionUpdate>> getAttractionUpdates(
            @Header("Authorization") String authorization, @Query("user_id") long userId);

    @POST(Config.API_VIEWS_CREATE)
    Observable<Response> attractionViewed(
            @Header("Authorization") String authorization, @Body AttractionView viewBody);

    @POST(Config.API_RATINGS_CREATE)
    Observable<Response> rateAttraction(
            @Header("Authorization") String authorization, @Body AttractionRating ratingBody);

}
