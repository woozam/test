package kr.co.foodfly.androidapp.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-10.
 */
public class ReferralCodeActivity extends BaseActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, ReferralCodeActivity.class);
        context.startActivity(intent);
    }

    private TextInputLayout mEditText;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditText = (TextInputLayout) findViewById(R.id.referral_code_edit_text);
        mEditText.setHintTextAppearance(R.style.AppTheme_Dark);
        mButton = (Button) findViewById(R.id.referral_code_button);

        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mButton) {
            send();
        }
    }

    private void send() {
        String referralCode = mEditText.getEditText().getText().toString();
        if (TextUtils.isEmpty(referralCode)) {
            mEditText.setError("추천코드를 입력해주세요.");
            return;
        }
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("code", referralCode);
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_REFERRAL).toString();
        GsonRequest<ReferralResponse> request = new GsonRequest(Method.POST, url, ReferralResponse.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<ReferralResponse>() {
            @Override
            public void onResponse(ReferralResponse response) {
                dismissProgressDialog();
                Toast.makeText(ReferralCodeActivity.this, "추천코드 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                ReferralResponse response = BaseResponse.parseError(ReferralResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(ReferralCodeActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private class ReferralResponse extends BaseResponse {
    }
}