package com.foodfly.gcm.app.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by woozam on 2016-08-02.
 */
public class FixedRelativeLayout extends RelativeLayout {

    private static final int AT_MOST_UNSPECIFIED = MeasureSpec.makeMeasureSpec((1 << 30) - 1, MeasureSpec.AT_MOST);

    public FixedRelativeLayout(Context context) {
        super(context);
    }

    public FixedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        // RelativeLayout has bugs when measured in UNSPECIFIED height mode that were supposedly fixed
        // in API 18. However the 'fix' does not work with breaks gravity == center_vertical in TextView.
        // https://code.google.com/p/android/issues/detail?id=63673
        if (VERSION.SDK_INT == 19) {
            if (MeasureSpec.getMode(heightSpec) == MeasureSpec.UNSPECIFIED) heightSpec = AT_MOST_UNSPECIFIED;
            super.onMeasure(widthSpec, heightSpec);
        } else {
            super.onMeasure(widthSpec, heightSpec);
        }
    }
}