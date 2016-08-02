package com.foodfly.gcm.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class Martfly extends RealmObject {

    @SerializedName("show")
    private RealmList<MartflyShow> mShow;

    public Martfly() {
    }

    public RealmList<MartflyShow> getShow() {
        return mShow;
    }

    public void setShow(RealmList<MartflyShow> show) {
        mShow = show;
    }
}