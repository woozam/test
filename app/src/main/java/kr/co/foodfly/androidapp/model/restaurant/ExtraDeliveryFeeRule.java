package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-31.
 */
public class ExtraDeliveryFeeRule extends RealmObject {

    @SerializedName("menu_sum_thr")
    private int mMenuSumThr;
    @SerializedName("additional_fee_percent")
    private int mAdditionalFeePercent;

    public ExtraDeliveryFeeRule() {
    }

    public int getMenuSumThr() {
        return mMenuSumThr;
    }

    public void setMenuSumThr(int menuSumThr) {
        mMenuSumThr = menuSumThr;
    }

    public int getAdditionalFeePercent() {
        return mAdditionalFeePercent;
    }

    public void setAdditionalFeePercent(int additionalFeePercent) {
        mAdditionalFeePercent = additionalFeePercent;
    }
}