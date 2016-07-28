package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class DefaultsCoupons extends RealmObject {

    @SerializedName("value")
    private String mValue;

    public DefaultsCoupons() {
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getDefaultCouponValue() {
        return getValue();
    }
}