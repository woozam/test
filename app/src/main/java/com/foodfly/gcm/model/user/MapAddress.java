package com.foodfly.gcm.model.user;

import android.text.TextUtils;

import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.ResponseError;
import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by woozam on 2016-06-27.
 */
public class MapAddress extends RealmObject {

    public static final double DEFAULT_LAT = 37.4993931;
    public static final double DEFAULT_LON = 127.0430146;
    public static final long DEFAULT_AREA_CODE = 11680101;

    public static MapAddress getAddress() {
        UserResponse user = UserManager.fetchUser();
        MapAddress mapAddress;
        if (user == null) {
            Realm realm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
            mapAddress = realm.where(MapAddress.class).findFirst();
            if (mapAddress != null) {
                mapAddress = realm.copyFromRealm(mapAddress);
            }
            realm.close();
        } else {
            mapAddress = user.getUser().getAddress();
        }
        if (mapAddress == null) {
            mapAddress = new MapAddress();
            mapAddress.setLat(MapAddress.DEFAULT_LAT);
            mapAddress.setLon(MapAddress.DEFAULT_LON);
            mapAddress.setAreaCode(MapAddress.DEFAULT_AREA_CODE);
        }
        return mapAddress;
    }

    @PrimaryKey
    @SerializedName("id")
    private String mId;
    @SerializedName("alias")
    private String mAlias;
    @SerializedName("formatted_address")
    private String mFormattedAddress;
    @SerializedName("street_address")
    private String mStreetAddress;
    @SerializedName("detail_address")
    private String mDetailAddress;
    @SerializedName("areacode")
    private long mAreaCode;
    @SerializedName("lat")
    private double mLat;
    @SerializedName("lon")
    private double mLon;
    @SerializedName("is_default")
    private boolean mDefault;
    @SerializedName("is_chefly_available")
    private String mCheflyAvailable;

    @SerializedName("bldg_name")
    private String mBldgName;
    @SerializedName("src")
    private String mSrc;
    @SerializedName("took")
    private int mTook;
    @SerializedName("is_service_area")
    private boolean mServiceArea;

    @Ignore
    private ResponseError mError;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        mAlias = alias;
    }

    public String getFormattedAddress() {
        return mFormattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        mFormattedAddress = formattedAddress;
    }

    public String getStreetAddress() {
        return mStreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        mStreetAddress = streetAddress;
    }

    public String getDetailAddress() {
        return mDetailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        mDetailAddress = detailAddress;
    }

    public long getAreaCode() {
        return mAreaCode;
    }

    public void setAreaCode(long areaCode) {
        mAreaCode = areaCode;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public boolean isDefault() {
        return mDefault;
    }

    public void setDefault(boolean aDefault) {
        mDefault = aDefault;
    }

    public String getCheflyAvailable() {
        return mCheflyAvailable;
    }

    public void setCheflyAvailable(String cheflyAvailable) {
        mCheflyAvailable = cheflyAvailable;
    }

    public String getBldgName() {
        return mBldgName;
    }

    public void setBldgName(String bldgName) {
        mBldgName = bldgName;
    }

    public String getSrc() {
        return mSrc;
    }

    public void setSrc(String src) {
        mSrc = src;
    }

    public int getTook() {
        return mTook;
    }

    public void setTook(int took) {
        mTook = took;
    }

    public boolean isServiceArea() {
        return mServiceArea;
    }

    public void setServiceArea(boolean serviceArea) {
        mServiceArea = serviceArea;
    }

    public ResponseError getError() {
        return mError;
    }

    public void setError(ResponseError error) {
        mError = error;
    }

    public String getDisplayContent() {
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(getFormattedAddress())) {
            sb.append(getStreetAddress());
        } else {
            sb.append(getFormattedAddress());
        }
        if (!TextUtils.isEmpty(getDetailAddress())) {
            sb.append(" ");
            sb.append(getDetailAddress());
        }
        return sb.toString();
    }
}