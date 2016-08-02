package com.foodfly.gcm.app.view.recyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Size;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import com.foodfly.gcm.R;
import com.foodfly.gcm.common.CommonUtils;


public class LinearDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mPadding[];
    private ArrayList<Integer> mSkipPosition;
    private int mHeight;

    public LinearDividerItemDecoration(Context context) {
        this(context, new int[]{0, 0});
    }

    public LinearDividerItemDecoration(Context context, @Size(2) int[] padding) {
        this(context, padding, null);
    }

    public LinearDividerItemDecoration(Context context, @Size(2) int[] padding, ArrayList<Integer> skipPosition) {
        mDivider = new ColorDrawable(ResourcesCompat.getColor(context.getResources(), R.color.md_divider_black, null));
        mPadding = padding;
        mSkipPosition = new ArrayList<>();
        if (skipPosition != null) {
            mSkipPosition.addAll(skipPosition);
        }
        mHeight = CommonUtils.convertDipToPx(context, 1);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + mPadding[0];
        int right = parent.getWidth() - parent.getPaddingRight() - mPadding[1];

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childAdapterPosition = parent.getChildAdapterPosition(child);
            if (childAdapterPosition == state.getItemCount() - 1) {
            } else if (mSkipPosition.contains(childAdapterPosition)) {
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mHeight;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}