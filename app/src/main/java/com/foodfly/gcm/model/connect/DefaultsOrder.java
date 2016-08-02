package com.foodfly.gcm.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class DefaultsOrder extends RealmObject {

    @SerializedName("value")
    private String mValue;
    @SerializedName("exceptions")
    private RealmList<DefaultsOrderException> mExceptions;

    public DefaultsOrder() {
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public RealmList<DefaultsOrderException> getExceptions() {
        return mExceptions;
    }

    public void setExceptions(RealmList<DefaultsOrderException> exceptions) {
        mExceptions = exceptions;
    }

    public String getDefaultOrderValue(long areaCode) {
        for (DefaultsOrderException exception : getExceptions()) {
            if (String.valueOf(areaCode).startsWith(String.valueOf(exception.getAreaCodePrefix()))) {
                return exception.getValue();
            }
        }
        return getValue();
    }
}