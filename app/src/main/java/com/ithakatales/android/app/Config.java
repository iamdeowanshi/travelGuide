package com.ithakatales.android.app;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for keeping application configuration as constants.
 *
 * @author Farhan Ali
 */
public class Config {

    public static final boolean DEBUG = true;

    //--------------------------------------------------------------------------------
    // API related constants/configurations - used in ApiModule & Api class, prefix with API_
    //--------------------------------------------------------------------------------
    public static final String API_BASE_URL_PRODUCTION  = "";
    public static final String API_BASE_URL_DEVELOP     = "http://52.77.211.228:1337/v1";
    //public static final String API_BASE_URL_DEVELOP     = "http://52.74.156.9:1337/v1/";
    public static final String API_BASE_URL_MOCK        = "http://ithaka-mobile.getsandbox.com";

    // Active base url
    public static final String API_BASE_URL = API_BASE_URL_DEVELOP;

    // authentication related apis

    // POST: user normal registration
    public static final String API_SIGNUP           = "/users/signup";
    // POST: verify after registration using otp
    public static final String API_EMAIL_VERIFY     = "/users/email_verification";
    // POST: normal loginNormal
    public static final String API_NORMAL_LOGIN     = "/users/login";
    // POST: social loginNormal
    public static final String API_SOCIAL_LOGIN     = "/users/signup";
    // POST: request for a password change
    public static final String API_FORGOT_PASSWORD  = "/users/forgot_password";
    // POST: request for resetting a password
    public static final String API_RESET_PASSWORD   = "/users/reset_password";
    // POST: profile pic upload
    public static final String API_AVATAR_UPLOAD    = "/images/upload";
    // PUT: user profile update | Path param: user_id : required
    public static final String API_PROFILE_UPDATE   = "/users/{user_id}/profile";

    // attraction related apis

    // GET: get cities
    public static final String API_CITIES               = "/cities";
    // GET: get attractions | Query param : city_id : required
    public static final String API_ATTRACTIONS          = "/attractions";
    // GET: get full attraction info | Path param : attraction_id : required
    public static final String API_ATTRACTION_INFO      = "/attractions/{attraction_id}";
    // POST: send an attraction download status after success full download.
    public static final String API_DOWNLOADS_SAVE       = "/attraction_downloads";
    // GET: updated attractions list for a user | Query param : user_id : required
    public static final String API_DOWNLOADS_UPDATED    = "/attraction_downloads";
    // POST: send an attraction view status.
    public static final String API_VIEWS_CREATE         = "/attraction_views";
    // POST: send an attraction rating by the user.
    public static final String API_RATINGS_CREATE       = "/attraction_ratings";

    // Cache size in bytes, 50 MB = 50 * 1024 KB, 1 KB  = 1024 Bytes
    public static final int API_HTTP_DISK_CACHE_SIZE = (int) 50 * 1024 * 1024;

    // Headers required to be added by interceptor
    public static final Map<String, String> API_HEADERS = new HashMap<String, String>() {{
        put("User-Agent", "Ithaka-Tales-Android-App");
        put("Content-Type", "application/json");
    }};

    //--------------------------------------------------------------------------------
    // ORM related constants/configurations - used in OrmModule, prefix with DB_
    //--------------------------------------------------------------------------------

    public static final String DB_NAME = "ithaka.realm";

    public static final int DB_VERSION = 1;

    //--------------------------------------------------------------------------------
    // Storage related constants/configurations, prefix with STORAGE_
    //--------------------------------------------------------------------------------

    public static final String STORAGE_DATA_FOLDER      = "Ithaka";
    public static final boolean STORAGE_USE_INTERNAL    = false;
    public static final boolean STORAGE_HIDE_FILES      = false;

    //--------------------------------------------------------------------------------
    // // TODO: 06/01/16 commenting
    //--------------------------------------------------------------------------------

    public static final String LINK_PRIVACY_POLICY  = "http://www.google.com";
    public static final String LINK_TERMS           = "http://www.google.com";

    public static final String SHARE_TOUR_URL_BASE  = "http://www.ithakatales.com/tours/";

    public static final String FEEDBACK_EMAIL_TO    = "ithakatales@gmail.com";
    public static final String FEEDBACK_SUBJECT     = "Ithakatales Feedback";

}
