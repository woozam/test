package kr.co.foodfly.androidapp.app.activity.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import kr.co.foodfly.androidapp.app.activity.restaurant.RestaurantListAdapter.RestaurantListViewHolder;
import kr.co.foodfly.androidapp.app.view.recyclerView.RealmRecyclerViewAdapter;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;

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
    public void onBindViewHolder(RestaurantListViewHolder holder, int position) {
        holder.setItem(getItem(position));
        holder.getFavoriteIcon().setVisibility(View.GONE);
    }
}