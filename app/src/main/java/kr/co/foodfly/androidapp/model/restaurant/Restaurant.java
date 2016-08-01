package kr.co.foodfly.androidapp.model.restaurant;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.data.RealmString;

/**
 * Created by woozam on 2016-06-23.
 */
public class Restaurant extends RealmObject {

    public static final int CATEGORY_ALL = 0;
    public static final int CATEGORY_KOREAN = 1;
    public static final int CATEGORY_WESTERN = 2;
    public static final int CATEGORY_BURGER = 15;
    public static final int CATEGORY_CHICKEN = 5;
    public static final int CATEGORY_FUSION = 12;
    public static final int CATEGORY_CAFE = 16;
    public static final int CATEGORY_JAPANESE = 13;
    public static final int CATEGORY_CHINESE = 14;
    public static final int CATEGORY_FLOUR_BASED = 17;
    public static final int CATEGORY_PIZZA = 18;

    public static final int DELIVERY_TYPE_FOODFLY = 1;
    public static final int DELIVERY_TYPE_DIRECT = 2;
    public static final int DELIVERY_TYPE_TAKEOUT = 3;

    @PrimaryKey
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("tag")
    private String mTag;
    @SerializedName("thumbnail")
    private String mThumbnail;
    @SerializedName("rate_avg")
    private float mRateAvg;
    @SerializedName("rate_count")
    private int mRateCount;
    @SerializedName("review_count")
    private int mReviewCount;
    @SerializedName("distance")
    private float mDistance;
    @SerializedName("delivery_type")
    private int mDeliveryType;
    @SerializedName("delivery_tips")
    private RealmList<FeeInfo> mDeliveryTips;
    @SerializedName("delivery_fees")
    private RealmList<FeeInfo> mDeliveryFees;
    @SerializedName("takeout_available")
    private boolean mTakeoutAvailable;
    @SerializedName("is_open")
    private boolean mOpen;
    @SerializedName("is_break_time")
    private boolean mBreakTime;
    @SerializedName("is_area_open")
    private boolean mAreaOpen;
    @SerializedName("is_contracted")
    private boolean mContracted;
    @SerializedName("available_distance")
    private float mAvailableDistance;
    @SerializedName("status")
    private int mStatus;
    @SerializedName("delivery_status")
    private int mDeliveryStatus;
    @SerializedName("delivery_available_distance")
    private float mDeliveryAvailableDistance;
    @SerializedName("discount_type")
    private int mDiscountType; // type => 0:없음, 1:배달팁할인(%), 2:배달팁할인(원), 3:총액할인(원)
    @SerializedName("discount_amount")
    private int mDiscountAmount;
    @SerializedName("reservation_offset")
    private int mReservationOffset;
    @SerializedName("flags")
    private Flags mFlags;
    @SerializedName("address")
    private Address mAddress;
    @SerializedName("info")
    private Info mInfo;
    @SerializedName("open_hours")
    private RealmList<OpenHour> mOpenHours;
    @SerializedName("images")
    private RealmList<RealmString> mImages;
    @SerializedName("categories")
    private RealmList<Category> mCategories;
    @SerializedName("menus")
    private RealmList<Menu> mMenus;
    @SerializedName("popular_menus")
    private RealmList<RealmString> mPopularMenus;
    @SerializedName("is_favorite")
    private boolean mFavorite;
    @SerializedName("opening_hour")
    private OpeningHour mOpeningHour;
    @SerializedName("extra_delivery_fee_rule")
    private ExtraDeliveryFeeRule mExtraDeliveryFeeRule;

    @Index
    private Date mLastVisitTime;

    @Ignore
    private HashMap<Category, ArrayList<Menu>> mMenuMap;
    @Ignore
    private ArrayList<Category> mCategoryList;
    @Ignore
    String[] mOpenHourString;

    public Restaurant() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    public float getRateAvg() {
        return mRateAvg;
    }

    public void setRateAvg(float rateAvg) {
        mRateAvg = rateAvg;
    }

    public int getRateCount() {
        return mRateCount;
    }

    public void setRateCount(int rateCount) {
        mRateCount = rateCount;
    }

    public int getReviewCount() {
        return mReviewCount;
    }

    public void setReviewCount(int reviewCount) {
        mReviewCount = reviewCount;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        mDistance = distance;
    }

    public int getDeliveryType() {
        return mDeliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        mDeliveryType = deliveryType;
    }

    public RealmList<FeeInfo> getDeliveryTips() {
        return mDeliveryTips;
    }

    public void setDeliveryTips(RealmList<FeeInfo> deliveryTips) {
        mDeliveryTips = deliveryTips;
    }

    public int getDeliveryTip(int price) {
        int tip = 0;
        for (int i = 0; i < mDeliveryTips.size(); i++) {
            FeeInfo feeInfo = mDeliveryTips.get(i);
            if (feeInfo.getMinimumOrderAmount() <= price) {
                tip = feeInfo.getFee();
            }
        }
        return tip;
    }

    public RealmList<FeeInfo> getDeliveryFees() {
        return mDeliveryFees;
    }

    public void setDeliveryFees(RealmList<FeeInfo> deliveryFees) {
        mDeliveryFees = deliveryFees;
    }

    public boolean isTakeoutAvailable() {
        return mTakeoutAvailable;
    }

    public void setTakeoutAvailable(boolean takeoutAvailable) {
        mTakeoutAvailable = takeoutAvailable;
    }

    public boolean isOpen() {
        return mOpen;
    }

    public void setOpen(boolean open) {
        mOpen = open;
    }

    public boolean isBreakTime() {
        return mBreakTime;
    }

    public void setBreakTime(boolean breakTime) {
        mBreakTime = breakTime;
    }

    public boolean isAreaOpen() {
        return mAreaOpen;
    }

    public void setAreaOpen(boolean areaOpen) {
        mAreaOpen = areaOpen;
    }

    public boolean isContracted() {
        return mContracted;
    }

    public void setContracted(boolean contracted) {
        mContracted = contracted;
    }

    public float getAvailableDistance() {
        return mAvailableDistance;
    }

    public void setAvailableDistance(float availableDistance) {
        mAvailableDistance = availableDistance;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getDeliveryStatus() {
        return mDeliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        mDeliveryStatus = deliveryStatus;
    }

    public float getDeliveryAvailableDistance() {
        return mDeliveryAvailableDistance;
    }

    public void setDeliveryAvailableDistance(float deliveryAvailableDistance) {
        mDeliveryAvailableDistance = deliveryAvailableDistance;
    }

    public int getDiscountType() {
        return mDiscountType;
    }

    public void setDiscountType(int discountType) {
        mDiscountType = discountType;
    }

    public String getDiscountTypeString() {
        if (getDiscountType() != 0) {
            if (getDiscountType() == 1 || getDiscountType() == 2) {
                return "배달팁할인";
            } else {
                return "총액할인";
            }
        }
        return null;
    }

    public int getDiscountAmount() {
        return mDiscountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        mDiscountAmount = discountAmount;
    }

    public int getReservationOffset() {
        return mReservationOffset;
    }

    public void setReservationOffset(int reservationOffset) {
        mReservationOffset = reservationOffset;
    }

    public Flags getFlags() {
        return mFlags;
    }

    public void setFlags(Flags flags) {
        mFlags = flags;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public Info getInfo() {
        return mInfo;
    }

    public void setInfo(Info info) {
        mInfo = info;
    }

    public RealmList<OpenHour> getOpenHours() {
        return mOpenHours;
    }

    public void setOpenHours(RealmList<OpenHour> openHours) {
        mOpenHours = openHours;
    }

    public long[] getAvailableDeliveryHour() {
        Calendar calendar = GregorianCalendar.getInstance();
        long start = System.currentTimeMillis();
        start += (getReservationOffset() == 0 ? 2 * 3600000 : getReservationOffset() * 3600000);
        calendar.setTimeInMillis(start);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, 10 - minute % 10);
        int startTimeH = calendar.get(Calendar.HOUR_OF_DAY);
        int startTimeM = calendar.get(Calendar.MINUTE);
        int startTime = startTimeH * 60 + startTimeM;
        start = calendar.getTimeInMillis();

        long end = start;
        String[] ends = mOpeningHour.getEnd().split(":");
        if (ends.length >= 2) {
            int endTimeH = Integer.parseInt(ends[0]);
            int endTimeM = Integer.parseInt(ends[1]);
            int endTime = endTimeH * 60 + endTimeM;
            if (endTime < startTime) {
                endTime += 24 * 60;
            }
            int addTime = endTime - startTime;
            calendar.add(Calendar.MINUTE, addTime);
            end = calendar.getTimeInMillis();
        }
        return new long[]{start, end};
    }

    public String[] getOpenHoursString() {
        if (mOpenHourString == null) {
            String timeString = "";
            String offDayString = "";
            boolean sameTimeWeekday = true;
            boolean sameTimeWeekend = true;
            ArrayList<OpenHour> weekdayOpenHours = new ArrayList<>();
            ArrayList<OpenHour> weekendOpenHours = new ArrayList<>();
            for (OpenHour openHour : mOpenHours) {
                List<OpenHour> openHours = openHour.getWeekday() == 6 || openHour.getWeekday() == 7 ? weekendOpenHours : weekdayOpenHours;
                if (openHour.getWeekday() == 6 || openHour.getWeekday() == 7 ? sameTimeWeekend : sameTimeWeekday) {
                    for (OpenHour weekendOpenHour : openHours) {
                        if (!TextUtils.equals(openHour.getStart(), weekendOpenHour.getStart()) || !TextUtils.equals(openHour.getEnd(), weekendOpenHour.getEnd())) {
                            if (openHour.getWeekday() == 6 || openHour.getWeekday() == 7) {
                                sameTimeWeekend = false;
                            } else {
                                sameTimeWeekday = false;
                            }
                        }
                    }
                }
                openHours.add(openHour);
                if (openHour.isOff()) {
                    offDayString += offDayString.length() > 0 ? ", " : "";
                    offDayString += openHour.getDayString();
                }
            }
            if (sameTimeWeekday) {
                timeString += String.format(Locale.getDefault(), "평일 %s~%s", weekdayOpenHours.get(0).getStart().substring(0, 5), weekdayOpenHours.get(0).getEnd().substring(0, 5));
            } else {
                for (OpenHour openHour : weekdayOpenHours) {
                    timeString += timeString.length() > 0 ? "\n" : "";
                    timeString += String.format(Locale.getDefault(), "%s %s~%s", openHour.getDayString(), openHour.getStart().substring(0, 5), openHour.getEnd().substring(0, 5));
                }
            }
            if (sameTimeWeekend) {
                timeString += timeString.length() > 0 ? "\n" : "";
                timeString += String.format(Locale.getDefault(), "주말 %s~%s", weekendOpenHours.get(0).getStart().substring(0, 5), weekendOpenHours.get(0).getEnd().substring(0, 5));
            } else {
                for (OpenHour openHour : weekendOpenHours) {
                    timeString += timeString.length() > 0 ? "\n" : "";
                    timeString += String.format(Locale.getDefault(), "%s %s~%s", openHour.getDayString(), openHour.getStart().substring(0, 5), openHour.getEnd().substring(0, 5));
                }
            }
            return mOpenHourString = new String[]{timeString, offDayString};
        } else {
            return mOpenHourString;
        }
    }

    public RealmList<RealmString> getImages() {
        return mImages;
    }

    public void setImages(RealmList<RealmString> images) {
        mImages = images;
    }

    public RealmList<Category> getCategories() {
        return mCategories;
    }

    public void setCategories(RealmList<Category> categories) {
        mCategories = categories;
    }

    public RealmList<Menu> getMenus() {
        return mMenus;
    }

    public void setMenus(RealmList<Menu> menus) {
        mMenus = menus;
    }

    public RealmList<RealmString> getPopularMenus() {
        return mPopularMenus;
    }

    public void setPopularMenus(RealmList<RealmString> popularMenus) {
        mPopularMenus = popularMenus;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public OpeningHour getOpeningHour() {
        return mOpeningHour;
    }

    public void setOpeningHour(OpeningHour openingHour) {
        mOpeningHour = openingHour;
    }

    public ExtraDeliveryFeeRule getExtraDeliveryFeeRule() {
        return mExtraDeliveryFeeRule;
    }

    public void setExtraDeliveryFeeRule(ExtraDeliveryFeeRule extraDeliveryFeeRule) {
        mExtraDeliveryFeeRule = extraDeliveryFeeRule;
    }

    public Date getLastVisitTime() {
        return mLastVisitTime;
    }

    public void setLastVisitTime(Date lastVisitTime) {
        mLastVisitTime = lastVisitTime;
    }

    public boolean isAvailable() {
        if (!isAreaOpen()) {
            return false;
        } else if (!isOpen()) {
            return false;
        } else if (getDeliveryStatus() != 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getAvailableTitle() {
        if (!isAreaOpen()) {
            return "배달 준비중";
        } else if (!isOpen()) {
            return "영업 준비중";
        } else if (getDeliveryStatus() == 1) {
            return "테이크아웃만 가능";
        } else if (getDeliveryStatus() == 2) {
            return "일시적으로 배송불가";
        } else if (getDeliveryStatus() == 3) {
            return "일시적으로 배송불가";
        } else {
            return "";
        }
    }

    public String getAvailableMessage() {
        if (!isAreaOpen()) {
            return "해당지역은 서비스 운영 준비중이므로 주문을 하실 수 없습니다.";
        } else if (!isOpen()) {
            return "현재 음식점은 영업 준비중이므로 주문을 하실 수 없습니다.";
        } else if (getDeliveryStatus() == 1) {
            return String.format(Locale.getDefault(), "실시간 배송현황에 따른 현재 최대배송거리는 %s 입니다. 테이크아웃 주문만 가능합니다.", UnitUtils.distanceFormat(getAvailableDistance()));
        } else if (getDeliveryStatus() == 2) {
            return String.format(Locale.getDefault(), "실시간 배송현황에 따른 현재 최대배송거리는 %s 입니다.", UnitUtils.distanceFormat(getAvailableDistance()));
        } else if (getDeliveryStatus() == 3) {
            return "죄송합니다. 선택하신 배송지는 현재 일시적으로 배송이 불가합니다.";
        } else {
            return "";
        }
    }

    public String getOriginalTipString() {
        if (getDeliveryTips().get(0).getFee() < getDeliveryTips().get(0).getOriginalFee()) {
            return UnitUtils.priceFormat(getDeliveryFees().get(0).getOriginalFee()) + "원";
        } else {
            return null;
        }
    }

    public String getTipString() {
        String result = "";
        if (getDeliveryTips().size() > 1) {
            int tip = Integer.MAX_VALUE;
            int tipMinimumOrderAmount = 0;
            int minimumOrderAmount = Integer.MAX_VALUE;
            for (int i = 0; i < getDeliveryTips().size(); i++) {
                if (getDeliveryTips().get(i).getFee() < tip) {
                    tip = getDeliveryTips().get(i).getFee();
                    tipMinimumOrderAmount = getDeliveryTips().get(i).getMinimumOrderAmount();
                }
                if (minimumOrderAmount > getDeliveryTips().get(i).getMinimumOrderAmount()) {
                    minimumOrderAmount = getDeliveryTips().get(i).getMinimumOrderAmount();
                }
            }
            result += UnitUtils.priceFormat(tip) + "원";
            if (tipMinimumOrderAmount > minimumOrderAmount) {
                result += " (" + UnitUtils.priceFormat(tipMinimumOrderAmount) + "원 이상)";
            }
        } else {
            if (getDeliveryTips().get(0).getFee() == 0) {
                result += "무료";
            } else {
                result += UnitUtils.priceFormat(getDeliveryFees().get(0).getFee()) + "원";
            }
        }
        return result;
    }

    public String getMinimumOrderAmountString() {
        int minimumOrderAmount = Integer.MAX_VALUE;
        for (int i = 0; i < getDeliveryTips().size(); i++) {
            if (minimumOrderAmount > getDeliveryTips().get(i).getMinimumOrderAmount()) {
                minimumOrderAmount = getDeliveryTips().get(i).getMinimumOrderAmount();
            }
        }
        if (minimumOrderAmount < Integer.MAX_VALUE) {
            return UnitUtils.priceFormat(minimumOrderAmount) + "원";
        } else {
            return "-";
        }
    }

    public int getMinimumOrderAmount() {
        int minimumOrderAmount = Integer.MAX_VALUE;
        for (int i = 0; i < getDeliveryTips().size(); i++) {
            if (minimumOrderAmount > getDeliveryTips().get(i).getMinimumOrderAmount()) {
                minimumOrderAmount = getDeliveryTips().get(i).getMinimumOrderAmount();
            }
        }
        if (minimumOrderAmount < Integer.MAX_VALUE) {
            return minimumOrderAmount;
        } else {
            return 0;
        }
    }

    public String getDeliveryTypeString() {
        if (getDeliveryType() == DELIVERY_TYPE_DIRECT) {
            return "음식점 자체배달";
        } else if (getDeliveryType() == DELIVERY_TYPE_TAKEOUT) {
            return "테이크아웃";
        } else {
            return null;
        }
    }

    private void makeupMenu() {
        mMenuMap = new HashMap<>();
        mCategoryList = new ArrayList<>();
        HashMap<String, Menu> menus = new HashMap<>();
        HashMap<String, Category> categories = new HashMap<>();
        for (Menu menu : mMenus) {
            menus.put(menu.getId(), menu);
        }
        for (Category category : mCategories) {
            categories.put(category.getId(), category);
        }
        if (mPopularMenus != null && mPopularMenus.size() > 0) {
            Category category = new Category();
            category.setId("popular");
            category.setName("인기메뉴");
            ArrayList<Menu> menuList = new ArrayList<>();
            for (RealmString realmString : mPopularMenus) {
                menuList.add(menus.get(realmString.getValue()));
            }
            mMenuMap.put(category, menuList);
            mCategoryList.add(category);
        }
        for (Menu menu : mMenus) {
            Category category = categories.get(menu.getCategoryId());
            if (category == null) {
                category = new Category();
                category.setId("none");
                category.setName("-");
            }
            ArrayList<Menu> menuList = mMenuMap.get(category);
            if (menuList == null) {
                menuList = new ArrayList<>();
                mMenuMap.put(category, menuList);
                mCategoryList.add(category);
            }
            menuList.add(menu);
        }
    }

    public HashMap<Category, ArrayList<Menu>> getMenuMap() {
        if (mMenuMap == null) {
            makeupMenu();
        }
        return mMenuMap;
    }

    public ArrayList<Category> getCategoryList() {
        if (mCategoryList == null) {
            makeupMenu();
        }
        return mCategoryList;
    }
}