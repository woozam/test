package com.foodfly.gcm.app.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.view.recyclerView.LinearDividerItemDecoration;
import com.foodfly.gcm.app.view.recyclerView.RecyclerViewEmptySupport;
import com.foodfly.gcm.common.TimeUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.order.OrderListItem;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

import com.foodfly.gcm.R;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.user.UserManager;

/**
 * Created by woozam on 2016-07-29.
 */
public class OrderListActivity extends BaseActivity {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, OrderListActivity.class);
        context.startActivity(intent);
    }

    private ArrayList<OrderListItem> mOrderList;
    private OrderListAdapter mOrderListAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private View mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOrderList = new ArrayList<>();
        mOrderListAdapter = new OrderListAdapter();
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.order_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mOrderListAdapter);
        mRecyclerView.addItemDecoration(new LinearDividerItemDecoration(this));
        mEmptyView = findViewById(R.id.order_list_empty_view);

        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            finish();
            return;
        }

        loadOrderList();
    }

    private void loadOrderList() {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ORDER).toString();
            GsonRequest<OrderListItem[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<OrderListItem[]>() {
            }.getType(), APIs.createHeadersWithToken(), new Listener<OrderListItem[]>() {
                @Override
                public void onResponse(OrderListItem[] response) {
                    dismissProgressDialog();
                    mRecyclerView.setEmptyView(mEmptyView);
                    mOrderList.clear();
                    Collections.addAll(mOrderList, response);
                    Collections.reverse(mOrderList);
                    mOrderListAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(OrderListActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    private class OrderListAdapter extends Adapter<OrderListViewHolder> {

        @Override
        public OrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OrderListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_list, parent, false));
        }

        @Override
        public void onBindViewHolder(final OrderListViewHolder holder, int position) {
            holder.setItem(mOrderList.get(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderDetailActivity.createInstance(OrderListActivity.this, mOrderList.get(holder.getAdapterPosition()).getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mOrderList.size();
        }
    }

    public static class OrderListViewHolder extends ViewHolder {

        private NetworkImageView mImage;
        private TextView mDate;
        private TextView mName;

        public OrderListViewHolder(View itemView) {
            super(itemView);
            mImage = (NetworkImageView) itemView.findViewById(R.id.order_list_image);
            mDate = (TextView) itemView.findViewById(R.id.order_list_date);
            mName = (TextView) itemView.findViewById(R.id.order_list_restaurant_name);
        }

        public void setItem(OrderListItem order) {
            mImage.setImageUrl(order.getRestaurant().getThumbnail(), VolleySingleton.getInstance(mImage.getContext()).getImageLoader());
            mDate.setText(TimeUtils.getYearMonthDateTimeString(order.getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
            mName.setText(order.getRestaurant().getName());
        }
    }
}