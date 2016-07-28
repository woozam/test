package kr.co.foodfly.androidapp.model.restaurant;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class Category extends RealmObject {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;

    public Category() {
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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (o instanceof Category) {
            return TextUtils.equals(getId(), ((Category) o).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(getId())) {
            return 0;
        } else {
            return getId().hashCode();
        }
    }
}