package kr.co.foodfly.androidapp.app.activity.main;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import kr.co.foodfly.androidapp.BuildConfig;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.connect.MartflyShow;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-06.
 */
public class MainNavigationView extends RelativeLayout implements OnClickListener {

    private ImageView mBgImageView;
    private View mLoginLayout;
    private View mLogin1;
    private View mLogin2;
    private View mSignUp;
    private View mUserLayout;
    private View mUserName;
    private TextView mUserNameText;
    private TextView mUserReferralCode;
    private TextView mUserMileage;
    private TextView mUserCoupon;
    private View mCart;
    private View mRecent;
    private View mTheme2;
    private View mThemeDivider;
    private View mGrid;
    private View mGridDivider;
    private View mTheme;
    private View mMartfly;
    private View mReferral;
    private View mPromotion;
    private View mCS;
    private View mFacebook;
    private View mInstagram;
    private View mBlog;
    private View mYoutube;
    private TextView mVersion;
    private Realm mUserRealm;
    private Realm mConnectRealm;
    private OnMainNavigationMenuListener mOnMainNavigationMenuListener;

    private View mOrderDivider;
    private View mOrder;
    private View mFavoriteDivider;
    private View mFavorite;
    private ImageView mChefly;

    public MainNavigationView(Context context) {
        super(context);
        initialize();
    }

    public MainNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MainNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_main_navigation, this, true);
        mBgImageView = (ImageView) findViewById(R.id.main_navigation_bg_view);
        mLoginLayout = findViewById(R.id.main_navigation_login_layout);
        mLogin1 = findViewById(R.id.main_navigation_login_1);
        mLogin2 = findViewById(R.id.main_navigation_login_2);
        mUserLayout = findViewById(R.id.main_navigation_user_layout);
        mUserName = findViewById(R.id.main_navigation_user_name);
        mUserNameText = (TextView) findViewById(R.id.main_navigation_user_name_text);
        mUserReferralCode = (TextView) findViewById(R.id.main_navigation_user_referral_code);
        mUserCoupon = (TextView) findViewById(R.id.main_navigation_user_coupon);
        mUserMileage = (TextView) findViewById(R.id.main_navigation_user_mileage);
        mSignUp = findViewById(R.id.main_navigation_sign_up);
        mCart = findViewById(R.id.main_navigation_cart);
        mRecent = findViewById(R.id.main_navigation_recent);
        mTheme = findViewById(R.id.main_navigation_theme);
        mThemeDivider = findViewById(R.id.main_navigation_theme_divider);
        mGrid = findViewById(R.id.main_navigation_grid);
        mGridDivider = findViewById(R.id.main_navigation_grid_divider);
        mTheme2 = findViewById(R.id.main_navigation_theme_2);
        mMartfly = findViewById(R.id.main_navigation_martfly);
        mReferral = findViewById(R.id.main_navigation_referral);
        mPromotion = findViewById(R.id.main_navigation_promotion);
        mCS = findViewById(R.id.main_navigation_cs);
        mFacebook = findViewById(R.id.main_navigation_social_facebook);
        mInstagram = findViewById(R.id.main_navigation_social_insta);
        mBlog = findViewById(R.id.main_navigation_social_blog);
        mYoutube = findViewById(R.id.main_navigation_social_youtube);
        mVersion = (TextView) findViewById(R.id.main_navigation_version);
        mOrderDivider = findViewById(R.id.main_navigation_order_divider);
        mOrder = findViewById(R.id.main_navigation_order);
        mFavoriteDivider = findViewById(R.id.main_navigation_favorite_divider);
        mFavorite = findViewById(R.id.main_navigation_favorite);
        mChefly = (ImageView) findViewById(R.id.main_navigation_chefly);
        mLogin1.setOnClickListener(this);
        mLogin2.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mUserReferralCode.setOnClickListener(this);
        mUserCoupon.setOnClickListener(this);
        mUserMileage.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mCart.setOnClickListener(this);
        mRecent.setOnClickListener(this);
        mTheme.setOnClickListener(this);
        mTheme2.setOnClickListener(this);
        mMartfly.setOnClickListener(this);
        mReferral.setOnClickListener(this);
        mPromotion.setOnClickListener(this);
        mCS.setOnClickListener(this);
        mFacebook.setOnClickListener(this);
        mInstagram.setOnClickListener(this);
        mBlog.setOnClickListener(this);
        mYoutube.setOnClickListener(this);
        mVersion.setOnClickListener(this);
        mOrder.setOnClickListener(this);
        mFavorite.setOnClickListener(this);
        mChefly.setOnClickListener(this);
        mUserRealm = Realm.getInstance(RealmUtils.CONFIG_USER);
        mUserRealm.addChangeListener(mUserChangeListener);
        mConnectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        mConnectRealm.addChangeListener(mConnectChangeListener);
        setUser();
        setConnect();
        setVersion();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUserRealm.removeChangeListener(mUserChangeListener);
        mUserRealm.close();
        mConnectRealm.removeChangeListener(mConnectChangeListener);
        mConnectRealm.close();
    }

    @Override
    public void onClick(View v) {
        if (mOnMainNavigationMenuListener != null) {
            mOnMainNavigationMenuListener.onMenu(v.getId());
        }
    }

    private void setUser() {
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            mLoginLayout.setVisibility(VISIBLE);
            mUserLayout.setVisibility(GONE);
            mOrderDivider.setVisibility(GONE);
            mOrder.setVisibility(GONE);
            mFavoriteDivider.setVisibility(GONE);
            mFavorite.setVisibility(GONE);
        } else {
            mLoginLayout.setVisibility(GONE);
            mUserLayout.setVisibility(VISIBLE);
            mUserNameText.setText(String.format(Locale.getDefault(), "%s님", user.getUser().getName()));
            mUserReferralCode.setText(String.format(Locale.getDefault(), "추천코드 %s", TextUtils.isEmpty(user.getUser().getReferralCode()) ? "-" : user.getUser().getReferralCode().toUpperCase()));
            mUserReferralCode.setTag(TextUtils.isEmpty(user.getUser().getReferralCode()) ? "-" : user.getUser().getReferralCode().toUpperCase());
            mUserCoupon.setText(String.format(Locale.getDefault(), "할인쿠폰 %d장", user.getUser().getAvailableCouponCount()));
            mUserMileage.setText(String.format(Locale.getDefault(), "마일리지 %s원", UnitUtils.priceFormat(user.getUser().getMileage())));
            mOrderDivider.setVisibility(VISIBLE);
            mOrder.setVisibility(VISIBLE);
            mFavoriteDivider.setVisibility(VISIBLE);
            mFavorite.setVisibility(VISIBLE);
        }
        MapAddress address = MapAddress.getAddress();
        mChefly.setVisibility(TextUtils.equals(address.getCheflyAvailable(), "1") ? VISIBLE : GONE);
    }

    private void setConnect() {
        UserResponse user = UserManager.fetchUser();
        mGrid.setVisibility(GONE);
        mGridDivider.setVisibility(GONE);
        mTheme.setVisibility(VISIBLE);
        mThemeDivider.setVisibility(VISIBLE);
        Connect connect = mConnectRealm.where(Connect.class).findFirst();
        if (connect != null) {
            VolleySingleton.getInstance(getContext()).getImageLoader().get(connect.getPreference().getLoginBackground(), new ImageListener() {
                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    mBgImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }, CommonUtils.getScreenWidth() / 2, CommonUtils.getScreenHeight() / 2, ScaleType.CENTER_CROP);
            MapAddress address = MapAddress.getAddress();
            for (MartflyShow martflyShow : connect.getMartfly().getShow()) {
                if (String.valueOf(address.getAreaCode()).startsWith(String.valueOf(martflyShow.getAreaCodePrefix()))) {
                    mGrid.setVisibility(VISIBLE);
                    mGridDivider.setVisibility(VISIBLE);
                    mTheme.setVisibility(GONE);
                    mThemeDivider.setVisibility(GONE);
                    break;
                }
            }
        }
    }

    private void setVersion() {
        mVersion.setText(String.format(Locale.getDefault(), "ver %s", BuildConfig.VERSION_NAME));
    }

    private RealmChangeListener mUserChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object object) {
            setUser();
        }
    };

    private RealmChangeListener mConnectChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object object) {
            setConnect();
        }
    };

    public void setOnMainNavigationMenuListener(OnMainNavigationMenuListener onMainNavigationMenuListener) {
        mOnMainNavigationMenuListener = onMainNavigationMenuListener;
    }

    public interface OnMainNavigationMenuListener {
        void onMenu(int id);
    }
}