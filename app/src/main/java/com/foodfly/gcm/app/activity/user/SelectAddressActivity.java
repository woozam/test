package com.foodfly.gcm.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.setting.AddressActivity;
import com.foodfly.gcm.app.view.recyclerView.LinearDividerItemDecoration;
import com.foodfly.gcm.app.view.recyclerView.RecyclerViewEmptySupport;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.model.restaurant.Cart;
import com.foodfly.gcm.model.restaurant.Restaurant;
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
import io.realm.RealmResults;

/**
 * Created by woozam on 2016-07-08.
 */
public class SelectAddressActivity extends BaseActivity implements RealmChangeListener<RealmResults<MapAddress>>, OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, SelectAddressActivity.class);
        context.startActivity(intent);
    }

    private ArrayList<MapAddress> mAddressList;
    private SelectAddressAdapter mAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private View mAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddressList = new ArrayList<>();
        mAdapter = new SelectAddressAdapter();
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.select_address_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new LinearDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdd = findViewById(R.id.select_address_floating_action_button);
        mAdd.setOnClickListener(this);

        loadAddressList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressActivity.REQ_CODE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                loadAddressList();
            }
        }
    }

    private void loadAddressList() {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ADDRESS).toString();
            GsonRequest<MapAddress[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<MapAddress[]>() {
            }.getType(), APIs.createHeadersWithToken(), new Listener<MapAddress[]>() {
                @Override
                public void onResponse(MapAddress[] response) {
                    dismissProgressDialog();
                    mAddressList.clear();
                    Collections.addAll(mAddressList, response);
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(SelectAddressActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    @Override
    public void onChange(RealmResults<MapAddress> element) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == mAdd) {
            AddressActivity.createInstance(this);
        }
    }

    private class SelectAddressAdapter extends Adapter<SelectAddressViewHolder> {

        @Override
        public SelectAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SelectAddressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_address, parent, false));
        }

        @Override
        public void onBindViewHolder(final SelectAddressViewHolder holder, int position) {
            holder.setAddress(getItem(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int DEFAULT;
                    final int DELETE;
                    String[] items;
                    final MapAddress address = getItem(holder.getAdapterPosition());
                    if (address.isDefault()) {
                        DEFAULT = -1;
                        DELETE = 0;
                        items = new String[]{"삭제"};
                    } else {
                        DEFAULT = 0;
                        DELETE = 1;
                        items = new String[]{"기본 배송지로 설정", "삭제"};
                    }
                    new MaterialDialog.Builder(SelectAddressActivity.this).items(items).itemsCallback(new ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            if (which == DEFAULT) {
                                final UserResponse user = UserManager.fetchUser();
                                if (user != null) {
                                    Realm cartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
                                    Cart cart = cartRealm.where(Cart.class).findFirst();
                                    if (cart != null) {
                                        cart = cartRealm.copyFromRealm(cart);
                                        loadRestaurant(user, cart.getRestaurant(), address);
                                    } else {
                                        save(user, address);
                                    }
                                    cartRealm.close();
                                }
                            } else if (which == DELETE) {
                                UserResponse user = UserManager.fetchUser();
                                if (user != null) {
                                    String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ADDRESS).appendPath(address.getId()).toString();
                                    GsonRequest<BaseResponse> request = new GsonRequest<>(Request.Method.DELETE, url, BaseResponse.class, APIs.createHeadersWithToken(), new Listener<BaseResponse>() {
                                        @Override
                                        public void onResponse(BaseResponse response) {
                                            dismissProgressDialog();
                                            mAddressList.remove(holder.getAdapterPosition());
                                            mAdapter.notifyItemRemoved(holder.getAdapterPosition());
                                        }
                                    }, new ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            dismissProgressDialog();
                                            BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                                            if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                                                new MaterialDialog.Builder(SelectAddressActivity.this).content(response.getError().message).positiveText("확인").show();
                                            }
                                        }
                                    }, RealmUtils.REALM_GSON);
                                    VolleySingleton.getInstance(SelectAddressActivity.this).addToRequestQueue(request);
                                    showProgressDialog();
                                }
                            }
                        }
                    }).show();
                }
            });
        }

        private void loadRestaurant(final UserResponse user, final Restaurant restaurant, final MapAddress address) {
            Uri.Builder builder = APIs.getRestaurantPath().appendPath(restaurant.getId()).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(address.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(address.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(address.getAreaCode()));
            if (user != null) {
                builder.appendQueryParameter(APIs.PARAM_USER_ID, user.getId());
            }
            String url = builder.toString();
            GsonRequest<Restaurant> request = new GsonRequest<>(Method.GET, url, Restaurant.class, APIs.createHeadersWithToken(), new Listener<Restaurant>() {
                @Override
                public void onResponse(Restaurant response) {
                    dismissProgressDialog();
                    if (response.getDistance() > response.getAvailableDistance()) {
                        new MaterialDialog.Builder(SelectAddressActivity.this).content(String.format(Locale.getDefault(), "변경하시려는 배송지에서 배달 불가능한 %s 매장의 메뉴가 장바구니에서 삭제됩니다.\n새 배송지로 변경하시겠습니까?", restaurant.getName())).positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Realm cartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
                                cartRealm.beginTransaction();
                                cartRealm.deleteAll();
                                cartRealm.commitTransaction();
                                cartRealm.close();
                                save(user, address);
                            }
                        }).show();
                    } else {
                        save(user, address);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(SelectAddressActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(SelectAddressActivity.this).addToRequestQueue(request);
            showProgressDialog();
        }

        private void save(final UserResponse user, final MapAddress address) {
            JsonObject json = new JsonObject();
            json.addProperty("is_default", true);
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ADDRESS).appendPath(address.getId()).toString();
            GsonRequest<BaseResponse> request = new GsonRequest<>(Request.Method.PUT, url, BaseResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<BaseResponse>() {
                @Override
                public void onResponse(BaseResponse response) {
                    dismissProgressDialog();
                    for (MapAddress each : mAddressList) {
                        each.setDefault(false);
                    }
                    address.setDefault(true);
                    mAdapter.notifyDataSetChanged();
                    user.getUser().setAddress(address);
                    UserManager.setUser(user);
                    Connect.updateConnect();
                    finish();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(SelectAddressActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(SelectAddressActivity.this).addToRequestQueue(request);
            showProgressDialog();
        }

        @Override
        public int getItemCount() {
            return mAddressList.size();
        }

        public MapAddress getItem(int position) {
            return mAddressList.get(position);
        }
    }

    private static class SelectAddressViewHolder extends ViewHolder {

        private TextView mTitle;

        public SelectAddressViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.select_address_title);
        }

        public void setAddress(MapAddress address) {
            if (TextUtils.isEmpty(address.getFormattedAddress())) {
                mTitle.setText(address.getStreetAddress());
            } else {
                mTitle.setText(address.getFormattedAddress());
            }
            if (!TextUtils.isEmpty(address.getDetailAddress())) {
                mTitle.append(" ");
                mTitle.append(address.getDetailAddress());
            }
            if (address.isDefault()) {
                mTitle.append(" (기본)");
            }
        }
    }
}