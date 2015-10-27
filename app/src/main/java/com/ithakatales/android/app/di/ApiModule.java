package com.ithakatales.android.app.di;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.data.api.IthakaApi;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmObject;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Provides dependencies related to the ApiModule.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class ApiModule {

    @Provides
    @Singleton
    public IthakaApi provideApi(RestAdapter restAdapter) {
        return restAdapter.create(IthakaApi.class);
    }

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient client,
                                          RequestInterceptor interceptor, Gson gson) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setRequestInterceptor(interceptor)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Provides
    @Named("api_base_url")
    public String provideBaseUrl() {
        return Config.BASE_URL;
    }

    @Provides
    @Singleton
    public Endpoint provideEndpoint(@Named("api_base_url") String baseUrl) {
        return Endpoints.newFixedEndpoint(baseUrl);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Context context) {
        return createOkHttpClient(context);
    }

    @Provides
    @Singleton
    public RequestInterceptor provideRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                for (Map.Entry<String, String> header : Config.HEADERS.entrySet()) {
                    request.addHeader(header.getKey(), header.getValue());
                }
            }
        };
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        ExclusionStrategy realmExclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };

        return new GsonBuilder()
                .setExclusionStrategies(realmExclusionStrategy)
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .create();
    }

    /**
     * Creates a http cache enable OkHttpClient
     *
     * @param context Application Context
     * @return OkHttpClient
     */
    private static OkHttpClient createOkHttpClient(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = null;
        try {
            cache = new Cache(cacheDir, Config.HTTP_DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return client;
    }

}
