package com.foodfly.gcm.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import com.foodfly.gcm.model.restaurant.Restaurant;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderListItem {

    @SerializedName("id")
    private String mId;
    @SerializedName("timestamp")
    private Date mTimestamp;
    @SerializedName("restaurant")
    private Restaurant mRestaurant;

    public OrderListItem() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }
}