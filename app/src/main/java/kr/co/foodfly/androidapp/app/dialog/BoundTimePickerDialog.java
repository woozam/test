package kr.co.foodfly.androidapp.app.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

public class BoundTimePickerDialog extends TimePickerDialog {

    private int minHour = -1;
    private int minMinute = -1;

    private int maxHour = 25;
    private int maxMinute = 25;

    public BoundTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    public void setMin(int hour, int minute) {
        minHour = hour;
        minMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Log.d("DADADADA", "onTimeChanged");

        boolean validTime = true;
        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)) {
            validTime = false;
        }

        if (hourOfDay > maxHour || (hourOfDay == maxHour && minute > maxMinute)) {
            validTime = false;
        }

        final Button positive = getButton(BUTTON_POSITIVE);
        positive.setEnabled(validTime);
    }
}