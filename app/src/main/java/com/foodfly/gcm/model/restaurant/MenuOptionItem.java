package com.foodfly.gcm.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOptionItem extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String mId;
    private String mParentId;
    @SerializedName("name")
    private String mName;
    @SerializedName("price")
    private int mPrice;

    public MenuOptionItem() {
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

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }
}