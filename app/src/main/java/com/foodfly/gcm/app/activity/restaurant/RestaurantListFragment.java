package com.foodfly.gcm.app.activity.restaurant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.main.MainActivity;
import com.foodfly.gcm.app.activity.main.SearchActivity;
import com.foodfly.gcm.app.dialog.RestaurantFilter;
import com.foodfly.gcm.app.view.recyclerView.GridSpacingItemDecoration;
import com.foodfly.gcm.app.view.recyclerView.LazyAdapter;
import com.foodfly.gcm.app.view.recyclerView.RecyclerViewEmptySupport;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.PreferenceUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.model.restaurant.Restaurant;
import com.foodfly.gcm.model.restaurant.Theme;
import com.foodfly.gcm.model.user.MapAddress;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by woozam on 2016-06-23.
 */
public class RestaurantListFragment extends Fragment implements OnRefreshListener, OnClickListener {

    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_COLUMN_COUNT = "extra_column_count";
    public static final String EXTRA_THEME = "extra_theme";
    public static final String EXTRA_ROW_LAYOUT_RES_ID = "extra_row_layout_res_id";
    public static final String EXTRA_SPACING = "extra_spacing";
    public static final String EXTRA_FAVORITE = "extra_favorite";
    public static final String EXTRA_USE_BANNER = "extra_banner";
    public static final String EXTRA_USE_SEARCH = "extra_use_search";
    public static final String EXTRA_QUERY = "extra_query";
    public static final String EXTRA_USE_COMMON_FOOTER = "extra_use_common_footer";

    private static final int LIMIT = 30;

    private SwipeRefreshLayout mRoot;
    private ArrayList<Restaurant> mRestaurantList;
    private RestaurantListAdapter mAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private View mEmptyView;
    private View mEmptySearchView;
    private View mEmptySearchButton;
    private String mCategory;
    private boolean mLoadCompleted = false;
    private boolean mRequesting = false;
    private MapAddress mAddress;
    private Object mLoadTag = new Object();
    private boolean mUseSearch = false;
    private String mSearchQuery;

    private Realm mUserRealm;
    private Realm mAddressRealm;
    private Realm mConnectRealm;
    private int mRowLayoutResId = R.layout.row_restaurant_list;
    private int mColumnCount = 2;
    private Theme mTheme;
    private int mSpacing = 8;
    private boolean mFavorite;
    private boolean mUseBanner = false;
    private boolean mUseCommonFooter = false;

    private String mOrder;
    private String mCoupon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(RestaurantFilter.ACTION_RESTAURANT_FILTER_CHANGE);
        intentFilter.addAction(SearchActivity.ACTION_SEARCH);
        getContext().registerReceiver(mRestaurantFilterChangeReceiver, intentFilter);
        mUserRealm = Realm.getInstance(RealmUtils.CONFIG_USER);
        mUserRealm.addChangeListener(mUserChangeListener);
        mAddressRealm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
        mAddressRealm.addChangeListener(mAddressChangeListener);
        mConnectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        mConnectRealm.addChangeListener(mConnectChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mRestaurantFilterChangeReceiver);
        mUserRealm.removeChangeListener(mUserChangeListener);
        mUserRealm.close();
        mAddressRealm.removeChangeListener(mAddressChangeListener);
        mAddressRealm.close();
        mConnectRealm.removeChangeListener(mConnectChangeListener);
        mConnectRealm.close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            Bundle args = getArguments();
            if (args != null) {
                mCategory = args.getString(EXTRA_CATEGORY, "0");
                mColumnCount = args.getInt(EXTRA_COLUMN_COUNT, 2);
                mTheme = (Theme) args.getSerializable(EXTRA_THEME);
                mRowLayoutResId = args.getInt(EXTRA_ROW_LAYOUT_RES_ID, R.layout.row_restaurant_list);
                mSpacing = args.getInt(EXTRA_SPACING, 8);
                mFavorite = args.getBoolean(EXTRA_FAVORITE, false);
                mSearchQuery = args.getString(EXTRA_QUERY);
                mUseBanner = args.getBoolean(EXTRA_USE_BANNER, false);
                mUseSearch = args.getBoolean(EXTRA_USE_SEARCH, false);
                mUseCommonFooter = args.getBoolean(EXTRA_USE_COMMON_FOOTER, false);
            }
            String detailImageUrl = mTheme != null && !TextUtils.isEmpty(mTheme.getDetailImage()) ? mTheme.getDetailImage() : null;
            mRoot = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_restaurant_list, container, false);
            mRoot.setOnRefreshListener(this);
            mRestaurantList = new ArrayList<>();
            Connect connect = mConnectRealm.where(Connect.class).findFirst();
            mAdapter = new RestaurantListAdapter(mRestaurantList, (int) ((CommonUtils.getScreenWidth() - CommonUtils.convertDipToPx(getContext(), mSpacing * (mColumnCount + 1))) / mColumnCount * 330f / 272f), mRowLayoutResId, detailImageUrl, mFavorite, this, (mUseBanner && connect != null) ? connect.getBanners() : null, mUseCommonFooter);
            mEmptyView = mRoot.findViewById(R.id.restaurant_list_empty_view);
            mEmptySearchView = mRoot.findViewById(R.id.restaurant_list_empty_search_view);
            mEmptySearchView.setVisibility(mUseSearch ? View.VISIBLE : View.GONE);
            mEmptySearchButton = mRoot.findViewById(R.id.restaurant_list_empty_search_button);
            mEmptySearchButton.setOnClickListener(this);
            mRecyclerView = (RecyclerViewEmptySupport) mRoot.findViewById(R.id.restaurant_list_recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (mAdapter.getItemViewType(position)) {
                        case RestaurantListAdapter.BANNER_ITEM_VIEW_TYPE:
                        case RestaurantListAdapter.DETAIL_IMAGE_ITEM_VIEW_TYPE:
                        case LazyAdapter.LAZY_LOAD_ITEM_VIEW_TYPE:
                        case RestaurantListAdapter.COMMON_FOOTER_ITEM_VIEW_TYPE:
                            return mColumnCount;
                        default:
                            return 1;
                    }
                }
            });
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(mColumnCount, CommonUtils.convertDipToPx(getContext(), mSpacing), true) {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    if (parent.getChildViewHolder(view).getItemViewType() == RestaurantListAdapter.DETAIL_IMAGE_ITEM_VIEW_TYPE) {
                        outRect.bottom = spacing;
                        return;
                    }
                    if (parent.getChildViewHolder(view).getItemViewType() == RestaurantListAdapter.BANNER_ITEM_VIEW_TYPE) {
                        outRect.bottom = spacing;
                        return;
                    }
                    if (parent.getChildViewHolder(view).getItemViewType() == RestaurantListAdapter.COMMON_FOOTER_ITEM_VIEW_TYPE) {
                        return;
                    }
                    super.getItemOffsets(outRect, view, parent, state);
                }

                @Override
                protected int getPositionOffset() {
                    int positionOffset = 0;
                    positionOffset += ((mAdapter.getBannerList() != null && mAdapter.getBannerList().size() > 0) ? mColumnCount - 1 : 0);
                    positionOffset += TextUtils.isEmpty(mAdapter.getDetailImageUrl()) ? 0 : mColumnCount - 1;
                    return positionOffset;
                }
            });
            mRecyclerView.addOnScrollListener(mOnScrollListener);
            mAddress = MapAddress.getAddress();

            mOrder = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, connect == null ? "0" : connect.getDefaults().getOrders().getDefaultOrderValue(mAddress.getAreaCode()));
            mCoupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, connect == null ? "0" : connect.getDefaults().getCoupons().getDefaultCouponValue());

            getRestaurantList(0);
        } else {
            onRefresh();
            mRoot.setRefreshing(true);
        }
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private RealmChangeListener mUserChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            MapAddress preAddress = mAddress;
            mAddress = MapAddress.getAddress();
            if ((preAddress != null && mAddress == null) || (preAddress == null && mAddress != null) || (preAddress != null && mAddress != null && (preAddress.getLat() != mAddress.getLat() || preAddress.getLon() != mAddress.getLon()))) {
                Connect connect = mConnectRealm.where(Connect.class).findFirst();
                mOrder = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, connect == null ? "0" : connect.getDefaults().getOrders().getDefaultOrderValue(mAddress.getAreaCode()));
                mCoupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, connect == null ? "0" : connect.getDefaults().getCoupons().getDefaultCouponValue());
                if (isAdded() && mTheme == null && !mFavorite) {
                    onRefresh();
                    mRoot.setRefreshing(true);
                }
            }
        }
    };

    private RealmChangeListener mAddressChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            MapAddress preAddress = mAddress;
            mAddress = MapAddress.getAddress();
            if ((preAddress != null && mAddress == null) || (preAddress == null && mAddress != null) || (preAddress != null && mAddress != null && (preAddress.getLat() != mAddress.getLat() || preAddress.getLon() != mAddress.getLon()))) {
                Connect connect = mConnectRealm.where(Connect.class).findFirst();
                mOrder = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, connect == null ? "0" : connect.getDefaults().getOrders().getDefaultOrderValue(mAddress.getAreaCode()));
                mCoupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, connect == null ? "0" : connect.getDefaults().getCoupons().getDefaultCouponValue());
                if (isAdded() && mTheme == null && !mFavorite) {
                    onRefresh();
                    mRoot.setRefreshing(true);
                }
            }
        }
    };

    private RealmChangeListener mConnectChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Connect connect = mConnectRealm.where(Connect.class).findFirst();
            mAdapter.setBannerList((mUseBanner && connect != null) ? connect.getBanners() : null);
            if (connect != null && !connect.getArea().enableTimeFoodflyServiceTime()) {
                for (Restaurant restaurant : mRestaurantList) {
                    restaurant.setAreaOpen(false);
                    restaurant.setServiceArea(connect.getArea().isServiceArea());
                    restaurant.setConnectAreaOpenHour(String.format(Locale.getDefault(), "%s~%s", connect.getArea().getStart().substring(0, 5), connect.getArea().getEnd().substring(0, 5)));
                }
            }
            mAdapter.notifyDataSetChanged();
            String order = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, connect == null ? "0" : connect.getDefaults().getOrders().getDefaultOrderValue(mAddress.getAreaCode()));
            String coupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, connect == null ? "0" : connect.getDefaults().getCoupons().getDefaultCouponValue());
            if (!TextUtils.equals(order, mOrder) || !TextUtils.equals(coupon, mCoupon)) {
                mOrder = order;
                mCoupon = coupon;
                if (isAdded() && mTheme == null && !mFavorite) {
                    onRefresh();
                    mRoot.setRefreshing(true);
                }
            }
        }
    };

    private BroadcastReceiver mRestaurantFilterChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestaurantFilter.ACTION_RESTAURANT_FILTER_CHANGE)) {
                mOrder = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, "0");
                mCoupon = PreferenceUtils.getStringValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, "0");
                if (isAdded() && mTheme == null && !mFavorite) {
                    onRefresh();
                    mRoot.setRefreshing(true);
                }
            } else if (mUseSearch && intent.getAction().equals(SearchActivity.ACTION_SEARCH)) {
                mSearchQuery = intent.getStringExtra(EXTRA_QUERY);
                if (isAdded() && mTheme == null && !mFavorite) {
                    if (TextUtils.isEmpty(mSearchQuery)) {
                        mRestaurantList.clear();
                        mRecyclerView.setEmptyView(null);
                        mEmptyView.setVisibility(View.GONE);
                        mRoot.setRefreshing(false);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        onRefresh();
                        mRoot.setRefreshing(true);
                    }
                }
            }
        }
    };

    private void getRestaurantList(int offset) {
        if (mUseSearch && TextUtils.isEmpty(mSearchQuery)) {
            return;
        }
        if (!mRequesting) {
            mRequesting = true;
            double lat = mAddress.getLat();
            double lon = mAddress.getLon();
            long areaCode = mAddress.getAreaCode();
            Uri.Builder builder = APIs.getRestaurantPath().appendQueryParameter(APIs.PARAM_OFFSET, String.valueOf(offset)).appendQueryParameter(APIs.PARAM_LIMIT, String.valueOf(LIMIT)).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(lat)).appendQueryParameter(APIs.PARAM_LON, String.valueOf(lon)).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(areaCode));
            if (mTheme != null) {
                builder.appendQueryParameter(APIs.PARAM_THEME, mTheme.getId());
            } else if (mFavorite) {
                UserResponse user = UserManager.fetchUser();
                if (user == null) {
                    return;
                } else {
                    builder.appendQueryParameter(APIs.PARAM_FAVORITE_OF, user.getId());
                }
            } else {
                builder.appendQueryParameter(APIs.PARAM_CATEGORY, mCategory).appendQueryParameter(APIs.PARAM_ORDER_BY, mOrder).appendQueryParameter(APIs.PARAM_COUPON, mCoupon);
                if (!TextUtils.isEmpty(mSearchQuery)) {
                    builder.appendQueryParameter(APIs.PARAM_KEYWORD, mSearchQuery);
                }
            }
            String url = builder.toString();
            GsonRequest<Restaurant[]> request = (GsonRequest<Restaurant[]>) new GsonRequest<>(Method.GET, url, new TypeToken<Restaurant[]>() {
            }.getType(), APIs.createHeadersWithToken(), new Listener<Restaurant[]>() {
                @Override
                public void onResponse(Restaurant[] response) {
                    mAdapter.onLazyLoadComplete();
                    mRecyclerView.setEmptyView(mEmptyView);
                    if (mRoot.isRefreshing()) {
                        mRestaurantList.clear();
                        mRoot.setRefreshing(false);
                    }
                    int start = mRestaurantList.size() + mAdapter.getHeaderCount();
                    if (mFavorite) {
                        for (Restaurant restaurant : response) {
                            restaurant.setFavorite(true);
                        }
                    }
                    Connect connect = mConnectRealm.where(Connect.class).findFirst();
                    if (connect != null && !connect.getArea().enableTimeFoodflyServiceTime()) {
                        for (Restaurant restaurant : response) {
                            restaurant.setAreaOpen(false);
                            restaurant.setServiceArea(connect.getArea().isServiceArea());
                            restaurant.setConnectAreaOpenHour(String.format(Locale.getDefault(), "%s~%s", connect.getArea().getStart().substring(0, 5), connect.getArea().getEnd().substring(0, 5)));
                        }
                    }
                    Collections.addAll(mRestaurantList, response);
                    if (start == 0) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemRangeInserted(start, response.length);
                    }
                    mRequesting = false;
                    if (response.length < LIMIT) {
                        mLoadCompleted = true;
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mAdapter.onLazyLoadComplete();
                    if (mRoot.isRefreshing()) {
                        mRoot.setRefreshing(false);
                    }
                    mRecyclerView.setEmptyView(mEmptyView);
                    mRequesting = false;
                }
            }).setTag(mLoadTag);
            VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
            if (!mRoot.isRefreshing()) {
                mAdapter.onLazyLoadStart();
            }
        }
    }

    @Override
    public void onRefresh() {
        VolleySingleton.getInstance(getContext()).getRequestQueue().cancelAll(mLoadTag);
        mRequesting = false;
        mLoadCompleted = false;
        getRestaurantList(0);
    }

    private void checkAndLoad() {
        if (mRecyclerView == null || mLoadCompleted) {
            return;
        }
        if (mRestaurantList.size() > 0) {
            int firstVisibleItemPosition, visibleItemCount, totalItemCount;
            visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
            totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
            firstVisibleItemPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 10) {
                getRestaurantList(mRestaurantList.size());
            }
        }
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                checkAndLoad();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.restaurant_favorite) {
            toggleFavorite((Integer) v.getTag());
        } else if (v.getId() == R.id.restaurant_list_empty_search_button) {
            MainActivity.createInstance(getContext(), "0");
            getActivity().finish();
        }
    }

    private void toggleFavorite(final int position) {
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }
        final Restaurant restaurant = mAdapter.getItem(position);
        Uri.Builder builder = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_FAVORITE);
        String url;
        GsonRequest<BaseResponse> request;
        if (restaurant.isFavorite()) {
            builder.appendPath(restaurant.getId());
            url = builder.toString();
            request = new GsonRequest<>(Method.DELETE, url, BaseResponse.class, APIs.createHeadersWithToken(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    if (isAdded()) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        restaurant.setFavorite(false);
                        mAdapter.notifyItemChanged(position);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isAdded()) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                        if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                            new MaterialDialog.Builder(getContext()).content(response.getError().message).positiveText("확인").show();
                        }
                    }
                }
            }, RealmUtils.REALM_GSON);
        } else {
            JsonObject json = new JsonObject();
            json.addProperty("restaurant_id", restaurant.getId());
            url = builder.toString();
            request = new GsonRequest<>(Method.POST, url, BaseResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    if (isAdded()) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        restaurant.setFavorite(true);
                        mAdapter.notifyItemChanged(position);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isAdded()) {
                        ((BaseActivity) getActivity()).dismissProgressDialog();
                        BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                        if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                            new MaterialDialog.Builder(getContext()).content(response.getError().message).positiveText("확인").show();
                        }
                    }
                }
            }, RealmUtils.REALM_GSON);
        }
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
        ((BaseActivity) getActivity()).showProgressDialog();
    }
}