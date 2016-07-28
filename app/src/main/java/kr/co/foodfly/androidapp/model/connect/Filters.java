package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class Filters extends RealmObject {

    @SerializedName("orders")
    private RealmList<FiltersOrder> mOrders;
    @SerializedName("coupons")
    private RealmList<FiltersCoupon> mCoupons;

    public Filters() {
    }

    public RealmList<FiltersOrder> getOrders() {
        return mOrders;
    }

    public void setOrders(RealmList<FiltersOrder> orders) {
        mOrders = orders;
    }

    public RealmList<FiltersCoupon> getCoupons() {
        return mCoupons;
    }

    public void setCoupons(RealmList<FiltersCoupon> coupons) {
        mCoupons = coupons;
    }
}
