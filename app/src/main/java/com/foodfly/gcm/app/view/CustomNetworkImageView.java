package com.foodfly.gcm.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

import com.foodfly.gcm.common.CommonUtils;

/**
 * Created by woozam on 2016-07-11.
 */
public class CustomNetworkImageView extends NetworkImageView {
    public CustomNetworkImageView(Context context) {
        super(context);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm != null) {
            int height = (int) (bm.getHeight() / ((float) bm.getWidth() / CommonUtils.getScreenWidth()));
            if (getLayoutParams().height != height) {
                getLayoutParams().height = height;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
//        setImageUrl(null, VolleySingleton.getInstance(getContext()).getImageLoader());
        super.onDetachedFromWindow();
    }
}