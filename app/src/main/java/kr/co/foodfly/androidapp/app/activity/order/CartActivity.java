package kr.co.foodfly.androidapp.app.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;
import kr.co.foodfly.androidapp.app.activity.kickOff.LoginActivity;
import kr.co.foodfly.androidapp.app.activity.restaurant.RestaurantActivity;
import kr.co.foodfly.androidapp.app.activity.setting.AddressActivity;
import kr.co.foodfly.androidapp.app.view.recyclerView.RecyclerViewEmptySupport;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmUtils;
import kr.co.foodfly.androidapp.model.BaseResponse;
import kr.co.foodfly.androidapp.model.connect.Area;
import kr.co.foodfly.androidapp.model.connect.Connect;
import kr.co.foodfly.androidapp.model.restaurant.Cart;
import kr.co.foodfly.androidapp.model.restaurant.CartMenu;
import kr.co.foodfly.androidapp.model.restaurant.Restaurant;
import kr.co.foodfly.androidapp.model.user.MapAddress;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-25.
 */
public class CartActivity extends BaseActivity implements RealmChangeListener<Realm>, OnClickListener {

    public static final int REQ_CODE_CART = CartActivity.class.hashCode() & 0x000000ff;

    public static void createInstance(Activity activity) {
        Intent intent = new Intent(activity, CartActivity.class);
        activity.startActivityForResult(intent, REQ_CODE_CART);
    }

    private Realm mCartRealm;
    private Cart mCart;
    private CartAdapter mCartAdapter;
    private RecyclerViewEmptySupport mRecyclerView;
    private TextView mMenuCount;
    private TextView mTotalPrice;
    private View mAdd;
    private View mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCartRealm = Realm.getInstance(RealmUtils.CONFIG_CART);
        mCartRealm.addChangeListener(this);
        mCart = mCartRealm.where(Cart.class).findFirst();
        if (mCart == null) {
            finish();
            return;
        }
        mCartAdapter = new CartAdapter();
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.cart_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCartAdapter);

        mMenuCount = (TextView) findViewById(R.id.cart_menu_count);
        mTotalPrice = (TextView) findViewById(R.id.cart_menu_total);
        mAdd = findViewById(R.id.cart_add);
        mOrder = findViewById(R.id.cart_order);

        mAdd.setOnClickListener(this);
        mOrder.setOnClickListener(this);

        loadRestaurant();
        notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCartRealm.removeChangeListener(this);
        mCartRealm.close();
    }

    private void loadRestaurant() {
        MapAddress mapAddress = MapAddress.getAddress();
        Uri.Builder builder = APIs.getRestaurantPath().appendPath(mCart.getId()).appendQueryParameter(APIs.PARAM_LAT, String.valueOf(mapAddress.getLat())).appendQueryParameter(APIs.PARAM_LON, String.valueOf(mapAddress.getLon())).appendQueryParameter(APIs.PARAM_AREA_CODE, String.valueOf(mapAddress.getAreaCode()));
        String url = builder.toString();
        GsonRequest<Restaurant> request = new GsonRequest<>(Method.GET, url, Restaurant.class, APIs.createHeadersWithToken(), new Listener<Restaurant>() {
            @Override
            public void onResponse(Restaurant response) {
                dismissProgressDialog();
                mCartRealm.beginTransaction();
                Cart cart = mCartRealm.copyFromRealm(mCart);
                cart.setRestaurant(response);
                mCartRealm.copyToRealmOrUpdate(cart);
                mCartRealm.commitTransaction();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
                BaseResponse response = BaseResponse.parseError(BaseResponse.class, error);
                if (response != null && response.getError() != null && !TextUtils.isEmpty(response.getError().message)) {
                    new MaterialDialog.Builder(CartActivity.this).content(response.getError().message).positiveText("확인").show();
                }
            }
        }, RealmUtils.REALM_GSON);
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    private void notifyDataSetChanged() {
        mMenuCount.setText(String.format(Locale.getDefault(), "총 주문 금액(%d개)", mCart.getMenuCount()));
        mTotalPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mCart.getTotal())));
    }

    @Override
    public void onChange(Realm element) {
        if (!mCart.isValid() || mCart.getMenuCount() == 0) {
            finish();
        } else {
            notifyDataSetChanged();
            mCartAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v == mAdd) {
            RestaurantActivity.createInstance(this, mCart.getRestaurant().getId());
        } else if (v == mOrder) {
            order();
        } else if (v.getId() == R.id.cart_menu_quantity_add) {
            final CartMenu cartMenu = mCart.getCartMenus().get((Integer) v.getTag());
            mCartRealm.beginTransaction();
            cartMenu.setQuantity(cartMenu.getQuantity() + 1);
            mCartRealm.commitTransaction();
        } else if (v.getId() == R.id.cart_menu_quantity_sub) {
            final CartMenu cartMenu = mCart.getCartMenus().get((Integer) v.getTag());
            if (cartMenu.getQuantity() > 1) {
                mCartRealm.beginTransaction();
                cartMenu.setQuantity(cartMenu.getQuantity() - 1);
                mCartRealm.commitTransaction();
            } else {
                new MaterialDialog.Builder(this).content("최소 주문가능 수량은 1개입니다. 메뉴를 삭제하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mCartRealm.beginTransaction();
                        mCart.getCartMenus().remove(cartMenu);
                        mCartRealm.commitTransaction();
                    }
                }).show();
            }
        }
    }

    private void order() {
        // 로그인 안한상태
        UserResponse user = UserManager.fetchUser();
        if (user == null) {
            new MaterialDialog.Builder(this).content("로그인 후 이용해주세요.").positiveText("로그인").negativeText("취소").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    LoginActivity.createInstance(CartActivity.this);
                }
            }).show();
            return;
        }

        // 주소 설정 안한 상태
        if (user.getUser().getAddress() == null) {
            new MaterialDialog.Builder(this).content("주소를 설정하신 후 이용해주세요.").positiveText("주소설정").negativeText("취소").onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    AddressActivity.createInstance(CartActivity.this);
                }
            }).show();
            return;
        }

        Realm connectRealm = Realm.getInstance(RealmUtils.CONFIG_CONNECT);
        Connect connect = connectRealm.where(Connect.class).findFirst();
        if (connect != null) {
            connect = connectRealm.copyFromRealm(connect);
        }
        connectRealm.close();
        Area area = connect.getArea();

        if (!area.isServiceArea() && mCart.getRestaurant().isAreaOpen()) {
            mCartRealm.beginTransaction();
            mCart.getRestaurant().setAreaOpen(false);
            mCartRealm.commitTransaction();
        }

        // 배달 안됨
        if (!mCart.getRestaurant().isAvailable() && mCart.getRestaurant().getDeliveryStatus() != 1) {
            String message = mCart.getRestaurant().getAvailableMessage();
            if (area.isServiceArea() && !mCart.getRestaurant().isAreaOpen()) {
                message += String.format(Locale.getDefault(), "\n서비스 운영시간은 %s:%s~%s:%s 입니다.", area.getStart().split(":")[0], area.getStart().split(":")[1], area.getEnd().split(":")[0], area.getEnd().split(":")[1]);
            }
            new MaterialDialog.Builder(this).content(message).positiveText("확인").show();
            return;
        }

        // 메뉴 없음
        if (mCart.getCartMenus().size() == 0) {
            new MaterialDialog.Builder(this).content("주문할 메뉴가 없습니다.").positiveText("확인").show();
            return;
        }

        // 메뉴 적음
        if (mCart.getTotal() < mCart.getRestaurant().getMinimumOrderAmount()) {
            new MaterialDialog.Builder(this).content("최소 주문금액보다 적어 주문을 하실 수 없습니다.").positiveText("확인").show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (mCart.getRestaurant().getDeliveryStatus() == 1) {
            sb.append(String.format(Locale.getDefault(), "실시간 배송현황에 따른 현재 최대배송거리는 %.02fkm 입니다.\n일시적으로 테이크아웃 주문만 가능합니다.\n주문 하시겠습니까?", mCart.getRestaurant().getDeliveryAvailableDistance()));
        } else {
            boolean messageAdded = false;
            if (!TextUtils.isEmpty(area.getDeliveryTimeMessage())) {
                sb.append(area.getDeliveryTimeMessage());
                messageAdded = true;
            }
            if (area.isUseDeliveryTime()) {
                if (area.getDeliveryTime() == 0) {
                    if (messageAdded) {
                        sb.append("\n");
                    }
                    sb.append("주문 하시겠습니까?");
                } else {
                    if (messageAdded) {
                        sb.append(" ");
                    }
                    sb.append(String.format(Locale.getDefault(), "지금 바로 주문 시 최대 %d분 소요될 수 있습니다\n주문 하시겠습니까?", area.getDeliveryTime()));
                }
            } else {
                if (messageAdded) {
                    sb.append("\n");
                }
                sb.append("주문 하시겠습니까?");
            }
        }

        new MaterialDialog.Builder(this).content(sb.toString()).positiveText("주문").negativeText("취소").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                OrderActivity.createInstance(CartActivity.this);
            }
        }).show();
    }

    private class CartAdapter extends Adapter<CartViewHolder> {

        public static final int VIEW_TYPE_HEADER = 0;
        public static final int VIEW_TYPE_MENU = 1;

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_MENU;
            }
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                return new CartHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_header, parent, false));
            } else if (viewType == VIEW_TYPE_MENU) {
                return new CartMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_menu, parent, false));
            } else {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
                holder.setItem(mCart.getRestaurant());
            } else if (holder.getItemViewType() == VIEW_TYPE_MENU) {
                holder.setItem(mCart.getCartMenus().get(position - 1));
                ((CartMenuViewHolder) holder).mQuantityAdd.setTag(position - 1);
                ((CartMenuViewHolder) holder).mQuantityAdd.setOnClickListener(CartActivity.this);
                ((CartMenuViewHolder) holder).mQuantitySub.setTag(position - 1);
                ((CartMenuViewHolder) holder).mQuantitySub.setOnClickListener(CartActivity.this);
            }
        }

        @Override
        public int getItemCount() {
            return 1 + mCart.getCartMenus().size();
        }
    }

    public static abstract class CartViewHolder extends ViewHolder {

        public CartViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setItem(Object object);
    }

    public static class CartHeaderViewHolder extends CartViewHolder {

        private NetworkImageView mImage;
        private TextView mName;

        public CartHeaderViewHolder(View itemView) {
            super(itemView);
            mImage = (NetworkImageView) itemView.findViewById(R.id.cart_header_image);
            mName = (TextView) itemView.findViewById(R.id.cart_header_name);
        }

        @Override
        public void setItem(Object object) {
            Restaurant restaurant = (Restaurant) object;
            mImage.setImageUrl(restaurant.getThumbnail(), VolleySingleton.getInstance(mImage.getContext()).getImageLoader());
            mName.setText(restaurant.getName());
        }
    }

    public static class CartMenuViewHolder extends CartViewHolder {

        private TextView mName;
        private TextView mQuantity;
        private View mQuantityAdd;
        private View mQuantitySub;
        private TextView mPrice;
        private TextView mOptionPrice;
        private TextView mOptionList;
        private TextView mOriginalTotalPrice;
        private TextView mTotalPrice;

        public CartMenuViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.cart_menu_name);
            mQuantity = (TextView) itemView.findViewById(R.id.cart_menu_quantity);
            mQuantityAdd = itemView.findViewById(R.id.cart_menu_quantity_add);
            mQuantitySub = itemView.findViewById(R.id.cart_menu_quantity_sub);
            mPrice = (TextView) itemView.findViewById(R.id.cart_menu_price);
            mOptionPrice = (TextView) itemView.findViewById(R.id.cart_menu_option_price);
            mOptionList = (TextView) itemView.findViewById(R.id.cart_menu_option_list);
            mOriginalTotalPrice = (TextView) itemView.findViewById(R.id.cart_menu_original_price);
            mOriginalTotalPrice.setPaintFlags(mOriginalTotalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mTotalPrice = (TextView) itemView.findViewById(R.id.cart_menu_total_price);
        }

        @Override
        public void setItem(Object object) {
            CartMenu cartMenu = (CartMenu) object;
            mName.setText(cartMenu.getMenu().getName());
            mQuantity.setText(String.valueOf(cartMenu.getQuantity()));
            mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(cartMenu.getMenu().getPrice())));
            mOptionPrice.setText(String.format(Locale.getDefault(), "+%s원", UnitUtils.priceFormat(cartMenu.getOptionPrice())));
            mOptionList.setText(cartMenu.getOptionString());
            mOptionList.setVisibility(mOptionList.length() == 0 ? View.GONE : View.VISIBLE);
            mTotalPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(cartMenu.getTotalPrice())));
            mOriginalTotalPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(cartMenu.getOriginalTotalPrice())));
            mOriginalTotalPrice.setVisibility(cartMenu.getMenu().getDiscountType() != 0 ? View.VISIBLE : View.GONE);
        }
    }
}