package com.foodfly.gcm.app.activity.setting;

import android.Manifest.permission;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.BaseActivity;
import com.foodfly.gcm.app.activity.etc.ImageViewerActivity;
import com.foodfly.gcm.app.view.MapWrapper;
import com.foodfly.gcm.app.view.MapWrapper.OnTouchUpListener;
import com.foodfly.gcm.app.view.recyclerView.RecyclerViewEmptySupport;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.ViewSupportUtils;
import com.foodfly.gcm.data.RealmUtils;
import com.foodfly.gcm.model.BaseResponse;
import com.foodfly.gcm.model.connect.Connect;
import com.foodfly.gcm.model.restaurant.Cart;
import com.foodfly.gcm.model.restaurant.Restaurant;
import com.foodfly.gcm.model.user.MapAddress;
import com.foodfly.gcm.model.user.UserManager;
import com.foodfly.gcm.model.user.UserResponse;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class AddressActivity extends BaseActivity implements OnMapReadyCallback, OnCameraChangeListener, OnClickListener, OnEditorActionListener, OnTouchListener, OnTouchUpListener {

    private static final int REQ_CODE_MY_LOCATION = 0x0010;
    public static final int REQ_CODE_ADDRESS = AddressActivity.class.hashCode() & 0x000000ff;

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, AddressActivity.class);
        context.startActivity(intent);
    }

    public static void createInstance(Activity activity) {
        Intent intent = new Intent(activity, AddressActivity.class);
        activity.startActivityForResult(intent, REQ_CODE_ADDRESS);
    }

    private MapWrapper mMapWrapper;
    private GoogleMap mMap;
    private View mMarker;
    private View mDetailSearchLayout;
    private View mDetailContentIcon;
    private TextView mDetailContent;
    private View mDetailCancel;
    private EditText mDetailSearch;
    private Button mDetailButton;
    private View mSearchLayout;
    private EditText mSearch;
    private View mMyLocation;
    private View mMapSearchResultLayout;
    private ArrayList<MapAddress> mMapSearchResultList;
    private MapSearchResultAdapter mMapSearchResultAdapter;
    private RecyclerViewEmptySupport mMapSearchResultRecyclerView;
    private View mMapSearchResultEmptyView;
    private boolean mShowDetail = false;
    private boolean mShowAnimation = false;
    private MapAddress mAddress;
    private View mSearchGuide;
    private double mLastLat;
    private double mLastLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.address_map_fragment);
        mapFragment.getMapAsync(this);

        mMapWrapper = (MapWrapper) findViewById(R.id.address_map_wrapper);
        mMarker = findViewById(R.id.address_map_marker);
        mDetailSearchLayout = findViewById(R.id.address_detail_search_layout);
        mDetailContentIcon = findViewById(R.id.address_detail_content_icon);
        mDetailContent = (TextView) findViewById(R.id.address_detail_content);
        mDetailCancel = findViewById(R.id.address_detail_cancel);
        mDetailSearch = (EditText) findViewById(R.id.address_detail_search);
        mDetailButton = (Button) findViewById(R.id.address_detail_button);
        mSearchLayout = findViewById(R.id.address_map_search_layout);
        mSearch = (EditText) findViewById(R.id.address_map_search_edit_text);
        mMyLocation = findViewById(R.id.address_map_my_location);
        mMapSearchResultLayout = findViewById(R.id.address_map_search_result_layout);
        mMapSearchResultRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.address_map_search_result_recycler_view);
        mMapSearchResultEmptyView = findViewById(R.id.address_map_search_result_empty_view);
        mSearchGuide = findViewById(R.id.address_map_search_result_guide);
        mSearch.clearFocus();
        mDetailSearch.clearFocus();

        mSearch.setOnEditorActionListener(this);
        mMapSearchResultList = new ArrayList<>();
        mMapSearchResultAdapter = new MapSearchResultAdapter();
        mMapSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMapSearchResultRecyclerView.setAdapter(mMapSearchResultAdapter);
        mMapSearchResultRecyclerView.setEmptyView(mMapSearchResultEmptyView);

        mMapWrapper.setOnTouchUpListener(this);
        mMapSearchResultLayout.setOnTouchListener(this);
        mDetailCancel.setOnClickListener(this);
        mDetailButton.setOnClickListener(this);
        mMyLocation.setOnClickListener(this);
        mSearchGuide.setOnClickListener(this);
        mDetailCancel.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraChangeListener(this);
        mAddress = MapAddress.getAddress();
        LatLng defaultLatLng = new LatLng(mAddress.getLat(), mAddress.getLon());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 16));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (!mMapWrapper.isTouchDown()) {
            double latitude = cameraPosition.target.latitude;
            double longitude = cameraPosition.target.longitude;
            getAddress(latitude, longitude);
        }
    }

    private void getAddress(double lat, double lon) {
        if (lat != mLastLat || lon != mLastLon) {
            mLastLat = lat;
            mLastLon = lon;
            Log.d("map", lat + " : " + lon);
            String url = APIs.getAddressPath().appendQueryParameter(APIs.PARAM_LAT, String.valueOf(lat)).appendQueryParameter(APIs.PARAM_LON, String.valueOf(lon)).toString();
            GsonRequest<MapAddress[]> request = new GsonRequest<>(Method.GET, url, new TypeToken<MapAddress[]>() {
            }.getType(), APIs.createHeaders(), new Listener<MapAddress[]>() {
                @Override
                public void onResponse(MapAddress[] response) {
                    if (response != null && response.length > 0) {
                        setAddress(response[0]);
                    } else {
                        setAddress(null);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }
    }

    @Override
    public void onBackPressed() {
        if (mShowAnimation) {
            return;
        } else if (mMapSearchResultLayout.getVisibility() == View.VISIBLE) {
            mMapSearchResultLayout.setVisibility(View.GONE);
        } else if (mShowDetail) {
            hideDetail();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mMapSearchResultLayout.getVisibility() == View.VISIBLE) {
                mMapSearchResultLayout.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mDetailButton) {
            if (mShowDetail) {
                done();
            } else if (mAddress != null) {
                if (mAddress.isServiceArea()) {
                    showDetail();
                } else {
                    new MaterialDialog.Builder(this).content("해당 지역은 서비스 준비중입니다. 계속 진행하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showDetail();
                        }
                    }).show();
                }
            }
        } else if (v == mDetailCancel) {
            hideDetail();
        } else if (v == mMyLocation) {
            if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, REQ_CODE_MY_LOCATION);
            } else {
                moveToMyLocation();
            }
        } else if (v == mSearchGuide) {
            Realm realm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
            Connect connect = realm.where(Connect.class).findFirst();
            if (connect != null) {
                ImageViewerActivity.createInstance(this, connect.getPreference().getServiceArea());
            }
            realm.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_MY_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                moveToMyLocation();
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void moveToMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            List<String> providers = locationManager.getProviders(true);
            for (String each : providers) {
                if (!TextUtils.equals(each, provider)) {
                    locationManager.requestSingleUpdate(each, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, null);
                }
            }
        }
    }

    private void done() {
        final UserResponse user = UserManager.fetchUser();
        Realm cartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
        Cart cart = cartRealm.where(Cart.class).findFirst();
        if (cart != null) {
            cart = cartRealm.copyFromRealm(cart);
            loadRestaurant(user, cart.getRestaurant(), mAddress);
        } else {
            save(user);
        }
        cartRealm.close();
    }

    private void loadRestaurant(final UserResponse user, final Restaurant restaurant, final MapAddress address) {
        Uri.Builder builder = APIs.getRestaurantPath().appendPath(restaurant.getId()).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(address.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(address.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(address.getAreaCode()));
        if (user != null) {
            builder.appendQueryParameter(APIs.PARAM_USER_ID, user.getId());
        }
        String url = builder.toString();
        GsonRequest<Restaurant> request = new GsonRequest<>(Method.GET, url, Restaurant.class, APIs.createHeadersWithToken(), new Listener<Restaurant>() {
            @Override
            public void onResponse(Restaurant response) {
                dismissProgressDialog();
                if (response.getDistance() > response.getAvailableDistance()) {
                    new MaterialDialog.Builder(AddressActivity.this).content(String.format(Locale.getDefault(), "변경하시려는 배송지에서 배달 불가능한 %s 매장의 메뉴가 장바구니에서 삭제됩니다.\n새 배송지로 변경하시겠습니까?", restaurant.getName())).positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Realm cartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
                            cartRealm.beginTransaction();
                            cartRealm.deleteAll();
                            cartRealm.commitTransaction();
                            cartRealm.close();
                            save(user);
                        }
                    }).show();
                } else {
                    save(user);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(AddressActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(AddressActivity.this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void save(final UserResponse user) {
        if (user == null) {
            mAddress.setDetailAddress(mDetailSearch.getText().toString());
            mAddress.setDefault(true);
            Realm realm = Realm.getInstance(RealmUtils.CONFIG_ADDRESS);
            realm.beginTransaction();
            realm.deleteAll();
            realm.copyToRealm(mAddress);
            realm.commitTransaction();
            realm.close();
            Connect.updateConnect();
            setResult(RESULT_OK);
            finish();
        } else {
            JsonObject address = new JsonObject();
            address.addProperty("lat", mAddress.getLat());
            address.addProperty("lon", mAddress.getLon());
            address.addProperty("is_default", true);
            address.addProperty("detail_address", mDetailSearch.getText().toString());
            address.addProperty("formatted_address", mAddress.getFormattedAddress());
            address.addProperty("street_address", mAddress.getStreetAddress());
            String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ADDRESS).toString();
            GsonRequest<MapAddress> request = new GsonRequest<>(Method.POST, url, MapAddress.class, APIs.createHeadersWithToken(), RealmUtils.REALM_GSON.toJson(address).getBytes(), new Listener<MapAddress>() {
                @Override
                public void onResponse(MapAddress response) {
                    dismissProgressDialog();
                    user.getUser().setAddress(response);
                    UserManager.setUser(user);
                    Connect.updateConnect();
                    setResult(RESULT_OK);
                    finish();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                    MapAddress response = BaseResponse.parseError(MapAddress.class, error);
                    if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                        new MaterialDialog.Builder(AddressActivity.this).content(response.getError().message).positiveText("확인").show();
                    }
                }
            }, RealmUtils.REALM_GSON);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    private void showDetail() {
        if (!mShowDetail && !mShowAnimation) {
            mShowAnimation = true;

            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mDetailSearch.setText(null);
            mMyLocation.setVisibility(View.GONE);
            mSearchLayout.setVisibility(View.GONE);
            ViewSupportUtils.hideSoftInput(mSearch);

            mDetailSearchLayout.clearAnimation();
            mDetailSearchLayout.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            final int dst = mDetailSearchLayout.getMeasuredHeight() + CommonUtils.convertDipToPx(this, 16);
            ValueAnimator slideAnimator = ValueAnimator.ofInt(0, dst).setDuration(250);
            slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDetailSearchLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mDetailSearchLayout.requestLayout();
                    if ((Integer) animation.getAnimatedValue() == dst) {
                        mShowDetail = true;
                        mShowAnimation = false;
                        mDetailCancel.setVisibility(View.VISIBLE);
                        mDetailButton.setText("배송지 설정 완료");
                    }
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    private void hideDetail() {
        if (mShowDetail && !mShowAnimation) {
            mShowAnimation = true;

            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMyLocation.setVisibility(View.VISIBLE);
            mSearchLayout.setVisibility(View.VISIBLE);
            ViewSupportUtils.hideSoftInput(mDetailContent);

            mDetailSearchLayout.clearAnimation();
            final int dst = 0;
            ValueAnimator slideAnimator = ValueAnimator.ofInt(mDetailSearchLayout.getMeasuredHeight(), 0).setDuration(250);
            slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDetailSearchLayout.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    mDetailSearchLayout.requestLayout();
                    if ((Integer) animation.getAnimatedValue() == dst) {
                        mShowDetail = false;
                        mShowAnimation = false;
                        mDetailCancel.setVisibility(View.GONE);
                        mDetailButton.setText("위 배송지로 설정");
                    }
                }
            });

            AnimatorSet set = new AnimatorSet();
            set.play(slideAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    private void setAddress(MapAddress address) {
        mAddress = address;
        if (address == null) {
            mMarker.setEnabled(false);
            mDetailContentIcon.setEnabled(false);
            mDetailContent.setText(null);
        } else {
            mMarker.setEnabled(address.isServiceArea());
            mDetailContentIcon.setEnabled(address.isServiceArea());
            if (TextUtils.isEmpty(address.getFormattedAddress())) {
                mDetailContent.setText(address.getStreetAddress());
            } else {
                mDetailContent.setText(address.getFormattedAddress());
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == mSearch) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString();
                if (TextUtils.isEmpty(query)) {
                    return false;
                }
                try {
                    URL tmp = new URL("https://api.foodfly.co.kr");
                    String url = new Uri.Builder().scheme(tmp.getProtocol()).authority(tmp.getAuthority()).path(tmp.getPath()).appendPath("v1").appendPath("address").appendQueryParameter(APIs.PARAM_KEYWORD, String.valueOf(query)).toString();
                    GsonRequest<MapAddress[]> request = new GsonRequest<>(Method.GET, url, new TypeToken<MapAddress[]>() {
                    }.getType(), APIs.createHeaders(), new Listener<MapAddress[]>() {
                        @Override
                        public void onResponse(MapAddress[] response) {
                            mMapSearchResultLayout.setVisibility(View.VISIBLE);
                            mMapSearchResultList.clear();
                            Collections.addAll(mMapSearchResultList, response);
                            mMapSearchResultAdapter.notifyDataSetChanged();
                            mMapSearchResultRecyclerView.scrollToPosition(0);
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    VolleySingleton.getInstance(this).addToRequestQueue(request);
                    ViewSupportUtils.hideSoftInput(v);
                    v.clearFocus();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    public void onTouchUp() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        double latitude = mMap.getCameraPosition().target.latitude;
                        double longitude = mMap.getCameraPosition().target.longitude;
                        getAddress(latitude, longitude);
                        mTimer = null;
                    }
                });
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 1000);
    }

    private class MapSearchResultAdapter extends Adapter<MapSearchResultViewHolder> {

        @Override
        public MapSearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MapSearchResultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_map_search_result, parent, false));
        }

        @Override
        public void onBindViewHolder(final MapSearchResultViewHolder holder, int position) {
            holder.setAddress(getItem(position));
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem(holder.getAdapterPosition()).isServiceArea()) {
                        double latitude = getItem(holder.getAdapterPosition()).getLat();
                        double longitude = getItem(holder.getAdapterPosition()).getLon();
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMapSearchResultLayout.setVisibility(View.GONE);
                    } else {
                        new MaterialDialog.Builder(v.getContext()).title("서비스 지역").content("해당 지역은 서비스 준비중입니다.").show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mMapSearchResultList.size();
        }

        public MapAddress getItem(int position) {
            return mMapSearchResultList.get(position);
        }
    }

    private static class MapSearchResultViewHolder extends ViewHolder {

        private View mIcon;
        private TextView mFormattedAddress;
        private TextView mStreetAddress;

        public MapSearchResultViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.map_search_result_icon);
            mFormattedAddress = (TextView) itemView.findViewById(R.id.map_search_result_formatted_address);
            mStreetAddress = (TextView) itemView.findViewById(R.id.map_search_result_street_address);
        }

        public void setAddress(MapAddress address) {
            mIcon.setEnabled(address.isServiceArea());
            mFormattedAddress.setText(address.getFormattedAddress());
            mStreetAddress.setText(address.getStreetAddress());
            if (mStreetAddress.length() == 0) {
                mStreetAddress.setVisibility(View.GONE);
            }
        }
    }
}