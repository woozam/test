package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class OpenHour extends RealmObject {

    @SerializedName("weekday")
    private int mWeekday;
    @SerializedName("start")
    private String mStart;
    @SerializedName("end")
    private String mEnd;
    @SerializedName("off")
    private boolean mOff;

    public OpenHour() {
    }

    public int getWeekday() {
        return mWeekday;
    }

    public void setWeekday(int weekday) {
        mWeekday = weekday;
    }

    public String getStart() {
        return mStart;
    }

    public void setStart(String start) {
        mStart = start;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String end) {
        mEnd = end;
    }

    public boolean isOff() {
        return mOff;
    }

    public void setOff(boolean off) {
        mOff = off;
    }

    public String getDayString() {
        switch (mWeekday) {
            case 1:
                return "월요일";
            case 2:
                return "화요일";
            case 3:
                return "수요일";
            case 4:
                return "목요일";
            case 5:
                return "금요일";
            case 6:
                return "토요일";
            case 7:
                return "일요일";
            default:
                return "";
        }
    }
}