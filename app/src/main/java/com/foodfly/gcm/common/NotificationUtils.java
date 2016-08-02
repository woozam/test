package com.foodfly.gcm.common;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.foodfly.gcm.Application;
import com.foodfly.gcm.R;

/**
 * Created by woozam on 2016-07-31.
 */
public class NotificationUtils {

    public static final String TAG = NotificationUtils.class.getSimpleName();

    private static final int ALARM_WAKE_UP_TIMEOUT = 10 * 1000;

    public static void showNotification(String messageId, long sentTime, String title, String message, String url, Bitmap image) {
        Context context = Application.getContext();

        if (TextUtils.isEmpty(url)) {
            return;
        }

        Intent sintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pIntent = PendingIntent.getActivity(context, messageId.hashCode(), sintent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = null;
        try {
            mBuilder = new Notification.Builder(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mBuilder != null) {
            mBuilder.setSmallIcon(R.mipmap.ic_notification);
            mBuilder.setTicker(title);
            mBuilder.setWhen(sentTime);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(message);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            mBuilder.setContentIntent(pIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setVibrate(new long[]{200, 300, 200, 300, 200, 300});
            if (image != null) {
                BigPictureStyle bigPictureStyle = new BigPictureStyle(mBuilder);
                bigPictureStyle.bigPicture(image);
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(message);
                mBuilder.setStyle(bigPictureStyle);
            }
            notiManager.notify(messageId.hashCode(), mBuilder.build());
        }
    }
}