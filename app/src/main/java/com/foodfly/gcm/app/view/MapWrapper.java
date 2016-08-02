package com.foodfly.gcm.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by woozam on 2016-08-03.
 */
public class MapWrapper extends RelativeLayout {

    private boolean mTouchDown = false;
    private OnTouchUpListener mOnTouchUpListener;

    public boolean isTouchDown() {
        return mTouchDown;
    }

    public void setOnTouchUpListener(OnTouchUpListener onTouchUpListener) {
        mOnTouchUpListener = onTouchUpListener;
    }

    public interface OnTouchUpListener {
        void onTouchUp();
    }

    public MapWrapper(Context context) {
        super(context);
    }

    public MapWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDown = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchDown = false;
                mOnTouchUpListener.onTouchUp();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}