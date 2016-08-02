package com.foodfly.gcm.model.user;

import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.Application;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.JsonObject;

import io.realm.Realm;

/**
 * Created by woozam on 2016-06-27.
 */
public class UserManager {

    public static UserResponse fetchUser() {
        Realm realm = Realm.getInstance(RealmUtils.CONFIG_USER);
        UserResponse user = realm.where(UserResponse.class).findFirst();
        if (user != null) {
            user = realm.copyFromRealm(user);
        }
        realm.close();
        return user;
    }

    public static void setUser(UserResponse userResponse) {
        Realm realm = Realm.getInstance(RealmUtils.CONFIG_USER);
        realm.beginTransaction();
        realm.deleteAll();
        realm.copyToRealmOrUpdate(userResponse);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteUser() {
        Realm realm = Realm.getInstance(RealmUtils.CONFIG_USER);
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }

    public static void fetchUserFromServer() {
        final UserResponse user = fetchUser();
        if (user == null) {
            return;
        }
        String url = APIs.getUserPath().appendPath(user.getId()).toString();
        GsonRequest<User> request = new GsonRequest<User>(Method.GET, url, User.class, APIs.createHeadersWithToken(), new Listener<User>() {
            @Override
            public void onResponse(User response) {
                User pre = user.getUser();
                if (TextUtils.isEmpty(response.getReferralCode())) {
                    response.setReferralCode(pre.getReferralCode());
                }
                if (TextUtils.isEmpty(response.getReferralCode())) {
                    fetchReferralCodeFromServer();
                }
                user.setUser(response);
                user.setId(response.getId());
                setUser(user);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(Application.getContext()).addToRequestQueue(request);
    }

    public static void fetchReferralCodeFromServer() {
        final UserResponse user = fetchUser();
        if (user == null) {
            return;
        }
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_REFERRAL).toString();
        GsonRequest<ReferralResponse> request = new GsonRequest<ReferralResponse>(Method.GET, url, ReferralResponse.class, APIs.createHeadersWithToken(), new Listener<ReferralResponse>() {
            @Override
            public void onResponse(ReferralResponse response) {
                user.getUser().setReferralCode(response.code);
                setUser(user);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(Application.getContext()).addToRequestQueue(request);
    }

    private static class ReferralResponse extends BaseResponse {
        String image;
        String code;
        String url;
    }

    public static void refreshToken() {
        final UserResponse user = fetchUser();
        if (user == null) {
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("auth_token", user.getAuthToken());
        json.addProperty("refresh_token", user.getRefreshToken());
        String url = APIs.getAuthPath().appendPath(APIs.AUTH_REFRESH).toString();
        GsonRequest<UserResponse> request = new GsonRequest<>(Method.POST, url, UserResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<UserResponse>() {
            @Override
            public void onResponse(UserResponse response) {
                user.setAuthToken(response.getAuthToken());
                user.setAuthTokenValidUntil(response.getAuthTokenValidUntil());
                user.setRefreshToken(response.getRefreshToken());
                user.setRefreshTokenValidUntil(response.getRefreshTokenValidUntil());
                setUser(user);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(Application.getContext()).addToRequestQueue(request);
    }

    public static void onLogout() {
        Realm realm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
        realm = Realm.getInstance(RealmUtils.CONFIG_CART);
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
        UserManager.deleteUser();
        Connect.updateConnect();
    }
}