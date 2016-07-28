package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.view.MenuView.IMenuOptionItemView;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.model.restaurant.MenuOptionItem;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOptionCheckBoxItemView extends RelativeLayout implements IMenuOptionItemView {

    private AppCompatCheckBox mCheckBox;
    private TextView mPrice;
    private MenuOptionItem mMenuOptionItem;

    public MenuOptionCheckBoxItemView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.row_menu_option_check_box, this, true);
        mCheckBox = (AppCompatCheckBox) findViewById(R.id.menu_option_check_box_name);
        mPrice = (TextView) findViewById(R.id.menu_option_check_box_price);
    }

    @Override
    public void setMenuOptionItem(MenuOptionItem menuOptionItem) {
        mMenuOptionItem = menuOptionItem;
        mCheckBox.setText(menuOptionItem.getName());
        mPrice.setText(String.format(Locale.getDefault(), "+%s원", UnitUtils.priceFormat(menuOptionItem.getPrice())));
    }

    @Override
    public MenuOptionItem getMenuOptionItem() {
        return mMenuOptionItem;
    }

    public AppCompatCheckBox getCheckBox() {
        return mCheckBox;
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }
}