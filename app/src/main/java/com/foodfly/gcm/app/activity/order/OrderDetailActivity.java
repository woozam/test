package com.foodfly.gcm.app.activity.order;

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
import com.bumptech.glide.Glide;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.view.CircularImageView;
import com.foodfly.gcm.common.TimeUtils;
import com.foodfly.gcm.common.UnitUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.order.Order;
import com.foodfly.gcm.model.order.OrderMenu;
import com.foodfly.gcm.model.restaurant.Restaurant;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderDetailActivity extends BaseActivity implements OnRefreshListener {

    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DISPLAY_RESTAURANT = "extra_display_restaurant";

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
    private View mCanceled;

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
        mCanceled = findViewById(R.id.order_detail_canceled);
        mCanceled.setVisibility(View.GONE);

        if (mOrder == null) {
            showProgressDialog();
            loadOrder();
        } else {
            if (mOrder.getStatus() == 3) {
                mCanceled.setVisibility(View.VISIBLE);
            } else {
                mCanceled.setVisibility(View.GONE);
            }
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
                    if (response.getStatus() == 3) {
                        mCanceled.setVisibility(View.VISIBLE);
                    } else {
                        mCanceled.setVisibility(View.GONE);
                    }
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
            } else if (viewType == VIEW_ITEM_TYPE_DELIVERY_INFO) {
                return new OrderDetailDeliveryInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_delivery_info, parent, false));
            } else if (viewType == VIEW_ITEM_TYPE_MENU_TITLE) {
                return new OrderDetailMenuTitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_menu_title, parent, false));
            } else if (viewType == VIEW_ITEM_TYPE_PAYMENT) {
                return new OrderDetailPaymentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_payment, parent, false));
            } else if (viewType == VIEW_ITEM_TYPE_MENU_ITEM) {
                return new OrderDetailMenuItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_detail_menu_item, parent, false));
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
            } else if (holder.getItemViewType() == VIEW_ITEM_TYPE_DELIVERY_INFO) {
                holder.setItem(mOrder);
            } else if (holder.getItemViewType() == VIEW_ITEM_TYPE_PAYMENT) {
                holder.setItem(mOrder);
            } else if (holder.getItemViewType() == VIEW_ITEM_TYPE_MENU_ITEM) {
                holder.setItem(mOrder.getMenus().get(position - (mDisplayRestaurant ? 4 : 3)));
                ((OrderDetailMenuItemViewHolder) holder).mDivider.setVisibility(position < mOrder.getMenus().size() - 1 + (mDisplayRestaurant ? 4 : 3) ? View.VISIBLE : View.GONE);
            } else {
                holder.setItem(mOrder);
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

        private ImageView mImage;
        private TextView mName;
        private ImageView mFavorite;
        private TextView mDate;
        private TextView mMenu;
        private TextView mPrice;

        public OrderDetailRestaurantViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.order_detail_restaurant_image);
            mName = (TextView) itemView.findViewById(R.id.order_detail_restaurant_name);
            mFavorite = (ImageView) itemView.findViewById(R.id.order_detail_restaurant_favorite);
            mDate = (TextView) itemView.findViewById(R.id.order_detail_restaurant_date);
            mMenu = (TextView) itemView.findViewById(R.id.order_detail_restaurant_menu);
            mPrice = (TextView) itemView.findViewById(R.id.order_detail_restaurant_price);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                Order order = (Order) object;
                Glide.with(mImage.getContext()).load(order.getRestaurant().getThumbnail()).placeholder(R.drawable.placeholder).crossFade().into(mImage);
                mName.setText(order.getRestaurant().getName());
                mFavorite.setSelected(order.getRestaurant().isFavorite());
                mDate.setText(TimeUtils.getYearMonthDateTimeString(order.getOrderTime().getTime() + TimeZone.getDefault().getRawOffset()));
                mDate.setVisibility(order.getOrderTime().getTime() == 0 ? View.INVISIBLE : View.VISIBLE);
                mMenu.setText(order.getMenuString());
                mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getTotalAmountDue())));
            }
        }
    }

    public static class OrderDetailDeliveryStatusViewHolder extends OrderDetailViewHolder {

        private CircularImageView mIcon1;
        private CircularImageView mIcon2;
        private CircularImageView mIcon3;
        private CircularImageView mIcon4;
        private TextView mRiderName1;
        private TextView mRiderName2;
        private TextView mRiderName3;
        private TextView mRiderName4;
        private TextView mStatus1;
        private TextView mStatus2;
        private TextView mStatus3;
        private TextView mStatus4;
        private TextView mTime1;
        private TextView mTime2;
        private TextView mTime3;
        private TextView mTime4;
        private View mDot1;
        private View mDot2;
        private View mDot3;
        private View mDot4;
        private View mRefresh;

        public OrderDetailDeliveryStatusViewHolder(View itemView) {
            super(itemView);
            mIcon1 = (CircularImageView) itemView.findViewById(R.id.order_detail_delivery_status_icon_1);
            mIcon2 = (CircularImageView) itemView.findViewById(R.id.order_detail_delivery_status_icon_2);
            mIcon3 = (CircularImageView) itemView.findViewById(R.id.order_detail_delivery_status_icon_3);
            mIcon4 = (CircularImageView) itemView.findViewById(R.id.order_detail_delivery_status_icon_4);
            mRiderName1 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_rider_name_1);
            mRiderName2 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_rider_name_2);
            mRiderName3 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_rider_name_3);
            mRiderName4 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_rider_name_4);
            mStatus1 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_text_1);
            mStatus2 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_text_2);
            mStatus3 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_text_3);
            mStatus4 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_text_4);
            mTime1 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_time_1);
            mTime2 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_time_2);
            mTime3 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_time_3);
            mTime4 = (TextView) itemView.findViewById(R.id.order_detail_delivery_status_time_4);
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
                NetworkImageView icon;
                TextView name;
                mIcon1.setSelected(false);
                mIcon2.setSelected(false);
                mIcon3.setSelected(false);
                mIcon4.setSelected(false);
                mIcon1.setImageBitmap(null);
                mIcon2.setImageBitmap(null);
                mIcon3.setImageBitmap(null);
                mIcon4.setImageBitmap(null);
                mRiderName1.setText(null);
                mRiderName2.setText(null);
                mRiderName3.setText(null);
                mRiderName4.setText(null);
                mDot1.setSelected(false);
                mDot2.setSelected(false);
                mDot3.setSelected(false);
                mDot4.setSelected(false);
                mRefresh.setVisibility(View.VISIBLE);
                if (order.getDeliveryStatus() < 3) {
                    mIcon1.setSelected(true);
                    mDot1.setSelected(true);
                    icon = mIcon1;
                    name = mRiderName1;
                } else if (order.getDeliveryStatus() < 4) {
                    mIcon2.setSelected(true);
                    mDot2.setSelected(true);
                    icon = mIcon2;
                    name = mRiderName2;
                } else if (order.getDeliveryStatus() < 5) {
                    mIcon3.setSelected(true);
                    mDot3.setSelected(true);
                    icon = mIcon3;
                    name = mRiderName3;
                } else {
                    mIcon4.setSelected(true);
                    mDot4.setSelected(true);
                    icon = mIcon4;
                    name = mRiderName4;
                    mRefresh.setVisibility(View.GONE);
                }
                icon.setImageUrl(order.getRider().getPhoto(), VolleySingleton.getInstance(icon.getContext()).getImageLoader());
                name.setText(order.getRider().getName());
                mStatus1.setText(order.getTimestamps().get(0).getTag());
                mStatus2.setText(order.getTimestamps().get(1).getTag());
                mStatus3.setText(order.getTimestamps().get(2).getTag());
                mStatus4.setText(order.getTimestamps().get(3).getTag());
//                mTime1.setText(TimeUtils.getHourMinuteTimeString(order.getTimestamps().get(0).getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
                mTime2.setText(TimeUtils.getHourMinuteTimeString(order.getTimestamps().get(1).getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
                mTime3.setText(TimeUtils.getHourMinuteTimeString(order.getTimestamps().get(2).getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
                mTime4.setText(TimeUtils.getHourMinuteTimeString(order.getTimestamps().get(3).getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
                mTime1.setVisibility(View.INVISIBLE);
                mTime2.setVisibility(order.getTimestamps().get(1).getTimestamp().getTime() == 0 ? View.INVISIBLE : View.VISIBLE);
                mTime3.setVisibility(order.getTimestamps().get(2).getTimestamp().getTime() == 0 ? View.INVISIBLE : View.VISIBLE);
                mTime4.setVisibility(order.getTimestamps().get(3).getTimestamp().getTime() == 0 ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

    public static class OrderDetailDeliveryInfoViewHolder extends OrderDetailViewHolder {

        private TextView mRecipient;
        private TextView mPhone;
        private TextView mAddress;
        private TextView mAddressDetail;
        private TextView mDeliveryType;
        private TextView mDeliveryTime;

        public OrderDetailDeliveryInfoViewHolder(View itemView) {
            super(itemView);
            mRecipient = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_recipient_text);
            mPhone = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_phone_text);
            mAddress = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_address_text);
            mAddressDetail = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_address_detail_text);
            mDeliveryType = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_type_text);
            mDeliveryTime = (TextView) itemView.findViewById(R.id.order_detail_delivery_info_time_text);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                Order order = (Order) object;
                mRecipient.setText(order.getRecipientName());
                mPhone.setText(order.getRecipientPhone());
                mAddress.setText(order.getAddress().getFormattedAddress());
                mAddressDetail.setText(order.getAddress().getDetailAddress());
                mDeliveryType.setText(order.getDeliveryTypeString());
                if (order.getReservationTime() == null || order.getReservationTime().getTime() == 0) {
                    mDeliveryTime.setText("바로주문");
                } else {
                    mDeliveryTime.setText(TimeUtils.getFullTimeString(order.getReservationTime().getTime() + TimeZone.getDefault().getRawOffset()));
                }
            }
        }
    }

    public static class OrderDetailMenuTitleViewHolder extends OrderDetailViewHolder {

        public OrderDetailMenuTitleViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setItem(Object object) {
        }
    }

    public static class OrderDetailPaymentViewHolder extends OrderDetailViewHolder {

        private TextView mPrice;
        private TextView mDiscount;
        private TextView mTip;
        private TextView mType;
        private TextView mTotal;
        private TextView mTotal2;

        public OrderDetailPaymentViewHolder(View itemView) {
            super(itemView);
            mPrice = (TextView) itemView.findViewById(R.id.order_detail_payment_price_text);
            mDiscount = (TextView) itemView.findViewById(R.id.order_detail_payment_discount_text);
            mTip = (TextView) itemView.findViewById(R.id.order_detail_payment_tip_text);
            mType = (TextView) itemView.findViewById(R.id.order_detail_payment_type_text);
            mTotal = (TextView) itemView.findViewById(R.id.order_detail_payment_total_price_text);
            mTotal2 = (TextView) itemView.findViewById(R.id.order_detail_payment_total_price_text_2);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                Order order = (Order) object;
                mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getTotalMenu())));
                mDiscount.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getDiscount().getTotal())));
                mTip.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getDeliveryFee())));
                mType.setText(order.getPaymentString());
                mTotal.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getTotalAmountDue())));
                mTotal2.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(order.getCharge().getTotalAmountDue())));
            }
        }
    }

    public static class OrderDetailMenuItemViewHolder extends OrderDetailViewHolder {

        private TextView mName;
        private TextView mQuantity;
        private TextView mPrice;
        private View mOptionLayout;
        private TextView mOptionPrice;
        private TextView mOptionList;
        private TextView mTotalPrice;
        private View mDivider;

        public OrderDetailMenuItemViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.order_detail_menu_name);
            mQuantity = (TextView) itemView.findViewById(R.id.order_detail_menu_quantity);
            mPrice = (TextView) itemView.findViewById(R.id.order_detail_menu_price);
            mOptionLayout = itemView.findViewById(R.id.order_detail_menu_option_layout);
            mOptionPrice = (TextView) itemView.findViewById(R.id.order_detail_menu_option_price);
            mOptionList = (TextView) itemView.findViewById(R.id.order_detail_menu_option_list);
            mTotalPrice = (TextView) itemView.findViewById(R.id.order_detail_menu_total_price);
            mDivider = itemView.findViewById(R.id.order_detail_menu_divider);
        }

        @Override
        public void setItem(Object object) {
            if (object != null) {
                OrderMenu orderMenu = (OrderMenu) object;
                mName.setText(orderMenu.getName());
                mQuantity.setText(String.format(Locale.getDefault(), "%d개", orderMenu.getQuantity()));
                mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(orderMenu.getPrice())));
                mOptionPrice.setText(String.format(Locale.getDefault(), "+%s원", UnitUtils.priceFormat(orderMenu.getOptionPrice())));
                mOptionList.setText(orderMenu.getOptionString());
                mOptionLayout.setVisibility(mOptionList.length() == 0 ? View.GONE : View.VISIBLE);
                mTotalPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(orderMenu.getCharge())));

            }
        }
    }
}