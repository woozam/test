package com.foodfly.gcm.app.view.recyclerView;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

    protected int mHeight;
    protected ArrayList<Integer> mPosition;

    public LinearSpacingItemDecoration(int height, Integer... position) {
        mHeight = height;
        mPosition = new ArrayList<>();
        if (position != null) {
            Collections.addAll(mPosition, position);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (mPosition.size() > 0 && !mPosition.contains(parent.getChildAdapterPosition(view))) {
                continue;
            }
            if (((LinearLayoutManager) parent.getLayoutManager()).getOrientation() == LinearLayoutManager.VERTICAL) {
                outRect.top = mHeight;
                if (parent.getAdapter().getItemCount() - 1 == parent.getChildAdapterPosition(view)) {
                    outRect.bottom = mHeight;
                }
            } else {
                outRect.left = mHeight;
            }
        }
    }
}