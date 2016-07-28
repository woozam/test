package kr.co.foodfly.androidapp.app.activity.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonObject;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager.OnGroupCollapseListener;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager.OnGroupExpandListener;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.etc.ImageViewerActivity;
import kr.co.foodfly.androidapp.app.activity.kickOff.LoginActivity;
import kr.co.foodfly.androidapp.app.activity.order.CartActivity;
import kr.co.foodfly.androidapp.app.activity.order.OrderActivity;
import kr.co.foodfly.androidapp.app.activity.setting.AddressActivity;
import kr.co.foodfly.androidapp.app.view.CircleViewPagerIndicator;
import kr.co.foodfly.androidapp.app.view.MenuView;
import kr.co.foodfly.androidapp.app.view.MenuView.OnButtonListener;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.connect.Area;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.restaurant.Cart;
import kr.co.foodfly.androidapp.model.restaurant.CartMenu;
import kr.co.foodfly.androidapp.model.restaurant.Menu;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-06-24.
 */
public class RestaurantActivity extends BaseActivity implements OnOffsetChangedListener, OnPageChangeListener, OnGroupExpandListener, OnGroupCollapseListener, OnClickListener, PanelSlideListener, OnButtonListener {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

    public static void createInstance(Context context, String restaurantId) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(EXTRA_RESTAURANT_ID, restaurantId);
        context.startActivity(intent);
    }

    private String mRestaurantId;
    private Restaurant mRestaurant;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RestaurantImageAdapter mImageAdapter;
    private ViewPager mImageViewPager;
    private CircleViewPagerIndicator mImageViewPagerIndicator;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private RestaurantAdapter mWrappedAdapter;
    private Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private Timer mImageChangeTimer;
    private TimerTask mImageChangeTimerTask;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private HashSet<Integer> mMenuExpandedSet;
    private HashSet<Integer> mDetailExpandedSet;
    private View mSlidingTouch;
    private View mNameBG;
    private MenuView mMenuView;
    private View mCartButton;
    private TextView mCartPrice;
    private View mOrderButton;

    private Realm mCartRealm;
    private Cart mCart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("-");

        mCartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
        mCartRealm.addChangeListener(mCartChangeListener);
        mCart = mCartRealm.where(Cart.class).findFirst();
        if (mCart != null) {
            mCart = mCartRealm.copyFromRealm(mCart);
        }

        mRestaurantId = getIntent().getStringExtra(EXTRA_RESTAURANT_ID);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mAppBarLayout.getLayoutParams().height = CommonUtils.getScreenWidth() + CommonUtils.convertDipToPx(this, 48);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mImageAdapter = new RestaurantImageAdapter(getSupportFragmentManager());
        mImageViewPager = (ViewPager) findViewById(R.id.restaurant_image_view_pager);
        mImageViewPager.setOffscreenPageLimit(2);
        mImageViewPager.setAdapter(mImageAdapter);
        mImageViewPager.addOnPageChangeListener(this);
        mImageViewPagerIndicator = (CircleViewPagerIndicator) findViewById(R.id.restaurant_image_view_pager_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.restaurant_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);
        mWrappedAdapter = new RestaurantAdapter(this, this);
        mAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(mWrappedAdapter);
        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
        animator.setSupportsChangeAnimations(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(animator);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);

        mMenuView = (MenuView) findViewById(R.id.restaurant_menu_view);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingUpPanelLayout.setPanelHeight(0);
        mSlidingUpPanelLayout.addPanelSlideListener(this);
        mSlidingUpPanelLayout.setTouchEnabled(false);
        mSlidingTouch = findViewById(R.id.sliding_touch);
        mSlidingTouch.setOnClickListener(this);

        mMenuExpandedSet = new HashSet<>();
        mDetailExpandedSet = new HashSet<>();
        mNameBG = findViewById(R.id.restaurant_name_bg);
        mNameBG.setOnClickListener(this);

        mCartButton = findViewById(R.id.restaurant_cart);
        mCartPrice = (TextView) findViewById(R.id.restaurant_cart_title);
        mOrderButton = findViewById(R.id.restaurant_order);

        mCartButton.setOnClickListener(this);
        mOrderButton.setOnClickListener(this);

        loadRestaurant();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWrappedAdapter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWrappedAdapter.pause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mWrappedAdapter.lowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter.destroy();
        }

        mCartRealm.removeChangeListener(mCartChangeListener);
        mCartRealm.close();

        stopImageChangeTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CartActivity.REQ_CODE_CART) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("add", false)) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED || mSlidingUpPanelLayout.getPanelState() == PanelState.DRAGGING || mSlidingUpPanelLayout.getVisibility() == View.VISIBLE) {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        int height = CommonUtils.convertDipToPx(RestaurantActivity.this, 335);
        int r = 0xff - (int) Math.abs((0xff - 0x46) * (float) verticalOffset / (float) height);
        int g = 0xff - (int) Math.abs((0xff - 0x46) * (float) verticalOffset / (float) height);
        int b = 0xff - (int) Math.abs((0xff - 0x46) * (float) verticalOffset / (float) height);
        int r2 = 0xff - (int) Math.abs((0xff - 0x25) * (float) verticalOffset / (float) height);
        int g2 = 0xff - (int) Math.abs((0xff - 0xc1) * (float) verticalOffset / (float) height);
        int b2 = 0xff - (int) Math.abs((0xff - 0xf1) * (float) verticalOffset / (float) height);
        int color = Color.argb(0xff, r, g, b);
        int color2 = Color.argb(0xff, r2, g2, b2);
        ColorFilter colorFilter = new PorterDuffColorFilter(color, Mode.SRC_ATOP);
        ColorFilter colorFilter2 = new PorterDuffColorFilter(color2, Mode.SRC_ATOP);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            final View v = toolbar.getChildAt(i);
            if (v instanceof ImageButton) {
                ((ImageButton) v).setColorFilter(colorFilter);
            } else if (v instanceof ActionMenuView) {
                for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {
                    final View v2 = ((ActionMenuView) v).getChildAt(j);
                    if (v2 instanceof ActionMenuItemView) {
                        if (((ActionMenuItemView) v2).getItemData().getItemId() == R.id.action_favorite) {
                            Drawable drawable = ((ActionMenuItemView) v2).getCompoundDrawables()[0];
                            if (drawable != null) {
                                drawable.setColorFilter(colorFilter2);
                            }
                            ((ActionMenuItemView) v2).getPaint().setColorFilter(colorFilter);
                        } else {
                            Drawable drawable = ((ActionMenuItemView) v2).getCompoundDrawables()[0];
                            if (drawable != null) {
                                drawable.setColorFilter(colorFilter);
                            }
                            ((ActionMenuItemView) v2).getPaint().setColorFilter(colorFilter);
                        }
                    }
                }
            }
        }
        ImageView cart = (ImageView) toolbar.findViewById(R.id.cart_image);
        if (cart != null) {
            cart.getDrawable().setColorFilter(colorFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant, menu);
        menu.findItem(R.id.action_cart).getActionView().findViewById(R.id.cart_root).setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        MenuItem favorite = menu.findItem(R.id.action_favorite);
        UserResponse user = UserManager.fetchUser();
        if (mRestaurant == null || user == null) {
            favorite.setVisible(false);
        } else {
            favorite.setVisible(true);
            favorite.setIcon(mRestaurant.isFavorite() ? R.mipmap.btn_like : R.mipmap.btn_like_off);
        }
        TextView badge = (TextView) menu.findItem(R.id.action_cart).getActionView().findViewById(R.id.cart_badge);
        if (mCart == null || mCart.getCartMenus().size() == 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(mCart.getCartMenus().size()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                toggleFavorite();
                return true;
            case R.id.action_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRestaurant() {
        UserResponse user = UserManager.fetchUser();
        MapAddress mapAddress = MapAddress.getAddress();
        Uri.Builder builder = APIs.getRestaurantPath().appendPath(mRestaurantId).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(mapAddress.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(mapAddress.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(mapAddress.getAreaCode()));
        if (user != null) {
            builder.appendQueryParameter(APIs.PARAM_USER_ID, user.getId());
        }
        String url = builder.toString();
        GsonRequest<Restaurant> request = new GsonRequest<>(Method.GET, url, Restaurant.class, APIs.createHeadersWithToken(), new Listener<Restaurant>() {
            @Override
            public void onResponse(Restaurant response) {
                dismissProgressDialog();
                setRestaurant(response);
                response.setLastVisitTime(new Date());
                Realm realm = Realm.getInstance(RealmUtils.CONFIG_RESTAURANT);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(response);
                realm.commitTransaction();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(RestaurantActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
        if (restaurant != null) {
            mImageAdapter.notifyDataSetChanged();
            mImageViewPagerIndicator.setPageCount(mImageAdapter.getCount());
            startImageChangeTimer();
            getSupportActionBar().setTitle(restaurant.getName());
            mCollapsingToolbarLayout.setTitle(restaurant.getName());
            mWrappedAdapter.setRestaurant(restaurant);
            mRecyclerViewExpandableItemManager.notifyGroupItemChanged(0);
            mRecyclerViewExpandableItemManager.notifyGroupItemRangeInserted(1, restaurant.getCategoryList().size(), true);
            for (int i = 1; i < restaurant.getCategoryList().size() + 1; i++) {
                mMenuExpandedSet.add(i);
            }
        }
        updateCartPrice();
        invalidateOptionsMenu();
    }

    private void startImageChangeTimer() {
        stopImageChangeTimer();
        mImageChangeTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mImageAdapter.getCount() > 1) {
                                int current = mImageViewPager.getCurrentItem();
                                if (current == mImageAdapter.getCount() - 1) {
                                    mImageViewPager.setCurrentItem(0);
                                } else {
                                    mImageViewPager.setCurrentItem(current + 1);
                                }
                            }
                        }
                    });
                } catch (Exception ignored) {
                }
            }
        };
        mImageChangeTimer = new Timer();
        mImageChangeTimer.schedule(mImageChangeTimerTask, 5000);
    }

    private void stopImageChangeTimer() {
        if (mImageChangeTimer != null) {
            mImageChangeTimer.cancel();
            mImageChangeTimer.purge();
            mImageChangeTimer = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mImageViewPagerIndicator.setPosition(position);
        startImageChangeTimer();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            stopImageChangeTimer();
        }
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {
    }

    private void toggleFavorite() {
        UserResponse user = UserManager.fetchUser();
        if (user == null || mRestaurant == null) {
            return;
        }
        Uri.Builder builder = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_FAVORITE);
        String url;
        GsonRequest<BaseResponse> request;
        if (mRestaurant.isFavorite()) {
            builder.appendPath(mRestaurant.getId());
            url = builder.toString();
            request = new GsonRequest<>(Method.DELETE, url, BaseResponse.class, APIs.createHeadersWithToken(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    dismissProgressDialog();
                    mRestaurant.setFavorite(false);
                    invalidateOptionsMenu();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(RestaurantActivity.this).content(response.getError().message).positiveText("확인").show();
                    }

                }
            }, RealmUtils.REALM_GSON);
        } else {
            JsonObject json = new JsonObject();
            json.addProperty("restaurant_id", mRestaurant.getId());
            url = builder.toString();
            request = new GsonRequest<>(Method.POST, url, BaseResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    dismissProgressDialog();
                    mRestaurant.setFavorite(true);
                    invalidateOptionsMenu();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(RestaurantActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @Override
    public void onClick(View v) {
        if (mRestaurant == null) {
            return;
        }
        if (v.getId() == R.id.cart_root) {
            if (mCart != null && mCart.getCartMenus().size() > 0) {
                CartActivity.createInstance(this);
            } else {
                new MaterialDialog.Builder(this).content("장바구니가 비었습니다.").positiveText("확인").show();
            }
        } else if (v.getId() == R.id.category_root) {
            int groupPosition = (int) v.getTag();
            if (mRecyclerViewExpandableItemManager.isGroupExpanded(groupPosition)) {
                mRecyclerViewExpandableItemManager.collapseGroup(groupPosition);
                mMenuExpandedSet.remove(groupPosition);
            } else {
                mRecyclerViewExpandableItemManager.expandGroup(groupPosition);
                mMenuExpandedSet.add(groupPosition);
            }
        } else if (v.getId() == R.id.restaurant_menu_root) {
            Menu menu = (Menu) v.getTag();
            showMenu(menu);
        } else if (v.getId() == R.id.restaurant_info_menu) {
            showMenuList();
        } else if (v.getId() == R.id.restaurant_info_detail) {
            showDetail();
        } else if (v.getId() == R.id.restaurant_detail_title_root) {
            int groupPosition = (int) v.getTag();
            if (mRecyclerViewExpandableItemManager.isGroupExpanded(groupPosition)) {
                mRecyclerViewExpandableItemManager.collapseGroup(groupPosition);
                mDetailExpandedSet.remove(groupPosition);
            } else {
                mRecyclerViewExpandableItemManager.expandGroup(groupPosition);
                mDetailExpandedSet.add(groupPosition);
            }
        } else if (v.getId() == R.id.restaurant_detail_message_expand) {
            int groupPosition = (int) v.getTag();
            mWrappedAdapter.expandDetailMessage();
            mRecyclerViewExpandableItemManager.notifyGroupItemChanged(groupPosition);
        } else if (v == mSlidingTouch) {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else if (v == mNameBG) {
            showDetail();
            mRecyclerView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
            mRecyclerView.dispatchNestedPreScroll(0, 1, null, null);
            mRecyclerView.dispatchNestedPreFling(0, mRecyclerView.getTop() * 4);
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            mRecyclerView.stopNestedScroll();
        } else if (v == mCartButton) {
            if (mCart != null && mCart.getCartMenus().size() > 0) {
                CartActivity.createInstance(this);
            } else {
                new MaterialDialog.Builder(this).content("장바구니가 비었습니다.").positiveText("확인").show();
            }
        } else if (v == mOrderButton) {
            order();
        }
    }

    private void order() {
        // 로그인 안한상태
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            new MaterialDialog.Builder(this).content("로그인 후 이용해주세요.").positiveText("로그인").negativeText("취소").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    LoginActivity.createInstance(RestaurantActivity.this);
                }
            }).show();
            return;
        }

        // 주소 설정 안한 상태
        if (user.getUser().getAddress() == null) {
            new MaterialDialog.Builder(this).content("주소를 설정하신 후 이용해주세요.").positiveText("주소설정").negativeText("취소").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    AddressActivity.createInstance(RestaurantActivity.this);
                }
            }).show();
            return;
        }

        Realm connectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        Connect connect = connectRealm.where(Connect.class).findFirst();
        if (connect != null) {
            connect = connectRealm.copyFromRealm(connect);
        }
        connectRealm.close();
        Area area = connect.getArea();

        // 메뉴 없음
        if (mCart == null || mCart.getCartMenus().size() == 0) {
            new MaterialDialog.Builder(this).content("주문할 메뉴가 없습니다.").positiveText("확인").show();
            return;
        }

        if (!area.isServiceArea() && mCart.getRestaurant().isAreaOpen()) {
            mCartRealm.beginTransaction();
            mCart.getRestaurant().setAreaOpen(false);
            mCartRealm.commitTransaction();
        }

        // 배달 안됨
        if (!mCart.getRestaurant().isAvailable() && mCart.getRestaurant().getDeliveryStatus() != 1) {
            String message = mCart.getRestaurant().getAvailableMessage();
            if (area.isServiceArea() && !mCart.getRestaurant().isAreaOpen()) {
                message += String.format(Locale.getDefault(), "\n서비스 운영시간은 %s:%s~%s:%s 입니다.", area.getStart().split(":")[0], area.getStart().split(":")[1], area.getEnd().split(":")[0], area.getEnd().split(":")[1]);
            }
            new MaterialDialog.Builder(this).content(message).positiveText("확인").show();
            return;
        }

        // 다른 음식점
        if (!TextUtils.equals(mCart.getId(), mRestaurantId)) {
            new MaterialDialog.Builder(this).content(String.format(Locale.getDefault(), "'%s' 메뉴가 이미 장바구니에 담겨있습니다. 장바구니로 이동하시겠습니까?", mCart.getRestaurant().getName())).positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    CartActivity.createInstance(RestaurantActivity.this);
                }
            }).show();
        }

        // 메뉴 적음
        if (mCart.getTotal() < mCart.getRestaurant().getMinimumOrderAmount()) {
            new MaterialDialog.Builder(this).content("최소 주문금액보다 적어 주문을 하실 수 없습니다.").positiveText("확인").show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (mCart.getRestaurant().getDeliveryStatus() == 1) {
            sb.append(String.format(Locale.getDefault(), "실시간 배송현황에 따른 현재 최대배송거리는 %.02fkm 입니다.\n일시적으로 테이크아웃 주문만 가능합니다.\n주문 하시겠습니까?", mCart.getRestaurant().getDeliveryAvailableDistance()));
        } else {
            boolean messageAdded = false;
            if (!TextUtils.isEmpty(area.getDeliveryTimeMessage())) {
                sb.append(area.getDeliveryTimeMessage());
                messageAdded = true;
            }
            if (area.isUseDeliveryTime()) {
                if (area.getDeliveryTime() == 0) {
                    if (messageAdded) {
                        sb.append("\n");
                    }
                    sb.append("주문 하시겠습니까?");
                } else {
                    if (messageAdded) {
                        sb.append(" ");
                    }
                    sb.append(String.format(Locale.getDefault(), "지금 바로 주문 시 최대 %d분 소요될 수 있습니다\n주문 하시겠습니까?", area.getDeliveryTime()));
                }
            } else {
                if (messageAdded) {
                    sb.append("\n");
                }
                sb.append("주문 하시겠습니까?");
            }
        }

        new MaterialDialog.Builder(this).content(sb.toString()).positiveText("주문").negativeText("취소").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                OrderActivity.createInstance(RestaurantActivity.this);
            }
        }).show();
    }

    private void showMenuList() {
        if (mRestaurant == null) {
            return;
        }
        if (mWrappedAdapter.getMode() == RestaurantAdapter.MODE_DETAIL) {
            mWrappedAdapter.setMode(RestaurantAdapter.MODE_MENU);
            mRecyclerViewExpandableItemManager.notifyGroupItemChanged(0);
            mRecyclerViewExpandableItemManager.notifyGroupItemRangeRemoved(1, 3 + (TextUtils.isEmpty(mRestaurant.getInfo().getOriginalInfo()) ? 0 : 1) + (TextUtils.isEmpty(mRestaurant.getInfo().getNaverURL()) ? 0 : 1));
            mRecyclerViewExpandableItemManager.notifyGroupItemRangeInserted(1, mRestaurant.getCategoryList().size());
            for (int groupPosition : mMenuExpandedSet) {
                mRecyclerViewExpandableItemManager.expandGroup(groupPosition);
            }
        }
    }

    private void showDetail() {
        if (mRestaurant == null) {
            return;
        }
        if (mWrappedAdapter.getMode() == RestaurantAdapter.MODE_MENU) {
            mWrappedAdapter.setMode(RestaurantAdapter.MODE_DETAIL);
            mRecyclerViewExpandableItemManager.notifyGroupItemChanged(0);
            mRecyclerViewExpandableItemManager.notifyGroupItemRangeRemoved(1, mRestaurant.getCategoryList().size());
            mRecyclerViewExpandableItemManager.notifyGroupItemRangeInserted(1, 3 + (TextUtils.isEmpty(mRestaurant.getInfo().getOriginalInfo()) ? 0 : 1) + (TextUtils.isEmpty(mRestaurant.getInfo().getNaverURL()) ? 0 : 1));
            mRecyclerViewExpandableItemManager.expandGroup(2);
            mRecyclerViewExpandableItemManager.expandGroup(3);
            for (int groupPosition : mDetailExpandedSet) {
                mRecyclerViewExpandableItemManager.expandGroup(groupPosition);
            }
        }
    }

    private void showMenu(Menu menu) {
        if (menu.isSoldOut()) {
            new MaterialDialog.Builder(this).content("메뉴가 매진되었습니다.").positiveText("확인").show();
        } else {
            String url = APIs.getRestaurantPath().appendPath(mRestaurantId).appendPath(APIs.RESTAURANT_MENU).appendPath(menu.getId()).toString();
            GsonRequest<Menu> request = new GsonRequest<>(Method.GET, url, Menu.class, APIs.createHeadersWithToken(), new Listener<Menu>() {
                @Override
                public void onResponse(final Menu menu) {
                    if (TextUtils.isEmpty(menu.getImage())) {
                        dismissProgressDialog();
                        showMenu(menu, 0);
                    } else {
                        VolleySingleton.getInstance(RestaurantActivity.this).getImageLoader().get(menu.getImage(), new ImageListener() {
                            @Override
                            public void onResponse(ImageContainer response, boolean isImmediate) {
                                if (!isImmediate || response.getBitmap() != null) {
                                    dismissProgressDialog();
                                    showMenu(menu, (int) ((float) response.getBitmap().getHeight() / ((float) response.getBitmap().getWidth() / (float) CommonUtils.getScreenWidth())));
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }, CommonUtils.getScreenWidth(), CommonUtils.getScreenHeight(), ScaleType.CENTER_CROP);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(RestaurantActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    private void showMenu(Menu menu, int imageHeight) {
        mSlidingUpPanelLayout.setVisibility(View.VISIBLE);
        mMenuView.setMenu(menu, imageHeight, this);
        mMenuView.getContentLayout().post(new Runnable() {
            @Override
            public void run() {
                mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
            }
        });
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
        if (newState == PanelState.COLLAPSED) {
            mSlidingUpPanelLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCancel() {
        mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
    }

    @Override
    public void onAddToCart(final CartMenu cartMenu) {
        Realm connectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        Connect connect = connectRealm.where(Connect.class).findFirst();
        if (connect != null) {
            connect = connectRealm.copyFromRealm(connect);
        }
        connectRealm.close();
        Area area = connect.getArea();

        if (!area.isServiceArea() && mRestaurant.isAreaOpen()) {
            mCartRealm.beginTransaction();
            mRestaurant.setAreaOpen(false);
            mCartRealm.commitTransaction();
        }

        // 배달 안됨
        if (!mRestaurant.isAvailable() && mRestaurant.getDeliveryStatus() != 1) {
            String message = mRestaurant.getAvailableMessage();
            if (area.isServiceArea() && !mRestaurant.isAreaOpen()) {
                message += String.format(Locale.getDefault(), "\n서비스 운영시간은 %s:%s~%s:%s 입니다.", area.getStart().split(":")[0], area.getStart().split(":")[1], area.getEnd().split(":")[0], area.getEnd().split(":")[1]);
            }
            new MaterialDialog.Builder(this).content(message).positiveText("확인").show();
            return;
        }

        if (mCart == null) {
            mCart = new Cart(mRestaurant);
            mCart.getCartMenus().add(cartMenu);
            mCartRealm.beginTransaction();
            mCartRealm.deleteAll();
            mCartRealm.copyToRealmOrUpdate(mCart);
            mCartRealm.commitTransaction();
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            if (TextUtils.equals(mCart.getId(), mRestaurantId)) {
                CartMenu sameMenu = mCart.getSameMenu(cartMenu);
                if (sameMenu != null) {
                    sameMenu.setQuantity(sameMenu.getQuantity() + cartMenu.getQuantity());
                } else {
                    mCart.getCartMenus().add(cartMenu);
                }
                mCartRealm.beginTransaction();
                mCartRealm.deleteAll();
                mCartRealm.copyToRealmOrUpdate(mCart);
                mCartRealm.commitTransaction();
                mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
            } else {
                new MaterialDialog.Builder(this).content(String.format(Locale.getDefault(), "장바구니에 '%s' 매장의 메뉴가 담겨있습니다.\n새로운 메뉴로 변경하시겠습니까?", mCart.getRestaurant().getName())).positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mCart = new Cart(mRestaurant);
                        mCart.getCartMenus().add(cartMenu);
                        mCartRealm.beginTransaction();
                        mCartRealm.deleteAll();
                        mCartRealm.copyToRealmOrUpdate(mCart);
                        mCartRealm.commitTransaction();
                        mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                    }
                }).show();
            }
        }
    }

    private void updateCartPrice() {
        int price = 0;
        if (mCart != null && TextUtils.equals(mCart.getId(), mRestaurantId)) {
            price = mCart.getTotal();
        }
        mCartPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(price)));
    }

    private RealmChangeListener mCartChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            mCart = mCartRealm.where(Cart.class).findFirst();
            if (mCart != null) {
                mCart = mCartRealm.copyFromRealm(mCart);
            }
            updateCartPrice();
            invalidateOptionsMenu();
        }
    };

    public static class RestaurantImageFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle args = getArguments();
            final String url = args.getString("url");
            NetworkImageView imageView = new NetworkImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageView.setImageUrl(url, VolleySingleton.getInstance(getContext()).getImageLoader());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewerActivity.createInstance(v.getContext(), url);
                }
            });
            return imageView;
        }
    }

    private class RestaurantImageAdapter extends FragmentPagerAdapter {

        public RestaurantImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString("url", mRestaurant.getImages().get(position).getValue());
            return RestaurantImageFragment.instantiate(RestaurantActivity.this, RestaurantImageFragment.class.getName(), args);
        }

        @Override
        public int getCount() {
            return mRestaurant == null ? 0 : mRestaurant.getImages().size();
        }
    }
}