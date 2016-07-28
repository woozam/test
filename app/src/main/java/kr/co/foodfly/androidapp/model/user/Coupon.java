package kr.co.foodfly.androidapp.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;

/**
 * Created by woozam on 2016-07-08.
 */
public class Coupon extends RealmObject {

    public static final int DISCOUNT_TYPE_PRICE = 1;
    public static final int DISCOUNT_TYPE_PERCENT = 2;
    public static final int COUPON_TYPE_DELIVERY_TIP = 1;
    public static final int COUPON_TYPE_MENU = 2;
    public static final int COUPON_TYPE_TOTAL = 3;

    @PrimaryKey
    @SerializedName("id")
    private String mId;
    @SerializedName("meta_id")
    private String mMetaId;
    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("type")
    private int mType;
    @SerializedName("discount_type")
    private int mDiscountType;
    @SerializedName("discount_amount")
    private int mDiscountAmount;
    @SerializedName("restaurant")
    private Restaurant mRestaurant;
    @SerializedName("start_date")
    private Date mStartDate;
    @SerializedName("end_date")
    private Date mEndDate;
    @SerializedName("is_expired")
    private boolean mExpired;
    @SerializedName("is_valid")
    private boolean mValid;
    @SerializedName("is_used")
    private boolean mUsed;
    @SerializedName("min_amount")
    private int mMinAmount;

    public Coupon() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMetaId() {
        return mMetaId;
    }

    public void setMetaId(String metaId) {
        mMetaId = metaId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getDiscountType() {
        return mDiscountType;
    }

    public void setDiscountType(int discountType) {
        mDiscountType = discountType;
    }

    public int getDiscountAmount() {
        return mDiscountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        mDiscountAmount = discountAmount;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public boolean isExpired() {
        return mExpired;
    }

    public void setExpired(boolean expired) {
        mExpired = expired;
    }

    public boolean isCouponValid() {
        return mValid;
    }

    public void setValid(boolean valid) {
        mValid = valid;
    }

    public boolean isUsed() {
        return mUsed;
    }

    public void setUsed(boolean used) {
        mUsed = used;
    }

    public int getMinAmount() {
        return mMinAmount;
    }

    public void setMinAmount(int minAmount) {
        mMinAmount = minAmount;
    }
}
