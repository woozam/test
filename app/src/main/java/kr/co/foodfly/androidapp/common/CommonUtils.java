package kr.co.foodfly.androidapp.common;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import kr.co.foodfly.androidapp.Application;

public class CommonUtils {

    public static DisplayMetrics DISPLAY_METRICS;
    private static int screenWidth;
    private static int screenHeight;

    public static boolean isInstalledApp(Context context, String packageName) {
        try {
            List<PackageInfo> packageList = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
            for (PackageInfo info : packageList) {
                if (info != null && info.packageName != null && info.packageName.equals(packageName))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static int convertDipToPx(float dip) {
        if (DISPLAY_METRICS == null)
            DISPLAY_METRICS = Application.getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DISPLAY_METRICS);
    }

    public static int convertDipToPx(Context context, float dip) {
        if (DISPLAY_METRICS == null) DISPLAY_METRICS = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DISPLAY_METRICS);
    }

    public static int convertSpToPx(float sp) {
        if (DISPLAY_METRICS == null)
            DISPLAY_METRICS = Application.getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, DISPLAY_METRICS);
    }

    public static int convertSpToPx(Context context, float sp) {
        if (DISPLAY_METRICS == null) DISPLAY_METRICS = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, DISPLAY_METRICS);
    }

    public static String replaceInvalidCharatctersForFile(String string) {
        if (string == null) return null;
        string = string.replaceAll("[:\\?*/\"<>|]", "");
        return string;
    }

    public static int getScreenWidth() {
        screenWidth = Application.getContext().getResources().getDisplayMetrics().widthPixels;
        return screenWidth;
    }

    public static int getScreenWidthDp() {
        return (int) (getScreenWidth() / Application.getContext().getResources().getDisplayMetrics().density);
    }

    public static int getScreenHeight() {
        screenHeight = Application.getContext().getResources().getDisplayMetrics().heightPixels;
        return screenHeight;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = Application.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = Application.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getUDID() {
        return Build.SERIAL;
    }

    private static int COUNT = 0;

    public static String generateClientId() {
        return CommonUtils.getUDID() + "_" + System.currentTimeMillis() + "_" + COUNT++;
    }

    public static boolean checkEmailAddress(String email) {
        int atIndex = email.indexOf("@");
        int dotIndex = email.lastIndexOf(".");

        if (email.length() <= 5 || atIndex < 0 || dotIndex < 0 || atIndex > dotIndex || ((atIndex + 1) == dotIndex)) {
            return false;
        }

        return true;
    }

    public static void openBrowser(Context context, String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(url, "utf-8")));
            context.startActivity(browserIntent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void copyToClipboard(String content) {
        ClipData.Item item = new ClipData.Item(content);
        String[] mimeType = new String[1];
        mimeType[0] = ClipDescription.MIMETYPE_TEXT_PLAIN;
        ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
        ClipboardManager clipboardManager = (ClipboardManager) Application.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(cd);
    }
}