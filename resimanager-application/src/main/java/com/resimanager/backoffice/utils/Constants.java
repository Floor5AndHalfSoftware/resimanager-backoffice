package com.resimanager.backoffice.utils;

public class Constants {
    public static final String API_VERSION_PATH = "/v1";

    //Auth
    public static final String AUTH_PATH = "/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String LOGIN_URL = API_VERSION_PATH + AUTH_PATH + LOGIN_PATH;

    //Cache
    public static final String LOGIN_ATTEMPTS_CACHE = "loginAttempts";

    // JWT
    public static final String ISSUER_INFO = "space-api";
    public static final String SUPER_SECRET_KEY = "spaceapi5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final Integer TOKEN_EXPIRATION_TIME_IN_MINUTES = 1440; //One day
    public static final String HEADER_AUTHORIZACION_KEY = "Authorization";
    public static final String TOKEN_BEARER_PREFIX = "Bearer ";

}
