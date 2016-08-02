package com.foodfly.gcm.model.user;

import com.foodfly.gcm.model.restaurant.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by woozam on 2016-07-10.
 */
public class Mileage {

    @SerializedName("id")
    private String mId;
    @SerializedName("type")
    private String mType;
    @SerializedName("timestamp")
    private Date mTimestamp;
    @SerializedName("restaurant")
    private Restaurant mRestaurant;
    @SerializedName("pay_amount")
    private int mPayAmount;
    @SerializedName("amount")
    private int mAmount;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public int getPayAmount() {
        return mPayAmount;
    }

    public void setPayAmount(int payAmount) {
        mPayAmount = payAmount;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }

    public String getTypeString() {
        //'적립금 수정 타입 0:관리자수정,1:이벤트적립,2:주문사용,3:주문취소,4:이벤트만료,5:결제실패,6:주문적립,7:주문취소차감',
        switch (mType) {
            case "0":
                return "CS";
            case "1":
                return "적립";
            case "2":
                return "사용";
            case "3":
                return "주문취소";
            case "4":
                return "만료";
            case "5":
                return "결제실패";
            case "6":
                return "적립";
            case "7":
                return "회수";
            default:
                return "-";
        }
    }
}