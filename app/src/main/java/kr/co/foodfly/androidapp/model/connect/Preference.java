package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class Preference extends RealmObject {

    @SerializedName("drawer_background")
    private String mDrawerBackground;
    @SerializedName("login_background")
    private String mLoginBackground;
    @SerializedName("service_area")
    private String mServiceArea;
    @SerializedName("closing_alert_margin")
    private int mClosingAlertMargin;
    @SerializedName("closing_time_margin")
    private int mClosingTimeMargin;

    public Preference() {
    }

    public String getDrawerBackground() {
        return mDrawerBackground;
    }

    public void setDrawerBackground(String drawerBackground) {
        mDrawerBackground = drawerBackground;
    }

    public String getLoginBackground() {
        return mLoginBackground;
    }

    public void setLoginBackground(String loginBackground) {
        mLoginBackground = loginBackground;
    }

    public String getServiceArea() {
        return mServiceArea;
    }

    public void setServiceArea(String serviceArea) {
        mServiceArea = serviceArea;
    }

    public int getClosingAlertMargin() {
        return mClosingAlertMargin;
    }

    public void setClosingAlertMargin(int closingAlertMargin) {
        mClosingAlertMargin = closingAlertMargin;
    }

    public int getClosingTimeMargin() {
        return mClosingTimeMargin;
    }

    public void setClosingTimeMargin(int closingTimeMargin) {
        mClosingTimeMargin = closingTimeMargin;
    }
}