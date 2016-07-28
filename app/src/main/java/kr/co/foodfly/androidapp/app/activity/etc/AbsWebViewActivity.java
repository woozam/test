package kr.co.foodfly.androidapp.app.activity.etc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.app.activity.BaseActivity;

/**
 * Created by woozam on 2016-07-07.
 */
public abstract class AbsWebViewActivity extends BaseActivity {

    protected WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }
}