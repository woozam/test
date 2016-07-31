package kr.co.foodfly.androidapp.app.activity.user;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
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
import kr.co.foodfly.androidapp.app.view.recyclerView.LinearSpacingItemDecoration;
import kr.co.foodfly.androidapp.app.view.recyclerView.RecyclerViewEmptySupport;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.TimeUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.user.Coupon;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

import static kr.co.foodfly.androidapp.R.id.coupon_date;

/**
 * Created by woozam on 2016-07-10.
 */
public class CouponActivity extends BaseActivity implements OnClickListener {

    public static final int REQ_CODE_COUPON = CouponActivity.class.hashCode() & 0x000000ff;

    public static final String EXTRA_SELECT = "extra_select";
    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_COUPON_ID = "extra_coupon_id";

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, CouponActivity.class);
        context.startActivity(intent);
    }

    public static void createInstance(Activity activity, boolean select, String restaurantId) {
        Intent intent = new Intent(activity, CouponActivity.class);
        intent.putExtra(EXTRA_SELECT, select);
        intent.putExtra(EXTRA_RESTAURANT_ID, restaurantId);
        activity.startActivityForResult(intent, REQ_CODE_COUPON);
    }

    private ArrayList<Coupon> mCouponList;
    private CouponAdapter mAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private View mOverlayView;
    private View mHelpLayout;
    private ImageView mHelpHandle;
    private TextView mHelpContent;
    private boolean mShowHelp;
    private boolean mInHelpAnimation;
    private boolean mSelect;
    private String mRestaurantId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mSelect = intent.getBooleanExtra(EXTRA_SELECT, false);
        mRestaurantId = intent.getStringExtra(EXTRA_RESTAURANT_ID);

        mCouponList = new ArrayList<>();
        mAdapter = new CouponAdapter();
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.coupon_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new LinearSpacingItemDecoration(CommonUtils.convertDipToPx(this, 18)) {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getAdapter().getItemCount() - 1 == parent.getChildAdapterPosition(view)) {
                    outRect.bottom = mHeight + mHelpHandle.getMeasuredHeight();
                    outRect.top = mHeight;
                } else {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mOverlayView = findViewById(R.id.coupon_overlay_view);
        mHelpLayout = findViewById(R.id.coupon_help_layout);
        mHelpContent = (TextView) findViewById(R.id.coupon_help_drag_content);
        mHelpHandle = (ImageView) findViewById(R.id.coupon_help_drag_handle);
        mHelpHandle.setOnClickListener(this);
        mOverlayView.setAlpha(0.0f);
        mOverlayView.setVisibility(View.GONE);
        mOverlayView.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerViewEmptySupport.SCROLL_STATE_DRAGGING) {
                    hideHelp();
                }
            }
        });

        loadCouponList();
    }

    private void loadCouponList() {
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_COUPON).toString();
            GsonRequest<Coupon[]> request = new GsonRequest<>(Request.Method.GET, url, new TypeToken<Coupon[]>() {
            }.getType(), APIs.createHeadersWithToken(), new Listener<Coupon[]>() {
                @Override
                public void onResponse(Coupon[] response) {
                    dismissProgressDialog();
                    mRecyclerView.setEmptyView(findViewById(R.id.coupon_empty_view));
                    mCouponList.clear();
                    Collections.addAll(mCouponList, response);
                    mAdapter.notifyDataSetChanged();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(CouponActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (mShowHelp) {
            hideHelp();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mHelpHandle) {
            if (mShowHelp) {
                hideHelp();
            } else {
                showHelp();
            }
        } else if (v == mOverlayView) {
            hideHelp();
        }
    }

    private void showHelp() {
        if (!mShowHelp && !mInHelpAnimation) {
            mInHelpAnimation = true;
            mOverlayView.setVisibility(View.VISIBLE);
            mHelpLayout.clearAnimation();
            mHelpContent.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            final int from = 0;
            final int to = findViewById(R.id.coupon_help_drag_content_2).getMeasuredHeight() + CommonUtils.convertDipToPx(this, 18);
            ValueAnimator slideAnimator = ValueAnimator.ofInt(from, to).setDuration(250);
            slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHelpHandle.setImageResource(R.mipmap.coupon_close);
                    mHelpLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mHelpLayout.requestLayout();
                    mOverlayView.setAlpha(((float) ((Integer) animation.getAnimatedValue() - from)) / ((float) (to - from)));
                    if ((Integer) animation.getAnimatedValue() == to) {
                        mShowHelp = true;
                        mInHelpAnimation = false;
                    }
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    private void hideHelp() {
        if (mShowHelp && !mInHelpAnimation) {
            mInHelpAnimation = true;
            mHelpLayout.clearAnimation();
            final int from = mHelpLayout.getMeasuredHeight();
            final int to = 0;
            ValueAnimator slideAnimator = ValueAnimator.ofInt(from, to).setDuration(250);
            slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHelpHandle.setImageResource(R.mipmap.coupon_open);
                    mHelpLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mHelpLayout.requestLayout();
                    mOverlayView.setAlpha(1 - ((float) ((Integer) animation.getAnimatedValue() - from)) / ((float) (to - from)));
                    if ((Integer) animation.getAnimatedValue() == to) {
                        mShowHelp = false;
                        mInHelpAnimation = false;
                        mOverlayView.setVisibility(View.GONE);
                    }
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    private class CouponAdapter extends Adapter<CouponViewHolder> {

        @Override
        public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CouponViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coupon, parent, false));
        }

        @Override
        public void onBindViewHolder(final CouponViewHolder holder, int position) {
            holder.setCoupon(mCouponList.get(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelect) {
                        Coupon coupon = mCouponList.get(holder.getAdapterPosition());
                        if (!coupon.isCouponValid()) {
                            new MaterialDialog.Builder(v.getContext()).content(String.format(Locale.getDefault(), coupon.getStatusMessage(), coupon.getRestaurant().getName())).positiveText("확인").show();
                        } else if (!coupon.isExpired() && !coupon.isUsed()) {
                            if (!TextUtils.isEmpty(mRestaurantId) && TextUtils.equals(mRestaurantId, coupon.getRestaurant().getId())) {
                                new MaterialDialog.Builder(v.getContext()).content(String.format(Locale.getDefault(), "이 쿠폰은 %s에서만 사용하실 수 있습니다.", coupon.getRestaurant().getName())).positiveText("확인").show();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_COUPON_ID, coupon.getId());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCouponList.size();
        }
    }

    public static class CouponViewHolder extends ViewHolder {

        private TextView mAmount;
        private TextView mRestaurantName;
        private TextView mDate;
        private TextView mExpire;

        public CouponViewHolder(View itemView) {
            super(itemView);
            itemView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mAmount = (TextView) itemView.findViewById(R.id.coupon_amount);
            mRestaurantName = (TextView) itemView.findViewById(R.id.coupon_restaurant_name);
            mDate = (TextView) itemView.findViewById(coupon_date);
            mExpire = (TextView) itemView.findViewById(R.id.coupon_expire);
        }

        public void setCoupon(Coupon coupon) {
            mAmount.setText(null);
            if (coupon.getType() == 1) {
                mAmount.append("배달팁 ");
            } else if (coupon.getType() == 2) {
                mAmount.append("메뉴 ");
            } else if (coupon.getType() == 3) {
                mAmount.append("총액 ");
            }
            if (coupon.getDiscountType() == 1) {
                mAmount.append(String.format(Locale.getDefault(), "%s원 할인", UnitUtils.priceFormat(coupon.getDiscountAmount())));
            } else if (coupon.getDiscountType() == 2) {
                mAmount.append(String.format(Locale.getDefault(), "%d%% 할인", coupon.getDiscountAmount()));
            }
            if (coupon.getRestaurant() == null || TextUtils.isEmpty(coupon.getRestaurant().getName())) {
                mRestaurantName.setVisibility(View.GONE);
            } else {
                mRestaurantName.setVisibility(View.VISIBLE);
                mRestaurantName.setText(coupon.getRestaurant().getName());
            }
            mDate.setText(String.format(Locale.getDefault(), "%s ~ %s", TimeUtils.getYearMonthDateTimeString(coupon.getStartDate().getTime() + TimeZone.getDefault().getRawOffset()), TimeUtils.getYearMonthDateTimeString(coupon.getEndDate().getTime() + TimeZone.getDefault().getRawOffset())));
            int expire = (int) Math.ceil((coupon.getEndDate().getTime() - System.currentTimeMillis()) / (float) (1000 * 3600 * 24));
            if (!coupon.isCouponValid()) {
                mExpire.setText(coupon.getStatusMessage());
                mExpire.setBackgroundColor(ResourcesCompat.getColor(mExpire.getResources(), R.color.textColorHint, null));
            } else if (coupon.isUsed()) {
                mExpire.setText("사용 완료");
                mExpire.setBackgroundColor(ResourcesCompat.getColor(mExpire.getResources(), R.color.textColorHint, null));
            } else if (expire < 0 || coupon.isExpired()) {
                mExpire.setText("기간 만료");
                mExpire.setBackgroundColor(ResourcesCompat.getColor(mExpire.getResources(), R.color.textColorHint, null));
            } else if (expire == 0) {
                mExpire.setText("오늘까지");
                mExpire.setBackgroundColor(ResourcesCompat.getColor(mExpire.getResources(), R.color.colorPrimary, null));
            } else {
                mExpire.setText(String.format(Locale.getDefault(), "%d일 남음", expire));
                mExpire.setBackgroundColor(ResourcesCompat.getColor(mExpire.getResources(), R.color.colorPrimary, null));
            }
        }
    }
}
