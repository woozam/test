package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class OpeningHourExtra extends RealmObject {

    @SerializedName("override_reservation")
    private boolean mOverrideReservation;
    @SerializedName("reservation_start")
    private String mReservationStart;
    @SerializedName("override_delivery")
    private boolean mOverrideDelivery;
    @SerializedName("delivery_start")
    private String mDeliveryStart;
    @SerializedName("delivery_end")
    private String mDeliveryEnd;

    public OpeningHourExtra() {
    }

    public boolean isOverrideReservation() {
        return mOverrideReservation;
    }

    public void setOverrideReservation(boolean overrideReservation) {
        mOverrideReservation = overrideReservation;
    }

    public String getReservationStart() {
        return mReservationStart;
    }

    public void setReservationStart(String reservationStart) {
        mReservationStart = reservationStart;
    }

    public boolean isOverrideDelivery() {
        return mOverrideDelivery;
    }

    public void setOverrideDelivery(boolean overrideDelivery) {
        mOverrideDelivery = overrideDelivery;
    }

    public String getDeliveryStart() {
        return mDeliveryStart;
    }

    public void setDeliveryStart(String deliveryStart) {
        mDeliveryStart = deliveryStart;
    }

    public String getDeliveryEnd() {
        return mDeliveryEnd;
    }

    public void setDeliveryEnd(String deliveryEnd) {
        mDeliveryEnd = deliveryEnd;
    }
}