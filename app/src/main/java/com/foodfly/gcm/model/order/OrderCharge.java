package com.foodfly.gcm.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderCharge {

    @SerializedName("total_menu")
    private int mTotalMenu;
    @SerializedName("delivery_fee")
    private int mDeliveryFee;
    @SerializedName("discount")
    private OrderChargeDiscount mDiscount;
    @SerializedName("total_amount_due")
    private int mTotalAmountDue;

    public OrderCharge() {
    }

    public int getTotalAmountDue() {
        return mTotalAmountDue;
    }

    public void setTotalAmountDue(int totalAmountDue) {
        mTotalAmountDue = totalAmountDue;
    }

    public int getTotalMenu() {
        return mTotalMenu;
    }

    public void setTotalMenu(int totalMenu) {
        mTotalMenu = totalMenu;
    }

    public int getDeliveryFee() {
        return mDeliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        mDeliveryFee = deliveryFee;
    }

    public OrderChargeDiscount getDiscount() {
        return mDiscount;
    }

    public void setDiscount(OrderChargeDiscount discount) {
        mDiscount = discount;
    }
}