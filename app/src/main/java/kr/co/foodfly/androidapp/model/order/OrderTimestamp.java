package kr.co.foodfly.androidapp.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderTimestamp {

    @SerializedName("tag")
    private String mTag;
    @SerializedName("timestamp")
    private Date mTimestamp;

    public OrderTimestamp() {
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }
}