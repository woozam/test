package com.foodfly.gcm.app.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.kickOff.PrivacyPolicyActivity;
import com.foodfly.gcm.app.activity.kickOff.TermsActivity;
import com.foodfly.gcm.app.activity.main.MainActivity;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.gson.JsonObject;

import io.realm.Realm;
import io.realm.RealmChangeListener;

/**
 * Created by woozam on 2016-07-10.
 */
public class CommonFooter extends LinearLayout implements RealmChangeListener<Realm> {

    private Realm mUserRealm;

    public CommonFooter(Context context) {
        super(context);
        initialize();
    }

    public CommonFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CommonFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUserRealm = Realm.getInstance(RealmUtils.CONFIG_USER);
        mUserRealm.addChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mUserRealm.removeChangeListener(this);
        mUserRealm.close();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.footer_common, this, true);

        checkUser();
    }

    private void checkUser() {
        TextView textView = (TextView) findViewById(R.id.terms);
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            SpannableStringBuilder ssb = new SpannableStringBuilder("이용약관 | 개인정보취급방침");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PrivacyPolicyActivity.createInstance(getContext());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(0xff9a9a9a);
                    ds.setUnderlineText(false);
                }
            }, 7, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    TermsActivity.createInstance(getContext());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(0xff9a9a9a);
                    ds.setUnderlineText(false);
                }
            }, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb);
            textView.setHighlightColor(Color.TRANSPARENT);
        } else {
            SpannableStringBuilder ssb = new SpannableStringBuilder("이용약관 | 개인정보취급방침 | 회원탈퇴");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PrivacyPolicyActivity.createInstance(getContext());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(0xff9a9a9a);
                    ds.setUnderlineText(false);
                }
            }, 7, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    TermsActivity.createInstance(getContext());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(0xff9a9a9a);
                    ds.setUnderlineText(false);
                }
            }, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    new MaterialDialog.Builder(getContext()).title("회원탈퇴").content("정말 탈퇴하시겠습니까?\n탈퇴 후 재가입 시, 제한을 받을 수 있습니다.").positiveText("탈퇴").negativeText("취소").onPositive(new SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            final Context context = getContext();
                            UserResponse user = UserManager.fetchUser();
                            String url = APIs.getAuthPath().appendPath(APIs.AUTH_SIGN_OUT).toString();
                            JsonObject json = new JsonObject();
                            json.addProperty("user_id", user.getId());
                            GsonRequest<BaseResponse> request = new GsonRequest<>(Method.POST, url, BaseResponse.class, APIs.createHeadersWithToken(), json.toString().getBytes(), new Listener<BaseResponse>() {
                                @Override
                                public void onResponse(BaseResponse response) {
                                    if (context instanceof BaseActivity) {
                                        ((BaseActivity) context).dismissProgressDialog();
                                    }
                                    UserManager.onLogout();
                                    MainActivity.clearAndcreateInstance(context);
                                }
                            }, new ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (context instanceof BaseActivity) {
                                        ((BaseActivity) context).dismissProgressDialog();
                                    }
                                    BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                                        new MaterialDialog.Builder(context).content(response.getError().message).positiveText("확인").show();
                                    }
                                }
                            }, RealmUtils.REALM_GSON);
                            VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
                            if (context instanceof BaseActivity) {
                                ((BaseActivity) context).showProgressDialog();
                            }
                        }
                    }).show();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(0xff9a9a9a);
                    ds.setUnderlineText(false);
                }
            }, 18, 22, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb);
            textView.setHighlightColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onChange(Realm element) {
        checkUser();
    }
}