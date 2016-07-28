package kr.co.foodfly.androidapp.app.activity.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonObject;

import java.util.Locale;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.order.Order;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderDetailActivity extends BaseActivity implements OnRefreshListener {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DISPLAY_RESTAURANT = "extra_title";

    private static Order mParamOrder;

    public static void createInstance(Context context, String id) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        context.startActivity(intent);
    }

    public static void createInstance(Context context, Order order, String title, boolean displayRestaurant) {
        mParamOrder = order;
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_DISPLAY_RESTAURANT, displayRestaurant);
        context.startActivity(intent);
    }

    private String mOrderId;
    private Order mOrder;
    private boolean mDisplayRestaurant;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OrderDetailAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mOrderId = intent.getStringExtra(EXTRA_ID);
        if (TextUtils.isEmpty(mOrderId)) {
            mOrder = mParamOrder;
            mParamOrder = null;
            if (mOrder == null) {
                finish();
                return;
            }
            mOrderId = mOrder.getId();
        }
        String title = intent.getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(title);
        }
        mDisplayRestaurant = intent.getBooleanExtra(EXTRA_DISPLAY_RESTAURANT, true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_detail_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new OrderDetailAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.order_detail_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        if (mOrder == null) {
            showProgressDialog();
            loadOrder();
        }
    }

    @Override
    public void onRefresh() {
        loadOrder();
    }

    private void loadOrder() {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ORDER).appendPath(mOrderId).toString();
            GsonRequest<Order> request = new GsonRequest<>(Method.GET, url, Order.class, APIs.createHeadersWithToken(), new Listener<Order>() {
                @Override
                public void onResponse(Order response) {
                    dismissProgressDialog();
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mOrder = response;
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(OrderDetailActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }
    }

    private void toggleFavorite() {
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }
        final Restaurant restaurant = mOrder.getRestaurant();
        Uri.Builder builder = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_FAVORITE);
        String url;
        GsonRequest<BaseResponse> request;
        if (restaurant.isFavorite()) {
            builder.appendPath(restaurant.getId());
            url = builder.toString();
            request = new GsonRequest<>(Method.DELETE, url, BaseResponse.class, APIs.createHeadersWithToken(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    dismissProgressDialog();
                    restaurant.setFavorite(false);
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(OrderDetailActivity.this).content(response.getError().message).positiveText("확인").show();
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
                    dismissProgressDialog();
                    restaurant.setFavorite(true);
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(OrderDetailActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
        }
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private class OrderDetailAdapter extends Adapter<OrderDetailViewHolder> {

        public static final int VIEW_ITEM_TYPE_RESTAURANT = 0;
        public static final int VIEW_ITEM_TYPE_DELIVERY_STATUS = 1;
        public static final int VIEW_ITEM_TYPE_DELIVERY_INFO = 2;
        public static final int VIEW_ITEM_TYPE_MENU_TITLE = 3;
        public static final int VIEW_ITEM_TYPE_MENU_ITEM = 4;
        public static final int VIEW_ITEM_TYPE_PAYMENT = 5;

        @Override
        public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM_TYPE_RESTAURANT) {
                return new OrderDetailRestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_restaurant, parent, false));
            } else if (viewType == VIEW_ITEM_TYPE_DELIVERY_STATUS) {
                return new OrderDetailDeliveryStatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_delivery_status, parent, false));
            } else {
                return new OrderDetailDeliveryStatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_delivery_status, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
            if (holder.getItemViewType() == VIEW_ITEM_TYPE_RESTAURANT) {
                holder.setItem(mOrder);
                ((OrderDetailRestaurantViewHolder) holder).mFavorite.getDrawable().setColorFilter(ResourcesCompat.getColor(((OrderDetailRestaurantViewHolder) holder).itemView.getResources(), R.color.colorPrimary, null), Mode.SRC_ATOP);
                ((OrderDetailRestaurantViewHolder) holder).mFavorite.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleFavorite();
                    }
                });
            } else if (holder.getItemViewType() == VIEW_ITEM_TYPE_DELIVERY_STATUS) {
                holder.setItem(mOrder);
                ((OrderDetailDeliveryStatusViewHolder) holder).mRefresh.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        loadOrder();
                    }
                });
            } else {
                holder.setItem(mOrder);
                ((OrderDetailDeliveryStatusViewHolder) holder).mRefresh.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        loadOrder();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mDisplayRestaurant) {
                if (position < 4) {
                    return position;
                } else if (position < 4 + mOrder.getMenus().size()) {
                    return VIEW_ITEM_TYPE_MENU_ITEM;
                } else {
                    return VIEW_ITEM_TYPE_PAYMENT;
                }
            } else {
                if (position < 3) {
                    return position + 1;
                } else if (position < 3 + mOrder.getMenus().size()) {
                    return VIEW_ITEM_TYPE_MENU_ITEM;
                } else {
                    return VIEW_ITEM_TYPE_PAYMENT;
                }
            }
        }

        @Override
        public int getItemCount() {
            return mOrder == null ? 0 : (mDisplayRestaurant ? (5 + mOrder.getMenus().size()) : (4 + mOrder.getMenus().size()));
        }
    }

    public static abstract class OrderDetailViewHolder extends ViewHolder {

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setItem(Object object);
    }

    public static class OrderDetailRestaurantViewHolder extends OrderDetailViewHolder {

        private NetworkImageView mImage;
        private TextView mName;
        private ImageView mFavorite;
        private TextView mMenu;
        private TextView mPrice;

        public OrderDetailRestaurantViewHolder(View itemView) {
            super(itemView);
            mImage = (NetworkImageView) itemView.findViewById(R.id.order_detail_restaurant_image);
            mName = (TextView) itemView.findViewById(R.id.order_detail_restaurant_name);
            mFavorite = (ImageView) itemView.findViewById(R.id.order_detail_restaurant_favorite);
            mMenu = (TextView) itemView.findViewById(R.id.order_detail_restaurant_menu);
            mPrice = (TextView) itemView.findViewById(R.id.order_detail_restaurant_price);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                Order order = (Order) object;
                mImage.setImageUrl(order.getRestaurant().getThumbnail(), VolleySingleton.getInstance(mImage.getContext()).getImageLoader());
                mName.setText(order.getRestaurant().getName());
                mFavorite.setSelected(order.getRestaurant().isFavorite());
                mMenu.setText(order.getMenuString());
                mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getTotalAmountDue())));
            }
        }
    }

    public static class OrderDetailDeliveryStatusViewHolder extends OrderDetailViewHolder {

        private View mIcon1;
        private View mIcon2;
        private View mIcon3;
        private View mIcon4;
        private View mDot1;
        private View mDot2;
        private View mDot3;
        private View mDot4;
        private View mRefresh;

        public OrderDetailDeliveryStatusViewHolder(View itemView) {
            super(itemView);
            mIcon1 = itemView.findViewById(R.id.order_detail_delivery_status_icon_1);
            mIcon2 = itemView.findViewById(R.id.order_detail_delivery_status_icon_2);
            mIcon3 = itemView.findViewById(R.id.order_detail_delivery_status_icon_3);
            mIcon4 = itemView.findViewById(R.id.order_detail_delivery_status_icon_4);
            mDot1 = itemView.findViewById(R.id.order_detail_delivery_status_dot_1);
            mDot2 = itemView.findViewById(R.id.order_detail_delivery_status_dot_2);
            mDot3 = itemView.findViewById(R.id.order_detail_delivery_status_dot_3);
            mDot4 = itemView.findViewById(R.id.order_detail_delivery_status_dot_4);
            mRefresh = itemView.findViewById(R.id.order_detail_delivery_status_refresh);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                Order order = (Order) object;
                mIcon1.setSelected(false);
                mIcon2.setSelected(false);
                mIcon3.setSelected(false);
                mIcon4.setSelected(false);
                mDot1.setSelected(false);
                mDot2.setSelected(false);
                mDot3.setSelected(false);
                mDot4.setSelected(false);
                mRefresh.setVisibility(View.VISIBLE);
                if (order.getDeliveryStatus() < 3) {
                    mIcon1.setSelected(true);
                    mDot1.setSelected(true);
                } else if (order.getDeliveryStatus() < 4) {
                    mIcon2.setSelected(true);
                    mDot2.setSelected(true);
                } else if (order.getDeliveryStatus() < 5) {
                    mIcon3.setSelected(true);
                    mDot3.setSelected(true);
                } else {
                    mIcon4.setSelected(true);
                    mDot4.setSelected(true);
                    mRefresh.setVisibility(View.GONE);
                }
            }
        }
    }
}