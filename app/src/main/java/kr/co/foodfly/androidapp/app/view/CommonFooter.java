package kr.co.foodfly.androidapp.app.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.kickOff.PrivacyPolicyActivity;
import kr.co.foodfly.androidapp.app.activity.kickOff.TermsActivity;

/**
 * Created by woozam on 2016-07-10.
 */
public class CommonFooter extends LinearLayout {
    public CommonFooter(Context context) {
        super(context);
        initialize();
    }

    public CommonFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CommonFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.footer_common, this, true);
        TextView textView = (TextView) findViewById(R.id.terms);
        SpannableStringBuilder ssb = new SpannableStringBuilder(textView.getText());
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                PrivacyPolicyActivity.createInstance(getContext());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(0xff9a9a9a);
                ds.setUnderlineText(false);
            }
        }, 7, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TermsActivity.createInstance(getContext());
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(0xff9a9a9a);
                ds.setUnderlineText(false);
            }
        }, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(ssb);
        textView.setHighlightColor(Color.TRANSPARENT);
    }
}