package com.foodfly.gcm.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class Info extends RealmObject {

    @SerializedName("restaurant_info")
    private String mRestaurantInfo;
    @SerializedName("origin_info")
    private String mOriginalInfo;
    @SerializedName("youtube_url")
    private String mYoutubeURL;
    @SerializedName("naver_url")
    private String mNaverURL;
    @SerializedName("logo")
    private String mLogo;
    @SerializedName("tel")
    private String mTel;

    public Info() {
    }

    public String getRestaurantInfo() {
        return mRestaurantInfo;
    }

    public void setRestaurantInfo(String restaurantInfo) {
        mRestaurantInfo = restaurantInfo;
    }

    public String getOriginalInfo() {
        return mOriginalInfo;
    }

    public void setOriginalInfo(String originalInfo) {
        mOriginalInfo = originalInfo;
    }

    public String getYoutubeURL() {
        return mYoutubeURL;
    }

    public void setYoutubeURL(String youtubeURL) {
        mYoutubeURL = youtubeURL;
    }

    public String getNaverURL() {
        return mNaverURL;
    }

    public void setNaverURL(String naverURL) {
        mNaverURL = naverURL;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public String getTel() {
        return mTel;
    }

    public void setTel(String tel) {
        mTel = tel;
    }
}