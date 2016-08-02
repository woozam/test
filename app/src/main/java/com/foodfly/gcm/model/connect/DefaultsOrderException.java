package com.foodfly.gcm.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class DefaultsOrderException extends RealmObject {

    @SerializedName("areacode_prefix")
    private long mAreaCodePrefix;
    @SerializedName("value")
    private String mValue;

    public DefaultsOrderException() {
    }

    public long getAreaCodePrefix() {
        return mAreaCodePrefix;
    }

    public void setAreaCodePrefix(long areaCodePrefix) {
        mAreaCodePrefix = areaCodePrefix;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}