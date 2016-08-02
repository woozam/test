package com.foodfly.gcm.app.view;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.view.MenuView.IMenuOptionItemView;
import com.foodfly.gcm.common.UnitUtils;
import com.foodfly.gcm.model.restaurant.MenuOptionItem;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOptionRadioItemView extends RelativeLayout implements IMenuOptionItemView {

    private AppCompatRadioButton mRadioButton;
    private TextView mPrice;
    private MenuOptionItem mMenuOptionItem;

    public MenuOptionRadioItemView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.row_menu_option_radio_button, this, true);
        mRadioButton = (AppCompatRadioButton) findViewById(R.id.menu_option_radio_button_name);
        mPrice = (TextView) findViewById(R.id.menu_option_radio_button_price);
    }

    @Override
    public void setMenuOptionItem(MenuOptionItem menuOptionItem) {
        mMenuOptionItem = menuOptionItem;
        mRadioButton.setText(menuOptionItem.getName());
        mPrice.setText(String.format(Locale.getDefault(), "+%s원", UnitUtils.priceFormat(menuOptionItem.getPrice())));
    }

    @Override
    public MenuOptionItem getMenuOptionItem() {
        return mMenuOptionItem;
    }

    public AppCompatRadioButton getRadioButton() {
        return mRadioButton;
    }

    @Override
    public boolean isChecked() {
        return mRadioButton.isChecked();
    }
}