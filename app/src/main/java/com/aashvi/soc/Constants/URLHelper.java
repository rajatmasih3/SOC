package com.aashvi.soc.Constants;


public class URLHelper {

    //Application WEB Connectivity Details
    public static final String BASE_URL = "http://aashviinnovations.com/soc/public/";
    public static final int CLIENT_ID = 1;
    public static final String CLIENT_SECRET_KEY = "wrJ3OjBAOYGfelAtHFevUdEvw064gS21CDzKCAjm";
    public static final String APP_LINK = "https://play.google.com/store/search?q=aashvi%20innovation";
    public static final String STRIPE_TOKEN = "pk_test_0G4SKYMm8dK6kgayCPwKWTXy";

    //Help
    public static final String HELP_REDIRECT_URL = BASE_URL+"";

    //Image load options URL
    public static final String BASE_IMAGE_LOAD_URL = BASE_URL +"";
    public static final String BASE_IMAGE_LOAD_URL_WITH_STORAGE = BASE_URL +"storage/";

    //WEB API LIST
    public static final String LOGIN = BASE_URL +"oauth/token";
    public static final String REGISTER = BASE_URL +"api/user/signup";
    public static final String GET_USER_PROFILE = BASE_URL +"api/user/details";
    public static final String GET_PROVIDER_PROFILE = BASE_URL +"api/user/provider";
    public static final String USER_PROFILE_UPDATE = BASE_URL +"api/user/update/profile";
    public static final String GET_SERVICE_LIST_API = BASE_URL +"api/user/services";
    public static final String REQUEST_STATUS_CHECK_API = BASE_URL +"api/user/request/check";
    public static final String ESTIMATED_FARE_DETAILS_API = BASE_URL +"api/user/estimated/fare";
    public static final String SEND_REQUEST_API = BASE_URL +"api/user/send/request";
    public static final String CANCEL_REQUEST_API = BASE_URL +"api/user/cancel/request";
    public static final String PAY_NOW_API = BASE_URL +"api/user/payment";
    public static final String RATE_PROVIDER_API = BASE_URL +"api/user/rate/provider";
    public static final String CARD_PAYMENT_LIST = BASE_URL +"api/user/card";
    public static final String ADD_CARD_TO_ACCOUNT_API = BASE_URL +"api/user/card";
    public static final String DELETE_CARD_FROM_ACCOUNT_API = BASE_URL +"api/user/card/destory";
    public static final String GET_HISTORY_API = BASE_URL +"api/user/trips";
    public static final String GET_HISTORY_DETAILS_API = BASE_URL +"api/user/trip/details";
    public static final String ADD_CARD = BASE_URL +"api/user/add/money";
    public static final String COUPON_LIST_API = BASE_URL +"api/user/promocodes";
    public static final String ADD_COUPON_API = BASE_URL +"api/user/promocode/add";
    public static final String CHANGE_PASSWORD_API = BASE_URL +"api/user/change/password";
    public static final String UPCOMING_TRIP_DETAILS = BASE_URL +"api/user/upcoming/trip/details";
    public static final String UPCOMING_TRIPS = BASE_URL +"api/user/upcoming/trips";
    public static final String GET_PROVIDERS_LIST_API = BASE_URL +"api/user/show/providers";
    public static final String RESET_PASSWORD = BASE_URL +"api/user/reset/password";
    public static final String FORGET_PASSWORD = BASE_URL +"api/user/forgot/password";
    public static final String GET_HELP_DETAILS = BASE_URL + "api/user/help";
    public static final String LOGOUT = BASE_URL + "api/user/logout";
}
