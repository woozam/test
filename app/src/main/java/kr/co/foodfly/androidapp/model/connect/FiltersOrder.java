package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class FiltersOrder extends RealmObject {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;

    public FiltersOrder() {
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
}