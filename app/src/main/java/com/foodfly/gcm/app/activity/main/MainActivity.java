package com.foodfly.gcm.app.activity.main;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.Behavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.astuetz.PagerSlidingTabStrip;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.etc.CSActivity;
import com.foodfly.gcm.app.activity.kickOff.LoginActivity;
import com.foodfly.gcm.app.activity.kickOff.SignUpActivity;
import com.foodfly.gcm.app.activity.main.MainNavigationView.OnMainNavigationMenuListener;
import com.foodfly.gcm.app.activity.order.CartActivity;
import com.foodfly.gcm.app.activity.order.OrderListActivity;
import com.foodfly.gcm.app.activity.restaurant.FavoriteRestaurantListActivity;
import com.foodfly.gcm.app.activity.restaurant.RecentRestaurantListActivity;
import com.foodfly.gcm.app.activity.restaurant.RestaurantListFragment;
import com.foodfly.gcm.app.activity.setting.AddressActivity;
import com.foodfly.gcm.app.activity.theme.ThemeActivity;
import com.foodfly.gcm.app.activity.user.CouponActivity;
import com.foodfly.gcm.app.activity.user.MileageActivity;
import com.foodfly.gcm.app.activity.user.MyReferralCodeActivity;
import com.foodfly.gcm.app.activity.user.PromotionActivity;
import com.foodfly.gcm.app.activity.user.ReferralCodeActivity;
import com.foodfly.gcm.app.activity.user.SelectAddressActivity;
import com.foodfly.gcm.app.activity.user.UserActivity;
import com.foodfly.gcm.app.dialog.RestaurantFilter;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.PreferenceUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.Chefly;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.model.restaurant.CartMenu;
import com.foodfly.gcm.model.user.MapAddress;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity implements OnClickListener, RealmChangeListener<RealmResults<MapAddress>>, OnMainNavigationMenuListener, DrawerListener {

    public static final String EXTRA_CATEGORY_ID = "extra_category_id";

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void clearAndcreateInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void createInstance(Context context, String categoryId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        context.startActivity(intent);
    }

    private RestaurantListFragmentAdapter mAdapter;
    private CoordinatorLayout mContentLayout;
    private ViewPager mViewPager;
    private AppBarLayout mPagerTabStripLayout;
    private PagerSlidingTabStrip mPagerTabStrip;
    private View mAddressView;
    private View mAddressIcon;
    private TextView mFormattedAddress;
    private TextView mStreetAddress;
    private RealmResults<MapAddress> mAddressResults;
    private MapAddress mAddress;
    private View mFilter;
    private DrawerLayout mDrawerLayout;
    private MainNavigationView mMainNavigationView;
    private ImageView mChefly;

    private Realm mUserRealm;
    private Realm mConnectRealm;
    private Connect mConnect;
    private String mCategoryId;
    private long mBackPressedTime = 0;
    private boolean mCheckPopup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setCustomView(R.layout.main_custom_action_bar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        mCategoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);

        Realm realm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
        mAddressResults = realm.where(MapAddress.class).equalTo("mDefault", true).findAll();
        mAddressResults.addChangeListener(this);
        UserResponse user = UserManager.fetchUser();
        mUserRealm = Realm.getInstance(RealmUtils.CONFIG_USER);
        mUserRealm.addChangeListener(mUserChangeListener);
        if (user != null) {
            mAddress = user.getUser().getAddress();
        } else {
            if (mAddressResults.size() > 0) {
                mAddress = mAddressResults.get(0);
            }
        }

        mConnectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        mConnectRealm.addChangeListener(mConnectChangeListener);
        mConnect = mConnectRealm.where(Connect.class).findFirst();
        if (mConnect == null || mConnect.getTimestamp().getTime() < System.currentTimeMillis() - 3600 * 24 * 1000) {
            Connect.updateConnect();
        }

        mContentLayout = (CoordinatorLayout) findViewById(R.id.main_content_layout);
        mAdapter = new RestaurantListFragmentAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mViewPager.setAdapter(mAdapter);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateTabPadding();
            }
        });
        mPagerTabStripLayout = (AppBarLayout) findViewById(R.id.main_view_pager_tab_strip_layout);
        mPagerTabStrip = (PagerSlidingTabStrip) findViewById(R.id.main_view_pager_tab_strip);
        mPagerTabStrip.setViewPager(mViewPager);
        mAddressView = findViewById(R.id.main_address_layout);
        mAddressIcon = findViewById(R.id.address_icon);
        mFormattedAddress = (TextView) findViewById(R.id.address_formatted_address);
        mStreetAddress = (TextView) findViewById(R.id.address_street_address);
        mFilter = findViewById(R.id.main_filter);

        mAddressView.setOnClickListener(this);
        mFilter.setOnClickListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        mDrawerLayout.addDrawerListener(this);
        toggle.syncState();

        mMainNavigationView = (MainNavigationView) findViewById(R.id.main_navigation_view);
        mMainNavigationView.setOnMainNavigationMenuListener(this);

        mChefly = (ImageView) findViewById(R.id.main_chefly);
        mChefly.setOnClickListener(this);

        updateAddress();
        updateTabPadding();
        updateChefly();
        showCategory();

        if (mAddress == null) {
            AddressActivity.createInstance(this);
            mCheckPopup = true;
        } else {
            showPopupIfExist();
        }

        UserManager.fetchUserFromServer();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCategoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);
        showCategory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAddressResults.removeChangeListener(this);
        Realm.getInstance(RealmUtils.CONFIG_ADDRESS).close();
        mUserRealm.removeChangeListener(mUserChangeListener);
        mUserRealm.close();
        mConnectRealm.removeChangeListener(mConnectChangeListener);
        mConnectRealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressActivity.REQ_CODE_ADDRESS) {
            if (mCheckPopup) {
                mCheckPopup = false;
                if (resultCode != RESULT_OK) {
                    showPopupIfExist();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mBackPressedTime > 3000) {
                mBackPressedTime = currentTime;
                Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료 됩니다.", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackPressedTime = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchActivity.createInstance(this, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mAddressView) {
            showAddressDialog();
        } else if (v == mFilter) {
            showFilterDialog();
        } else if (v == mChefly) {
            Chefly.createCheflyInstance(this);
        }
    }

    @Override
    public void onChange(RealmResults<MapAddress> element) {
        if (mAddressResults.size() > 0) {
            mAddress = mAddressResults.get(0);
        } else {
            mAddress = null;
        }
        updateAddress();
        expandTab();
    }

    private void showCategory() {
        if (!TextUtils.isEmpty(mCategoryId)) {
            if (mConnect != null) {
                for (int i = 0; i < mConnect.getCategories().size(); i++) {
                    if (TextUtils.equals(mConnect.getCategories().get(i).getId(), mCategoryId)) {
                        mViewPager.setCurrentItem(i);
                        break;
                    }
                }
                mCategoryId = null;
            }
        }
    }

    private RealmChangeListener mUserChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            UserResponse user = UserManager.fetchUser();
            if (user != null) {
                mAddress = user.getUser().getAddress();
            } else {
                mAddress = null;
            }
            updateAddress();
            expandTab();
        }
    };

    private RealmChangeListener mConnectChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            dismissProgressDialog();
            boolean newConnect = false;
            if (mConnect == null) {
                newConnect = true;
            }
            mConnect = mConnectRealm.where(Connect.class).findFirst();
            if (newConnect) {
                showPopupIfExist();
            }
            mAdapter.notifyDataSetChanged();
            showCategory();
            updateChefly();
        }
    };

    private void showPopupIfExist() {
        if (mConnect != null) {
            if (mConnect.getPopups().size() > 0) {
                mConnect.getPopups().get(0).show(this);
            }
        }
    }

    private void showAddressDialog() {
        UserResponse user = UserManager.fetchUser();
        if (user == null || user.getUser().getAddress() == null) {
            AddressActivity.createInstance(this);
        } else {
            new MaterialDialog.Builder(this).items("새 배송지 등록", "주소록에서 선택").itemsCallback(new ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    if (which == 0) {
                        AddressActivity.createInstance(MainActivity.this);
                    } else {
                        SelectAddressActivity.createInstance(MainActivity.this);
                    }
                }
            }).show();
        }
    }

    private void showFilterDialog() {
        if (mConnect == null) {
            Connect.updateConnect();
            showProgressDialog();
        } else {
            MapAddress mapAddress = MapAddress.getAddress();
            final RestaurantFilter restaurantFilter = new RestaurantFilter(this, mConnectRealm.copyFromRealm(mConnect.getFilters()), mConnectRealm.copyFromRealm(mConnect.getDefaults()), mapAddress.getAreaCode());
            new MaterialDialog.Builder(this).title("필터").customView(restaurantFilter, true).positiveText("결과보기").positiveColorRes(R.color.colorPrimary).negativeText("취소").negativeColorRes(R.color.textColorHint).neutralText("초기화").neutralColorRes(R.color.colorAccent).autoDismiss(false).onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    PreferenceUtils.setValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, restaurantFilter.getOrder());
                    PreferenceUtils.setValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, restaurantFilter.getCoupon());
                    MainActivity.this.sendBroadcast(new Intent(RestaurantFilter.ACTION_RESTAURANT_FILTER_CHANGE));
                    expandTab();
                    dialog.dismiss();
                }
            }).onNegative(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            }).onNeutral(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    restaurantFilter.setDefaultValue();
                }
            }).show();
        }
    }

    private void updateTabPadding() {
        ViewGroup viewGroup = (ViewGroup) mPagerTabStrip.getChildAt(0);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            view.setPadding(CommonUtils.convertDipToPx(MainActivity.this, 18), 0, CommonUtils.convertDipToPx(MainActivity.this, 18), 0);
        }
    }

    private void expandTab() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mPagerTabStripLayout.getLayoutParams();
        Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(mContentLayout, mPagerTabStripLayout, null, 0, -10000, true);
        }
    }

    private void updateAddress() {
        if (mAddress == null) {
            mAddressIcon.setEnabled(false);
            mFormattedAddress.setText("설정하기");
            mStreetAddress.setVisibility(View.GONE);
        } else {
            mAddressIcon.setEnabled(true);
            mStreetAddress.setVisibility(View.GONE);
            mFormattedAddress.setText(mAddress.getDisplayContent());
        }
    }

    @Override
    public void onMenu(int id) {
        switch (id) {
            case R.id.main_navigation_login_1:
            case R.id.main_navigation_login_2:
                LoginActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_sign_up:
                SignUpActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_user_name:
                UserActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_user_referral_code:
                MyReferralCodeActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_user_coupon:
                CouponActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_user_mileage:
                MileageActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_cart:
                Realm realm = Realm.getInstance(RealmUtils.CONFIG_CART);
                long count = realm.where(CartMenu.class).count();
                realm.close();
                if (count > 0) {
                    CartActivity.createInstance(this);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    new MaterialDialog.Builder(this).content("장바구니가 비었습니다.").positiveText("확인").show();
                }
                break;
            case R.id.main_navigation_recent:
                RecentRestaurantListActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_theme:
            case R.id.main_navigation_theme_2:
                ThemeActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_martfly:
                CommonUtils.openBrowser(this, "http://m.martfly.co.kr");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_referral:
                if (UserManager.fetchUser() == null) {
                    askLogin();
                } else {
                    ReferralCodeActivity.createInstance(this);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.main_navigation_promotion:
                if (UserManager.fetchUser() == null) {
                    askLogin();
                } else {
                    PromotionActivity.createInstance(this);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.main_navigation_cs:
                CSActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_social_facebook:
                CommonUtils.openBrowser(this, "https://www.facebook.com/foodfly.co.kr");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_social_insta:
                CommonUtils.openBrowser(this, "https://www.instagram.com/foodfly_official");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_social_blog:
                CommonUtils.openBrowser(this, "http://blog.foodfly.co.kr");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_social_youtube:
                CommonUtils.openBrowser(this, "https://www.youtube.com/channel/UCYVpMZrbkiEIoFJMKU3uZFg");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_order:
                OrderListActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_favorite:
                FavoriteRestaurantListActivity.createInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.main_navigation_chefly:
                Chefly.createCheflyInstance(this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private void askLogin() {
        new MaterialDialog.Builder(this).content("로그인 후 이용해주세요.").positiveText("로그인").negativeText("취소").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                LoginActivity.createInstance(MainActivity.this);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }).show();
    }

    private void updateChefly() {
        mChefly.setVisibility(mConnect != null && mConnect.isFloatingEvent() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        UserManager.fetchUserFromServer();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

    private class RestaurantListFragmentAdapter extends FragmentPagerAdapter {

        public RestaurantListFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putInt(RestaurantListFragment.EXTRA_COLUMN_COUNT, 2);
            args.putString(RestaurantListFragment.EXTRA_CATEGORY, mConnect.getCategories().get(position).getId());
            if (position == 0) {
                args.putBoolean(RestaurantListFragment.EXTRA_USE_BANNER, true);
            }
            args.putBoolean(RestaurantListFragment.EXTRA_USE_COMMON_FOOTER, true);
            return RestaurantListFragment.instantiate(MainActivity.this, RestaurantListFragment.class.getName(), args);
        }

        @Override
        public int getCount() {
            if (mConnect != null) {
                return mConnect.getCategories().size();
            } else {
                return 0;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mConnect.getCategories().get(position).getName();
        }
    }
}