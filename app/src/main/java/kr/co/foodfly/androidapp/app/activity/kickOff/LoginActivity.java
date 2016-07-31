package kr.co.foodfly.androidapp.app.activity.kickOff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.realm.Realm;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.gcm.GcmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-06.
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private ImageView mBgImage;
    private TextInputLayout mId;
    private TextInputLayout mPassword;
    private View mLogin;
    private View mFindId;
    private View mFindPw;
    private View mSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBgImage = (ImageView) findViewById(R.id.login_bg_image);
        mId = (TextInputLayout) findViewById(R.id.login_id);
        mPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLogin = findViewById(R.id.login_button);
        mFindId = findViewById(R.id.login_find_id);
        mFindPw = findViewById(R.id.login_find_pw);
        mSignUp = findViewById(R.id.login_sign_up_button);

        mLogin.setOnClickListener(this);
        mFindId.setOnClickListener(this);
        mFindPw.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        Realm realm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        Connect connect = realm.where(Connect.class).findFirst();
        if (connect != null) {
            VolleySingleton.getInstance(this).getImageLoader().get(connect.getPreference().getLoginBackground(), new ImageListener() {
                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    mBgImage.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }, CommonUtils.getScreenWidth() / 2, CommonUtils.getScreenHeight() / 2, ScaleType.CENTER_CROP);
        }
        realm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.REQ_CODE_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mLogin) {
            login();
        } else if (v == mFindId) {
            findId();
        } else if (v == mFindPw) {
            findPw();
        } else if (v == mSignUp) {
            signUp();
        }
    }

    private void login() {
        String id = mId.getEditText().getText().toString();
        String pw = mPassword.getEditText().getText().toString();
        if (TextUtils.isEmpty(id)) {
            mId.setError("아이디를 입력해주세요.");
            return;
        }
        if (TextUtils.isEmpty(pw)) {
            mPassword.setError("비밀번호를 입력해주세요.");
            return;
        }

        JsonObject login = new JsonObject();
        login.addProperty("username", id);
        login.addProperty("password", pw);

        String url = APIs.getAuthPath().appendPath(APIs.AUTH_LOGIN).toString();
        GsonRequest<UserResponse> request = new GsonRequest<>(Method.POST, url, UserResponse.class, APIs.createHeaders(), RealmUtils.REALM_GSON.toJson(login).getBytes(), new Listener<UserResponse>() {
            @Override
            public void onResponse(final UserResponse response) {
                dismissProgressDialog();
                response.getUser().setReferralCode(null);
                response.setId(response.getUser().getId());
                UserManager.setUser(response);

                Realm realm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
                ArrayList<MapAddress> addressList = new ArrayList<>();
                addressList.addAll(realm.copyFromRealm(realm.where(MapAddress.class).equalTo("mDefault", true).findAll()));
                realm.close();
                if (addressList.size() > 0 && response.getUser().getAddress() != null) {
                    final MapAddress address1 = response.getUser().getAddress();
                    final MapAddress address2 = addressList.get(0);
                    new MaterialDialog.Builder(LoginActivity.this).title("주소 선택").items(address1.getDisplayContent(), address2.getDisplayContent()).itemsCallback(new ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            MapAddress address;
                            if (which == 0) {
                                address = address1;
                            } else {
                                address = address2;
                            }
                            setAddress(response, address);
                        }
                    }).cancelable(false).canceledOnTouchOutside(false).show();
                } else if (addressList.size() > 0) {
                    MapAddress address = addressList.get(0);
                    setAddress(response, address);
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                UserResponse response = BaseResponse.parseError(UserResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(LoginActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void setAddress(final UserResponse user, MapAddress address) {
        JsonObject json = new JsonObject();
        json.addProperty("lat", address.getLat());
        json.addProperty("lon", address.getLon());
        json.addProperty("is_default", true);
        json.addProperty("detail_address", address.getDetailAddress());
        json.addProperty("formatted_address", address.getFormattedAddress());
        json.addProperty("street_address", address.getStreetAddress());
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ADDRESS).toString();
        GsonRequest<MapAddress> request = new GsonRequest<>(Method.POST, url, MapAddress.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<MapAddress>() {
            @Override
            public void onResponse(MapAddress response) {
                dismissProgressDialog();
                user.getUser().setAddress(response);
                UserManager.setUser(user);
                Connect.updateArea();
                GcmUtils.register(LoginActivity.this);
                setResult(RESULT_OK);
                finish();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                MapAddress response = BaseResponse.parseError(MapAddress.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(LoginActivity.this).content(response.getError().message).positiveText("확인").show();
                }
                setResult(RESULT_OK);
                finish();
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void findId() {
        CommonUtils.openBrowser(this, "http://m.foodfly.co.kr/home/findid");
    }

    private void findPw() {
        CommonUtils.openBrowser(this, "http://m.foodfly.co.kr/home/findpw");
    }

    private void signUp() {
        SignUpActivity.createInstance(this);
    }
}