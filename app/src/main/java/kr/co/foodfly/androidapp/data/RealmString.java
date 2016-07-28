package kr.co.foodfly.androidapp.data;

import io.realm.RealmObject;

/**
 * Created by woody on 2016-06-08.
 */
public class RealmString extends RealmObject {

    private String mValue;

    public RealmString() {
        mValue = "";
    }

    public RealmString(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public String toString() {
        return mValue;
    }
}