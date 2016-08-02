package com.foodfly.gcm.model.restaurant;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by woozam on 2016-07-01.
 */
public class SearchInfo extends RealmObject {

    @PrimaryKey
    private String mQuery;
    private Date mDate;

    @Ignore
    private boolean mDeletable = true;

    public SearchInfo() {
    }

    public SearchInfo(String query, Date date) {
        mQuery = query;
        mDate = date;
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isDeletable() {
        return mDeletable;
    }

    public void setDeletable(boolean deletable) {
        mDeletable = deletable;
    }
}