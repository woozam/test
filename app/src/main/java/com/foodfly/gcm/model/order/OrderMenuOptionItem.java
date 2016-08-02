package com.foodfly.gcm.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderMenuOptionItem {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("charge")
    private int mCharge;

    public OrderMenuOptionItem() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getCharge() {
        return mCharge;
    }

    public void setCharge(int charge) {
        mCharge = charge;
    }
}