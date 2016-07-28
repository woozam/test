package kr.co.foodfly.androidapp.model.restaurant;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-06-23.
 */
public class OpeningHour extends RealmObject {

    @SerializedName("is_open")
    private boolean mOpen;
    @SerializedName("start")
    private String mStart;
    @SerializedName("end")
    private String mEnd;
    @SerializedName("has_breaktime")
    private boolean mBreakTime;
    @SerializedName("break_start")
    private String mBreakStart;
    @SerializedName("break_end")
    private String mBreakEnd;
    @SerializedName("date")
    private OpeningHourDate mDate;
    @SerializedName("extra")
    private OpeningHourExtra mExtra;

    public OpeningHour() {
    }

    public boolean isOpen() {
        return mOpen;
    }

    public void setOpen(boolean open) {
        mOpen = open;
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

    public boolean hasBreakTime() {
        return mBreakTime;
    }

    public void setBreakTime(boolean breakTime) {
        mBreakTime = breakTime;
    }

    public String getBreakStart() {
        return mBreakStart;
    }

    public void setBreakStart(String breakStart) {
        mBreakStart = breakStart;
    }

    public String getBreakEnd() {
        return mBreakEnd;
    }

    public void setBreakEnd(String breakEnd) {
        mBreakEnd = breakEnd;
    }

    public OpeningHourDate getDate() {
        return mDate;
    }

    public void setDate(OpeningHourDate date) {
        mDate = date;
    }

    public OpeningHourExtra getExtra() {
        return mExtra;
    }

    public void setExtra(OpeningHourExtra extra) {
        mExtra = extra;
    }
}