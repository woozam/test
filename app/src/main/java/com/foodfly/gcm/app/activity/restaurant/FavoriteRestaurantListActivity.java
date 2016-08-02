package com.foodfly.gcm.app.activity.restaurant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;

/**
 * Created by woozam on 2016-07-14.
 */
public class FavoriteRestaurantListActivity extends BaseActivity {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, FavoriteRestaurantListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = new Bundle();
        args.putInt(RestaurantListFragment.EXTRA_COLUMN_COUNT, 1);
        args.putInt(RestaurantListFragment.EXTRA_ROW_LAYOUT_RES_ID, R.layout.row_restaurant_list_2);
        args.putInt(RestaurantListFragment.EXTRA_SPACING, 12);
        args.putBoolean(RestaurantListFragment.EXTRA_FAVORITE, true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("restaurantList");
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        RestaurantListFragment fragment = (RestaurantListFragment) RestaurantListFragment.instantiate(this, RestaurantListFragment.class.getName(), args);
        ft.add(R.id.favorite_restaurant_container, fragment, "restaurantList");
        ft.commitAllowingStateLoss();
    }
}
