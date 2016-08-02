package com.foodfly.gcm.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonObject;

import com.foodfly.gcm.BuildConfig;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("139494071673", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_PUSH_TOKEN).toString();
            JsonObject json = new JsonObject();
            json.addProperty("platform", "android");
            json.addProperty("device_type", Build.MODEL);
            json.addProperty("device_version", VERSION.RELEASE);
            json.addProperty("app_version", BuildConfig.VERSION_NAME);
            json.addProperty("app_version_code", BuildConfig.VERSION_CODE);
            json.addProperty("device_uuid", Build.SERIAL);
            json.addProperty("push_token", token);
            GsonRequest<PushTokenResponse> request = new GsonRequest<PushTokenResponse>(Method.POST, url, PushTokenResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<PushTokenResponse>() {
                @Override
                public void onResponse(PushTokenResponse response) {
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }
    }

    private class PushTokenResponse {
        boolean debug;
    }
}