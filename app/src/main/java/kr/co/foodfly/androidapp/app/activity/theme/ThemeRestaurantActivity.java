package kr.co.foodfly.androidapp.app.activity.theme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.RestaurantListFragment;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.restaurant.Theme;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-11.
 */
public class ThemeRestaurantActivity extends BaseActivity {

    public static final String EXTRA_THEME = "extra_theme";
    public static final String EXTRA_ID = "extra_id";

    public static void createInstance(Context context, Theme theme) {
        Intent intent = new Intent(context, ThemeRestaurantActivity.class);
        intent.putExtra(EXTRA_THEME, theme);
        context.startActivity(intent);
    }

    public static void createInstance(Context context, String id) {
        Intent intent = new Intent(context, ThemeRestaurantActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Theme theme = (Theme) intent.getSerializableExtra(EXTRA_THEME);
        String id = intent.getStringExtra(EXTRA_ID);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("restaurantList");
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        ft.commitAllowingStateLoss();

        if (theme != null) {
            showThemeRestaurantList(theme);
        } else if (!TextUtils.isEmpty(id)) {
            loadThemeList(id);
        } else {
            finish();
        }
    }

    private void showThemeRestaurantList(Theme theme) {
        getSupportActionBar().setTitle(theme.getName());

        Bundle args = new Bundle();
        args.putInt(RestaurantListFragment.EXTRA_COLUMN_COUNT, 1);
        args.putSerializable(RestaurantListFragment.EXTRA_THEME, theme);
        args.putInt(RestaurantListFragment.EXTRA_ROW_LAYOUT_RES_ID, R.layout.row_restaurant_list_2);
        args.putInt(RestaurantListFragment.EXTRA_SPACING, 12);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RestaurantListFragment fragment = (RestaurantListFragment) RestaurantListFragment.instantiate(this, RestaurantListFragment.class.getName(), args);
        ft.add(R.id.theme_restaurant_container, fragment, "restaurantList");
        ft.commitAllowingStateLoss();
    }

    private void loadThemeList(final String id) {
        MapAddress mapAddress = MapAddress.getAddress();
        String url = APIs.getThemePath().appendQueryParameter(APIs.PARAM_LAT, String.valueOf(mapAddress.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(mapAddress.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(mapAddress.getAreaCode())).toString();
        GsonRequest<Theme[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<Theme[]>() {
        }.getType(), APIs.createHeadersWithToken(), new Listener<Theme[]>() {
            @Override
            public void onResponse(Theme[] response) {
                dismissProgressDialog();
                for (Theme theme : response) {
                    if (TextUtils.equals(theme.getId(), id)) {
                        showThemeRestaurantList(theme);
                        return;
                    }
                }
                finish();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(ThemeRestaurantActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }
}
