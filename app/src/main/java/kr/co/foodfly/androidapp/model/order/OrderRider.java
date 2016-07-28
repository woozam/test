package kr.co.foodfly.androidapp.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderRider {

    @SerializedName("name")
    private String mName;
    @SerializedName("photo")
    private String mPhoto;

    public OrderRider() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }
}