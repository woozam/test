package com.foodfly.gcm.common;

import android.os.SystemClock;

/**
 * Created by woozam on 2016-08-02.
 */
public class AppClock {

    private static long CONNECT_TIME = -1;
    private static long CONNECT_ELAPSED_REAL_TIME = -1;

    public static long currentTimeMillis() {
        if (CONNECT_TIME == -1) {
            return System.currentTimeMillis();
        } else {
            return CONNECT_TIME + SystemClock.elapsedRealtime() - CONNECT_ELAPSED_REAL_TIME;
        }
    }

    public static void onConnect(long connectTime) {
        CONNECT_TIME = connectTime;
        CONNECT_ELAPSED_REAL_TIME = SystemClock.elapsedRealtime();
    }
}