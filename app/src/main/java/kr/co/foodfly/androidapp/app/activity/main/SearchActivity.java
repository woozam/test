package kr.co.foodfly.androidapp.app.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.RestaurantListFragment;
import kr.co.foodfly.androidapp.app.view.CustomSearchView;
import kr.co.foodfly.androidapp.app.view.recyclerView.LinearDividerItemDecoration;
import kr.co.foodfly.androidapp.app.view.recyclerView.RecyclerViewEmptySupport;
import kr.co.foodfly.androidapp.data.RealmString;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.restaurant.SearchInfo;

/**
 * Created by woozam on 2016-07-30.
 */
public class SearchActivity extends BaseActivity implements OnQueryTextListener, OnCloseListener, OnClickListener {

    public static final String ACTION_SEARCH = "action_search";
    public static final String EXTRA_QUERY = "extra_query";

    public static void createInstance(Context context, String query) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_QUERY, query);
        context.startActivity(intent);
    }

    private CustomSearchView mSearchView;
    private String mQuery;
    private View mRecommendView;
    private TextView mRecommendTitle;
    private Realm mSearchInfoRealm;
    private List<SearchInfo> mSearchHistoryList;
    private List<SearchInfo> mSearchRecommendList;
    private SearchRecommendAdapter mRecommendAdapter;
    private RecyclerViewEmptySupport mRecommendRecyclerView;
    private RealmChangeListener mSearchHistoryChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            mRecommendAdapter.notifyDataSetChanged();
            mRecommendTitle.setText(mSearchHistoryList.size() == 0 ? "추천검색어" : "최근검색어");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSearchView = new CustomSearchView(this);
        mSearchView.setQueryHint("음식명, 레스토랑명 검색");
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnSearchClickListener(this);
        ((MarginLayoutParams) ((ViewGroup) mSearchView.getChildAt(0)).getChildAt(2).getLayoutParams()).leftMargin = 0;

        getSupportActionBar().setCustomView(mSearchView);

        mSearchInfoRealm = Realm.getInstance(RealmUtils.CONFIG_SEARCH_INFO);
        mSearchHistoryList = mSearchInfoRealm.where(SearchInfo.class).findAllSorted("mDate", Sort.DESCENDING);
        ((RealmResults) mSearchHistoryList).addChangeListener(mSearchHistoryChangeListener);
        mSearchRecommendList = new ArrayList<>();
        Realm connectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        Connect connect = connectRealm.where(Connect.class).findFirst();
        if (connect != null) {
            for (RealmString recommend : connect.getRecommendedKeywords()) {
                SearchInfo searchInfo = new SearchInfo();
                searchInfo.setQuery(recommend.getValue());
                searchInfo.setDeletable(false);
                mSearchRecommendList.add(searchInfo);
            }
        }
        connectRealm.close();

        mRecommendAdapter = new SearchRecommendAdapter();
        mRecommendView = findViewById(R.id.search_recommend_layout);
        mRecommendTitle = (TextView) findViewById(R.id.search_recommend_title);
        mRecommendRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.search_recommend_recycler_view);
        mRecommendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecommendRecyclerView.addItemDecoration(new LinearDividerItemDecoration(this));
        mRecommendRecyclerView.setAdapter(mRecommendAdapter);
        mRecommendTitle.setText(mSearchHistoryList.size() == 0 ? "추천검색어" : "최근검색어");

        Intent intent = getIntent();
        setQuery(intent.getStringExtra(EXTRA_QUERY));
        mSearchView.getSearchTextView().setText(mQuery);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Bundle args = new Bundle();
            args.putInt(RestaurantListFragment.EXTRA_COLUMN_COUNT, 2);
            args.putString(RestaurantListFragment.EXTRA_CATEGORY, "0");
            args.putString(RestaurantListFragment.EXTRA_QUERY, mQuery);
            args.putBoolean(RestaurantListFragment.EXTRA_USE_SEARCH, true);
            ft.add(R.id.search_fragment_container, RestaurantListFragment.instantiate(this, RestaurantListFragment.class.getName(), args), "restaurantList");
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((RealmResults) mSearchHistoryList).removeChangeListener(mSearchHistoryChangeListener);
        mSearchInfoRealm.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        SearchInfo searchInfo = new SearchInfo(query, new Date());
        Realm realm = Realm.getInstance(RealmUtils.CONFIG_SEARCH_INFO);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(searchInfo);
        realm.commitTransaction();
        realm.close();
        setQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            setQuery(newText);
            return true;
        }
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    private void setQuery(String query) {
        mQuery = query;
        mRecommendView.setVisibility(TextUtils.isEmpty(mQuery) ? View.VISIBLE : View.GONE);
        mRecommendTitle.setText(mSearchHistoryList.size() == 0 ? "추천검색어" : "최근검색어");
        Intent intent = new Intent(ACTION_SEARCH);
        intent.putExtra(RestaurantListFragment.EXTRA_QUERY, mQuery);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
    }

    private class SearchRecommendAdapter extends Adapter<SearchRecommendViewHolder> {

        @Override
        public SearchRecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchRecommendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_recommend, parent, false));
        }

        @Override
        public void onBindViewHolder(final SearchRecommendViewHolder holder, int position) {
            holder.setItem(getItem(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchInfo searchInfo = getItem(holder.getAdapterPosition());
                    mSearchView.clearFocus();
                    setQuery(searchInfo.getQuery());
                    mSearchView.getSearchTextView().setText(mQuery);
                    if (searchInfo.isValid()) {
                        mSearchInfoRealm.beginTransaction();
                        searchInfo.setDate(new Date());
                        mSearchInfoRealm.commitTransaction();
                    }
                }
            });
            holder.mDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchInfo searchInfo = getItem(holder.getAdapterPosition());
                    if (searchInfo.isValid()) {
                        mSearchInfoRealm.beginTransaction();
                        searchInfo.deleteFromRealm();
                        mSearchInfoRealm.commitTransaction();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSearchHistoryList.size() == 0 ? mSearchRecommendList.size() : mSearchHistoryList.size();
        }

        public SearchInfo getItem(int position) {
            return mSearchHistoryList.size() == 0 ? mSearchRecommendList.get(position) : mSearchHistoryList.get(position);
        }
    }

    public static class SearchRecommendViewHolder extends ViewHolder {

        private TextView mTitle;
        private View mDelete;

        public SearchRecommendViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.search_recommend_title);
            mDelete = itemView.findViewById(R.id.search_recommend_remove);
        }

        public void setItem(SearchInfo searchInfo) {
            mTitle.setText(searchInfo.getQuery());
            mDelete.setVisibility(searchInfo.isDeletable() ? View.VISIBLE : View.GONE);
        }
    }
}