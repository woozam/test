package kr.co.foodfly.androidapp.model.connect;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by woozam on 2016-07-16.
 */
public class MileageRule extends RealmObject {

    @SerializedName("grade")
    private int mGrade;
    @SerializedName("threshold")
    private int mThreshold;

    public MileageRule() {
    }

    public int getGrade() {
        return mGrade;
    }

    public void setGrade(int grade) {
        mGrade = grade;
    }

    public int getThreshold() {
        return mThreshold;
    }

    public void setThreshold(int threshold) {
        mThreshold = threshold;
    }
}