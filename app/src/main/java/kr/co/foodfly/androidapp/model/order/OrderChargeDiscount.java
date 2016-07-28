package kr.co.foodfly.androidapp.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderChargeDiscount {

    @SerializedName("event")
    private int mEvent;
    @SerializedName("coupon")
    private int mCoupon;
    @SerializedName("mileage")
    private int mMileage;
    @SerializedName("total")
    private int mTotal;

    public OrderChargeDiscount() {
    }

    public int getEvent() {
        return mEvent;
    }

    public void setEvent(int event) {
        mEvent = event;
    }

    public int getCoupon() {
        return mCoupon;
    }

    public void setCoupon(int coupon) {
        mCoupon = coupon;
    }

    public int getMileage() {
        return mMileage;
    }

    public void setMileage(int mileage) {
        mMileage = mileage;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }
}