package com.foodfly.gcm.model.user;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-27.
 */
public class User extends RealmObject {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("username")
    private String mUserName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("mileage")
    private int mMileage;
    @SerializedName("mileage_grade")
    private int mMileageGrade;
    @SerializedName("agree_recv_email")
    private boolean mAgreeRecvEmail;
    @SerializedName("agree_recv_sms")
    private boolean mAgreeRecvSMS;
    @SerializedName("agree_recv_push")
    private boolean mAgreeRecvPush;
    @SerializedName("address")
    private MapAddress mAddress;
    @SerializedName("coupons")
    private RealmList<Coupon> mCouponList;
    @SerializedName("referral_code")
    private String mReferralCode;

    public User() {
        mCouponList = new RealmList<>();
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

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public int getMileage() {
        return mMileage;
    }

    public void setMileage(int mileage) {
        mMileage = mileage;
    }

    public int getMileageGrade() {
        return mMileageGrade;
    }

    public void setMileageGrade(int mileageGrade) {
        mMileageGrade = mileageGrade;
    }

    public boolean isAgreeRecvEmail() {
        return mAgreeRecvEmail;
    }

    public void setAgreeRecvEmail(boolean agreeRecvEmail) {
        mAgreeRecvEmail = agreeRecvEmail;
    }

    public boolean isAgreeRecvSMS() {
        return mAgreeRecvSMS;
    }

    public void setAgreeRecvSMS(boolean agreeRecvSMS) {
        mAgreeRecvSMS = agreeRecvSMS;
    }

    public boolean isAgreeRecvPush() {
        return mAgreeRecvPush;
    }

    public void setAgreeRecvPush(boolean agreeRecvPush) {
        mAgreeRecvPush = agreeRecvPush;
    }

    public MapAddress getAddress() {
        return mAddress;
    }

    public void setAddress(MapAddress address) {
        mAddress = address;
    }

    public RealmList<Coupon> getCouponList() {
        return mCouponList;
    }

    public int getAvailableCouponCount() {
        if (mCouponList == null) {
            return 0;
        } else {
            int count = 0;
            for (Coupon coupon : mCouponList) {
                if (!coupon.isExpired() && !coupon.isUsed() && coupon.isCouponValid()) {
                    count++;
                }
            }
            return count;
        }
    }

    public void setCouponList(RealmList<Coupon> couponList) {
        mCouponList = couponList;
    }

    public String getReferralCode() {
        return mReferralCode;
    }

    public void setReferralCode(String referralCode) {
        mReferralCode = referralCode;
    }
}