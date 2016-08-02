package com.foodfly.gcm.app.activity.restaurant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import com.foodfly.gcm.app.activity.restaurant.RestaurantListAdapter.RestaurantListViewHolder;
import com.foodfly.gcm.app.view.recyclerView.RealmRecyclerViewAdapter;
import com.foodfly.gcm.model.restaurant.Restaurant;

/**
 * Created by woozam on 2016-06-23.
 */
public class RecentRestaurantListAdapter extends RealmRecyclerViewAdapter<Restaurant, RestaurantListViewHolder> {

    private int mImageHeight;
    private int mRowLayoutResId;

    public RecentRestaurantListAdapter(Context context, RealmResults<Restaurant> realmResults, RealmChangeListener listener, int imageHeight, int rowLayoutResId) {
        super(context, realmResults, listener);
        mImageHeight = imageHeight;
        mRowLayoutResId = rowLayoutResId;
    }

    @Override
    public RestaurantListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RestaurantListViewHolder(LayoutInflater.from(parent.getContext()).inflate(mRowLayoutResId, parent, false), mImageHeight);
    }

    @Override
    public void onBindViewHolder(final RestaurantListViewHolder holder, int position) {
        holder.setItem(getItem(position));
        holder.getFavoriteIcon().setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Restaurant restaurant = getItem(holder.getAdapterPosition());
                if (!restaurant.isAvailable()) {
                    new MaterialDialog.Builder(v.getContext()).content(restaurant.getAvailableMessage()).positiveText("확인").onPositive(new SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            RestaurantActivity.createInstance(v.getContext(), restaurant.getId());
                        }
                    }).show();
                } else {
                    RestaurantActivity.createInstance(v.getContext(), restaurant.getId());
                }
            }
        });
    }
}