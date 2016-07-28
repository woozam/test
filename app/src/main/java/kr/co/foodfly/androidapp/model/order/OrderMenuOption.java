package kr.co.foodfly.androidapp.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderMenuOption {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("charge")
    private int mCharge;
    @SerializedName("items")
    private ArrayList<OrderMenuOptionItem> mItems;

    public OrderMenuOption() {
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

    public int getCharge() {
        return mCharge;
    }

    public void setCharge(int charge) {
        mCharge = charge;
    }

    public ArrayList<OrderMenuOptionItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<OrderMenuOptionItem> items) {
        mItems = items;
    }
}