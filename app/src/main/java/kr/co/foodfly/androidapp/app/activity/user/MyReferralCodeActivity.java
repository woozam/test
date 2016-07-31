package kr.co.foodfly.androidapp.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Locale;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-31.
 */
public class MyReferralCodeActivity extends BaseActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, MyReferralCodeActivity.class);
        context.startActivity(intent);
    }

    private NetworkImageView mNetworkImageView;
    private Button mReferralCode;
    private Button mReferralLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referral_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNetworkImageView = (NetworkImageView) findViewById(R.id.my_referral_code_image);
        mReferralCode = (Button) findViewById(R.id.my_referral_code_copy_code);
        mReferralLink = (Button) findViewById(R.id.my_referral_code_copy_link);

        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            finish();
            return;
        }

        mReferralCode.setText(user.getUser().getReferralCode().toUpperCase());
        mReferralCode.setTag(user.getUser().getReferralCode().toUpperCase());
        mReferralLink.setText(String.format(Locale.getDefault(), "http://m.foodfly.co.kr/referral/v2/%s", user.getUser().getReferralCode().toUpperCase()));
        mReferralLink.setTag(String.format(Locale.getDefault(), "http://m.foodfly.co.kr/referral/v2/%s", user.getUser().getReferralCode().toUpperCase()));
        mReferralCode.setOnClickListener(this);
        mReferralLink.setOnClickListener(this);

        loadReferral();
    }

    private void loadReferral() {
        UserResponse user = UserManager.fetchUser();
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_REFERRAL).toString();
        GsonRequest<ReferralResponse> request = new GsonRequest<>(Method.GET, url, ReferralResponse.class, APIs.createHeadersWithToken(), new Listener<ReferralResponse>() {
            @Override
            public void onResponse(ReferralResponse response) {
                dismissProgressDialog();
                mNetworkImageView.setImageUrl(response.image, VolleySingleton.getInstance(mNetworkImageView.getContext()).getImageLoader());
                mReferralCode.setText(response.code);
                mReferralLink.setText(response.url);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(MyReferralCodeActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @Override
    public void onClick(View v) {
        CommonUtils.copyToClipboard((String) v.getTag());
    }

    private class ReferralResponse {
        String image;
        String code;
        String url;
    }
}