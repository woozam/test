package com.foodfly.gcm.app.view.recyclerView;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.view.recyclerView.LazyAdapter.LazyViewHolder;

/**
 * Created by woozam on 2016-06-29.
 */
public abstract class LazyAdapter extends Adapter<LazyViewHolder> {

    public static final int LAZY_LOAD_ITEM_VIEW_TYPE = 3999;
    protected boolean mLazyLoad = false;

    public abstract int getOriginalItemCount();

    @Override
    public int getItemViewType(int position) {
        if (mLazyLoad && position == getItemCount() - 1) {
            return LAZY_LOAD_ITEM_VIEW_TYPE;
        } else {
            return getOriginalItemViewType(position);
        }
    }

    public abstract int getOriginalItemViewType(int position);

    @Override
    public LazyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LAZY_LOAD_ITEM_VIEW_TYPE) {
            return new LazyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_progress, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(LazyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return getOriginalItemCount() + (mLazyLoad ? 1 : 0);
    }

    @Override
    public long getItemId(int position) {
        if (mLazyLoad && position == getItemCount() - 1) {
            return -1;
        } else {
            return super.getItemId(position);
        }
    }

    public void onLazyLoadStart() {
        if (!mLazyLoad) {
            mLazyLoad = true;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void onLazyLoadComplete() {
        if (mLazyLoad) {
            mLazyLoad = false;
            notifyItemInserted(getItemCount());
        }
    }

    public static class LazyViewHolder extends ViewHolder {

        public LazyViewHolder(View itemView) {
            super(itemView);
        }
    }
}