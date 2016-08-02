package com.foodfly.gcm.app.activity.kickOff;

import android.os.AsyncTask;
import android.os.Bundle;

import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.main.MainActivity;
import com.google.android.gms.maps.MapView;

import com.foodfly.gcm.R;

public class LaunchActivity extends BaseActivity {

    private static boolean sInitialized = false;
    private boolean mPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    private boolean mWebViewLoaded = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (sInitialized) {
            route();
        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(500);
                        while (!mWebViewLoaded) {
                            Thread.sleep(50);
                        }
                        sInitialized = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    route();
                }
            }.execute();

            findViewById(R.id.launch_root).postDelayed(new Runnable() {
                @Override
                public void run() {
                    MapView mapView = new MapView(LaunchActivity.this);
                    mapView.onCreate(null);
                    mapView.onResume();
                    mapView.onPause();
                    mapView.onDestroy();
                    mWebViewLoaded = true;
                }
            }, 50);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    private void route() {
        MainActivity.createInstance(this);
        finish();
    }
}