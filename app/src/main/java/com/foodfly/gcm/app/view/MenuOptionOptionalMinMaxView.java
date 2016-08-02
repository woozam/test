package com.foodfly.gcm.app.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.view.MenuView.IMenuOptionItemView;
import com.foodfly.gcm.app.view.MenuView.IMenuOptionView;
import com.foodfly.gcm.app.view.MenuView.OnOptionChangedListener;
import com.foodfly.gcm.model.restaurant.MenuOption;
import com.foodfly.gcm.model.restaurant.MenuOptionItem;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOptionOptionalMinMaxView extends LinearLayout implements IMenuOptionView, OnClickListener {

    private TextView mName;
    private TextView mType;
    private LinearLayout mCheckBoxGroup;
    private MenuOption mMenuOption;
    private ArrayList<AppCompatCheckBox> mCheckBoxList;
    private OnOptionChangedListener mOnOptionChangedListener;
    private ArrayList<IMenuOptionItemView> mMenuOptionItemViews;

    public MenuOptionOptionalMinMaxView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.row_menu_option_optional, this, true);
        mName = (TextView) findViewById(R.id.menu_option_optional_name);
        mType = (TextView) findViewById(R.id.menu_option_optional_type);
        mCheckBoxGroup = (LinearLayout) findViewById(R.id.menu_option_optional_check_box_group);
        mCheckBoxList = new ArrayList<>();
        mMenuOptionItemViews = new ArrayList<>();
    }

    @Override
    public void setOption(MenuOption menuOption) {
        mMenuOption = menuOption;
        mName.setText(menuOption.getName());
        mType.setText(String.format(Locale.getDefault(), "(옵션, %d개 까지)", menuOption.getMax()));
        for (MenuOptionItem item : menuOption.getItems()) {
            MenuOptionCheckBoxItemView itemView = new MenuOptionCheckBoxItemView(getContext());
            itemView.setMenuOptionItem(item);
            itemView.getCheckBox().setOnClickListener(this);
            mCheckBoxList.add(itemView.getCheckBox());
            mCheckBoxGroup.addView(itemView);
            mMenuOptionItemViews.add(itemView);
        }
    }

    @Override
    public MenuOption getMenuOption() {
        return mMenuOption;
    }

    @Override
    public void setOnOptionChangedListener(OnOptionChangedListener l) {
        mOnOptionChangedListener = l;
    }

    @Override
    public List<MenuOptionItem> getCheckedItemList() {
        ArrayList<MenuOptionItem> checkedItemList = new ArrayList<>();
        for (IMenuOptionItemView menuOptionItemView : mMenuOptionItemViews) {
            if (menuOptionItemView.isChecked()) {
                MenuOptionItem menuOptionItem = menuOptionItemView.getMenuOptionItem();
                menuOptionItem.setParentId(mMenuOption.getId());
                checkedItemList.add(menuOptionItem);
            }
        }
        return checkedItemList;
    }

    @Override
    public void onClick(View v) {
        if (((CompoundButton) v).isChecked()) {
            int count = 0;
            for (AppCompatCheckBox checkBox : mCheckBoxList) {
                if (checkBox.isChecked()) {
                    count++;
                }
            }
            if (count > mMenuOption.getMax()) {
                Toast.makeText(getContext(), "최대 선택개수를 초과하였습니다.", Toast.LENGTH_SHORT).show();
                ((CompoundButton) v).setChecked(false);
            } else {
                mOnOptionChangedListener.onOptionChanged();
            }
        } else {
            mOnOptionChangedListener.onOptionChanged();
        }
    }
}