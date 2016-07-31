package kr.co.foodfly.androidapp.app.activity.kickOff;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.common.ViewSupportUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.model.user.VerificationCodeResponse;
import kr.co.foodfly.androidapp.model.user.VerifyResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-06.
 */
public class SignUpActivity extends BaseActivity implements OnClickListener, TextWatcher {

    public static final int REQ_CODE_SIGN_UP = SignUpActivity.class.hashCode() & 0x000000ff;

    public static final String EXTRA_REFERRAL_CODE = "extra_referral_code";

    public static void createInstance(Activity activity) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        activity.startActivityForResult(intent, REQ_CODE_SIGN_UP);
    }

    public static void createInstance(Activity activity, String referralCode) {
        Intent intent = new Intent(activity, SignUpActivity.class);
        intent.putExtra(EXTRA_REFERRAL_CODE, referralCode);
        activity.startActivityForResult(intent, REQ_CODE_SIGN_UP);
    }

    private EditText mId;
    private EditText mPw;
    private EditText mPwConfirm;
    private EditText mName;
    private EditText mEmail;
    private AppCompatSpinner mNumberCode;
    private EditText mNumber;
    private View mSendAuthCode;
    private EditText mAuthCode;
    private View mAuth;
    private EditText mReferralCodeText;
    private EditText mJoinChannel;
    private CheckBox mTerm;
    private CheckBox mAge;
    private CheckBox mMarketing;
    private CheckBox mMarketingEmail;
    private CheckBox mMarketingSMS;
    private CheckBox mMarketingPush;
    private View mSignUp;
    private VerificationCodeResponse mVerificationCodeResponse;
    private Handler mStopWatchHandler;
    private TextView mStopWatch;
    private Object mVerifyTag = new Object();

    private Realm mConnectRealm;
    private Connect mConnect;

    private String mReferralCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mReferralCode = intent.getStringExtra(EXTRA_REFERRAL_CODE);

        mConnectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        mConnectRealm.addChangeListener(mConnectChangeListener);
        mConnect = mConnectRealm.where(Connect.class).findFirst();

        mId = (EditText) findViewById(R.id.sign_up_id);
        mPw = (EditText) findViewById(R.id.sign_up_pw);
        mPwConfirm = (EditText) findViewById(R.id.sign_up_pw_confirm);
        mName = (EditText) findViewById(R.id.sign_up_name);
        mEmail = (EditText) findViewById(R.id.sign_up_email);
        mNumberCode = (AppCompatSpinner) findViewById(R.id.sign_up_number_code);
        mNumberCode.setAdapter(new ArrayAdapter<String>(this, R.layout.row_number_code, R.id.number_code, new String[]{"010", "011", "016", "017", "018", "019"}));
        mNumber = (EditText) findViewById(R.id.sign_up_number);
        mSendAuthCode = findViewById(R.id.sign_up_send_auth_code);
        mAuthCode = (EditText) findViewById(R.id.sign_up_auth_code);
        mAuth = findViewById(R.id.sign_up_auth);
        mReferralCodeText = (EditText) findViewById(R.id.sign_up_referral_code);
        mReferralCodeText.setText(mReferralCode);
        mJoinChannel = (EditText) findViewById(R.id.sign_up_join_channel);
        mTerm = (CheckBox) findViewById(R.id.sign_up_term);
        mAge = (CheckBox) findViewById(R.id.sign_up_age);
        mMarketing = (CheckBox) findViewById(R.id.sign_up_marketing);
        mMarketingEmail = (CheckBox) findViewById(R.id.sign_up_marketing_email);
        mMarketingSMS = (CheckBox) findViewById(R.id.sign_up_marketing_sms);
        mMarketingPush = (CheckBox) findViewById(R.id.sign_up_marketing_push);
        mSignUp = findViewById(R.id.sign_up_button);
        mStopWatch = (TextView) findViewById(R.id.sign_up_auth_code_stop_watch);

        mSendAuthCode.setOnClickListener(this);
        mAuth.setOnClickListener(this);
        mJoinChannel.setOnClickListener(this);
        mMarketing.setOnClickListener(this);
        mMarketingEmail.setOnClickListener(this);
        mMarketingSMS.setOnClickListener(this);
        mMarketingPush.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        mMarketing.setChecked(true);
        mMarketingEmail.setChecked(true);
        mMarketingSMS.setChecked(true);
        mMarketingPush.setChecked(true);

        mNumber.addTextChangedListener(this);

        ViewSupportUtils.disableEditText(mJoinChannel);

        SpannableStringBuilder ssb = new SpannableStringBuilder(mTerm.getText());
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicyActivity.createInstance(SignUpActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
                ds.setUnderlineText(false);
            }
        }, 6, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TermsActivity.createInstance(SignUpActivity.this);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ResourcesCompat.getColor(getResources(), R.color.blue, null));
                ds.setUnderlineText(false);
            }
        }, 1, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mTerm.setMovementMethod(LinkMovementMethod.getInstance());
        mTerm.setText(ssb);
        mTerm.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConnectRealm.removeChangeListener(mConnectChangeListener);
        mConnectRealm.close();
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

    @Override
    public void onClick(View v) {
        if (v == mMarketing) {
            mMarketingEmail.setChecked(mMarketing.isChecked());
            mMarketingSMS.setChecked(mMarketing.isChecked());
            mMarketingPush.setChecked(mMarketing.isChecked());
        } else if (v == mMarketingEmail || v == mMarketingSMS || v == mMarketingPush) {
            mMarketing.setChecked(mMarketingEmail.isChecked() && mMarketingSMS.isChecked() && mMarketingPush.isChecked());
        } else if (v == mSendAuthCode) {
            sendAuthCode();
        } else if (v == mAuth) {
            auth();
        } else if (v == mSignUp) {
            signUp();
        } else if (v == mJoinChannel) {
            selectJoinChannel();
        }
    }

    private RealmChangeListener mConnectChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            mConnect = mConnectRealm.where(Connect.class).findFirst();
            dismissProgressDialog();
        }
    };

    private void sendAuthCode() {
        String numberCode = (String) mNumberCode.getSelectedItem();
        String number = mNumber.getText().toString();
        if (TextUtils.isEmpty(number) || number.length() < 7) {
            Toast.makeText(this, "전화번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String phoneNumber = numberCode + "-" + number.substring(0, number.length() - 4) + "-" + number.substring(number.length() - 4, number.length());
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
                    new MaterialDialog.Builder(SignUpActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @SuppressWarnings("HandlerLeak")
    private void startAuth() {
        stopAuth();
        mAuthCode.setText(null);
        mAuthCode.requestFocus();
        mAuth.setBackgroundResource(R.drawable.bg_button_1_selector);
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
        mAuth.setBackgroundResource(R.drawable.bg_button_2_selector);
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
                    new MaterialDialog.Builder(SignUpActivity.this).content("인증되었습니다.").positiveText("확인").show();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                VerifyResponse response = BaseResponse.parseError(VerifyResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(SignUpActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        });
        request.setTag(mVerifyTag);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void selectJoinChannel() {
        if (mConnect == null) {
            Connect.updateConnect();
            showProgressDialog();
        } else {
            new MaterialDialog.Builder(this).title("가입경로").items(mConnect.getSignUpChannel()).itemsCallback(new ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    mJoinChannel.setText(text);
                }
            }).negativeText("취소").show();
        }
    }

    private void signUp() {
        String userName = mId.getText().toString();
        if (TextUtils.isEmpty(userName) || userName.length() < 4 || userName.length() > 20) {
            Toast.makeText(this, "아이디를 4~20자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = mPw.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6 || password.length() > 50) {
            Toast.makeText(this, "비밀번호를 6~50자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String passwordConfirm = mPwConfirm.getText().toString();
        if (!TextUtils.equals(password, passwordConfirm)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = mName.getText().toString();
        if (TextUtils.isEmpty(name) || name.length() < 1 || name.length() > 50) {
            Toast.makeText(this, "이름을 1~50자로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = mEmail.getText().toString();
        if (!TextUtils.isEmpty(email) && email.length() > 50) {
            Toast.makeText(this, "이메일을 50자 이내로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVerificationCodeResponse == null || !mVerificationCodeResponse.verified) {
            Toast.makeText(this, "휴대폰 번호를 인증해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = mVerificationCodeResponse.phoneNumber;
        String joinChannel = mJoinChannel.getText().toString();
        if (TextUtils.isEmpty(joinChannel)) {
            Toast.makeText(this, "가입 경로를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean agreeGeneralPolicy = mTerm.isChecked();
        boolean agreePrivacyPolicy = mTerm.isChecked();
        if (!agreeGeneralPolicy || !agreePrivacyPolicy) {
            Toast.makeText(this, "이용약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean age = mAge.isChecked();
        if (!age) {
            Toast.makeText(this, "푸드플라이 서비스는 만 14세 이상만 가입이 가능합니다. 만 14세 이상이신 경우 확인에 체크해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean agreeRecvEmail = mMarketingEmail.isChecked();
        boolean agreeRecvSMS = mMarketingSMS.isChecked();
        boolean agreeRecvPush = mMarketingPush.isChecked();
        String referralCode = mReferralCodeText.getText().toString();

        JsonObject user = new JsonObject();
        user.addProperty("username", userName);
        user.addProperty("password", password);
        user.addProperty("name", name);
        user.addProperty("email", email);
        user.addProperty("phone", phone);
        user.addProperty("verification_code", mVerificationCodeResponse.verification_code);
        user.addProperty("verification_token", mVerificationCodeResponse.token);
        user.addProperty("join_channel", joinChannel);
        user.addProperty("agree_general_policy", agreeGeneralPolicy);
        user.addProperty("agree_privacy_policy", agreePrivacyPolicy);
        user.addProperty("agree_recv_email", agreeRecvEmail);
        user.addProperty("agree_recv_sms", agreeRecvSMS);
        user.addProperty("agree_recv_push", agreeRecvPush);
        if (!TextUtils.isEmpty(referralCode)) user.addProperty("referral_code", referralCode);

        String url = APIs.getAuthPath().appendPath(APIs.AUTH_SIGN_UP).toString();
        GsonRequest<UserResponse> request = new GsonRequest<>(Method.POST, url, UserResponse.class, APIs.createHeaders(), RealmUtils.REALM_GSON.toJson(user).getBytes(), new Listener<UserResponse>() {
            @Override
            public void onResponse(UserResponse response) {
                dismissProgressDialog();
                response.getUser().setReferralCode(null);
                response.setId(response.getUser().getId());
                UserManager.setUser(response);
                setResult(RESULT_OK);
                finish();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                UserResponse response = BaseResponse.parseError(UserResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(SignUpActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }
}