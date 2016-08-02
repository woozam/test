package com.foodfly.gcm.app.activity.kickOff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import com.foodfly.gcm.R;
import com.foodfly.gcm.app.activity.etc.AbsWebViewActivity;
import com.foodfly.gcm.network.APIs;
import com.foodfly.gcm.network.GsonRequest;
import com.foodfly.gcm.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-07.
 */
public class PrivacyPolicyActivity extends AbsWebViewActivity implements OnClickListener {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, PrivacyPolicyActivity.class);
        context.startActivity(intent);
    }

    private View mSummaryView;
    private TextView mSummaryContent;
    private View mAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSummaryView = LayoutInflater.from(this).inflate(R.layout.view_privacy_policy_summary, mContainer, false);
        mSummaryContent = (TextView) mSummaryView.findViewById(R.id.privacy_policy_summary_content);
        mAll = mSummaryView.findViewById(R.id.privacy_policy_summary_all);
        mAll.setOnClickListener(this);
        mContainer.addView(mSummaryView);

        String url = APIs.getTermPath().appendPath(APIs.TERM_PRIVACY).appendQueryParameter(APIs.PARAM_SUMMARY, String.valueOf(true)).toString();
        GsonRequest<TermsResponse> request = new GsonRequest<>(Method.GET, url, TermsResponse.class, APIs.createHeaders(), new Listener<TermsResponse>() {
            @Override
            public void onResponse(TermsResponse response) {
                dismissProgressDialog();
                mSummaryContent.setText(Html.fromHtml(response.text));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgressDialog();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        showProgressDialog();
    }

    @Override
    public void onClick(View v) {
        if (v == mAll) {
            String url = APIs.getTermPath().appendPath(APIs.TERM_PRIVACY).toString();
            GsonRequest<TermsResponse> request = new GsonRequest<>(Method.GET, url, TermsResponse.class, APIs.createHeaders(), new Listener<TermsResponse>() {
                @Override
                public void onResponse(TermsResponse response) {
                    dismissProgressDialog();
                    mSummaryView.setVisibility(View.GONE);
                    mWebView.loadData(response.text, "text/html; charset=UTF-8", null);
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissProgressDialog();
                }
            });
            VolleySingleton.getInstance(this).addToRequestQueue(request);
            showProgressDialog();
        }
    }

    private class TermsResponse {
        String text;
    }
}
