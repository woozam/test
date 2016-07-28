package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import kr.co.foodfly.androidapp.model.BaseResponse;

/**
 * Created by woozam on 2016-07-10.
 */
public class Theme extends BaseResponse implements Serializable {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("title_image")
    private String mTitleImage;
    @SerializedName("detail_image")
    private String mDetailImage;

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

    public String getTitleImage() {
        return mTitleImage;
    }

    public void setTitleImage(String titleImage) {
        mTitleImage = titleImage;
    }

    public String getDetailImage() {
        return mDetailImage;
    }

    public void setDetailImage(String detailImage) {
        mDetailImage = detailImage;
    }
}
