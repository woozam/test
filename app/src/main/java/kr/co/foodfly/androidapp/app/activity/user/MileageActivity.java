package kr.co.foodfly.androidapp.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.view.recyclerView.LinearDividerItemDecoration;
import kr.co.foodfly.androidapp.app.view.recyclerView.RecyclerViewEmptySupport;
import kr.co.foodfly.androidapp.common.TimeUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.user.Mileage;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-10.
 */
public class MileageActivity extends BaseActivity {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, MileageActivity.class);
        context.startActivity(intent);
    }

    private TextView mAmount;
    private ArrayList<Mileage> mMileageList;
    private MileageAdapter mAdapter;
    private RecyclerViewEmptySupport mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mileage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAmount = (TextView) findViewById(R.id.mileage_amount);
        mMileageList = new ArrayList<>();
        mAdapter = new MileageAdapter();
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.mileage_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new LinearDividerItemDecoration(this));

        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            finish();
            return;
        } else {
            mAmount.setText(UnitUtils.priceFormat(user.getUser().getMileage()));
        }

        loadMileageList();
    }

    private void loadMileageList() {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_MILEAGE).toString();
            GsonRequest<Mileage[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<kr.co.foodfly.androidapp.model.user.Mileage[]>() {
            }.getType(), APIs.createHeadersWithToken(), new Listener<kr.co.foodfly.androidapp.model.user.Mileage[]>() {
                @Override
                public void onResponse(kr.co.foodfly.androidapp.model.user.Mileage[] response) {
                    dismissProgressDialog();
                    mRecyclerView.setEmptyView(findViewById(R.id.mileage_empty_view));
                    mMileageList.clear();
                    Collections.addAll(mMileageList, response);
                    Collections.reverse(mMileageList);
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(MileageActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    private class MileageAdapter extends Adapter<MileageViewHolder> {

        @Override
        public MileageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MileageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mileage, parent, false));
        }

        @Override
        public void onBindViewHolder(MileageViewHolder holder, int position) {
            holder.setMileage(mMileageList.get(position));
        }

        @Override
        public int getItemCount() {
            return mMileageList.size();
        }
    }

    private static class MileageViewHolder extends ViewHolder {

        private TextView mType;
        private TextView mDate;
        private TextView mRestaurant;
        private TextView mPayAmount;
        private TextView mAmount;

        public MileageViewHolder(View itemView) {
            super(itemView);
            mType = (TextView) itemView.findViewById(R.id.mileage_type);
            mDate = (TextView) itemView.findViewById(R.id.mileage_date);
            mRestaurant = (TextView) itemView.findViewById(R.id.mileage_restaurant);
            mPayAmount = (TextView) itemView.findViewById(R.id.mileage_pay_amount);
            mAmount = (TextView) itemView.findViewById(R.id.mileage_amount);
        }

        public void setMileage(Mileage mileage) {
            mType.setText(mileage.getTypeString());
            mDate.setText(TimeUtils.getYearMonthDateTimeString(mileage.getTimestamp().getTime() + TimeZone.getDefault().getRawOffset()));
            mRestaurant.setText((mileage.getRestaurant() != null && !TextUtils.isEmpty(mileage.getRestaurant().getName())) ? mileage.getRestaurant().getName() : "-");
            mPayAmount.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mileage.getPayAmount())));
            mAmount.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.signedPrice(mileage.getAmount())));
            mAmount.setTextColor(ResourcesCompat.getColor(mAmount.getResources(), mileage.getAmount() > 0 ? R.color.colorPrimary : (mileage.getAmount() < 0 ? R.color.colorAccent : R.color.textColorPrimary), null));
        }
    }
}