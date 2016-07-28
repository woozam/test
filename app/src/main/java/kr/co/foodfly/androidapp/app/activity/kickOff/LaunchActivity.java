package kr.co.foodfly.androidapp.app.activity.kickOff;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.MapView;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.main.MainActivity;

public class LaunchActivity extends BaseActivity {

    private static boolean sInitialized = false;
    private boolean mPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

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
            MapView mapView = new MapView(this);
            mapView.onCreate(null);
            mapView.onResume();
            mapView.onPause();
            mapView.onDestroy();
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