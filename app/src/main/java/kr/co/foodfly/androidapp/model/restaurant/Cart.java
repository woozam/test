package kr.co.foodfly.androidapp.model.restaurant;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by woozam on 2016-07-24.
 */
public class Cart extends RealmObject {

    @PrimaryKey
    private String mId;
    private Restaurant mRestaurant;
    private RealmList<CartMenu> mCartMenus;

    public Cart() {
        mCartMenus = new RealmList<>();
    }

    public Cart(Restaurant restaurant) {
        this();
        mRestaurant = restaurant;
        mId = mRestaurant.getId();
    }

    public String getId() {
        return mId;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public RealmList<CartMenu> getCartMenus() {
        return mCartMenus;
    }

    public void setCartMenus(RealmList<CartMenu> cartMenus) {
        mCartMenus = cartMenus;
    }

    public int getTotal() {
        int total = 0;
        for (CartMenu cartMenu : mCartMenus) {
            total += cartMenu.getTotalPrice();
        }
        return total;
    }

    public int getMenuCount() {
        int count = 0;
        for (CartMenu cartMenu : mCartMenus) {
            count += cartMenu.getQuantity();
        }
        return count;
    }

    public CartMenu getSameMenu(CartMenu cartMenu) {
        for (CartMenu each : mCartMenus) {
            if (cartMenu.getMenu().getId().equals(each.getMenu().getId())) {
                boolean same = true;
                if (cartMenu.getMenuOptionItems().size() == each.getMenuOptionItems().size()) {
                    for (int i = 0; i < cartMenu.getMenuOptionItems().size(); i++) {
                        if (!cartMenu.getMenuOptionItems().get(i).getId().equals(each.getMenuOptionItems().get(i).getId())) {
                            same = false;
                            break;
                        }
                    }
                }
                if (same) {
                    return each;
                }
            }
        }
        return null;
    }
}