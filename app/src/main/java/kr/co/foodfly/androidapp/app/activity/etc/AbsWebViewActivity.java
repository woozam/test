package kr.co.foodfly.androidapp.app.activity.etc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;

/**
 * Created by woozam on 2016-07-07.
 */
public abstract class AbsWebViewActivity extends BaseActivity implements OnClickListener {

    protected ViewGroup mContainer;
    protected WebView mWebView;
    protected View mCloseLayout;
    protected View mClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContainer = (ViewGroup) findViewById(R.id.web_view_layout);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mCloseLayout = findViewById(R.id.web_view_close_layout);
        mCloseLayout.setVisibility(View.GONE);
        mClose = findViewById(R.id.web_view_close);
        mClose.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mClose) {
            finish();
        }
    }
}