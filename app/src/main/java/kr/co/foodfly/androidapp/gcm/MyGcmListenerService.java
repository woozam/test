package kr.co.foodfly.androidapp.gcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gms.gcm.GcmListenerService;

import kr.co.foodfly.androidapp.Application;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.NotificationUtils;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.network.VolleySingleton;

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