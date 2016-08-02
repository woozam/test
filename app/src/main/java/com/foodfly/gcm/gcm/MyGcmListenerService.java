package com.foodfly.gcm.gcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.foodfly.gcm.Application;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.NotificationUtils;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.android.gms.gcm.GcmListenerService;

import com.foodfly.gcm.model.user.UserManager;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (from == null) {
            Log.w(TAG, "Couldn't determine origin of message. Skipping.");
            return;
        }
        if (UserManager.fetchUser() == null) {
            return;
        }
        digestData(data);
    }

    private void digestData(Bundle data) {
        try {
            final String imgUrl = data.getString("imgUrl");
            final String trgtUrl = data.getString("trgtUrl");
            final String title = data.getString("title");
            final String message = data.getString("message");
            final String messageId = data.getString("google.message_id");
            final long sentTime = data.getLong("google.sent_time");
            if (TextUtils.isEmpty(imgUrl)) {
                NotificationUtils.showNotification(messageId, sentTime, title, message, trgtUrl, null);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        VolleySingleton.getInstance(Application.getContext()).getImageLoader().get(imgUrl, new ImageListener() {
                            @Override
                            public void onResponse(ImageContainer response, boolean isImmediate) {
                                if (response != null && response.getBitmap() != null) {
                                    NotificationUtils.showNotification(messageId, sentTime, title, message, trgtUrl, response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NotificationUtils.showNotification(messageId, sentTime, title, message, trgtUrl, null);
                            }
                        }, CommonUtils.getScreenWidth() / 2, CommonUtils.getScreenWidth() / 2);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}