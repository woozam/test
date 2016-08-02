package com.foodfly.gcm.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderMenu {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("image")
    private String mImage;
    @SerializedName("price")
    private int mPrice;
    @SerializedName("charge")
    private int mCharge;
    @SerializedName("quantity")
    private int mQuantity;
    @SerializedName("options")
    private ArrayList<OrderMenuOption> mOptions;
    @SerializedName("like")
    private int mLike;
    @SerializedName("comment")
    private String mComment;

    public OrderMenu() {
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

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public int getCharge() {
        return mCharge;
    }

    public void setCharge(int charge) {
        mCharge = charge;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public ArrayList<OrderMenuOption> getOptions() {
        return mOptions;
    }

    public void setOptions(ArrayList<OrderMenuOption> options) {
        mOptions = options;
    }

    public int getLike() {
        return mLike;
    }

    public void setLike(int like) {
        mLike = like;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public int getOptionPrice() {
        int total = 0;
        for (OrderMenuOption menuOption : mOptions) {
            total += menuOption.getCharge();
        }
        return total;
    }

    public String getOptionString() {
        String option = "";
        for (OrderMenuOption menuOption : mOptions) {
            for (OrderMenuOptionItem item : menuOption.getItems()) {
                if (option.length() > 0) option += ", ";
                option += item.getName();
            }
        }
        return option;
    }
}