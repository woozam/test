package com.foodfly.gcm.app.activity.etc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.kickOff.LoginActivity;
import com.foodfly.gcm.app.activity.kickOff.SignUpActivity;
import com.foodfly.gcm.app.activity.main.MainActivity;
import com.foodfly.gcm.app.activity.main.SearchActivity;
import com.foodfly.gcm.app.activity.order.CartActivity;
import com.foodfly.gcm.app.activity.order.OrderDetailActivity;
import com.foodfly.gcm.app.activity.order.OrderListActivity;
import com.foodfly.gcm.app.activity.restaurant.FavoriteRestaurantListActivity;
import com.foodfly.gcm.app.activity.restaurant.RecentRestaurantListActivity;
import com.foodfly.gcm.app.activity.restaurant.RestaurantActivity;
import com.foodfly.gcm.app.activity.theme.ThemeActivity;
import com.foodfly.gcm.app.activity.theme.ThemeRestaurantActivity;
import com.foodfly.gcm.app.activity.user.CouponActivity;
import com.foodfly.gcm.app.activity.user.MileageActivity;
import com.foodfly.gcm.app.activity.user.PromotionActivity;
import com.foodfly.gcm.app.activity.user.ReferralCodeActivity;
import com.foodfly.gcm.app.activity.user.UserActivity;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.model.Chefly;
import com.foodfly.gcm.model.user.UserManager;

import java.net.URLDecoder;

/**
 * Created by woozam on 2016-07-10.
 */
public class IntentFilterActivity extends BaseActivity {

    public static final String TAG = IntentFilterActivity.class.getSimpleName();

    public static void createInstance(Context context, String uri) {
        try {
            Intent intent = new Intent(context, IntentFilterActivity.class);
            intent.setData(Uri.parse(URLDecoder.decode(uri, "utf-8")));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d(TAG, uri.toString());
            String host = uri.getHost();
            if (TextUtils.equals(host, "theme")) {
                String id = uri.getQueryParameter("id");
                if (!TextUtils.isEmpty(id)) {
                    ThemeRestaurantActivity.createInstance(this, id);
                }
            } else if (TextUtils.equals(host, "web")) {
                String url = uri.getQueryParameter("url");
                if (!TextUtils.isEmpty(url)) {
                    WebViewActivity.createInstance(this, url, null, false);
                }
            } else if (TextUtils.equals(host, "home")) {
                String categoryId = uri.getQueryParameter("id");
                MainActivity.createInstance(this, categoryId);
            } else if (TextUtils.equals(host, "mypage")) {
                UserActivity.createInstance(this);
            } else if (TextUtils.equals(host, "login")) {
                MainActivity.createInstance(this);
                if (UserManager.fetchUser() == null) {
                    LoginActivity.createInstance(this);
                }
            } else if (TextUtils.equals(host, "signup")) {
                MainActivity.createInstance(this);
                if (UserManager.fetchUser() == null) {
                    String referralCode = uri.getQueryParameter("code");
                    SignUpActivity.createInstance(this, referralCode);
                }
            } else if (TextUtils.equals(host, "coupon")) {
                CouponActivity.createInstance(this);
            } else if (TextUtils.equals(host, "mileage")) {
                MileageActivity.createInstance(this);
            } else if (TextUtils.equals(host, "cart")) {
                CartActivity.createInstance(this);
            } else if (TextUtils.equals(host, "favorite")) {
                FavoriteRestaurantListActivity.createInstance(this);
            } else if (TextUtils.equals(host, "myorder")) {
                String id = uri.getQueryParameter("id");
                if (TextUtils.isEmpty(id)) {
                    OrderListActivity.createInstance(this);
                } else {
                    OrderDetailActivity.createInstance(this, id);
                }
            } else if (TextUtils.equals(host, "referral")) {
                // no use
            } else if (TextUtils.equals(host, "referral2")) {
                // no use
            } else if (TextUtils.equals(host, "referralcode")) {
                ReferralCodeActivity.createInstance(this);
            } else if (TextUtils.equals(host, "promotion")) {
                PromotionActivity.createInstance(this);
            } else if (TextUtils.equals(host, "theme")) {
                String id = uri.getQueryParameter("id");
                if (TextUtils.isEmpty(id)) {
                    ThemeActivity.createInstance(this);
                } else {
                    ThemeRestaurantActivity.createInstance(this, id);
                }
            } else if (TextUtils.equals(host, "foomart")) {
                CommonUtils.openBrowser(this, "http://m.martfly.co.kr");
            } else if (TextUtils.equals(host, "helpdesk")) {
                CSActivity.createInstance(this);
            } else if (TextUtils.equals(host, "recent")) {
                RecentRestaurantListActivity.createInstance(this);
            } else if (TextUtils.equals(host, "search")) {
                String keyword = uri.getQueryParameter("keyword");
                SearchActivity.createInstance(this, keyword);
            } else if (TextUtils.equals(host, "restaurant")) {
                String id = uri.getQueryParameter("id");
                RestaurantActivity.createInstance(this, id);
            } else if (TextUtils.equals(host, "order")) {
                // no use
            } else if (TextUtils.equals(host, "safari")) {
                String url = uri.getQueryParameter("url");
                CommonUtils.openBrowser(this, url);
            } else if (TextUtils.equals(host, "chefly")) {
                Chefly.createCheflyInstance(this);
            }
        }
        finish();
    }

    //    메인	카테고리			foodflyios://home	/category?id=xxxx	O | O
    //    내정보				foodflyios://mypage		O
    //    로그인				foodflyios://login		O
    //    회원가입				foodflyios://signup	/referral?code=xxxx	O | O
    //    쿠폰				foodflyios://coupon		O
    //    마일리지				foodflyios://mileage		O
    //    장바구니				foodflyios://cart		O
    //    단골맛집				foodflyios://favorite		O
    //    내 주문 내역	주문 상세 내역			foodflyios://myorder	?id=xxxx	O | O	고객에게 주문완료되면 문자가 가는데, 이 떄 주문 상세내역 페이지를 (로그인)상태 유지하면서 앱을 실행할 수 있는지?
    //    친구추천				foodflyios://referral		O
    //    친구추천 시즌2				foodflyios://referral2		X	웹에는 반영안되있음
    //    친구초대코드				foodflyios://referralcode		O
    //    프로모션 코드				foodflyios://promotion		O
    //    테마맛집 전체	특정 테마			foodflyios://theme	?id=xxxx	O | O
    //    푸마트				foodflyios://foomart		O
    //    고객센터				foodflyios://helpdesk		O
    //    최근 본 맛집				foodflyios://recent		한글 포함시에는 단축 url로(단축 URL Convert : https://goo.gl/)
    //    검색				foodflyios://search?keyword=xxxx		O
    //    웹				foodflyios://web?url=xxxx		O
    //    음식점				foodflyios://restaurant?id=xxxx		O
    //    주문완료				foodflyios://order/finish	?id=xxxx&success=true/false	O
    //    Safari 이동				foodflyios://safari?url=xxxx		O
    //    Chefly 웹				foodflyios://chefly		O
}