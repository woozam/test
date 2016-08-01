package kr.co.foodfly.androidapp.app.activity.etc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.model.user.UserManager;
import kr.co.foodfly.androidapp.model.user.UserResponse;

/**
 * Created by woozam on 2016-07-26.
 */
public class WebViewActivity extends AbsWebViewActivity {

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IS_ROOT = "extra_is_root";

    public static void createInstance(Context context, String url, String title, boolean isRoot) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_IS_ROOT, isRoot);
        context.startActivity(intent);
    }

    private ContentLoadingProgressBar mProgressBar;
    private Map<String, String> mExtraHeaders = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);
        String title = intent.getStringExtra(EXTRA_TITLE);
        boolean isRoot = intent.getBooleanExtra(EXTRA_IS_ROOT, false);

        if (TextUtils.isEmpty(title)) {
            getSupportActionBar().setCustomView(R.layout.main_custom_action_bar);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } else {
            getSupportActionBar().setTitle(title);
        }
        if (!isRoot) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (url.contains("chefly.foodfly.co.kr") || url.equals("http://goo.gl/Ad6Bhq")) {
            getSupportActionBar().hide();
            mCloseLayout.setVisibility(View.VISIBLE);
        }

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.web_view_progress_bar);
        mWebView.setWebChromeClient(new myWebChromeClient());
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mWebView.getSettings().setSupportMultipleWindows(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(new MyView());
        UserResponse user = UserManager.fetchUser();
        if (user != null) {
            mExtraHeaders.put("FF_iOS_Auth_Token", user.getAuthToken());
            mExtraHeaders.put("FF_iOS_User_Id", user.getUser().getUserName());
        }
        mWebView.loadUrl(url, mExtraHeaders);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class MyView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("chefly.foodfly.co.kr")) {
                getSupportActionBar().hide();
                mCloseLayout.setVisibility(View.VISIBLE);
            }
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if (!TextUtils.isEmpty(scheme) && (scheme.equals("foodflyios") || scheme.equals("foodfly"))) {
                IntentFilterActivity.createInstance(WebViewActivity.this, url);
                finish();
                return true;
            } else {
                view.loadUrl(url, mExtraHeaders);
                mProgressBar.setVisibility(View.VISIBLE);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    class myWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(WebViewActivity.this, message, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}