package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class Defaults extends RealmObject {

    @SerializedName("orders")
    private DefaultsOrder mOrders;
    @SerializedName("coupons")
    private DefaultsCoupons mCoupons;

    public Defaults() {
    }

    public DefaultsOrder getOrders() {
        return mOrders;
    }

    public void setOrders(DefaultsOrder orders) {
        mOrders = orders;
    }

    public DefaultsCoupons getCoupons() {
        return mCoupons;
    }

    public void setCoupons(DefaultsCoupons coupons) {
        mCoupons = coupons;
    }
}