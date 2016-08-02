package com.foodfly.gcm.model.restaurant;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class Menu extends RealmObject {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image")
    private String mImage;
    @SerializedName("thumbnail")
    private String mThumbnail;
    @SerializedName("price")
    private int mPrice;
    @SerializedName("tag")
    private String mTag;
    @SerializedName("minimum_order")
    private int mMinimumOrder;
    @SerializedName("category_id")
    private String mCategoryId;
    @SerializedName("restaurant_id")
    private String mRestaurantId;
    @SerializedName("is_soldout")
    private boolean mSoldOut;
    @SerializedName("discount_type")
    private int mDiscountType;
    @SerializedName("discount_amount")
    private int mDiscountAmount;
    @SerializedName("discounted_price")
    private int mDiscountedPrice;
    @SerializedName("apply_tax")
    private boolean mApplyTax;
    @SerializedName("options")
    private RealmList<MenuOption> mOptions;

    public Menu() {
        mOptions = new RealmList<>();
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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public int getMinimumOrder() {
        return mMinimumOrder;
    }

    public void setMinimumOrder(int minimumOrder) {
        mMinimumOrder = minimumOrder;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        mRestaurantId = restaurantId;
    }

    public int getDiscountType() {
        return mDiscountType;
    }

    public void setDiscountType(int discountType) {
        mDiscountType = discountType;
    }

    public int getDiscountAmount() {
        return mDiscountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        mDiscountAmount = discountAmount;
    }

    public int getDiscountedPrice() {
        return mDiscountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        mDiscountedPrice = discountedPrice;
    }

    public boolean isApplyTax() {
        return mApplyTax;
    }

    public void setApplyTax(boolean applyTax) {
        mApplyTax = applyTax;
    }

    public boolean isSoldOut() {
        return mSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        mSoldOut = soldOut;
    }

    public RealmList<MenuOption> getOptions() {
        return mOptions;
    }

    public void setOptions(RealmList<MenuOption> options) {
        mOptions = options;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (o instanceof Menu) {
            return TextUtils.equals(getId(), ((Menu) o).getId());
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