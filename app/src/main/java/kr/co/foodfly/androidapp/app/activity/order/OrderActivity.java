package kr.co.foodfly.androidapp.app.activity.order;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.user.CouponActivity;
import kr.co.foodfly.androidapp.app.dialog.BoundTimePickerDialog;
import kr.co.foodfly.androidapp.common.TimeUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.common.ViewSupportUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.connect.MileageRule;
import kr.co.foodfly.androidapp.model.order.Order;
import kr.co.foodfly.androidapp.model.order.Payment;
import kr.co.foodfly.androidapp.model.restaurant.Cart;
import kr.co.foodfly.androidapp.model.restaurant.CartMenu;
import kr.co.foodfly.androidapp.model.restaurant.MenuOptionItem;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;
import kr.co.foodfly.androidapp.model.user.Coupon;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-27.
 */
public class OrderActivity extends BaseActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, OrderActivity.class);
        context.startActivity(intent);
    }

    private View mCartView;
    private EditText mRecipient;
    private EditText mPhoneNumber;
    private EditText mAddress;
    private EditText mDetailAddress;
    private View mDeliveryTypeFoodfly;
    private View mDeliveryTypeTakeout;
    private View mDeliveryTimeNow;
    private View mDeliveryTimeReserve;
    private Button mDeliveryReservedTime;
    private EditText mDeliveryMessage;
    private TextView mPrice;
    private TextView mPriceTip;
    private TextView mPriceTipExtra;
    private TextView mPriceEvent;
    private TextView mPriceCoupon;
    private TextView mPriceMileage;
    private EditText mPriceMileageAmount;
    private View mCouponButton;
    private View mMileageButton;
    private View mCouponMileageDisable;
    private TextView mPriceTotal;
    private View mPaymentTypeOfflineCard;
    private View mPaymentTypeOfflineCash;
    private View mPaymentTypeOnlineCreditCart;
    private View mPaymentTypeOnlineTransfer;
    private View mDone;

    private Realm mCartRealm;
    private Cart mCart;
    private Connect mConnect;
    private Restaurant mRestaurant;

    private int mTotal;
    private int mMenuTotal;
    private int mDeliveryTip;
    private int mDiscountPriceEvent;
    private int mDiscountPriceMenu;
    private int mDiscountPriceDelivery;
    private int mDiscountPriceCoupon;
    private int mDiscountPriceMileage;

    private Coupon mCoupon;
    private int mMileage;

    private Calendar mReservedCalendar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCartView = findViewById(R.id.order_cart);
        mRecipient = (EditText) findViewById(R.id.order_recipient_text);
        mPhoneNumber = (EditText) findViewById(R.id.order_phone_number_text);
        mAddress = (EditText) findViewById(R.id.order_address_text);
        mDetailAddress = (EditText) findViewById(R.id.order_address_detail_text);
        mDeliveryTypeFoodfly = findViewById(R.id.order_delivery_type_foodfly);
        mDeliveryTypeTakeout = findViewById(R.id.order_delivery_type_takeout);
        mDeliveryTimeNow = findViewById(R.id.order_delivery_time_now);
        mDeliveryTimeReserve = findViewById(R.id.order_delivery_time_reserve);
        mDeliveryReservedTime = (Button) findViewById(R.id.order_delivery_reserved_time);
        mDeliveryMessage = (EditText) findViewById(R.id.order_delivery_message_text);
        mPrice = (TextView) findViewById(R.id.order_price_text);
        mPriceTip = (TextView) findViewById(R.id.order_tip_text);
        mPriceTipExtra = (TextView) findViewById(R.id.order_tip_extra);
        mPriceEvent = (TextView) findViewById(R.id.order_event_text);
        mPriceCoupon = (TextView) findViewById(R.id.order_coupon_text);
        mPriceMileage = (TextView) findViewById(R.id.order_mileage_text);
        mPriceMileageAmount = (EditText) findViewById(R.id.order_mileage_amount);
        mCouponButton = findViewById(R.id.order_coupon_button);
        mMileageButton = findViewById(R.id.order_mileage_button);
        mCouponMileageDisable = findViewById(R.id.order_coupon_mileage_disable);
        mCouponMileageDisable.setVisibility(View.GONE);
        mPriceTotal = (TextView) findViewById(R.id.order_price_total);
        mPaymentTypeOfflineCard = findViewById(R.id.order_payment_type_offline_card);
        mPaymentTypeOfflineCash = findViewById(R.id.order_payment_type_offline_cash);
        mPaymentTypeOnlineCreditCart = findViewById(R.id.order_payment_type_online_credit_card);
        mPaymentTypeOnlineTransfer = findViewById(R.id.order_payment_type_online_transfer);
        mDone = findViewById(R.id.order_done);
        mPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        ViewSupportUtils.disableEditText(mAddress);

        mCartView.setOnClickListener(this);
        mDeliveryTypeFoodfly.setOnClickListener(this);
        mDeliveryTypeTakeout.setOnClickListener(this);
        mDeliveryTimeNow.setOnClickListener(this);
        mDeliveryTimeReserve.setOnClickListener(this);
        mDeliveryReservedTime.setOnClickListener(this);
        mCouponButton.setOnClickListener(this);
        mMileageButton.setOnClickListener(this);
        mCouponMileageDisable.setOnClickListener(this);
        mPaymentTypeOfflineCard.setOnClickListener(this);
        mPaymentTypeOfflineCash.setOnClickListener(this);
        mPaymentTypeOnlineCreditCart.setOnClickListener(this);
        mPaymentTypeOnlineTransfer.setOnClickListener(this);
        mDone.setOnClickListener(this);

        mCartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
        mCartRealm.addChangeListener(mCartChangeListener);
        mCart = mCartRealm.where(Cart.class).findFirst();
        if (mCart == null) {
            finish();
            return;
        }
        Realm connectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        mConnect = connectRealm.where(Connect.class).findFirst();
        if (mConnect == null) {
            connectRealm.close();
            finish();
            return;
        } else {
            mConnect = connectRealm.copyFromRealm(mConnect);
        }
        connectRealm.close();

        loadRestaurant();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadRestaurant();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCartRealm.removeChangeListener(mCartChangeListener);
        mCartRealm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CouponActivity.REQ_CODE_COUPON) {
            if (resultCode == RESULT_OK) {
                String couponId = data.getStringExtra(CouponActivity.EXTRA_COUPON_ID);
                loadCoupon(couponId);
            }
        } else if (requestCode == PayActivity.REQ_CODE_PAY) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String id = uri.getQueryParameter("id");
                boolean success = uri.getBooleanQueryParameter("success", false);
                if (success) {
                    orderComplete(mOrderResponse);
                } else {
                    cancelPayment(id);
                }
            } else {
                cancelPayment(mOrderResponse.order.getId());
            }
        }
    }

    private void cancelPayment(String orderId) {
        UserResponse user = UserManager.fetchUser();
        String url = APIs.getPaymentPath().appendPath(APIs.PAYMENT_CANCEL).appendQueryParameter("status", "3").appendQueryParameter("user_id", user.getId()).appendQueryParameter("order_id", orderId).toString();
        GsonRequest<BaseResponse> request = new GsonRequest<>(Method.GET, url, BaseResponse.class, APIs.createHeadersWithToken(), new Listener<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response) {
                dismissProgressDialog();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(OrderActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private RealmChangeListener mCartChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            loadRestaurant();
        }
    };

    private void loadRestaurant() {
        if (mCart == null || !mCart.isValid()) {
            return;
        }
        MapAddress mapAddress = MapAddress.getAddress();
        Uri.Builder builder = APIs.getRestaurantPath().appendPath(mCart.getRestaurant().getId()).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(mapAddress.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(mapAddress.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(mapAddress.getAreaCode()));
        String url = builder.toString();
        GsonRequest<Restaurant> request = new GsonRequest<>(Method.GET, url, Restaurant.class, APIs.createHeadersWithToken(), new Listener<Restaurant>() {
            @Override
            public void onResponse(Restaurant response) {
                dismissProgressDialog();
                mRestaurant = response;
                displayCart();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(OrderActivity.this).content(response.getError().message).positiveText("확인").dismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    }).show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void loadCoupon(String couponId) {
        UserResponse user = UserManager.fetchUser();
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_COUPON).appendPath(couponId).toString();
        GsonRequest<Coupon> request = new GsonRequest<>(Method.GET, url, Coupon.class, APIs.createHeadersWithToken(), new Listener<Coupon>() {
            @Override
            public void onResponse(final Coupon response) {
                dismissProgressDialog();
                if (response.getType() == Coupon.COUPON_TYPE_DELIVERY_TIP) {
                    if (mDeliveryTip - mDiscountPriceDelivery == 0) {
                        new MaterialDialog.Builder(OrderActivity.this).content("쿠폰을 적용할 배달팁이 없습니다.").positiveText("확인").show();
                        return;
                    }
                }
                if (response.getDiscountType() == Coupon.DISCOUNT_TYPE_PRICE) {
                    if (response.getType() == Coupon.COUPON_TYPE_DELIVERY_TIP) {
                        if (response.getDiscountAmount() > mDeliveryTip - mDiscountPriceDelivery) {
                            new MaterialDialog.Builder(OrderActivity.this).content("배달팁 보다 할인금액이 더 큽니다.\n적용하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mCoupon = response;
                                    calculateAndDisplay();
                                }
                            }).show();
                            return;
                        }
                    } else if (response.getType() == Coupon.COUPON_TYPE_MENU) {
                        if (response.getDiscountAmount() > mMenuTotal - mDiscountPriceMenu) {
                            new MaterialDialog.Builder(OrderActivity.this).content("주문 메뉴 총액 보다 할인금액이 더 큽니다.\n적용하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mCoupon = response;
                                    calculateAndDisplay();
                                }
                            }).show();
                            return;
                        }
                    } else if (response.getType() == Coupon.COUPON_TYPE_TOTAL) {
                        if (response.getDiscountAmount() > mMenuTotal - mDiscountPriceMenu) {
                            new MaterialDialog.Builder(OrderActivity.this).content("주문 총액 보다 할인금액이 더 큽니다.\n적용하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    mCoupon = response;
                                    calculateAndDisplay();
                                }
                            }).show();
                            return;
                        }
                    }
                }
                mCoupon = response;
                calculateAndDisplay();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(OrderActivity.this).content(response.getError().message).positiveText("확인").dismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    }).show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void displayCart() {
        if (mRestaurant == null) {
            return;
        }
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            finish();
            return;
        }
        MapAddress address = MapAddress.getAddress();
        if (address == null) {
            finish();
            return;
        }
        mRecipient.setText(user.getUser().getName());
        mRecipient.setSelection(mRecipient.length());
        mPhoneNumber.setText(user.getUser().getPhone());
        mPhoneNumber.setSelection(mPhoneNumber.length());
        if (TextUtils.isEmpty(address.getFormattedAddress())) {
            mAddress.setText(address.getStreetAddress());
        } else {
            mAddress.setText(address.getFormattedAddress());
        }
        mAddress.setSelection(mAddress.length());
        mDetailAddress.setText(address.getDetailAddress());
        mDetailAddress.setSelection(mDetailAddress.length());
        if (mRestaurant.getDeliveryStatus() == 3) {
            finish();
            return;
        }

        mDeliveryTypeFoodfly.setEnabled(true);
        mDeliveryTypeTakeout.setEnabled(true);
        mDeliveryTypeFoodfly.setSelected(true);
        mDeliveryTypeTakeout.setSelected(false);
        if (mRestaurant.getDeliveryStatus() == 1) { // 테이크아웃은 가능은 플로우 진행
            mDeliveryTypeFoodfly.setEnabled(false);
            mDeliveryTypeTakeout.setEnabled(true);
            mDeliveryTypeFoodfly.setSelected(false);
            mDeliveryTypeTakeout.setSelected(true);
        }
        if (!mRestaurant.isTakeoutAvailable()) {   // 테이크아웃 불가능 업장
            mDeliveryTypeFoodfly.setEnabled(true);
            mDeliveryTypeTakeout.setEnabled(false);
            mDeliveryTypeFoodfly.setSelected(true);
            mDeliveryTypeTakeout.setSelected(false);
        }

        mDeliveryTimeNow.setSelected(true);
        mDeliveryTimeReserve.setSelected(false);
        mPriceMileageAmount.setHint(String.valueOf(user.getUser().getMileage()));
        mPaymentTypeOfflineCard.setSelected(true);
        mPaymentTypeOfflineCash.setSelected(false);
        mPaymentTypeOnlineCreditCart.setSelected(false);
        mPaymentTypeOnlineTransfer.setSelected(false);

        if (mRestaurant.isContracted()) {
            mCouponMileageDisable.setVisibility(View.GONE);
            mCouponButton.setVisibility(View.VISIBLE);
            mMileageButton.setVisibility(View.VISIBLE);
        } else {
            mCouponMileageDisable.setVisibility(View.VISIBLE);
            mCouponButton.setVisibility(View.INVISIBLE);
            mMileageButton.setVisibility(View.INVISIBLE);
        }

        calculateAndDisplay();
    }

    private void calculateAndDisplay() {
        if (mRestaurant == null) {
            return;
        }
        if (mCart == null || !mCart.isValid()) {
            return;
        }
        mMenuTotal = mCart.getTotal();
        if (mDeliveryTypeTakeout.isSelected()) {
            mDeliveryTip = 0;
        } else {
            mDeliveryTip = mRestaurant.getDeliveryTip(mCart.getTotal());
            if (mRestaurant.getExtraDeliveryFeeRule().getAdditionalFeePercent() > 0 || mRestaurant.getExtraDeliveryFeeRule().getMenuSumThr() > 0) {
                mPriceTipExtra.setVisibility(View.VISIBLE);
                mPriceTipExtra.setText(String.format(Locale.getDefault(), "(본 음식점은 메뉴가의 %d%%가 배달팁에 추가됩니다)", mRestaurant.getExtraDeliveryFeeRule().getAdditionalFeePercent()));
                float fee = mRestaurant.getExtraDeliveryFeeRule().getAdditionalFeePercent();
                mDeliveryTip += Math.round((fee * ((float) mMenuTotal)) / 10000) * 100;
            } else {
                mPriceTipExtra.setVisibility(View.GONE);
            }
        }

        int discountType = mRestaurant.getDiscountType();
        int discountAmount = mRestaurant.getDiscountAmount();

        // type => 0:없음, 1:배달팁할인(%), 2:배달팁할인(원), 3:총액할인(원)
        switch (discountType) {
            case 0:
                mDiscountPriceEvent = 0;
                mDiscountPriceDelivery = 0;
                mDiscountPriceMenu = 0;
                break;
            case 1:
                mDiscountPriceEvent = ((int) (mDeliveryTip * discountAmount / 10000f)) * 100;
                mDiscountPriceDelivery = mDiscountPriceEvent;
                mDiscountPriceMenu = 0;
                break;
            case 2:
                mDiscountPriceEvent = Math.min(mDeliveryTip, discountAmount);
                mDiscountPriceDelivery = mDiscountPriceEvent;
                mDiscountPriceMenu = 0;
                break;
            case 3:
                mDiscountPriceEvent = discountAmount;
                mDiscountPriceDelivery = Math.min(mDiscountPriceEvent, mDeliveryTip);
                mDiscountPriceMenu = mDiscountPriceEvent - mDiscountPriceDelivery;
                break;
        }

        // discount_type => 1:총액할인, 2:%할인
        // coupon_type => 1:배달팁 할인, 2:메뉴 할인, 3:총액 할인
        mDiscountPriceCoupon = 0;
        if (mCoupon != null) {
            int couponType = mCoupon.getType();
            int couponDiscountType = mCoupon.getDiscountType();
            int couponDiscountAmount = mCoupon.getDiscountAmount();
            if (couponDiscountType == 1) {
                if (couponType == 1) {
                    mDiscountPriceCoupon = Math.min(couponDiscountAmount, mDeliveryTip - mDiscountPriceDelivery);
                } else if (couponType == 2) {
                    mDiscountPriceCoupon = Math.min(couponDiscountAmount, mMenuTotal - mDiscountPriceMenu);
                } else if (couponType == 3) {
                    mDiscountPriceCoupon = Math.min(couponDiscountAmount, mMenuTotal + mDeliveryTip - mDiscountPriceMenu - mDiscountPriceDelivery);
                }
            } else if (couponDiscountType == 2) {
                if (couponType == 1) {
                    mDiscountPriceCoupon = ((int) ((mDeliveryTip - mDiscountPriceDelivery) * couponDiscountAmount / 10000f)) * 100;
                } else if (couponType == 2) {
                    mDiscountPriceCoupon = ((int) ((mMenuTotal - mDiscountPriceMenu) * couponDiscountAmount / 10000f)) * 100;
                } else if (couponType == 3) {
                    mDiscountPriceCoupon = ((int) ((mMenuTotal + mDeliveryTip - mDiscountPriceMenu - mDiscountPriceDelivery) * couponDiscountAmount / 10000f)) * 100;
                }
            }
        }

        // mileage
        mDiscountPriceMileage = Math.min(mMileage, mMenuTotal + mDeliveryTip - mDiscountPriceMenu - mDiscountPriceDelivery - mDiscountPriceCoupon);

        mTotal = mMenuTotal + mDeliveryTip - mDiscountPriceMenu - mDiscountPriceDelivery - mDiscountPriceCoupon - mDiscountPriceMileage;

        mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mMenuTotal)));
        mPriceTip.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mDeliveryTip)));
        mPriceEvent.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(-mDiscountPriceEvent)));
        mPriceCoupon.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(-mDiscountPriceCoupon)));
        mPriceMileage.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(-mDiscountPriceMileage)));
        mPriceTotal.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mTotal)));
    }

    @Override
    public void onClick(View v) {
        if (v == mCartView) {
            CartActivity.createInstance(this);
        } else if (v == mDeliveryTypeFoodfly) {
            mDeliveryTypeFoodfly.setSelected(true);
            mDeliveryTypeTakeout.setSelected(false);
            calculateAndDisplay();
        } else if (v == mDeliveryTypeTakeout) {
            mDeliveryTypeFoodfly.setSelected(false);
            mDeliveryTypeTakeout.setSelected(true);
            calculateAndDisplay();
        } else if (v == mDeliveryTimeNow) {
            mDeliveryTimeNow.setSelected(true);
            mDeliveryTimeReserve.setSelected(false);
            mDeliveryReservedTime.setVisibility(View.GONE);
        } else if (v == mDeliveryTimeReserve) {
            if (mReservedCalendar == null) {
                mReservedCalendar = GregorianCalendar.getInstance();
            }
            mReservedCalendar.setTimeInMillis(System.currentTimeMillis());
            final int year = mReservedCalendar.get(Calendar.YEAR);
            final int month = mReservedCalendar.get(Calendar.MONTH);
            final int day = mReservedCalendar.get(Calendar.DAY_OF_MONTH);
            final int hour = mReservedCalendar.get(Calendar.HOUR_OF_DAY);
            final int minute = mReservedCalendar.get(Calendar.MINUTE);
            DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth) {
                    mReservedCalendar.set(Calendar.YEAR, _year);
                    mReservedCalendar.set(Calendar.MONTH, _monthOfYear);
                    mReservedCalendar.set(Calendar.DATE, _dayOfMonth);
                    BoundTimePickerDialog dialog = new BoundTimePickerDialog(OrderActivity.this, new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mReservedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mReservedCalendar.set(Calendar.MINUTE, minute);
                            mDeliveryTimeNow.setSelected(false);
                            mDeliveryTimeReserve.setSelected(true);
                            mDeliveryReservedTime.setVisibility(View.VISIBLE);
                            mDeliveryReservedTime.setText(TimeUtils.getFullTimeString(mReservedCalendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset()));
                        }
                    }, hour, minute, true);
                    int _a = _year * 384 + _monthOfYear * 32 + _dayOfMonth;
                    int a = year * 384 + month * 32 + day;
                    if (_a <= a) {
                        dialog.setMin(hour, minute);
                    }
                    dialog.show();
                }
            }, year, month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        } else if (v == mDeliveryReservedTime) {
            final int year = mReservedCalendar.get(Calendar.YEAR);
            final int month = mReservedCalendar.get(Calendar.MONTH);
            final int day = mReservedCalendar.get(Calendar.DAY_OF_MONTH);
            final int hour = mReservedCalendar.get(Calendar.HOUR_OF_DAY);
            final int minute = mReservedCalendar.get(Calendar.MINUTE);
            DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth) {
                    mReservedCalendar.set(Calendar.YEAR, _year);
                    mReservedCalendar.set(Calendar.MONTH, _monthOfYear);
                    mReservedCalendar.set(Calendar.DATE, _dayOfMonth);
                    BoundTimePickerDialog dialog = new BoundTimePickerDialog(OrderActivity.this, new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mReservedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            mReservedCalendar.set(Calendar.MINUTE, minute);
                            mDeliveryReservedTime.setText(TimeUtils.getFullTimeString(mReservedCalendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset()));
                        }
                    }, hour, minute, true);
                    int _a = _year * 384 + _monthOfYear * 32 + _dayOfMonth;
                    int a = year * 384 + month * 32 + day;
                    if (_a <= a) {
                        dialog.setMin(hour, minute);
                    }
                    dialog.show();
                }
            }, year, month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        } else if (v == mCouponButton) {
            if (mRestaurant == null) {
                return;
            }
            CouponActivity.createInstance(this, true, mRestaurant.getId());
        } else if (v == mMileageButton) {
            setMileage();
        } else if (v == mPaymentTypeOfflineCard) {
            mPaymentTypeOfflineCard.setSelected(true);
            mPaymentTypeOfflineCash.setSelected(false);
            mPaymentTypeOnlineCreditCart.setSelected(false);
            mPaymentTypeOnlineTransfer.setSelected(false);
        } else if (v == mPaymentTypeOfflineCash) {
            mPaymentTypeOfflineCard.setSelected(false);
            mPaymentTypeOfflineCash.setSelected(true);
            mPaymentTypeOnlineCreditCart.setSelected(false);
            mPaymentTypeOnlineTransfer.setSelected(false);
        } else if (v == mPaymentTypeOnlineCreditCart) {
            mPaymentTypeOfflineCard.setSelected(false);
            mPaymentTypeOfflineCash.setSelected(false);
            mPaymentTypeOnlineCreditCart.setSelected(true);
            mPaymentTypeOnlineTransfer.setSelected(false);
        } else if (v == mPaymentTypeOnlineTransfer) {
            mPaymentTypeOfflineCard.setSelected(false);
            mPaymentTypeOfflineCash.setSelected(false);
            mPaymentTypeOnlineCreditCart.setSelected(false);
            mPaymentTypeOnlineTransfer.setSelected(true);
        } else if (v == mDone) {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_confitm, null);
            TextView address = (TextView) view.findViewById(R.id.order_confirm_address);
            TextView deliveryType = (TextView) view.findViewById(R.id.order_confirm_delivery_type);
            TextView deliveryTime = (TextView) view.findViewById(R.id.order_confirm_delivery_time);
            address.setText(mAddress.getText());
            address.append(" " + mDetailAddress.getText());
            if (mDeliveryTypeFoodfly.isSelected()) {
                if (mRestaurant.getDeliveryType() == Restaurant.DELIVERY_TYPE_DIRECT) {
                    deliveryType.setText("매장 자체 배달");
                } else {
                    deliveryType.setText("푸드플라이 배달");
                }
            } else if (mDeliveryTypeTakeout.isSelected()) {
                deliveryType.setText("테이크아웃");
            } else if (mRestaurant.getDeliveryType() == Restaurant.DELIVERY_TYPE_DIRECT) {
                deliveryType.setText("매장 자체 배달");
            }
            if (mDeliveryTimeNow.isSelected()) {
                deliveryTime.setText("바로주문");
            } else if (mDeliveryTimeReserve.isSelected()) {
                deliveryTime.setText("예약주문");
                deliveryTime.append("(");
                deliveryTime.append(mDeliveryReservedTime.getText());
                deliveryTime.append(")");
            }
            new MaterialDialog.Builder(this).customView(view, true).positiveText("확인").negativeText("취소").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    done();
                }
            }).show();
        }
    }

    private void setMileage() {
        if (mPriceMileageAmount.length() <= 0) {
            mMileage = 0;
        } else {
            UserResponse user = UserManager.fetchUser();
            int mileage = Integer.parseInt(mPriceMileageAmount.getText().toString());
            MileageRule mileageRule = mConnect.getMileageRule(user.getUser().getMileageGrade());
            if (mileageRule == null) {
                return;
            } else if (mileageRule.getThreshold() > user.getUser().getMileage()) {
                new MaterialDialog.Builder(this).content(String.format(Locale.getDefault(), "마일리지는 %s원 이상 적립 후 사용이 가능합니다.", UnitUtils.priceFormat(mileageRule.getThreshold()))).positiveText("확인").show();
                return;
            } else if (mileage % 100 != 0) {
                new MaterialDialog.Builder(this).content("마일리지는 100원 단위로 사용이 가능합니다.").positiveText("확인").show();
                return;
            } else {
                mMileage = mileage;
            }
        }
        mPriceMileageAmount.clearFocus();
        ViewSupportUtils.hideSoftInput(mPriceMileageAmount);
        calculateAndDisplay();
    }

    private void done() {
        if (mRestaurant == null) {
            return;
        }
        if (mCart == null || !mCart.isValid()) {
            return;
        }
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty("restaurant_id", mRestaurant.getId());
        json.addProperty("address_id", MapAddress.getAddress().getId());
        json.addProperty("receipient_name", mRecipient.getText().toString());
        json.addProperty("receipient_phone", mPhoneNumber.getText().toString());
        json.addProperty("receipient_detail_address", mDetailAddress.getText().toString());
        JsonArray menus = new JsonArray();
        for (CartMenu cartMenu : mCart.getCartMenus()) {
            JsonObject menu = new JsonObject();
            menu.addProperty("id", cartMenu.getMenu().getId());
            menu.addProperty("quantity", cartMenu.getQuantity());
            JsonArray options = new JsonArray();
            HashMap<String, List<MenuOptionItem>> menuOptionItemsMap = cartMenu.getMenuOptionItemsMap();
            Set<String> keySet = menuOptionItemsMap.keySet();
            for (String parentId : keySet) {
                List<MenuOptionItem> menuOptionItems = menuOptionItemsMap.get(parentId);
                JsonObject option = new JsonObject();
                option.addProperty("id", parentId);
                JsonArray items = new JsonArray();
                for (MenuOptionItem menuOptionItem : menuOptionItems) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", menuOptionItem.getId());
                    items.add(item);
                }
                option.add("items", items);
                options.add(option);
            }
            menu.add("options", options);
            menus.add(menu);
        }
        if (mCoupon != null) {
            json.addProperty("coupon_id", mCoupon.getId());
        }
        json.add("menus", menus);
        json.addProperty("mileage", mDiscountPriceMileage);
        int paymentType = -1;
        if (mPaymentTypeOnlineCreditCart.isSelected()) {
            paymentType = 1;
        } else if (mPaymentTypeOnlineTransfer.isSelected()) {
            paymentType = 2;
        } else if (mPaymentTypeOfflineCard.isSelected()) {
            paymentType = 3;
        } else if (mPaymentTypeOfflineCash.isSelected()) {
            paymentType = 4;
        }
        json.addProperty("payment_type", paymentType);
        int deliveryType = -1;
        if (mDeliveryTypeFoodfly.isSelected()) {
            if (mRestaurant.getDeliveryType() == Restaurant.DELIVERY_TYPE_DIRECT) {
                deliveryType = 2;
            } else {
                deliveryType = 1;
            }
        } else if (mDeliveryTypeTakeout.isSelected()) {
            deliveryType = 3;
        } else if (mRestaurant.getDeliveryType() == Restaurant.DELIVERY_TYPE_DIRECT) {
            deliveryType = 2;
        }
        json.addProperty("delivery_type", deliveryType);
        if (mDeliveryTimeReserve.isSelected()) {
            json.addProperty("reservation_time", mReservedCalendar.getTimeInMillis() / 1000 + TimeZone.getDefault().getRawOffset() / 1000);
        }
        if (mDeliveryMessage.length() > 0) {
            json.addProperty("memo", mDeliveryMessage.getText().toString());
        }
        String url = APIs.getUserPath().appendPath(user.getId()).appendPath(APIs.USER_ORDER).toString();
        GsonRequest<OrderResponse> request = new GsonRequest<>(Method.POST, url, OrderResponse.class, APIs.createHeadersWithToken(), json.toString().getBytes(), new Listener<OrderResponse>() {
            @Override
            public void onResponse(OrderResponse response) {
                dismissProgressDialog();
                mOrderResponse = response;
                if (response.payment == null) {
                    orderComplete(response);
                } else {
                    mOrderResponse = response;
                    PayActivity.createInstance(OrderActivity.this, response.payment);
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(OrderActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void orderComplete(OrderResponse orderResponse) {
        mCartRealm.beginTransaction();
        mCartRealm.deleteAll();
        mCartRealm.commitTransaction();
        OrderDetailActivity.createInstance(OrderActivity.this, orderResponse.order, "주문 완료", false);
        finish();
    }

    private OrderResponse mOrderResponse;

    private class OrderResponse extends BaseResponse {
        Order order;
        Payment payment;
    }
}