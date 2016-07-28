package kr.co.foodfly.androidapp.model.restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-24.
 */
public class CartMenu extends RealmObject {

    private Menu mMenu;
    private int mQuantity = 1;
    private RealmList<MenuOptionItem> mMenuOptionItems;

    public CartMenu() {
        mMenuOptionItems = new RealmList<>();
    }

    public CartMenu(Menu menu) {
        this();
        mMenu = menu;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public RealmList<MenuOptionItem> getMenuOptionItems() {
        return mMenuOptionItems;
    }

    public HashMap<String, List<MenuOptionItem>> getMenuOptionItemsMap() {
        HashMap<String, List<MenuOptionItem>> hashMap = new HashMap();
        for (MenuOptionItem menuOptionItem : mMenuOptionItems) {
            List<MenuOptionItem> menuOptionItems = hashMap.get(menuOptionItem.getParentId());
            if (menuOptionItems == null) {
                menuOptionItems = new ArrayList<>();
                hashMap.put(menuOptionItem.getParentId(), menuOptionItems);
            }
            menuOptionItems.add(menuOptionItem);
        }
        return hashMap;
    }

    public void setMenuOptionItems(RealmList<MenuOptionItem> menuOptionItems) {
        mMenuOptionItems = menuOptionItems;
    }

    public int getOptionPrice() {
        int total = 0;
        for (MenuOptionItem menuOptionItem : mMenuOptionItems) {
            total += menuOptionItem.getPrice();
        }
        return total;
    }

    public int getTotalPrice() {
        int total = 0;
        total += mMenu.getPrice();
        total += getOptionPrice();
        total *= mQuantity;
        return total;
    }

    public String getOptionString() {
        String option = "";
        for (MenuOptionItem menuOptionItem : mMenuOptionItems) {
            if (option.length() > 0) option += ", ";
            option += menuOptionItem.getName();
        }
        return option;
    }
}