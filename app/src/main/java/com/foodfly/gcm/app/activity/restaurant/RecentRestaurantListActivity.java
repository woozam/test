package com.foodfly.gcm.app.activity.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.Sort;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.view.recyclerView.GridSpacingItemDecoration;
import com.foodfly.gcm.app.view.recyclerView.LazyAdapter;
import com.foodfly.gcm.app.view.recyclerView.RecyclerViewEmptySupport;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.restaurant.Restaurant;

/**
 * Created by woozam on 2016-07-15.
 */
public class RecentRestaurantListActivity extends BaseActivity implements RealmChangeListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, RecentRestaurantListActivity.class);
        context.startActivity(intent);
    }

    private RecentRestaurantListAdapter mAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private View mEmptyView;
    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_restaurant_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int columnCount = 1;
        final int spacing = 12;
        mRealm = Realm.getInstance(RealmUtils.CONFIG_RESTAURANT);
        mAdapter = new RecentRestaurantListAdapter(this, mRealm.where(Restaurant.class).findAllSorted("mLastVisitTime", Sort.DESCENDING), this, (int) ((CommonUtils.getScreenWidth() - CommonUtils.convertDipToPx(this, spacing * (columnCount + 1))) / columnCount * 330f / 272f), R.layout.row_restaurant_list_2);
        mEmptyView = findViewById(R.id.restaurant_list_empty_view);
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recent_restaurant_recycler_view);
        mRecyclerView.setEmptyView(mEmptyView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columnCount) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case RestaurantListAdapter.DETAIL_IMAGE_ITEM_VIEW_TYPE:
                    case LazyAdapter.LAZY_LOAD_ITEM_VIEW_TYPE:
                        return columnCount;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(columnCount, CommonUtils.convertDipToPx(this, spacing), true) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                if (parent.getChildViewHolder(view).getItemViewType() == RestaurantListAdapter.DETAIL_IMAGE_ITEM_VIEW_TYPE) {
                    outRect.bottom = spacing;
                    return;
                }
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
        mRealm.close();
    }

    @Override
    public void onChange(Object element) {
        mAdapter.notifyDataSetChanged();
    }
}