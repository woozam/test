package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.model.restaurant.CartMenu;
import kr.co.foodfly.androidapp.model.restaurant.Menu;
import kr.co.foodfly.androidapp.model.restaurant.MenuOption;
import kr.co.foodfly.androidapp.model.restaurant.MenuOptionItem;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-24.
 */
public class MenuView extends RelativeLayout implements OnClickListener {

    private ScrollView mScrollView;
    private View mContentLayout;
    private LinearLayout mMenuOptionView;
    private ImageView mImage;
    private TextView mName;
    private TextView mDescription;
    private TextView mQuantity;
    private View mQuantityAdd;
    private View mQuantitySub;
    private View mCancel;
    private View mAddToCart;
    private TextView mTotal;

    private ArrayList<IMenuOptionView> mMenuOptionViews;
    private CartMenu mCartMenu;
    private OnButtonListener mOnButtonListener;

    public MenuView(Context context) {
        super(context);
        initialize();
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_menu, this, true);
        mScrollView = (ScrollView) findViewById(R.id.menu_scroll_view);
        mContentLayout = findViewById(R.id.menu_content_layout);
        mMenuOptionView = (LinearLayout) findViewById(R.id.menu_option_layout);
        mImage = (ImageView) findViewById(R.id.menu_image);
        mImage.getLayoutParams().height = (int) Math.ceil(CommonUtils.getScreenWidth() * 390f / 710f);
        mName = (TextView) findViewById(R.id.menu_name);
        mDescription = (TextView) findViewById(R.id.menu_description);
        mQuantity = (TextView) findViewById(R.id.menu_quantity);
        mQuantityAdd = findViewById(R.id.menu_quantity_add);
        mQuantitySub = findViewById(R.id.menu_quantity_sub);
        mTotal = (TextView) findViewById(R.id.menu_total);
        mCancel = findViewById(R.id.menu_cancel);
        mAddToCart = findViewById(R.id.menu_add_to_cart);
        mMenuOptionViews = new ArrayList<>();
        mQuantityAdd.setOnClickListener(this);
        mQuantitySub.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mAddToCart.setOnClickListener(this);
        notifyDataSetChanged();
    }

    public void setMenu(Menu menu, int imageHeight, OnButtonListener onButtonListener) {
        mOnButtonListener = onButtonListener;
        mCartMenu = new CartMenu(menu);
        mImage.getLayoutParams().height = imageHeight;
        VolleySingleton.getInstance(getContext()).getImageLoader().get(menu.getImage(), new ImageListener() {
            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                mImage.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }, CommonUtils.getScreenWidth(), CommonUtils.getScreenHeight(), ScaleType.CENTER_CROP);
        mImage.setVisibility(TextUtils.isEmpty(menu.getImage()) ? GONE : VISIBLE);
        mName.setText(menu.getName());
        mDescription.setText(Html.fromHtml(menu.getDescription().replace("\n", "<br>")));
        mScrollView.scrollTo(0, 0);
        mMenuOptionView.removeAllViews();
        mMenuOptionViews.clear();
        for (MenuOption menuOption : menu.getOptions()) {
            IMenuOptionView menuOptionView = null;
            switch (menuOption.getType()) {
                case MenuOption.TYPE_REQUIRED:
                    menuOptionView = new MenuOptionRequiredView(getContext());
                    break;
                case MenuOption.TYPE_OPTIONAL:
                    menuOptionView = new MenuOptionOptionalView(getContext());
                    break;
                case MenuOption.TYPE_REQUIRED_MIN_MAX:
                    menuOptionView = new MenuOptionRequiredMinMaxView(getContext());
                    break;
                case MenuOption.TYPE_OPTIONAL_MIN_MAX:
                    menuOptionView = new MenuOptionOptionalMinMaxView(getContext());
                    break;
            }
            if (menuOptionView != null) {
                menuOptionView.setOption(menuOption);
                menuOptionView.setOnOptionChangedListener(mOnOptionChangedListener);
                mMenuOptionView.addView((View) menuOptionView);
                mMenuOptionViews.add(menuOptionView);
            }
        }
        notifyDataSetChanged();
    }

    public View getContentLayout() {
        return mContentLayout;
    }

    @Override
    public void onClick(View v) {
        if (v == mQuantityAdd) {
            mCartMenu.setQuantity(mCartMenu.getQuantity() + 1);
            notifyDataSetChanged();
        } else if (v == mQuantitySub) {
            if (mCartMenu.getQuantity() > 1) {
                mCartMenu.setQuantity(mCartMenu.getQuantity() - 1);
                notifyDataSetChanged();
            }
        } else if (v == mCancel) {
            mOnButtonListener.onCancel();
        } else if (v == mAddToCart) {
            for (IMenuOptionView menuOptionView : mMenuOptionViews) {
                switch (menuOptionView.getMenuOption().getType()) {
                    case MenuOption.TYPE_REQUIRED:
                        if (menuOptionView.getCheckedItemList().size() == 0) {
                            new MaterialDialog.Builder(getContext()).content(String.format(Locale.getDefault(), "%s (을)를 해주세요", menuOptionView.getMenuOption().getName())).positiveText("확인").show();
                            return;
                        }
                        break;
                    case MenuOption.TYPE_OPTIONAL:
                        break;
                    case MenuOption.TYPE_REQUIRED_MIN_MAX:
                        if (menuOptionView.getCheckedItemList().size() < menuOptionView.getMenuOption().getMin()) {
                            new MaterialDialog.Builder(getContext()).content(String.format(Locale.getDefault(), "%s (을)를 해주세요", menuOptionView.getMenuOption().getName())).positiveText("확인").show();
                            return;
                        }
                        break;
                    case MenuOption.TYPE_OPTIONAL_MIN_MAX:
                        break;
                }
            }
            mOnButtonListener.onAddToCart(mCartMenu);
        }
    }

    private OnOptionChangedListener mOnOptionChangedListener = new OnOptionChangedListener() {
        @Override
        public void onOptionChanged() {
            mCartMenu.getMenuOptionItems().clear();
            for (IMenuOptionView menuOptionView : mMenuOptionViews) {
                mCartMenu.getMenuOptionItems().addAll(menuOptionView.getCheckedItemList());
            }
            notifyDataSetChanged();
        }
    };

    private void notifyDataSetChanged() {
        if (mCartMenu != null) {
            mQuantity.setText(String.valueOf(mCartMenu.getQuantity()));
            mTotal.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(mCartMenu.getTotalPrice())));
        }
    }

    public interface IMenuOptionView {
        void setOption(MenuOption menuOption);
        MenuOption getMenuOption();
        void setOnOptionChangedListener(OnOptionChangedListener l);
        List<MenuOptionItem> getCheckedItemList();
    }

    public interface IMenuOptionItemView {
        void setMenuOptionItem(MenuOptionItem menuOptionItem);
        MenuOptionItem getMenuOptionItem();
        boolean isChecked();
    }

    public interface OnOptionChangedListener {
        void onOptionChanged();
    }

    public interface OnButtonListener {
        void onCancel();
        void onAddToCart(CartMenu cartMenu);
    }
}