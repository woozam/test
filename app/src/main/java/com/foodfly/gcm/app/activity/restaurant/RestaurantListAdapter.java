package com.foodfly.gcm.app.activity.restaurant;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.foodfly.gcm.R;
import com.foodfly.gcm.app.view.CircleViewPagerIndicator;
import com.foodfly.gcm.app.view.CommonFooter;
import com.foodfly.gcm.app.view.recyclerView.LazyAdapter;
import com.foodfly.gcm.common.CommonUtils;
import com.foodfly.gcm.common.UnitUtils;
import com.foodfly.gcm.model.connect.Banner;
import com.foodfly.gcm.model.restaurant.Restaurant;
import com.foodfly.gcm.network.VolleySingleton;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by woozam on 2016-06-23.
 */
public class RestaurantListAdapter extends LazyAdapter {

    public static final int DETAIL_IMAGE_ITEM_VIEW_TYPE = 3998;
    public static final int BANNER_ITEM_VIEW_TYPE = 3997;
    public static final int COMMON_FOOTER_ITEM_VIEW_TYPE = 3996;

    private ArrayList<Restaurant> mRestaurantList;
    private int mImageHeight;
    private int mRowLayoutResId;
    private String mDetailImageUrl;
    private boolean mUseFavorite;
    private OnClickListener mOnClickListener;
    private List<Banner> mBannerList;
    private boolean mUseCommonFooter;

    public RestaurantListAdapter(ArrayList<Restaurant> restaurantList, int imageHeight, int rowLayoutResId, String detailImageUrl, boolean useFavorite, OnClickListener onClickListener) {
        this(restaurantList, imageHeight, rowLayoutResId, detailImageUrl, useFavorite, onClickListener, null, false);
    }

    public RestaurantListAdapter(ArrayList<Restaurant> restaurantList, int imageHeight, int rowLayoutResId, String detailImageUrl, boolean useFavorite, OnClickListener onClickListener, List<Banner> bannerList, boolean useCommonFooter) {
        mRestaurantList = restaurantList;
        mImageHeight = imageHeight;
        mRowLayoutResId = rowLayoutResId;
        mDetailImageUrl = detailImageUrl;
        mUseFavorite = useFavorite;
        mOnClickListener = onClickListener;
        mBannerList = bannerList;
        mUseCommonFooter = useCommonFooter;
    }

    public void setBannerList(List<Banner> bannerList) {
        mBannerList = bannerList;
        notifyDataSetChanged();
    }

    public List<Banner> getBannerList() {
        return mBannerList;
    }

    public String getDetailImageUrl() {
        return mDetailImageUrl;
    }

    @Override
    public int getItemViewType(int position) {
        if (mBannerList != null && mBannerList.size() > 0) {
            if (position == 0) {
                return BANNER_ITEM_VIEW_TYPE;
            } else if (!TextUtils.isEmpty(mDetailImageUrl) && position == 1) {
                return DETAIL_IMAGE_ITEM_VIEW_TYPE;
            } else {
                if (mUseCommonFooter & getOriginalItemCount() > 0) {
                    if (mLazyLoad) {
                        if (position == getItemCount() - 2) {
                            return LAZY_LOAD_ITEM_VIEW_TYPE;
                        } else if (position == getItemCount() - 1) {
                            return COMMON_FOOTER_ITEM_VIEW_TYPE;
                        }
                    } else {
                        if (position == getItemCount() - 1) {
                            return COMMON_FOOTER_ITEM_VIEW_TYPE;
                        }
                    }
                }
                return super.getItemViewType(position);
            }
        } else {
            if (!TextUtils.isEmpty(mDetailImageUrl) && position == 0) {
                return DETAIL_IMAGE_ITEM_VIEW_TYPE;
            } else {
                if (mUseCommonFooter & getOriginalItemCount() > 0) {
                    if (mLazyLoad) {
                        if (position == getItemCount() - 2) {
                            return LAZY_LOAD_ITEM_VIEW_TYPE;
                        } else if (position == getItemCount() - 1) {
                            return COMMON_FOOTER_ITEM_VIEW_TYPE;
                        }
                    } else {
                        if (position == getItemCount() - 1) {
                            return COMMON_FOOTER_ITEM_VIEW_TYPE;
                        }
                    }
                }
                return super.getItemViewType(position);
            }
        }
    }

    @Override
    public int getOriginalItemViewType(int position) {
        return 0;
    }

    @Override
    public LazyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new RestaurantListViewHolder(LayoutInflater.from(parent.getContext()).inflate(mRowLayoutResId, parent, false), mImageHeight);
        } else if (viewType == DETAIL_IMAGE_ITEM_VIEW_TYPE) {
            return new RestaurantListDetailImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_detail_image, parent, false));
        } else if (viewType == BANNER_ITEM_VIEW_TYPE) {
            return new RestaurantListBannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_list_banner, parent, false));
        } else if (viewType == COMMON_FOOTER_ITEM_VIEW_TYPE) {
            return new CommonFooterViewHolder(new CommonFooter(parent.getContext()));
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final LazyViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ((RestaurantListViewHolder) holder).setItem(getItem(position));
            ((RestaurantListViewHolder) holder).getFavoriteIcon().setVisibility(mUseFavorite ? View.VISIBLE : View.GONE);
            ((RestaurantListViewHolder) holder).getFavoriteIcon().setTag(position);
            ((RestaurantListViewHolder) holder).getFavoriteIcon().getDrawable().setColorFilter(ResourcesCompat.getColor(((RestaurantListViewHolder) holder).itemView.getResources(), R.color.colorPrimary, null), Mode.SRC_ATOP);
            ((RestaurantListViewHolder) holder).getFavoriteIcon().setOnClickListener(mOnClickListener);
            ((RestaurantListViewHolder) holder).itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Restaurant restaurant = getItem(holder.getAdapterPosition());
                    if (!restaurant.isAvailable()) {
                        String message = restaurant.getAvailableMessage();
                        if (!restaurant.isAreaOpen() && restaurant.isServiceArea()) {
                            message += "\n" + String.format(Locale.getDefault(), "서비스 운영 시간은 %s 입니다.", restaurant.getConnectAreaOpenHour());
                        }
                        new MaterialDialog.Builder(v.getContext()).content(message).positiveText("확인").onPositive(new SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                RestaurantActivity.createInstance(v.getContext(), restaurant.getId());
                            }
                        }).show();
                    } else {
                        RestaurantActivity.createInstance(v.getContext(), restaurant.getId());
                    }
                }
            });
        } else if (holder.getItemViewType() == DETAIL_IMAGE_ITEM_VIEW_TYPE) {
            ((RestaurantListDetailImageViewHolder) holder).setImageUrl(mDetailImageUrl);
        } else if (holder.getItemViewType() == BANNER_ITEM_VIEW_TYPE) {
            ((RestaurantListBannerViewHolder) holder).onBind(mBannerList);
        } else if (holder.getItemViewType() == COMMON_FOOTER_ITEM_VIEW_TYPE) {
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (mBannerList != null && mBannerList.size() > 0 ? 1 : 0) + super.getItemCount() + (!TextUtils.isEmpty(mDetailImageUrl) ? 1 : 0) + (mUseCommonFooter && getOriginalItemCount() > 0 ? 1 : 0);
    }

    @Override
    public int getOriginalItemCount() {
        return mRestaurantList.size();
    }

    public Restaurant getItem(int position) {
        return mRestaurantList.get(position - (mBannerList != null && mBannerList.size() > 0 ? 1 : 0) - (!TextUtils.isEmpty(mDetailImageUrl) ? 1 : 0));
    }

    public int getHeaderCount() {
        return (TextUtils.isEmpty(mDetailImageUrl) ? 0 : 1) + (mBannerList != null && mBannerList.size() > 0 ? 1 : 0);
    }

    public static class RestaurantListBannerViewHolder extends LazyViewHolder implements OnPageChangeListener {

        private List<Banner> mBannerList = new ArrayList<>();
        private BannerAdapter mBannerAdapter;
        private ViewPager mViewPager;
        private CircleViewPagerIndicator mIndicator;

        public RestaurantListBannerViewHolder(View itemView) {
            super(itemView);
            itemView.getLayoutParams().height = CommonUtils.getScreenWidth() / 4;
            mBannerAdapter = new BannerAdapter();
            mViewPager = (ViewPager) itemView.findViewById(R.id.restaurant_list_banner_view_pager);
            mViewPager.setAdapter(mBannerAdapter);
            mViewPager.addOnPageChangeListener(this);
            mIndicator = (CircleViewPagerIndicator) itemView.findViewById(R.id.restaurant_list_banner_view_pager_indicator);
        }

        public void onBind(List<Banner> bannerList) {
            mBannerList = bannerList;
            mBannerAdapter.notifyDataSetChanged();
            mIndicator.setPageCount(mBannerList.size());
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mIndicator.setPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        private class BannerAdapter extends PagerAdapter {

            @Override
            public int getCount() {
                return mBannerList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final Banner banner = mBannerList.get(position);
                ImageView imageView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.item_banner, container, false);
                container.addView(imageView);
                if (banner.getImage().endsWith("gif")) {
                    Glide.with(imageView.getContext()).load(banner.getImage()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.placeholder).crossFade().into(imageView);
                } else {
                    Glide.with(imageView.getContext()).load(banner.getImage()).placeholder(R.drawable.placeholder).crossFade().into(imageView);
                }
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(banner.getUrl(), "utf-8")));
                            v.getContext().startActivity(intent);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        }
    }

    public static class RestaurantListDetailImageViewHolder extends LazyViewHolder {

        private NetworkImageView mImage;

        public RestaurantListDetailImageViewHolder(View itemView) {
            super(itemView);
            mImage = (NetworkImageView) itemView.findViewById(R.id.restaurant_detail_image);
            mImage.getLayoutParams().height = 1;
        }

        public void setImageUrl(final String url) {
            mImage.post(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.equals(mImage.getImageURL(), url)) {
                        mImage.setImageUrl(url, VolleySingleton.getInstance(itemView.getContext()).getImageLoader());
                    }
                }
            });
        }
    }

    public static class RestaurantListViewHolder extends LazyViewHolder {

        private ImageView mImage;
        private TextView mName;
        private View mFavorite;
        private TextView mTag;
        private TextView mOriginalDeliveryTip;
        private TextView mDeliveryTip;
        private TextView mMinimumOrderAmount;
        private TextView mClose;
        private TextView mDistance;
        private ImageView mDeliveryTypeIcon;
        private TextView mDeliveryType;
        private ImageView mDeliveryTypeIcon2;
        private TextView mDeliveryType2;
        private TextView mRating;

        public RestaurantListViewHolder(View itemView, int imageHeight) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.restaurant_image);
            mImage.getLayoutParams().height = imageHeight;
            mName = (TextView) itemView.findViewById(R.id.restaurant_name);
            mFavorite = itemView.findViewById(R.id.restaurant_favorite);
            mTag = (TextView) itemView.findViewById(R.id.restaurant_tag);
            mOriginalDeliveryTip = (TextView) itemView.findViewById(R.id.restaurant_delivery_original_tip);
            mOriginalDeliveryTip.setPaintFlags(mOriginalDeliveryTip.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mDeliveryTip = (TextView) itemView.findViewById(R.id.restaurant_delivery_tip);
            mMinimumOrderAmount = (TextView) itemView.findViewById(R.id.restaurant_minimum_order_amount);
            mClose = (TextView) itemView.findViewById(R.id.restaurant_close);
            mDistance = (TextView) itemView.findViewById(R.id.restaurant_distance);
            mDeliveryTypeIcon = (ImageView) itemView.findViewById(R.id.restaurant_delivery_type_icon);
            mDeliveryType = (TextView) itemView.findViewById(R.id.restaurant_delivery_type);
            mDeliveryTypeIcon2 = (ImageView) itemView.findViewById(R.id.restaurant_delivery_type_icon_2);
            mDeliveryType2 = (TextView) itemView.findViewById(R.id.restaurant_delivery_type_2);
            mRating = (TextView) itemView.findViewById(R.id.restaurant_rating);
        }

        public void setItem(final Restaurant restaurant) {
            Glide.with(mImage.getContext()).load(restaurant.getThumbnail()).placeholder(R.drawable.placeholder).crossFade().into(mImage);
            mName.setText(restaurant.getName());
            mFavorite.setSelected(restaurant.isFavorite());
            mTag.setText(restaurant.getTag());
            mOriginalDeliveryTip.setText(restaurant.getOriginalTipString());
            mOriginalDeliveryTip.setVisibility(mOriginalDeliveryTip.length() > 0 ? View.VISIBLE : View.GONE);
            mDeliveryTip.setText(restaurant.getTipString());
            mMinimumOrderAmount.setText(String.format(Locale.getDefault(), "최소주문: %s", restaurant.getMinimumOrderAmountString()));
            if (restaurant.isAvailable()) {
                mClose.setVisibility(View.INVISIBLE);
            } else {
                mClose.setVisibility(View.VISIBLE);
                mClose.setText(restaurant.getAvailableTitle());
            }
            mDistance.setText(UnitUtils.distanceFormat(restaurant.getDistance()));

            String deliveryTypeString = restaurant.getDeliveryTypeString();
            String discountTypeString = restaurant.getDiscountTypeString();
            if (!TextUtils.isEmpty(deliveryTypeString)) {
                mDeliveryType.setText(deliveryTypeString);
                mDeliveryType.setVisibility(View.VISIBLE);
                mDeliveryTypeIcon.setVisibility(View.VISIBLE);
                mDeliveryTypeIcon.setImageResource(R.mipmap.cloche);
                if (!TextUtils.isEmpty(discountTypeString)) {
                    mDeliveryType2.setText(discountTypeString);
                    mDeliveryType2.setVisibility(View.VISIBLE);
                    mDeliveryTypeIcon2.setVisibility(View.VISIBLE);
                    mDeliveryTypeIcon2.setImageResource(restaurant.getDiscountType() == 1 ? R.mipmap.discount_p : R.mipmap.discount_w);
                } else {
                    mDeliveryType2.setVisibility(View.GONE);
                    mDeliveryTypeIcon2.setVisibility(View.GONE);
                }
            } else {
                mDeliveryType2.setVisibility(View.GONE);
                mDeliveryTypeIcon2.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(discountTypeString)) {
                    mDeliveryType.setText(discountTypeString);
                    mDeliveryType.setVisibility(View.VISIBLE);
                    mDeliveryTypeIcon.setVisibility(View.VISIBLE);
                    mDeliveryTypeIcon.setImageResource(restaurant.getDiscountType() == 1 ? R.mipmap.discount_p : R.mipmap.discount_w);
                } else {
                    mDeliveryType.setVisibility(View.GONE);
                    mDeliveryTypeIcon.setVisibility(View.GONE);
                }
            }

            if (restaurant.getRateCount() >= 5) {
                mRating.setVisibility(View.VISIBLE);
                mRating.setText(UnitUtils.ratingFormat(restaurant.getRateAvg()));
            } else {
                mRating.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                }
            });
        }

        public ImageView getFavoriteIcon() {
            return (ImageView) mFavorite;
        }
    }

    public static class CommonFooterViewHolder extends LazyViewHolder {

        public CommonFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}