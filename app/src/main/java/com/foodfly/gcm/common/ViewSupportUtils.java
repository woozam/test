package com.foodfly.gcm.common;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by woody on 2016-04-18.
 */
public class ViewSupportUtils {

    public static void showSoftInput(final View view) {
        view.requestFocus();
        view.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        });
    }

    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideSoftInput(Context context, IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    public static void enableEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setTextIsSelectable(true);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setLongClickable(true);
        }
    }

    public static void disableEditText(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setTextIsSelectable(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setLongClickable(false);
        }
    }
}