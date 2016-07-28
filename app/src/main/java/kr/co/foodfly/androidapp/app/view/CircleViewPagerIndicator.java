package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.CommonUtils;

public class CircleViewPagerIndicator extends View {

    private static final int SIZE_UNSPECIFIED = -1;

    private Paint mPaint;
    private int mMaxAlpha = 0xff;
    private int mMinAlpha = 0x50;
    private int mHorizontalSpacing = 0;
    private float mPosition = 0;
    private int mMaxWidth = 0;
    private int mMaxHeight = 0;
    private int mPageCount = 3;
    private Bitmap mIndicatorBitmap;
    private int mHorizontalArrowSpacing = 0;
    private Bitmap mArrowLeft;
    private Bitmap mArrowRight;

    public CircleViewPagerIndicator(Context context) {
        super(context);
        initialize();
    }

    public CircleViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CircleViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.img_circle_indicator, null);
        ;
        drawable.setBounds(0, 0, CommonUtils.convertDipToPx(getContext(), 4), CommonUtils.convertDipToPx(getContext(), 4));
        mIndicatorBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(mIndicatorBitmap);
        drawable.draw(canvas);
        mHorizontalSpacing = CommonUtils.convertDipToPx(getContext(), 10);
        mHorizontalArrowSpacing = CommonUtils.convertDipToPx(getContext(), 12);
        computeMaxSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, mMaxWidth);
        final int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, mMaxHeight);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
        final int widthSize = resolveSizeAndStateRespectingMinSize(getSuggestedMinimumWidth(), getMeasuredWidth(), widthMeasureSpec);
        final int heightSize = resolveSizeAndStateRespectingMinSize(getSuggestedMinimumWidth(), getMeasuredHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == SIZE_UNSPECIFIED) {
            return measureSpec;
        }
        final int size = MeasureSpec.getSize(measureSpec);
        final int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return measureSpec;
            case MeasureSpec.AT_MOST:
                return MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), MeasureSpec.EXACTLY);
            case MeasureSpec.UNSPECIFIED:
                return MeasureSpec.makeMeasureSpec(maxSize, MeasureSpec.EXACTLY);
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != SIZE_UNSPECIFIED) {
            final int desiredWidth = Math.max(minSize, measuredSize);
            return resolveSizeAndState(desiredWidth, measureSpec, 0);
        } else {
            return measuredSize;
        }
    }

    private void computeMaxSize() {
        mMaxWidth = 0;
        mMaxWidth += getPaddingLeft();
        mMaxWidth += getPaddingRight();
        mMaxWidth += mIndicatorBitmap.getWidth() * mPageCount;
        mMaxWidth += mHorizontalSpacing * Math.max(0, mPageCount - 1);
        if (mArrowLeft != null) {
            mMaxWidth += mHorizontalArrowSpacing;
            mMaxWidth += mArrowLeft.getWidth();
        }
        if (mArrowRight != null) {
            mMaxWidth += mHorizontalArrowSpacing;
            mMaxWidth += mArrowRight.getWidth();
        }
        mMaxHeight = 0;
        mMaxHeight += getPaddingTop();
        mMaxHeight += getPaddingBottom();
        int indicatorHeight = mIndicatorBitmap.getHeight();
        int arrowLeftHeight = mArrowLeft == null ? 0 : mArrowLeft.getHeight();
        int arrowRightHeight = mArrowRight == null ? 0 : mArrowRight.getHeight();
        mMaxHeight += Math.max(indicatorHeight, Math.max(arrowLeftHeight, arrowRightHeight));
    }

    public void setIndicator(Bitmap indicator) {
        mIndicatorBitmap = indicator;
        computeMaxSize();
        requestLayout();
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
        computeMaxSize();
        requestLayout();
    }

    public void setPageCount(int count) {
        mPageCount = count;
        computeMaxSize();
        requestLayout();
    }

    public void setPosition(float position) {
        mPosition = position;
        invalidate();
    }

    public void setArrow(Bitmap arrowLeft, Bitmap arrowRight) {
        mArrowLeft = arrowLeft;
        mArrowRight = arrowRight;
        computeMaxSize();
        requestLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int left = getPaddingLeft();
        int integer = (int) Math.floor(mPosition);
        float fractional = mPosition - integer;
        int shift = 0;
        if (mArrowLeft != null) {
            mPaint.setAlpha(mMaxAlpha);
            canvas.drawBitmap(mArrowLeft, left + shift, mMaxHeight / 2 - mArrowLeft.getHeight() / 2, mPaint);
            shift += mHorizontalArrowSpacing + mArrowLeft.getWidth();
        }
        for (int i = 0; i < mPageCount; i++) {
            if (i == integer) {
                mPaint.setAlpha(mMaxAlpha - (int) ((mMaxAlpha - mMinAlpha) * fractional));
            } else if (i == integer + 1) {
                mPaint.setAlpha(mMinAlpha + (int) ((mMaxAlpha - mMinAlpha) * fractional));
            } else {
                mPaint.setAlpha(mMinAlpha);
            }
            canvas.drawBitmap(mIndicatorBitmap, left + shift, mMaxHeight / 2 - mIndicatorBitmap.getHeight() / 2, mPaint);
            shift += mIndicatorBitmap.getWidth() + mHorizontalSpacing;
        }
        shift -= mHorizontalSpacing;
        if (mArrowRight != null) {
            mPaint.setAlpha(mMaxAlpha);
            shift += mHorizontalArrowSpacing;
            canvas.drawBitmap(mArrowRight, left + shift, mMaxHeight / 2 - mArrowRight.getHeight() / 2, mPaint);
        }
    }
}