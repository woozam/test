package kr.co.foodfly.androidapp.model.restaurant;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class FeeInfo extends RealmObject implements Comparable<FeeInfo> {

    @SerializedName("minimum_order_amount")
    private int mMinimumOrderAmount;
    @SerializedName("fee")
    private int mFee;
    @SerializedName("original_fee")
    private int mOriginalFee;

    public FeeInfo() {
    }

    public int getMinimumOrderAmount() {
        return mMinimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        mMinimumOrderAmount = minimumOrderAmount;
    }

    public int getFee() {
        return mFee;
    }

    public void setFee(int fee) {
        mFee = fee;
    }

    public int getOriginalFee() {
        return mOriginalFee;
    }

    public void setOriginalFee(int originalFee) {
        mOriginalFee = originalFee;
    }

    @Override
    public int compareTo(@NonNull FeeInfo another) {
        return getFee() - another.getFee();
    }
}