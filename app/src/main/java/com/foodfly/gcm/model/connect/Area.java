package com.foodfly.gcm.model.connect;

import android.text.TextUtils;

import com.foodfly.gcm.common.AppClock;
import com.foodfly.gcm.common.TimeUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;
import java.util.TimeZone;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class Area extends RealmObject {

    @SerializedName("areacode")
    private long mAreaCode;
    @SerializedName("is_service_area")
    private boolean mServiceArea;
    @SerializedName("start")
    private String mStart;
    @SerializedName("end")
    private String mEnd;
    @SerializedName("use_delivery_distance")
    private boolean mUseDeliveryDistance;
    @SerializedName("use_delivery_time")
    private boolean mUseDeliveryTime;
    @SerializedName("delivery_distance")
    private int mDeliveryDistance;
    @SerializedName("delivery_time")
    private int mDeliveryTime;
    @SerializedName("delivery_time_message")
    private String mDeliveryTimeMessage;

    public Area() {
    }

    public long getAreaCode() {
        return mAreaCode;
    }

    public void setAreaCode(long areaCode) {
        mAreaCode = areaCode;
    }

    public boolean isServiceArea() {
        return mServiceArea;
    }

    public void setServiceArea(boolean serviceArea) {
        mServiceArea = serviceArea;
    }

    public String getStart() {
        return mStart;
    }

    public void setStart(String start) {
        mStart = start;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String end) {
        mEnd = end;
    }

    public boolean isUseDeliveryDistance() {
        return mUseDeliveryDistance;
    }

    public void setUseDeliveryDistance(boolean useDeliveryDistance) {
        mUseDeliveryDistance = useDeliveryDistance;
    }

    public boolean isUseDeliveryTime() {
        return mUseDeliveryTime;
    }

    public void setUseDeliveryTime(boolean useDeliveryTime) {
        mUseDeliveryTime = useDeliveryTime;
    }

    public int getDeliveryDistance() {
        return mDeliveryDistance;
    }

    public void setDeliveryDistance(int deliveryDistance) {
        mDeliveryDistance = deliveryDistance;
    }

    public int getDeliveryTime() {
        return mDeliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        mDeliveryTime = deliveryTime;
    }

    public String getDeliveryTimeMessage() {
        return mDeliveryTimeMessage;
    }

    public void setDeliveryTimeMessage(String deliveryTimeMessage) {
        mDeliveryTimeMessage = deliveryTimeMessage;
    }

    public boolean enableTimeFoodflyServiceTime() {
        if (!mServiceArea) {
            return mServiceArea;
        }

        if (TextUtils.isEmpty(mStart) || TextUtils.isEmpty(mEnd)) {
            return true;
        }

        String start = mStart;
        String end = mEnd;
        String starts[] = start.split(":");
        String ends[] = end.split(":");

        if (starts.length < 2 || ends.length < 2) {
            return true;
        }

        if (Integer.parseInt(ends[0]) == 0) {
            end = String.format(Locale.getDefault(), "24:%s", ends[1]);
        }

        String currentString = TimeUtils.getHourMinuteTimeString(AppClock.currentTimeMillis() + TimeZone.getDefault().getRawOffset());
        String startString = start.substring(0, 5);
        String endString = end.substring(0, 5);

        if (startString.compareTo(endString) == 0) {
            return true;
        }

        if (startString.compareTo(currentString) < 0 && endString.compareTo(currentString) > 0) {
            return true;
        } else {
            return false;
        }
    }
}