package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.view.MenuView.IMenuOptionItemView;
import kr.co.foodfly.androidapp.app.view.MenuView.IMenuOptionView;
import kr.co.foodfly.androidapp.app.view.MenuView.OnOptionChangedListener;
import kr.co.foodfly.androidapp.model.restaurant.MenuOption;
import kr.co.foodfly.androidapp.model.restaurant.MenuOptionItem;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuOptionOptionalView extends LinearLayout implements IMenuOptionView, OnClickListener {

    private TextView mName;
    private TextView mType;
    private LinearLayout mCheckBoxGroup;
    private MenuOption mMenuOption;
    private ArrayList<AppCompatCheckBox> mCheckBoxList;
    private OnOptionChangedListener mOnOptionChangedListener;
    private ArrayList<IMenuOptionItemView> mMenuOptionItemViews;

    public MenuOptionOptionalView(Context context) {
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
//        if (((CompoundButton) v).isChecked()) {
//            for (AppCompatCheckBox checkBox : mCheckBoxList) {
//                if (checkBox != v) {
//                    checkBox.setChecked(false);
//                }
//            }
//        }
        mOnOptionChangedListener.onOptionChanged();
    }
}