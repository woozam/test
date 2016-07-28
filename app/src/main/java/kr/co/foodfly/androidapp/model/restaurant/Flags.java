package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class Flags extends RealmObject {

    @SerializedName("event")
    private boolean mEvent;
    @SerializedName("discount")
    private boolean mDiscount;
    @SerializedName("delivery")
    private boolean mDelivery;
    @SerializedName("review")
    private boolean mReview;

    public Flags() {
    }

    public boolean isEvent() {
        return mEvent;
    }

    public void setEvent(boolean event) {
        mEvent = event;
    }

    public boolean isDiscount() {
        return mDiscount;
    }

    public void setDiscount(boolean discount) {
        mDiscount = discount;
    }

    public boolean isDelivery() {
        return mDelivery;
    }

    public void setDelivery(boolean delivery) {
        mDelivery = delivery;
    }

    public boolean isReview() {
        return mReview;
    }

    public void setReview(boolean review) {
        mReview = review;
    }
}