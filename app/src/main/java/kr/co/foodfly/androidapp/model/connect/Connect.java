package kr.co.foodfly.androidapp.model.connect;

import android.os.Build;
import android.os.Build.VERSION;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import kr.co.foodfly.androidapp.Application;
import kr.co.foodfly.androidapp.BuildConfig;
import kr.co.foodfly.androidapp.common.PreferenceUtils;
import kr.co.foodfly.androidapp.data.RealmString;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-15.
 */
public class Connect extends RealmObject {

    @PrimaryKey
    private String mPrimaryKey;
    @SerializedName("popups")
    private RealmList<Popup> mPopups;
    @SerializedName("banners")
    private RealmList<Banner> mBanners;
    @SerializedName("categories")
    private RealmList<ConnectCategory> mCategories;
    @SerializedName("filters")
    private Filters mFilters;
    @SerializedName("defaults")
    private Defaults mDefaults;
    @SerializedName("recommended_keywords")
    private RealmList<RealmString> mRecommendedKeywords;
    @SerializedName("preference")
    private Preference mPreference;
    @SerializedName("martfly")
    private Martfly mMartfly;
    @SerializedName("need_force_update")
    private boolean mNeedForceUpdate;
    @SerializedName("need_open_ffweb")
    private boolean mNeedOpenFFWeb;
    @SerializedName("area")
    private Area mArea;
    @SerializedName("custom_context")
    private String mCustomContext;
    @SerializedName("mileage_rules")
    private RealmList<MileageRule> mMileageRules;
    @SerializedName("signup_channel")
    private RealmList<RealmString> mSignUpChannel;
    @SerializedName("timestamp")
    private Date mTimestamp;

    public Connect() {
        mPrimaryKey = "CONNECT";
        mPopups = new RealmList<>();
        mBanners = new RealmList<>();
        mRecommendedKeywords = new RealmList<>();
        mMileageRules = new RealmList<>();
        mSignUpChannel = new RealmList<>();
    }

    public String getPrimaryKey() {
        return mPrimaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        mPrimaryKey = primaryKey;
    }

    public RealmList<Popup> getPopups() {
        return mPopups;
    }

    public void setPopups(RealmList<Popup> popups) {
        mPopups = popups;
    }

    public RealmList<Banner> getBanners() {
        return mBanners;
    }

    public void setBanners(RealmList<Banner> banners) {
        mBanners = banners;
    }

    public RealmList<ConnectCategory> getCategories() {
        return mCategories;
    }

    public void setCategories(RealmList<ConnectCategory> categories) {
        mCategories = categories;
    }

    public Filters getFilters() {
        return mFilters;
    }

    public void setFilters(Filters filters) {
        mFilters = filters;
    }

    public Defaults getDefaults() {
        return mDefaults;
    }

    public void setDefaults(Defaults defaults) {
        mDefaults = defaults;
    }

    public RealmList<RealmString> getRecommendedKeywords() {
        return mRecommendedKeywords;
    }

    public void setRecommendedKeywords(RealmList<RealmString> recommendedKeywords) {
        mRecommendedKeywords = recommendedKeywords;
    }

    public Preference getPreference() {
        return mPreference;
    }

    public void setPreference(Preference preference) {
        mPreference = preference;
    }

    public Martfly getMartfly() {
        return mMartfly;
    }

    public void setMartfly(Martfly martfly) {
        mMartfly = martfly;
    }

    public boolean isNeedForceUpdate() {
        return mNeedForceUpdate;
    }

    public void setNeedForceUpdate(boolean needForceUpdate) {
        mNeedForceUpdate = needForceUpdate;
    }

    public boolean isNeedOpenFFWeb() {
        return mNeedOpenFFWeb;
    }

    public void setNeedOpenFFWeb(boolean needOpenFFWeb) {
        mNeedOpenFFWeb = needOpenFFWeb;
    }

    public Area getArea() {
        return mArea;
    }

    public void setArea(Area area) {
        mArea = area;
    }

    public String getCustomContext() {
        return mCustomContext;
    }

    public void setCustomContext(String customContext) {
        mCustomContext = customContext;
    }

    public RealmList<MileageRule> getMileageRules() {
        return mMileageRules;
    }

    public void setMileageRules(RealmList<MileageRule> mileageRules) {
        mMileageRules = mileageRules;
    }

    public MileageRule getMileageRule(int grade) {
        for (MileageRule mileageRule : mMileageRules) {
            if (mileageRule.getGrade() == grade) {
                return mileageRule;
            }
        }
        return null;
    }

    public RealmList<RealmString> getSignUpChannel() {
        return mSignUpChannel;
    }

    public void setSignUpChannel(RealmList<RealmString> signUpChannel) {
        mSignUpChannel = signUpChannel;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    public static void updateConnect() {
        final MapAddress address = MapAddress.getAddress();
        JsonObject json = new JsonObject();
        json.addProperty("platform", "android");
        json.addProperty("device_type", Build.MODEL);
        json.addProperty("device_version", VERSION.RELEASE);
        json.addProperty("app_version", BuildConfig.VERSION_NAME);
        json.addProperty("app_version_code", BuildConfig.VERSION_CODE);
        json.addProperty("lat", address.getLat());
        json.addProperty("lon", address.getLon());
        json.addProperty("areacode", address.getAreaCode());
        String url = APIs.getConnectPath().toString();
        GsonRequest<Connect> request = new GsonRequest<>(Method.POST, url, Connect.class, APIs.createHeaders(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<Connect>() {
            @Override
            public void onResponse(Connect response) {
                Realm realm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
                if (realm.where(Connect.class).findFirst() == null) {
                    PreferenceUtils.setValue(PreferenceUtils.KEY_RESTAURANT_FILTER_ORDER, response.getDefaults().getOrders().getDefaultOrderValue(address.getAreaCode()));
                    PreferenceUtils.setValue(PreferenceUtils.KEY_RESTAURANT_FILTER_COUPON, response.getDefaults().getCoupons().getDefaultCouponValue());
                }
                realm.beginTransaction();
                realm.deleteAll();
                realm.copyToRealmOrUpdate(response);
                realm.commitTransaction();
                realm.close();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(Application.getContext()).addToRequestQueue(request);
    }

    public static void updateArea() {
        if (true) {
            updateConnect();
            return;
        }
        final MapAddress address = MapAddress.getAddress();
        JsonObject json = new JsonObject();
        json.addProperty("lat", address.getLat());
        json.addProperty("lon", address.getLon());
        json.addProperty("areacode", address.getAreaCode());
        String url = APIs.getConnectPath().appendPath(APIs.CONNECT_AREA).toString();
        GsonRequest<Connect> request = new GsonRequest<>(Method.POST, url, Connect.class, APIs.createHeaders(), RealmUtils.REALM_GSON.toJson(json).getBytes(), new Listener<Connect>() {
            @Override
            public void onResponse(Connect response) {
                Realm realm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
                realm.beginTransaction();
                Connect connect = realm.where(Connect.class).findFirst();
                if (connect != null) {
                    connect = realm.copyFromRealm(connect);
                    connect.setArea(response.getArea());
                    connect.setBanners(response.getBanners());
                    connect.setRecommendedKeywords(response.getRecommendedKeywords());
                    realm.copyToRealmOrUpdate(connect);
                }
                realm.commitTransaction();
                realm.close();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(Application.getContext()).addToRequestQueue(request);
    }
}