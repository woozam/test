package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
public class MenuOptionRequiredView extends LinearLayout implements IMenuOptionView, OnClickListener {

    private TextView mName;
    private TextView mType;
    private RadioGroup mRadioGroup;
    private MenuOption mMenuOption;
    private ArrayList<AppCompatRadioButton> mRadioButtonList;
    private OnOptionChangedListener mOnOptionChangedListener;
    private ArrayList<IMenuOptionItemView> mMenuOptionItemViews;

    public MenuOptionRequiredView(Context context) {
        super(context);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.row_menu_option_required, this, true);
        mName = (TextView) findViewById(R.id.menu_option_required_name);
        mType = (TextView) findViewById(R.id.menu_option_required_type);
        mRadioGroup = (RadioGroup) findViewById(R.id.menu_option_required_radio_group);
        mRadioButtonList = new ArrayList<>();
        mMenuOptionItemViews = new ArrayList<>();
    }

    @Override
    public void setOption(MenuOption menuOption) {
        mMenuOption = menuOption;
        mName.setText(menuOption.getName());
        for (MenuOptionItem item : menuOption.getItems()) {
            MenuOptionRadioItemView itemView = new MenuOptionRadioItemView(getContext());
            itemView.setMenuOptionItem(item);
            itemView.getRadioButton().setOnClickListener(this);
            mRadioButtonList.add(itemView.getRadioButton());
            mRadioGroup.addView(itemView);
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
            for (AppCompatRadioButton radioButton : mRadioButtonList) {
                if (radioButton != v) {
                    radioButton.setChecked(false);
                }
            }
        }
        mOnOptionChangedListener.onOptionChanged();
    }
}