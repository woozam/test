package kr.co.foodfly.androidapp.common;

import android.util.Log;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    private static final String TAG = "TimeUtil";
    private static final boolean DEBUG_LOG = false;

    private static long start;
    private static long end;
    private static long startNano;
    private static long endNano;

    private static SimpleDateFormat utctimeformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static SimpleDateFormat yearMonthDayTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat fullTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat hourMinuteTimeFormat = new SimpleDateFormat("HH:mm");

    static {
        utctimeformat.setTimeZone(TimeZone.getTimeZone("UTC"));
        yearMonthDayTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        fullTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        hourMinuteTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void start() {
        start = System.currentTimeMillis();
    }

    public static void elapsedTime(String str) {
        end = System.currentTimeMillis();
        Log.i(str, "duration : " + NumberFormat.getNumberInstance().format(end - start) + "ms");
        start = end;
    }

    public static void startNano() {
        startNano = System.nanoTime();
    }

    public static void elapsedTimeNano(String str) {
        endNano = System.nanoTime();
        Log.i(str, "duration : " + NumberFormat.getNumberInstance().format(endNano - startNano) + "ns");
        startNano = endNano;
    }

    public synchronized static String getUtcTimeString(long time) {
        return getUtcTimeDateFormat().format(new Date(time));
    }

    public synchronized static Date parseUtcTime(String utcTime) throws ParseException {
        return getUtcTimeDateFormat().parse(utcTime);
    }

    public static DateFormat getUtcTimeDateFormat() {
        return utctimeformat;
    }

    public synchronized static String getYearMonthDateTimeString(long time) {
        return getYearMonthDateTimeDateFormat().format(new Date(time));
    }

    public synchronized static Date parseYearMonthDateTime(String utcTime) throws ParseException {
        return getYearMonthDateTimeDateFormat().parse(utcTime);
    }

    public static DateFormat getYearMonthDateTimeDateFormat() {
        return yearMonthDayTimeFormat;
    }

    public synchronized static String getFullTimeString(long time) {
        return getFullTimeDateFormat().format(new Date(time));
    }

    public synchronized static Date parseFullTime(String utcTime) throws ParseException {
        return getFullTimeDateFormat().parse(utcTime);
    }

    public static DateFormat getFullTimeDateFormat() {
        return fullTimeFormat;
    }

    public synchronized static String getHourMinuteTimeString(long time) {
        return getHourMinuteTimeDateFormat().format(new Date(time));
    }

    public synchronized static Date parseFullHourMinuteTime(String utcTime) throws ParseException {
        return getHourMinuteTimeDateFormat().parse(utcTime);
    }

    public static DateFormat getHourMinuteTimeDateFormat() {
        return hourMinuteTimeFormat;
    }
}