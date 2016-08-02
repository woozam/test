package com.foodfly.gcm.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class OpeningHourDate extends RealmObject {

    @SerializedName("date")
    private String mDate;
    @SerializedName("timezone_type")
    private int mTimezoneType;
    @SerializedName("timezone")
    private String mTimezone;

    public OpeningHourDate() {
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getTimezoneType() {
        return mTimezoneType;
    }

    public void setTimezoneType(int timezoneType) {
        mTimezoneType = timezoneType;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }
}
