package kr.co.foodfly.androidapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.user.UserManager;

/**
 *
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
        UserManager.refreshToken();
        Connect.updateConnect();
    }
}