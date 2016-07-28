package kr.co.foodfly.androidapp.app.activity.main;

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
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.astuetz.PagerSlidingTabStrip;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.etc.CSActivity;
import kr.co.foodfly.androidapp.app.activity.kickOff.LoginActivity;
import kr.co.foodfly.androidapp.app.activity.kickOff.SignUpActivity;
import kr.co.foodfly.androidapp.app.activity.main.MainNavigationView.OnMainNavigationMenuListener;
import kr.co.foodfly.androidapp.app.activity.order.CartActivity;
import kr.co.foodfly.androidapp.app.activity.order.OrderListActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.FavoriteRestaurantListActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.RecentRestaurantListActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.RestaurantListFragment;
import kr.co.foodfly.androidapp.app.activity.setting.AddressActivity;
import kr.co.foodfly.androidapp.app.activity.theme.ThemeActivity;
import kr.co.foodfly.androidapp.app.activity.user.CouponActivity;
import kr.co.foodfly.androidapp.app.activity.user.MileageActivity;
import kr.co.foodfly.androidapp.app.activity.user.PromotionActivity;
import kr.co.foodfly.androidapp.app.activity.user.ReferralCodeActivity;
import kr.co.foodfly.androidapp.app.activity.user.SelectAddressActivity;
import kr.co.foodfly.androidapp.app.activity.user.UserActivity;
import kr.co.foodfly.androidapp.app.dialog.RestaurantFilter;
import kr.co.foodfly.androidapp.app.view.CustomSearchView;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.PreferenceUtils;
import kr.co.foodfly.androidapp.common.ViewSupportUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.Chefly;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.restaurant.CartMenu;
import kr.co.foodfly.androidapp.model.restaurant.SearchInfo;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;

public class MainActivity extends BaseActivity implements OnClickListener, RealmChangeListener<RealmResults<MapAddress>>, OnQueryTextListener, OnCloseListener, OnMainNavigationMenuListener, DrawerListener {

    public static final String ACTION_MAIN_SEARCH = "action_main_search";
    public static final String EXTRA_QUERY = "extra_query";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        context.startActivity(intent);
    }

    public static void createInstance(Context context, String categoryId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
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
    private CustomSearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private MainNavigationView mMainNavigationView;
    private String mLastQuery;
    private ImageView mChefly;

    private Realm mUserRealm;
    private Realm mConnectRealm;
    private Connect mConnect;
    private String mCategoryId;

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
        if (mAddress == null) {
            AddressActivity.createInstance(this);
        }
        showPopupIfExist();
        updateChefly();
        showCategory();

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
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            mSearchView = (CustomSearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setQueryHint("음식명, 레스토랑명 검색");
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnCloseListener(this);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
        updateChefly();
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
            updateChefly();
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
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(ACTION_MAIN_SEARCH);
        intent.putExtra(EXTRA_QUERY, query);
        sendBroadcast(intent);
        ViewSupportUtils.hideSoftInput(mSearchView);
        if (!TextUtils.isEmpty(query) && !TextUtils.equals(query, mLastQuery)) {
            mLastQuery = query;
            SearchInfo searchInfo = new SearchInfo(query, new Date());
            Realm realm = Realm.getInstance(RealmUtils.CONFIG_SEARCH_INFO);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(searchInfo);
            realm.commitTransaction();
            realm.close();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mLastQuery)) {
            mLastQuery = null;
            Intent intent = new Intent(ACTION_MAIN_SEARCH);
            sendBroadcast(intent);
        }
        return false;
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
                CommonUtils.copyToClipboard((String) findViewById(id).getTag());
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
                if (UserManager.fetchUser() == null) {
                    askLogin();
                } else {
                    Realm realm = Realm.getInstance(RealmUtils.CONFIG_CART);
                    long count = realm.where(CartMenu.class).count();
                    realm.close();
                    if (count > 0) {
                        CartActivity.createInstance(this);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        new MaterialDialog.Builder(this).content("장바구니가 비었습니다.").positiveText("확인").show();
                    }
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
        mChefly.setVisibility(mAddress != null && TextUtils.equals(mAddress.getCheflyAvailable(), "1") ? View.VISIBLE : View.GONE);
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
            args.putString(EXTRA_QUERY, mLastQuery);
            if (position == 0) {
                args.putBoolean(RestaurantListFragment.EXTRA_USE_BANNER, true);
            }
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