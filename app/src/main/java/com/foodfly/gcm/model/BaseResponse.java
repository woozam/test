package com.foodfly.gcm.model;

import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by woozam on 2016-07-08.
 */
public class BaseResponse {

    @SerializedName("error")
    private ResponseError mError;

    public ResponseError getError() {
        return mError;
    }

    public void setError(ResponseError error) {
        mError = error;
    }

    public static <T> T parseError(Class<T> type, VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String json = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                Log.v("parseErrorResponse", json);
                return new Gson().fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                error.printStackTrace();
            }
        } else {
            if (error instanceof NoConnectionError) {
                //                Toast.makeText(context, "network_error_host_not_found", Toast.LENGTH_SHORT).show();
            } else {
                //                Toast.makeText(context, "unknown_error_occurred", Toast.LENGTH_SHORT).show();
            }
            error.printStackTrace();
        }
        return null;
    }
}
