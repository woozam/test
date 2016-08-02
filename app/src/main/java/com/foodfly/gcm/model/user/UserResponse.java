package com.foodfly.gcm.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import com.foodfly.gcm.model.ResponseError;

/**
 * Created by woozam on 2016-07-08.
 */
public class UserResponse extends RealmObject {

    @PrimaryKey
    private String mId;
    @SerializedName("user")
    private User mUser;
    @SerializedName("auth_token")
    private String mAuthToken;
    @SerializedName("auth_token_valid_until")
    private Date mAuthTokenValidUntil;
    @SerializedName("refresh_token")
    private String mRefreshToken;
    @SerializedName("refresh_token_valid_until")
    private Date mRefreshTokenValidUntil;
    @Ignore
    @SerializedName("error")
    private ResponseError mError;

    public UserResponse() {
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    public Date getAuthTokenValidUntil() {
        return mAuthTokenValidUntil;
    }

    public void setAuthTokenValidUntil(Date authTokenValidUntil) {
        mAuthTokenValidUntil = authTokenValidUntil;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }

    public Date getRefreshTokenValidUntil() {
        return mRefreshTokenValidUntil;
    }

    public void setRefreshTokenValidUntil(Date refreshTokenValidUntil) {
        mRefreshTokenValidUntil = refreshTokenValidUntil;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public ResponseError getError() {
        return mError;
    }

    public void setError(ResponseError error) {
        mError = error;
    }
}