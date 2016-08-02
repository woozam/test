package com.foodfly.gcm.app.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.common.ViewSupportUtils;
import com.foodfly.gcm.model.user.User;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.model.user.VerificationCodeResponse;
import com.foodfly.gcm.model.user.VerifyResponse;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.JsonObject;

import java.util.Locale;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;

/**
 * Created by woozam on 2016-07-09.
 */
public class UserActivity extends BaseActivity implements OnClickListener, TextWatcher {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    private EditText mId;
    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mNewPasswordConfirm;
    private View mAuthLayout;
    private EditText mAuthCode;
    private View mAuth;
    private EditText mNumber;
    private Button mReSend;
    private EditText mEmail;
    private SwitchCompat mAgreeRecvSMS;
    private SwitchCompat mAgreeRecvEmail;
    private SwitchCompat mAgreeRecvPush;
    private View mLogout;
    private Button mCancel;
    private Button mSave;
    private VerificationCodeResponse mVerificationCodeResponse;
    private Handler mStopWatchHandler;
    private TextView mStopWatch;
    private Object mVerifyTag = new Object();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mId = (EditText) findViewById(R.id.user_id_edit_text);
        mOldPassword = (EditText) findViewById(R.id.user_pw_edit_text);
        mNewPassword = (EditText) findViewById(R.id.user_new_pw_edit_text);
        mNewPasswordConfirm = (EditText) findViewById(R.id.user_new_pw_confirm_edit_text);
        mNumber = (EditText) findViewById(R.id.user_number_edit_text);
        mReSend = (Button) findViewById(R.id.user_number_re_send);
        mAuthLayout = findViewById(R.id.user_number_auth_layout);
        mAuthCode = (EditText) findViewById(R.id.user_number_auth_code_edit_text);
        mAuth = findViewById(R.id.user_number_auth);
        mStopWatch = (TextView) findViewById(R.id.user_number_auth_code_stop_watch);
        mEmail = (EditText) findViewById(R.id.user_email_edit_text);
        mAgreeRecvSMS = (SwitchCompat) findViewById(R.id.user_agree_recv_sms);
        mAgreeRecvEmail = (SwitchCompat) findViewById(R.id.user_agree_recv_email);
        mAgreeRecvPush = (SwitchCompat) findViewById(R.id.user_agree_recv_push);

        mLogout = findViewById(R.id.user_logout);
        mCancel = (Button) findViewById(R.id.user_cancel);
        mSave = (Button) findViewById(R.id.user_save);

        mNumber.addTextChangedListener(this);
        mNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        ViewSupportUtils.disableEditText(mId);
        mReSend.setOnClickListener(this);
        mAuth.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mSave.setOnClickListener(this);

        setUser();
    }

    private void setUser() {
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            finish();
        } else {
            mId.setText(user.getUser().getUserName());
            mNumber.setText(user.getUser().getPhone());
            mEmail.setText(user.getUser().getEmail());
            mAgreeRecvSMS.setChecked(user.getUser().isAgreeRecvSMS());
            mAgreeRecvEmail.setChecked(user.getUser().isAgreeRecvEmail());
            mAgreeRecvPush.setChecked(user.getUser().isAgreeRecvPush());
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mReSend) {
            resend();
        } else if (v == mAuth) {
            auth();
        } else if (v == mLogout) {
            logout();
        } else if (v == mCancel) {
            finish();
        } else if (v == mSave) {
            save();
        }
    }

    private void resend() {
        String number = mNumber.getText().toString();
        if (TextUtils.isEmpty(number) || number.length() < 12) {
            Toast.makeText(this, "전화번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phoneNumber = number;
        String url = APIs.getAuthPath().appendPath(APIs.AUTH_VERIFICATION).appendQueryParameter(APIs.PARAM_PHONE, phoneNumber).toString();
        GsonRequest<VerificationCodeResponse> request = new GsonRequest<>(Method.GET, url, VerificationCodeResponse.class, APIs.createHeaders(), new Listener<VerificationCodeResponse>() {
            @Override
            public void onResponse(VerificationCodeResponse response) {
                dismissProgressDialog();
                mVerificationCodeResponse = response;
                mVerificationCodeResponse.phoneNumber = phoneNumber;
                startAuth();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                VerificationCodeResponse response = BaseResponse.parseError(VerificationCodeResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(UserActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @SuppressWarnings("HandlerLeak")
    private void startAuth() {
        stopAuth();
        mAuthLayout.setVisibility(View.VISIBLE);
        mAuthCode.setText(null);
        mAuthCode.requestFocus();
        mStopWatchHandler = new Handler() {
            long startTime = System.currentTimeMillis();
            long max = 3 * 60 * 1000;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime > max) {
                        mVerificationCodeResponse = null;
                        stopAuth();
                    } else {
                        long min = (max - elapsedTime) / (60 * 1000);
                        long sec = (max - elapsedTime) % (60 * 1000) / 1000;
                        mStopWatch.setText(String.format(Locale.getDefault(), "%d:%02d", min, sec));
                        sendMessageDelayed(obtainMessage(0), 1000);
                    }
                }
            }
        };
        mStopWatchHandler.sendEmptyMessage(0);
    }

    private void stopAuth() {
        mAuthLayout.setVisibility(View.GONE);
        mStopWatch.setText(null);
        if (mStopWatchHandler != null) {
            mStopWatchHandler.removeMessages(0);
            mStopWatchHandler.sendEmptyMessage(1);
            mStopWatchHandler = null;
        }
    }

    private void auth() {
        if (mVerificationCodeResponse == null) {
            Toast.makeText(this, "인증번호를 전송해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVerificationCodeResponse.verified) {
            Toast.makeText(this, "이미 인증되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String authCode = mAuthCode.getText().toString();
        if (TextUtils.isEmpty(authCode)) {
            Toast.makeText(this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = APIs.getAuthPath().appendPath(APIs.AUTH_VERIFY).appendQueryParameter(APIs.PARAM_PHONE, mVerificationCodeResponse.phoneNumber).appendQueryParameter(APIs.PARAM_CODE, authCode).appendQueryParameter(APIs.PARAM_TOKEN, mVerificationCodeResponse.token).toString();
        GsonRequest<VerifyResponse> request = new GsonRequest<>(Method.GET, url, VerifyResponse.class, APIs.createHeaders(), new Listener<VerifyResponse>() {
            @Override
            public void onResponse(VerifyResponse response) {
                dismissProgressDialog();
                if (mVerificationCodeResponse != null && response.verified) {
                    mVerificationCodeResponse.verified = true;
                    mVerificationCodeResponse.verification_code = authCode;
                    stopAuth();
                    new MaterialDialog.Builder(UserActivity.this).content("인증되었습니다.").positiveText("확인").show();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                VerifyResponse response = BaseResponse.parseError(VerifyResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(UserActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        });
        request.setTag(mVerifyTag);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void logout() {
        new MaterialDialog.Builder(this).title("로그아웃").content("로그아웃 하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String url = APIs.getAuthPath().appendPath(APIs.AUTH_LOGOUT).toString();
                GsonRequest<String> request = new GsonRequest<>(Method.GET, url, String.class, APIs.createHeadersWithToken(), new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserManager.onLogout();
                        finish();
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissProgressDialog();
                        BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                        if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                            new MaterialDialog.Builder(UserActivity.this).content(response.getError().message).positiveText("확인").show();
                        }
                    }
                });
                VolleySingleton.getInstance(UserActivity.this).addToRequestQueue(request);
            }
        }).show();
    }

    private void save() {
        final UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }

        String oldPassword = mOldPassword.getText().toString();
        String newPassword = mNewPassword.getText().toString();
        String newPasswordConfirm = mNewPasswordConfirm.getText().toString();
        String number = mNumber.getText().toString();
        String email = mEmail.getText().toString();
        boolean agreeRecvSMS = mAgreeRecvSMS.isChecked();
        boolean agreeRecvEmail = mAgreeRecvEmail.isChecked();
        boolean agreeRecvPush = mAgreeRecvPush.isChecked();

        if (!TextUtils.isEmpty(oldPassword)) {
            if (oldPassword.length() < 6) {
                Toast.makeText(this, "비밀번호를 6~50자로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.length() < 6) {
                Toast.makeText(this, "새 비밀번호를 6~50자로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(newPassword, newPasswordConfirm)) {
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.equals(user.getUser().getPhone(), number)) {
            if (mVerificationCodeResponse == null || !mVerificationCodeResponse.verified) {
                Toast.makeText(this, "휴대폰 번호를 인증해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(email) && email.length() > 50) {
            Toast.makeText(this, "이메일을 50자 이내로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        if (!TextUtils.isEmpty(oldPassword)) {
            json.addProperty("password", newPassword);
            json.addProperty("password_old", oldPassword);
        }
        if (!TextUtils.equals(user.getUser().getEmail(), email)) {
            json.addProperty("email", email);
        }
        if (!TextUtils.equals(user.getUser().getPhone(), number)) {
            json.addProperty("phone", mVerificationCodeResponse.phoneNumber);
            json.addProperty("verification_code", mVerificationCodeResponse.verification_code);
            json.addProperty("verification_token", mVerificationCodeResponse.token);
        }
        json.addProperty("agree_recv_sms", agreeRecvSMS);
        json.addProperty("agree_recv_email", agreeRecvEmail);
        json.addProperty("agree_recv_push", agreeRecvPush);

        String url = APIs.getUserPath().appendPath(user.getId()).toString();
        GsonRequest<User> request = new GsonRequest<>(Method.PUT, url, User.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<User>() {
            @Override
            public void onResponse(User response) {
                dismissProgressDialog();
                user.getUser().setEmail(response.getEmail());
                user.getUser().setPhone(response.getPhone());
                user.getUser().setMileage(response.getMileage());
                user.getUser().setMileageGrade(response.getMileageGrade());
                user.getUser().setAgreeRecvSMS(response.isAgreeRecvSMS());
                user.getUser().setAgreeRecvEmail(response.isAgreeRecvEmail());
                user.getUser().setAgreeRecvPush(response.isAgreeRecvPush());
                UserManager.setUser(user);
                setResult(RESULT_OK);
                finish();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(UserActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        stopAuth();
        mVerificationCodeResponse = null;
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll(mVerifyTag);
    }
}