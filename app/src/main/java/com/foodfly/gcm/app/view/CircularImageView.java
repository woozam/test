package com.foodfly.gcm.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by woody on 2015-08-03.
 */
public class CircularImageView extends NetworkImageView {

    public CircularImageView(Context context) {
        super(context);
        initialize();
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageDrawable(new CircularBitmapDrawable(getResources(), bm));
    }
}