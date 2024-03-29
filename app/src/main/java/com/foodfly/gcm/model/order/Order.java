package com.foodfly.gcm.model.order;

import com.foodfly.gcm.model.restaurant.Restaurant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.foodfly.gcm.model.user.MapAddress;

/**
 * Created by woozam on 2016-07-28.
 */
public class Order {

    public static final int STATUS_PENDING = 1;
    public static final int STATUS_COMPLETE = 2;
    public static final int STATUS_CANCEL = 3;

    @SerializedName("id")
    private String mId;
    @SerializedName("order_number")
    private String mOrderNumber;
    @SerializedName("order_time")
    private Date mOrderTime;
    @SerializedName("status")
    private int mStatus;
    @SerializedName("delivery_status")
    private int mDeliveryStatus;
    @SerializedName("restaurant")
    private Restaurant mRestaurant;
    @SerializedName("timestamps")
    private ArrayList<OrderTimestamp> mTimestamps;
    @SerializedName("rider")
    private OrderRider mRider;
    @SerializedName("address")
    private MapAddress mAddress;
    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("receipient_name")
    private String mRecipientName;
    @SerializedName("receipient_phone")
    private String mRecipientPhone;
    @SerializedName("delivery_type")
    private int mDeliveryType;
    @SerializedName("reservation_time")
    private Date mReservationTime;
    @SerializedName("payment_type")
    private int mPaymentType;
    @SerializedName("coupon_id")
    private String mCouponId;
    @SerializedName("menus")
    private ArrayList<OrderMenu> mMenus;
    @SerializedName("charge")
    private OrderCharge mCharge;

    public Order() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        mOrderNumber = orderNumber;
    }

    public Date getOrderTime() {
        return mOrderTime;
    }

    public void setOrderTime(Date orderTime) {
        mOrderTime = orderTime;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getDeliveryStatus() {
        return mDeliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        mDeliveryStatus = deliveryStatus;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public ArrayList<OrderTimestamp> getTimestamps() {
        return mTimestamps;
    }

    public void setTimestamps(ArrayList<OrderTimestamp> timestamps) {
        mTimestamps = timestamps;
    }

    public OrderRider getRider() {
        return mRider;
    }

    public void setRider(OrderRider rider) {
        mRider = rider;
    }

    public MapAddress getAddress() {
        return mAddress;
    }

    public void setAddress(MapAddress address) {
        mAddress = address;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getRecipientName() {
        return mRecipientName;
    }

    public void setRecipientName(String recipientName) {
        mRecipientName = recipientName;
    }

    public String getRecipientPhone() {
        return mRecipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        mRecipientPhone = recipientPhone;
    }

    public int getDeliveryType() {
        return mDeliveryType;
    }

    public String getDeliveryTypeString() {
        switch (mDeliveryType) {
            case 1:
                return "푸드플라이 배달";
            case 2:
                return "매장 자체 배달";
            case 3:
                return "테이크아웃";
        }
        return "";
    }

    public void setDeliveryType(int deliveryType) {
        mDeliveryType = deliveryType;
    }

    public Date getReservationTime() {
        return mReservationTime;
    }

    public void setReservationTime(Date reservationTime) {
        mReservationTime = reservationTime;
    }

    public int getPaymentType() {
        return mPaymentType;
    }

    public void setPaymentType(int paymentType) {
        mPaymentType = paymentType;
    }

    public String getPaymentString() {
        switch (mPaymentType) {
            case 1:
                return "온라인 카드";
            case 2:
                return "온라인 계좌이체";
            case 3:
                return "현장카드";
            case 4:
                return "현장현금";
        }
        return "";
    }

    public String getCouponId() {
        return mCouponId;
    }

    public void setCouponId(String couponId) {
        mCouponId = couponId;
    }

    public ArrayList<OrderMenu> getMenus() {
        return mMenus;
    }

    public void setMenus(ArrayList<OrderMenu> menus) {
        mMenus = menus;
    }

    public String getMenuString() {
        String menuString = "";
        for (OrderMenu menu : mMenus) {
            if (menuString.length() > 0) menuString += ", ";
            menuString += menu.getName();
        }
        menuString += String.format(Locale.getDefault(), "(총%d개)", mMenus.size());
        return menuString;
    }

    public OrderCharge getCharge() {
        return mCharge;
    }

    public void setCharge(OrderCharge charge) {
        mCharge = charge;
    }
}