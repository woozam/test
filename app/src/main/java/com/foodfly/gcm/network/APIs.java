package com.foodfly.gcm.network;

import android.net.Uri;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.foodfly.gcm.BuildConfig;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;

/**
 *
 * Created by woozam on 2016-06-22.
 */
public class APIs {

    private static URL URL_SERVER;

    private static final String VERSION_1_0 = "v1";
    private static final String VERSION_2_0 = "v2";

    private static final String PATH_ADDRESS = "address";
    private static final String PATH_AUTH = "auth";
    private static final String PATH_CONNECT = "connect";
    private static final String PATH_USER = "user";
    private static final String PATH_PAYMENT = "payment";
    private static final String PATH_PROMOTION = "promotion";
    private static final String PATH_RESTAURANT = "restaurant";
    private static final String PATH_TERM = "term";
    private static final String PATH_THEME = "theme";

    public static final String ADDRESS_PRE_SUBSCRIPTION = "pre_subscription";
    public static final String ADDRESS_AREA = "area";

    public static final String AUTH_LOGIN = "login";
    public static final String AUTH_REFRESH = "refresh";
    public static final String AUTH_LOGOUT = "logout";
    public static final String AUTH_SIGN_UP = "signup";
    public static final String AUTH_CHECK = "check";
    public static final String AUTH_VERIFICATION = "verification_code";
    public static final String AUTH_VERIFY = "verify";
    public static final String AUTH_SIGN_OUT = "signout";

    public static final String CONNECT_AREA = "area";

    public static final String USER_COUPON = "coupon";
    public static final String USER_FAVORITE = "favorite";
    public static final String USER_ORDER = "order";
    public static final String USER_ADDRESS = "address";
    public static final String USER_MILEAGE = "mileage";
    public static final String USER_REFERRAL = "referral";
    public static final String USER_PUSH_TOKEN = "pushtoken";

    public static final String PAYMENT_CANCEL = "cancel";

    public static final String RESTAURANT_MENU = "menu";

    public static final String TERM_PRIVACY = "privacy";
    public static final String TERM_GENERAL = "general";

    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LON = "lon";
    public static final String PARAM_KEYWORD = "keyword";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_AUTH_TOKEN = "auth_token";
    public static final String PARAM_REFRESH_TOKEN = "refresh_token";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_JOIN_CHANNEL = "join_channel";
    public static final String PARAM_AGREE_GENERAL_POLICY = "agree_general_policy";
    public static final String PARAM_AGREE_PRIVACY_POLICY = "agree_private_policy";
    public static final String PARAM_AGREE_RECV_EMAIL = "agree_recv_email";
    public static final String PARAM_AGREE_RECV_SMS = "agree_recv_sms";
    public static final String PARAM_REFERRAL_CODE = "referral_code";
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_PROMOTION_CODE = "promotion_code";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_DISCOUNT_TYPE = "discount_type";
    public static final String PARAM_DISCOUNT_AMOUNT = "discount_amount";
    public static final String PARAM_ORDER_ID = "order_id";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_DELIVERY_STATUS = "delivery_status";
    public static final String PARAM_PAYMENT_TYPE = "payment_type";
    public static final String PARAM_DELIVERY_TYPE = "delivery_type";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_OFFSET = "offset";
    public static final String PARAM_AREA_CODE = "areacode";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_ORDER_BY = "order_by";
    public static final String PARAM_COUPON = "coupon";
    public static final String PARAM_FAVORITE_OF = "favorite_of";
    public static final String PARAM_THEME = "theme";
    public static final String PARAM_ID = "id";
    public static final String PARAM_SID = "sid";
    public static final String PARAM_SUMMARY = "summary";

    static {
        try {
            URL_SERVER = new URL(BuildConfig.APISERVER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static Uri.Builder getDefaultPath() {
        return new Uri.Builder().scheme(URL_SERVER.getProtocol()).authority(URL_SERVER.getAuthority()).path(URL_SERVER.getPath());
    }

    public static Uri.Builder getAddressPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_ADDRESS);
    }

    public static Uri.Builder getAuthPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_AUTH);
    }

    public static Uri.Builder getConnectPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_CONNECT);
    }

    public static Uri.Builder getUserPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_USER);
    }

    public static Uri.Builder getPaymentPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_PAYMENT);
    }

    public static Uri.Builder getPromotionPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_PROMOTION);
    }

    public static Uri.Builder getRestaurantPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_RESTAURANT);
    }

    public static Uri.Builder getTermPath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_TERM);
    }

    public static Uri.Builder getThemePath() {
        return getDefaultPath().appendPath(VERSION_1_0).appendPath(PATH_THEME);
    }

    public static Map<String, String> createHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json");
        return params;
    }

    public static Map<String, String> createHeadersWithToken() {
        Map<String, String> params = createHeaders();
        UserResponse user = UserManager.fetchUser();
        if (user != null && !TextUtils.isEmpty(user.getAuthToken())) {
            params.put("HTTP_X_AUTH_TOKEN", user.getAuthToken());
        }
        return params;
    }
}
