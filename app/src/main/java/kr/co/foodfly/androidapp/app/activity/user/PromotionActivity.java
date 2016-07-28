package kr.co.foodfly.androidapp.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.etc.IntentFilterActivity;
import kr.co.foodfly.androidapp.app.activity.user.CouponActivity.CouponViewHolder;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.user.Coupon;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-10.
 */
public class PromotionActivity extends BaseActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, PromotionActivity.class);
        context.startActivity(intent);
    }

    private TextInputLayout mEditText;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditText = (TextInputLayout) findViewById(R.id.promotion_edit_text);
        mEditText.setHintTextAppearance(R.style.AppTheme_Dark);
        mButton = (Button) findViewById(R.id.promotion_button);

        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mButton) {
            send();
        }
    }

    private void send() {
        String promotionCode = mEditText.getEditText().getText().toString();
        if (TextUtils.isEmpty(promotionCode)) {
            mEditText.setError("프로모션 코드를 입력해주세요.");
            return;
        }
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }
        String url = APIs.getPromotionPath().toString();
        JsonObject json = new JsonObject();
        json.addProperty("user_id", user.getId());
        json.addProperty("code", promotionCode);
        GsonRequest<PromotionResponse> request = new GsonRequest<>(Method.POST, url, PromotionResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<PromotionResponse>() {
            @Override
            public void onResponse(PromotionResponse response) {
                dismissProgressDialog();
                mEditText.getEditText().setText(null);
                mEditText.setError(null);
                if (TextUtils.isEmpty(response.redirect_url)) {
                    if (TextUtils.isEmpty(response.message)) {
                        CouponViewHolder viewHolder = new CouponViewHolder(LayoutInflater.from(PromotionActivity.this).inflate(R.layout.row_coupon, null, false));
                        viewHolder.setCoupon(response.coupon);
                        new MaterialDialog.Builder(PromotionActivity.this).customView(viewHolder.itemView, false).title("쿠폰").positiveText("닫기").show();
                    } else {
                        new MaterialDialog.Builder(PromotionActivity.this).content(response.message).title("쿠폰").positiveText("닫기").show();
                    }
                } else {
                    IntentFilterActivity.createInstance(PromotionActivity.this, response.redirect_url);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                PromotionResponse response = BaseResponse.parseError(PromotionResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(PromotionActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private class PromotionResponse extends BaseResponse {
        Coupon coupon;
        String message;
        String redirect_url;
    }
}