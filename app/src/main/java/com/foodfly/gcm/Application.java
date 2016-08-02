package com.foodfly.gcm;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.foodfly.gcm.gcm.GcmUtils;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.model.user.UserManager;

/**
 * Created by woozam on 2016-06-22.
 */
public class Application extends MultiDexApplication {

    private static final String TAG = Application.class.getSimpleName();

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        Log.d(TAG, "attachBaseContext()");
        super.attachBaseContext(base);
        Log.d(TAG, "install()");
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        new Thread("Application Initialize") {
            @Override
            public void run() {
                super.run();
                UserManager.refreshToken();
                Connect.updateConnect();
                GcmUtils.register(getContext());
            }
        }.start();
    }
}