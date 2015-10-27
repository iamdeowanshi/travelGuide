package com.ithakatales.android.app;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for keeping application configuration as constants.
 *
 * @author Farhan Ali
 */
public class Config {

    //--------------------------------------------------------------------------------
    // API related constants/configurations - used in ApiModule & Api class
    //--------------------------------------------------------------------------------
    public static final String BASE_URL_PRODUCTION      = "";
    public static final String BASE_URL_DEVELOP         = "http://ithaka.getsandbox.com";

    // Active base url
    public static final String BASE_URL                 = BASE_URL_DEVELOP;

    /**
     * GET: all cities as a list
     */
    public static final String API_CITIES               = "/cities";

    /**
     * GET: all attractions for a city with basic details.
     *
     * Query param : city_id : required
     */
    public static final String API_ATTRACTIONS          = "/attractions";

    /**
     * GET: full information of an attraction including pois, audios & images.
     *
     * Path param : attraction_id : required
     */
    public static final String API_ATTRACTION_INFO      = "/attractions/{attraction_id}";

    /**
     * POST: send an attraction download status after success full download.
     */
    public static final String API_DOWNLOADS_SAVE       = "/attraction_downloads";

    /**
     * GET: updated attractions list for a user with attraction id and timestamps.
     *
     * Query param : user_id : required
     */
    public static final String API_DOWNLOADS_UPDATED    = "/attraction_downloads/updated";

    /**
     * POST: send an attraction view status.
     */
    public static final String API_VIEWS_CREATE         = "/attraction_views";

    /**
     * POST: send an attraction rating by the user.
     */
    public static final String API_RATINGS_CREATE       = "/attraction_ratings";

    // Cache size in bytes, 50 MB = 50 * 1024 KB, 1 KB  = 1024 Bytes
    public static final int HTTP_DISK_CACHE_SIZE        = (int) 50 * 1024 * 1024;

    // Headers required to be added by interceptor
    public static final Map<String, String> HEADERS = new HashMap<String, String>() {{
        put("User-Agent", "Ithaka-Tales-App");
        put("Content-Type", "application/json");
    }};

    //--------------------------------------------------------------------------------
    // ORM related constants/configurations - used in OrmModule
    //--------------------------------------------------------------------------------

    public static final String DB_NAME = "ithaka.realm";

    public static final int DB_VERSION = 2;

}
