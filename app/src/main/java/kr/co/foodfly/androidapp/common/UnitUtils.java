package kr.co.foodfly.androidapp.common;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by woozam on 2016-06-24.
 */
public class UnitUtils {

    private static DecimalFormat mFormatInt = new DecimalFormat("###,###,###,###");

    public static String priceFormat(int price) {
        return mFormatInt.format(price);
    }

    public static String distanceFormat(float distance) {
        float result = Math.round(distance * 10) / 10f;
        return String.format(Locale.getDefault(), "%.1fkm", result);
    }

    public static String ratingFormat(float rating) {
        float result = Math.round(rating * 10) / 10f;
        return String.format(Locale.getDefault(), "%.1f", result);
    }

    public static String signedPrice(int value) {
        if (value > 0) {
            return String.format(Locale.getDefault(), "+%s", priceFormat(value));
        } else {
            return priceFormat(value);
        }
    }
}