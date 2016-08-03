package com.foodfly.gcm.app.activity.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.restaurant.RestaurantAdapter.RestaurantChildViewHolder;
import com.foodfly.gcm.app.activity.restaurant.RestaurantAdapter.RestaurantGroupViewHolder;
import com.foodfly.gcm.app.view.DeliveryTipView;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.UnitUtils;
import com.foodfly.gcm.model.restaurant.Category;
import com.foodfly.gcm.model.restaurant.Menu;
import com.foodfly.gcm.model.restaurant.Restaurant;
import com.foodfly.gcm.network.VolleySingleton;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import static com.foodfly.gcm.R.id.restaurant_detail_open_hour_off_day;

public class RestaurantAdapter extends AbstractExpandableItemAdapter<RestaurantGroupViewHolder, RestaurantChildViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_MENU = 2;
    private static final int VIEW_TYPE_DETAIL = 3;
    private static final int VIEW_TYPE_DETAIL_TITLE = 4;
    private static final int VIEW_TYPE_DETAIL_OPEN_HOUR = 5;
    private static final int VIEW_TYPE_DETAIL_CONTACT = 6;
    private static final int VIEW_TYPE_DETAIL_ORIGIN = 7;
    private static final int VIEW_TYPE_LINK = 8;

    public static final int MODE_MENU = 0;
    public static final int MODE_DETAIL = 1;

    private Restaurant mRestaurant;
    private OnClickListener mOnClickListener;
    private int mMode = MODE_MENU;
    private boolean mDetailMessageExpanded = false;

    public RestaurantAdapter(Context context, OnClickListener onClickListener) {
        setHasStableIds(true);
        mOnClickListener = onClickListener;
    }

    public void resume() {
    }

    public void pause() {
    }

    public void lowMemory() {
    }

    public void destroy() {
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void expandDetailMessage() {
        mDetailMessageExpanded = true;
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        if (groupPosition == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            if (mMode == MODE_MENU) {
                return VIEW_TYPE_CATEGORY;
            } else if (mMode == MODE_DETAIL) {
                if (groupPosition == 1) {
                    return VIEW_TYPE_DETAIL;
                } else if (groupPosition == 4) {
                    if (TextUtils.isEmpty(mRestaurant.getInfo().getOriginalInfo())) {
                        return VIEW_TYPE_LINK;
                    } else {
                        return VIEW_TYPE_DETAIL_TITLE;
                    }
                } else if (groupPosition == 5) {
                    return VIEW_TYPE_LINK;
                } else {
                    return VIEW_TYPE_DETAIL_TITLE;
                }
            }
        }
        return -1;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        if (groupPosition > 0) {
            if (mMode == MODE_MENU) {
                return VIEW_TYPE_MENU;
            } else if (mMode == MODE_DETAIL) {
                int groupItemViewType = getGroupItemViewType(groupPosition);
                if (groupItemViewType == VIEW_TYPE_DETAIL_TITLE) {
                    if (groupPosition == 2) {
                        return VIEW_TYPE_DETAIL_OPEN_HOUR;
                    } else if (groupPosition == 3) {
                        return VIEW_TYPE_DETAIL_CONTACT;
                    } else if (groupPosition == 4) {
                        return VIEW_TYPE_DETAIL_ORIGIN;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public int getGroupCount() {
        if (mRestaurant == null) {
            return 1;
        } else {
            if (mMode == MODE_MENU) {
                return 1 + mRestaurant.getCategoryList().size();
            } else if (mMode == MODE_DETAIL) {
                return 1 + 3 + (TextUtils.isEmpty(mRestaurant.getInfo().getOriginalInfo()) ? 0 : 1) + (TextUtils.isEmpty(mRestaurant.getInfo().getNaverURL()) ? 0 : 1);
            }
        }
        return 0;
    }

    @Override
    public int getChildCount(int groupPosition) {
        if (groupPosition == 0) {
            return 0;
        } else {
            if (mMode == MODE_MENU) {
                return mRestaurant.getMenuMap().get(mRestaurant.getCategoryList().get(groupPosition - 1)).size();
            } else if (mMode == MODE_DETAIL) {
                int groupItemViewType = getGroupItemViewType(groupPosition);
                if (groupItemViewType == VIEW_TYPE_DETAIL_TITLE) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        if (groupPosition == 0) {
            return 0;
        } else {
            if (mMode == MODE_MENU) {
                return mRestaurant.getCategoryList().get(groupPosition - 1).hashCode();
            } else if (mMode == MODE_DETAIL) {
                return groupPosition;
            }
        }
        return -1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (mMode == MODE_MENU) {
            return mRestaurant.getMenuMap().get(mRestaurant.getCategoryList().get(groupPosition - 1)).get(childPosition).hashCode();
        } else if (mMode == MODE_DETAIL) {
            return 10 * groupPosition + childPosition;
        }
        return -1;
    }

    @Override
    public RestaurantGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new RestaurantInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_info, parent, false));
        } else if (viewType == VIEW_TYPE_CATEGORY) {
            return new RestaurantCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_category, parent, false));
        } else if (viewType == VIEW_TYPE_DETAIL) {
            return new RestaurantDetailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail, parent, false));
        } else if (viewType == VIEW_TYPE_DETAIL_TITLE) {
            return new RestaurantDetailTitleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_title, parent, false));
        } else if (viewType == VIEW_TYPE_LINK) {
            return new RestaurantLineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_link, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public RestaurantChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MENU) {
            return new RestaurantMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_menu, parent, false));
        } else if (viewType == VIEW_TYPE_DETAIL_OPEN_HOUR) {
            return new RestaurantDetailOpenHourViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_open_hour, parent, false));
        } else if (viewType == VIEW_TYPE_DETAIL_CONTACT) {
            return new RestaurantDetailContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_contact, parent, false));
        } else if (viewType == VIEW_TYPE_DETAIL_ORIGIN) {
            return new RestaurantDetailOriginViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_origin, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindGroupViewHolder(RestaurantGroupViewHolder holder, int groupPosition, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            holder.setItem(mRestaurant);
            ((RestaurantInfoViewHolder) holder).getMenuView().setOnClickListener(mOnClickListener);
            ((RestaurantInfoViewHolder) holder).getDetailView().setOnClickListener(mOnClickListener);
            if (mMode == MODE_MENU) {
                ((RestaurantInfoViewHolder) holder).getMenuView().setSelected(true);
                ((RestaurantInfoViewHolder) holder).getDetailView().setSelected(false);
            } else if (mMode == MODE_DETAIL) {
                ((RestaurantInfoViewHolder) holder).getMenuView().setSelected(false);
                ((RestaurantInfoViewHolder) holder).getDetailView().setSelected(true);
            }
        } else if (viewType == VIEW_TYPE_CATEGORY) {
            holder.setItem(mRestaurant.getCategoryList().get(groupPosition - 1));
            holder.itemView.setTag(groupPosition);
            holder.itemView.setOnClickListener(mOnClickListener);
            final int expandState = holder.getExpandStateFlags();
            if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
                boolean isExpanded = (expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0;
                ((RestaurantCategoryViewHolder) holder).mArrow.setImageResource(isExpanded ? R.mipmap.btn_arrow_bottom : R.mipmap.btn_arrow_top);
            }
        } else if (viewType == VIEW_TYPE_DETAIL) {
            holder.setItem(mRestaurant);
            ((RestaurantDetailViewHolder) holder).setExpand(mDetailMessageExpanded);
            ((RestaurantDetailViewHolder) holder).getExpandView().setTag(groupPosition);
            ((RestaurantDetailViewHolder) holder).getExpandView().setOnClickListener(mOnClickListener);
        } else if (viewType == VIEW_TYPE_DETAIL_TITLE) {
            String name = "";
            if (groupPosition == 2) {
                name = "영업시간";
                holder.itemView.setOnClickListener(null);
                ((RestaurantDetailTitleViewHolder) holder).mArrow.setVisibility(View.GONE);
            } else if (groupPosition == 3) {
                name = "위치&연락처";
                holder.itemView.setOnClickListener(null);
                ((RestaurantDetailTitleViewHolder) holder).mArrow.setVisibility(View.GONE);
            } else if (groupPosition == 4) {
                name = "원산지정보";
                holder.itemView.setTag(groupPosition);
                holder.itemView.setOnClickListener(mOnClickListener);
                ((RestaurantDetailTitleViewHolder) holder).mArrow.setVisibility(View.VISIBLE);
                final int expandState = holder.getExpandStateFlags();
                if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
                    boolean isExpanded = (expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0;
                    ((RestaurantDetailTitleViewHolder) holder).mArrow.setImageResource(isExpanded ? R.mipmap.btn_arrow_bottom : R.mipmap.btn_arrow_top);
                }
            }
            holder.setItem(name);
        } else if (viewType == VIEW_TYPE_LINK) {
            holder.setItem(mRestaurant.getInfo().getNaverURL());
        }
    }

    @Override
    public void onBindChildViewHolder(RestaurantChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        if (viewType == VIEW_TYPE_MENU) {
            Menu menu = mRestaurant.getMenuMap().get(mRestaurant.getCategoryList().get(groupPosition - 1)).get(childPosition);
            holder.setItem(menu);
            holder.itemView.setTag(menu);
            holder.itemView.setOnClickListener(mOnClickListener);
        } else if (viewType == VIEW_TYPE_DETAIL_OPEN_HOUR) {
            holder.setItem(mRestaurant);
        } else if (viewType == VIEW_TYPE_DETAIL_CONTACT) {
            holder.setItem(mRestaurant);
        } else if (viewType == VIEW_TYPE_DETAIL_ORIGIN) {
            holder.setItem(mRestaurant);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(RestaurantGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        if (holder.getItemViewType() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private interface Expandable extends ExpandableItemConstants {
    }

    public abstract static class RestaurantGroupViewHolder extends AbstractExpandableItemViewHolder {
        public RestaurantGroupViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void setItem(Object item);
    }

    public abstract static class RestaurantChildViewHolder extends AbstractExpandableItemViewHolder {
        public RestaurantChildViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void setItem(Object item);
    }

    public static class RestaurantInfoViewHolder extends RestaurantGroupViewHolder {

        private TextView mRating;
        private TextView mMinimumOrderAmount;
        private TextView mCoupon;
        private View mDeliveryTipLayout;
        private DeliveryTipView mDeliveryTipView;
        private View mMenu;
        private View mDetail;

        public RestaurantInfoViewHolder(View itemView) {
            super(itemView);
            mRating = (TextView) itemView.findViewById(R.id.restaurant_info_rating);
            mMinimumOrderAmount = (TextView) itemView.findViewById(R.id.restaurant_info_minimum_order_amount);
            mCoupon = (TextView) itemView.findViewById(R.id.restaurant_info_coupon);
            mDeliveryTipLayout = itemView.findViewById(R.id.restaurant_info_delivery_tip_layout);
            mDeliveryTipView = (DeliveryTipView) itemView.findViewById(R.id.restaurant_info_delivery_tip);
            mMenu = itemView.findViewById(R.id.restaurant_info_menu);
            mDetail = itemView.findViewById(R.id.restaurant_info_detail);
            mMenu.setSelected(true);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Restaurant restaurant = (Restaurant) item;
                mRating.setText(String.format(Locale.getDefault(), "%s(%d)", UnitUtils.ratingFormat(restaurant.getRateAvg()), restaurant.getRateCount()));
                mRating.setVisibility(restaurant.getRateCount() >= 5 ? View.VISIBLE : View.GONE);
                mMinimumOrderAmount.setText(String.format(Locale.getDefault(), "최소주문금액 : %s", restaurant.getMinimumOrderAmountString()));
                mCoupon.setVisibility(restaurant.isContracted() && restaurant.getDeliveryType() == Restaurant.DELIVERY_TYPE_FOODFLY ? View.VISIBLE : View.GONE);
                mDeliveryTipLayout.setVisibility(restaurant.getDeliveryTips() == null || restaurant.getDeliveryTips().size() < 2 ? View.GONE : View.VISIBLE);
                mDeliveryTipView.setDeliveryTips(restaurant.getDeliveryTips());
            }
        }

        public View getMenuView() {
            return mMenu;
        }

        public View getDetailView() {
            return mDetail;
        }
    }

    public static class RestaurantCategoryViewHolder extends RestaurantGroupViewHolder {

        private TextView mName;
        private View mIcon;
        private ImageView mArrow;

        public RestaurantCategoryViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.restaurant_category_name);
            mIcon = itemView.findViewById(R.id.restaurant_category_icon);
            mArrow = (ImageView) itemView.findViewById(R.id.restaurant_category_arrow);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Category category = (Category) item;
                mName.setText(category.getName());
                mName.setTypeface(null, category.getId().equals("popular") ? Typeface.BOLD : Typeface.NORMAL);
                mIcon.setVisibility(category.getId().equals("popular") ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static class RestaurantMenuViewHolder extends RestaurantChildViewHolder {

        private TextView mName;
        private NetworkImageView mImage;
        private TextView mOriginalPrice;
        private TextView mPrice;

        public RestaurantMenuViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.restaurant_menu_name);
            mImage = (NetworkImageView) itemView.findViewById(R.id.restaurant_menu_image);
            mOriginalPrice = (TextView) itemView.findViewById(R.id.restaurant_menu_original_price);
            mOriginalPrice.setPaintFlags(mOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mPrice = (TextView) itemView.findViewById(R.id.restaurant_menu_price);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Menu menu = (Menu) item;
                String name = menu.getName();
                if (!TextUtils.isEmpty(name)) {
                    name = name.replace("\t", "");
                }
                mName.setText(name);
                Drawable off = ResourcesCompat.getDrawable(mName.getResources(), R.mipmap.off, null);
                off.setBounds(0, 0, CommonUtils.convertSpToPx(mName.getContext(), 28), CommonUtils.convertSpToPx(mName.getContext(), 14));
                mName.setCompoundDrawables(null, null, menu.isSoldOut() ? off : null, null);
                mImage.setImageUrl(menu.getThumbnail(), VolleySingleton.getInstance(mImage.getContext()).getImageLoader());
                if (menu.getDiscountType() != 0) {
                    mOriginalPrice.setVisibility(View.VISIBLE);
                    mOriginalPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(menu.getPrice())));
                    mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(menu.getDiscountedPrice())));
                } else {
                    mOriginalPrice.setVisibility(View.GONE);
                    mPrice.setText(String.format(Locale.getDefault(), "%s원", UnitUtils.priceFormat(menu.getPrice())));
                }
            }
        }
    }

    public static class RestaurantDetailViewHolder extends RestaurantGroupViewHolder {

        private ImageView mLogo;
        private TextView mMessage;
        private TextView mMessageExpanded;
        private View mExpand;

        public RestaurantDetailViewHolder(View itemView) {
            super(itemView);
            mLogo = (ImageView) itemView.findViewById(R.id.restaurant_detail_logo);
            mMessage = (TextView) itemView.findViewById(R.id.restaurant_detail_message);
            mMessageExpanded = (TextView) itemView.findViewById(R.id.restaurant_detail_message_expanded);
            mExpand = itemView.findViewById(R.id.restaurant_detail_message_expand);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Restaurant restaurant = (Restaurant) item;
                Glide.with(mLogo.getContext()).load(restaurant.getInfo().getLogo()).placeholder(R.drawable.placeholder).crossFade().into(mLogo);
                mLogo.setVisibility(TextUtils.isEmpty(restaurant.getInfo().getLogo()) ? View.GONE : View.VISIBLE);
                mMessage.setText(Html.fromHtml(restaurant.getInfo().getRestaurantInfo().replace("\n", "<br>")));
                mMessageExpanded.setText(Html.fromHtml(restaurant.getInfo().getRestaurantInfo().replace("\n", "<br>")));
            }
        }

        public void setExpand(boolean expand) {
            mMessage.setVisibility(expand ? View.GONE : View.VISIBLE);
            mMessageExpanded.setVisibility(expand ? View.VISIBLE : View.GONE);
            if (!expand) {
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(CommonUtils.getScreenWidth() - CommonUtils.convertDipToPx(mMessageExpanded.getContext(), 32), View.MeasureSpec.AT_MOST);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                mMessageExpanded.measure(widthMeasureSpec, heightMeasureSpec);
                if (mMessageExpanded.getLineCount() > 3) {
                    mExpand.setVisibility(View.VISIBLE);
                } else {
                    mExpand.setVisibility(View.GONE);
                }
            } else {
                mExpand.setVisibility(View.GONE);
            }
        }

        public View getExpandView() {
            return mExpand;
        }
    }

    public static class RestaurantDetailTitleViewHolder extends RestaurantGroupViewHolder {

        private TextView mName;
        private ImageView mArrow;

        public RestaurantDetailTitleViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.restaurant_detail_title_name);
            mArrow = (ImageView) itemView.findViewById(R.id.restaurant_detail_title_arrow);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                String name = (String) item;
                mName.setText(name);
            }
        }
    }

    public static class RestaurantDetailOpenHourViewHolder extends RestaurantChildViewHolder {

        private TextView mOpenHour;
        private View mOffDayLayout;
        private TextView mOffDay;

        public RestaurantDetailOpenHourViewHolder(View itemView) {
            super(itemView);
            mOpenHour = (TextView) itemView.findViewById(R.id.restaurant_detail_open_hour);
            mOffDayLayout = itemView.findViewById(R.id.restaurant_detail_open_hour_bottom_layout);
            mOffDay = (TextView) itemView.findViewById(restaurant_detail_open_hour_off_day);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Restaurant restaurant = (Restaurant) item;
                String[] openHours = restaurant.getOpenHoursString();
                mOpenHour.setText(openHours[0]);
                mOffDay.setText(openHours[1]);
                mOffDayLayout.setVisibility(TextUtils.isEmpty(openHours[1]) ? View.GONE : View.VISIBLE);
            }
        }
    }

    public static class RestaurantDetailContactViewHolder extends RestaurantChildViewHolder {

        private static String NAVER_MAP_URL = "http://openapi.naver.com/map/getStaticMap?version=1.0&crs=EPSG:4326&center=%f,%f&level=13&w=%d&h=%d&maptype=default&markers=%f,%f&key=%s&uri=%s";

        private TextView mAddress;
        private ImageView mMapImage;

        public RestaurantDetailContactViewHolder(final View itemView) {
            super(itemView);
            mAddress = (TextView) itemView.findViewById(R.id.restaurant_detail_contact_address);
            mMapImage = (ImageView) itemView.findViewById(R.id.restaurant_detail_contact_map_image);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                final Restaurant restaurant = (Restaurant) item;
                mAddress.setText(restaurant.getAddress().getFormattedAddress());
                if (!TextUtils.isEmpty(restaurant.getInfo().getTel())) {
                    mAddress.append("\n");
                    mAddress.append(restaurant.getInfo().getTel());
                }
                final double lon = restaurant.getAddress().getLon();
                final double lat = restaurant.getAddress().getLat();
                mMapImage.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(mMapImage.getContext()).load(String.format(Locale.getDefault(), NAVER_MAP_URL, lon, lat, mMapImage.getMeasuredWidth(), mMapImage.getMeasuredHeight(), lon, lat, mMapImage.getResources().getString(R.string.api_key_naver), mMapImage.getResources().getString(R.string.api_uri_naver))).placeholder(R.drawable.placeholder).crossFade().into(mMapImage);
                        mMapImage.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonUtils.openMap(v.getContext(), lat, lon);
                            }
                        });
                    }
                });
            }
        }
    }

    public static class RestaurantDetailOriginViewHolder extends RestaurantChildViewHolder {

        private TextView mContent;

        public RestaurantDetailOriginViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.restaurant_detail_origin_content);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                Restaurant restaurant = (Restaurant) item;
                mContent.setText(Html.fromHtml(restaurant.getInfo().getOriginalInfo().replace("\n", "<br>")));
            }
        }
    }

    public static class RestaurantLineViewHolder extends RestaurantGroupViewHolder {

        private View mNaver;

        public RestaurantLineViewHolder(View itemView) {
            super(itemView);
            mNaver = itemView.findViewById(R.id.restaurant_link_naver);
        }

        @Override
        protected void setItem(Object item) {
            if (item != null) {
                final String naver = (String) item;
                mNaver.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(naver, "utf-8")));
                            v.getContext().startActivity(intent);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}