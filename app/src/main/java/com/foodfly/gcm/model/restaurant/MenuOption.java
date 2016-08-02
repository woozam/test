package com.foodfly.gcm.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOption extends RealmObject {

    public static final String TYPE_REQUIRED = "1";
    public static final String TYPE_OPTIONAL = "2";
    public static final String TYPE_REQUIRED_MIN_MAX = "3";
    public static final String TYPE_OPTIONAL_MIN_MAX = "4";

    @PrimaryKey
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("type")
    private String mType;
    @SerializedName("min")
    private int mMin;
    @SerializedName("max")
    private int mMax;
    @SerializedName("items")
    private RealmList<MenuOptionItem> mItems;

    public MenuOption() {
        mItems = new RealmList<>();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        mMin = min;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public RealmList<MenuOptionItem> getItems() {
        return mItems;
    }

    public void setItems(RealmList<MenuOptionItem> items) {
        mItems = items;
    }
}