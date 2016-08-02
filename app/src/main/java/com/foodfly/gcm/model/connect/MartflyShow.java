package com.foodfly.gcm.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class MartflyShow extends RealmObject {

    @SerializedName("areacode_prefix")
    private long mAreaCodePrefix;

    public MartflyShow() {
    }

    public long getAreaCodePrefix() {
        return mAreaCodePrefix;
    }

    public void setAreaCodePrefix(long areaCodePrefix) {
        mAreaCodePrefix = areaCodePrefix;
    }
}
