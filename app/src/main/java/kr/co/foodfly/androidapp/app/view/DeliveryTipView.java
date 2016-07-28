package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.CommonUtils;
import kr.co.foodfly.androidapp.common.UnitUtils;
import kr.co.foodfly.androidapp.model.restaurant.FeeInfo;

/**
 * Created by woozam on 2016-07-17.
 */
public class DeliveryTipView extends View {

    private List<FeeInfo> mDeliveryTips;
    private TextPaint mTextPaint;
    private Paint mLinePaint;
    private Paint mLinePaint2;
    private int mLineOffset;
    private Drawable mPoint;
    private int mGap1;
    private int mGap2;
    private int mGap3;
    private int mGap4;

    public DeliveryTipView(Context context) {
        super(context);
        initialize();
    }

    public DeliveryTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DeliveryTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        if (isInEditMode()) {
            mDeliveryTips = new ArrayList<>();
            FeeInfo feeInfo = new FeeInfo();
            feeInfo.setMinimumOrderAmount(5000);
            feeInfo.setFee(4500);
            mDeliveryTips.add(feeInfo);
            feeInfo = new FeeInfo();
            feeInfo.setMinimumOrderAmount(10000);
            feeInfo.setFee(4000);
            mDeliveryTips.add(feeInfo);
            feeInfo = new FeeInfo();
            feeInfo.setMinimumOrderAmount(15000);
            feeInfo.setFee(3500);
            mDeliveryTips.add(feeInfo);
        }
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(CommonUtils.convertSpToPx(getContext(), 11.5f));
        mLineOffset = CommonUtils.convertDipToPx(getContext(), 36);
        mPoint = ResourcesCompat.getDrawable(getResources(), R.mipmap.point, null);
        mPoint.setBounds(0, 0, CommonUtils.convertDipToPx(getContext(), 10), CommonUtils.convertDipToPx(getContext(), 5));
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(CommonUtils.convertDipToPx(getContext(), 1));
        mLinePaint.setColor(ResourcesCompat.getColor(getResources(), R.color.textColorHint, null));
        mLinePaint2 = new Paint();
        mLinePaint2.setStrokeWidth(CommonUtils.convertDipToPx(getContext(), 2));
        mLinePaint2.setColor(ResourcesCompat.getColor(getResources(), R.color.gray, null));
        mGap1 = CommonUtils.convertDipToPx(getContext(), 5);
        mGap2 = CommonUtils.convertDipToPx(getContext(), 4);
        mGap3 = CommonUtils.convertDipToPx(getContext(), 4);
        mGap4 = CommonUtils.convertDipToPx(getContext(), 3);
    }

    public void setDeliveryTips(List<FeeInfo> deliveryTips) {
        mDeliveryTips = deliveryTips;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        height += (fontMetrics.bottom - fontMetrics.top) + mGap1;
        height += mPoint.getBounds().height() + mGap2;
        height += mGap3;
        height += mGap4;
        height += (fontMetrics.bottom - fontMetrics.top);
        setMeasuredDimension(widthSize, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mDeliveryTips != null) {
            FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            int cellWidth = (getMeasuredWidth() - mLineOffset * 2) / mDeliveryTips.size();
            int saveCount = canvas.save();
            for (int i = 0; i < mDeliveryTips.size(); i++) {
                canvas.restoreToCount(saveCount);
                saveCount = canvas.save();
                FeeInfo feeInfo = mDeliveryTips.get(i);
                String minimumOrderAmount;
                if (i == mDeliveryTips.size() - 1) {
                    mTextPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
                    minimumOrderAmount = UnitUtils.priceFormat(feeInfo.getMinimumOrderAmount()) + "원 이상";
                    mLinePaint2.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                } else {
                    mTextPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.textColorHint, null));
                    minimumOrderAmount = UnitUtils.priceFormat(feeInfo.getMinimumOrderAmount()) + "원";
                    mLinePaint2.setColor(ResourcesCompat.getColor(getResources(), R.color.gray, null));
                }
                canvas.drawText(minimumOrderAmount, 0, minimumOrderAmount.length(), mLineOffset + cellWidth * i, (fontMetrics.descent - fontMetrics.ascent), mTextPaint);

                canvas.translate(0, (fontMetrics.descent - fontMetrics.ascent) + mGap1);
                canvas.translate(mLineOffset + i * cellWidth - mPoint.getBounds().width() / 2, 0);
                mPoint.draw(canvas);
                canvas.translate(mPoint.getBounds().width() / 2, mPoint.getBounds().height() + mGap2);
                canvas.drawLine(0, 0, cellWidth, 0, mLinePaint);
                canvas.translate(0, mGap3);
                canvas.drawLine(0, 0, cellWidth, 0, mLinePaint2);
                canvas.translate(0, mGap4);

                String deliveryTip;
                if (i == mDeliveryTips.size() - 1) {
                    mTextPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
                    deliveryTip = UnitUtils.priceFormat(feeInfo.getFee()) + "원";
                } else {
                    mTextPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.textColorHint, null));
                    deliveryTip = UnitUtils.priceFormat(feeInfo.getFee()) + "원";
                }
                canvas.drawText(deliveryTip, 0, deliveryTip.length(), cellWidth / 2, (fontMetrics.descent - fontMetrics.ascent), mTextPaint);
            }
            canvas.restoreToCount(saveCount);
        }
    }
}