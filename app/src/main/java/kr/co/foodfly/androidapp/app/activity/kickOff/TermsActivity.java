package kr.co.foodfly.androidapp.app.activity.kickOff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import kr.co.foodfly.androidapp.app.activity.etc.AbsWebViewActivity;
import kr.co.foodfly.androidapp.network.APIs;
import kr.co.foodfly.androidapp.network.GsonRequest;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-07.
 */
public class TermsActivity extends AbsWebViewActivity {

    public static void createInstance(Context context) {
        Intent intent = new Intent(context, TermsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = APIs.getTermPath().appendPath(APIs.TERM_GENERAL).toString();
        GsonRequest<TermsResponse> request = new GsonRequest<>(Method.GET, url, TermsResponse.class, APIs.createHeaders(), new Listener<TermsResponse>() {
            @Override
            public void onResponse(TermsResponse response) {
                dismissProgressDialog();
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

    private class TermsResponse {
        String text;
    }
}
