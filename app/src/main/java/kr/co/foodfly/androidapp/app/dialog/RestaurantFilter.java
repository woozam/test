package kr.co.foodfly.androidapp.app.dialog;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.PreferenceUtils;
import kr.co.foodfly.androidapp.model.connect.Defaults;
import kr.co.foodfly.androidapp.model.connect.Filters;
import kr.co.foodfly.androidapp.model.connect.FiltersCoupon;
import kr.co.foodfly.androidapp.model.connect.FiltersOrder;

/**
 * Created by woozam on 2016-06-29.
 */
public class RestaurantFilter extends LinearLayout implements OnClickListener {

    public static final String ACTION_RESTAURANT_FILTER_CHANGE = "action_restaurant_filter_change";

    private Button[] mOrderButtons;
    private Button[] mCouponButtons;
    private GridLayout mOrderLayout;
    private GridLayout mCouponLayout;
    private Filters mFilters;
    private Defaults mDefaults;
    private long mAreaCode;

    public RestaurantFilter(Context context, Filters filters, Defaults defaults, long areaCode) {
        super(context);
        mFilters = filters;
        mDefaults = defaults;
        mAreaCode = areaCode;
        initialize();
    }

    private void initialize() {
        String order = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, mDefaults.getOrders().getDefaultOrderValue(mAreaCode));
        String coupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, mDefaults.getCoupons().getDefaultCouponValue());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.dialog_restaurant_filter, this, true);
        mOrderLayout = (GridLayout) findViewById(R.id.filter_order);
        mCouponLayout = (GridLayout) findViewById(R.id.filter_coupon);
        mOrderButtons = new Button[mFilters.getOrders().size()];
        mCouponButtons = new Button[mFilters.getCoupons().size()];
        boolean orderSelected = false;
        boolean couponSelected = false;
        for (int i = 0; i < mFilters.getOrders().size(); i++) {
            FiltersOrder filtersOrder = mFilters.getOrders().get(i);
            Button button = (Button) inflater.inflate(R.layout.view_restaurant_filter_button, mOrderLayout, false);
            button.setOnClickListener(this);
            button.setTag(filtersOrder);
            button.setText(filtersOrder.getName());
            mOrderLayout.addView(button);
            mOrderButtons[i] = button;
            if (filtersOrder.getId().equals(order)) {
                button.setSelected(true);
                orderSelected = true;
            }
        }
        if (mFilters.getOrders().size() % 3 != 0) {
            for (int i = 0; i < 3 - mFilters.getOrders().size() % 3; i++) {
                Button button = (Button) inflater.inflate(R.layout.view_restaurant_filter_button, mOrderLayout, false);
                button.setVisibility(INVISIBLE);
                mOrderLayout.addView(button);
            }
        }
        for (int i = 0; i < mFilters.getCoupons().size(); i++) {
            FiltersCoupon filtersCoupon = mFilters.getCoupons().get(i);
            Button button = (Button) inflater.inflate(R.layout.view_restaurant_filter_button, mCouponLayout, false);
            button.setOnClickListener(this);
            button.setTag(filtersCoupon);
            button.setText(filtersCoupon.getName());
            mCouponLayout.addView(button);
            mCouponButtons[i] = button;
            if (filtersCoupon.getId().equals(coupon)) {
                button.setSelected(true);
                couponSelected = true;
            }
        }
        if (mFilters.getCoupons().size() % 3 != 0) {
            for (int i = 0; i < 3 - mFilters.getCoupons().size() % 3; i++) {
                Button button = (Button) inflater.inflate(R.layout.view_restaurant_filter_button, mCouponLayout, false);
                button.setVisibility(INVISIBLE);
                mCouponLayout.addView(button);
            }
        }
        if (!orderSelected || !couponSelected) {
            setDefaultValue();
        }
    }

    @Override
    public void onClick(View v) {
        for (Button button : mOrderButtons) {
            if (v == button) {
                unSelectOrder();
                break;
            }
        }
        for (Button button : mCouponButtons) {
            if (v == button) {
                unSelectCoupon();
                break;
            }
        }
        v.setSelected(true);
    }

    private void unSelectOrder() {
        for (Button button : mOrderButtons) {
            button.setSelected(false);
        }
    }

    private void unSelectCoupon() {
        for (Button button : mCouponButtons) {
            button.setSelected(false);
        }
    }

    public String getOrder() {
        for (Button button : mOrderButtons) {
            if (button.isSelected()) {
                return ((FiltersOrder) button.getTag()).getId();
            }
        }
        return null;
    }

    public String getCoupon() {
        for (Button button : mCouponButtons) {
            if (button.isSelected()) {
                return ((FiltersCoupon) button.getTag()).getId();
            }
        }
        return null;
    }

    public void setDefaultValue() {
        unSelectOrder();
        unSelectCoupon();
        String defaultOrderValue = mDefaults.getOrders().getDefaultOrderValue(mAreaCode);
        String defaultCouponValue = mDefaults.getCoupons().getDefaultCouponValue();
        for (Button button : mOrderButtons) {
            if (((FiltersOrder) button.getTag()).getId().equals(defaultOrderValue)) {
                button.setSelected(true);
                break;
            }
        }
        for (Button button : mCouponButtons) {
            if (((FiltersCoupon) button.getTag()).getId().equals(defaultCouponValue)) {
                button.setSelected(true);
                break;
            }
        }
    }
}