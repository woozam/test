package com.foodfly.gcm.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.foodfly.gcm.model.user.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import com.foodfly.gcm.BuildConfig;

public class GsonRequest<T> extends Request<T> {

    private static final Gson gson = new GsonBuilder()
            //            .setExclusionStrategies(new ExclusionStrategy() {
            //        @Override
            //        public boolean shouldSkipField(FieldAttributes f) {
            //            return f.getDeclaringClass().equals(RealmObject.class) || f.getDeclaringClass().equals(Drawable.class);
            //        }
            //
            //        @Override
            //        public boolean shouldSkipClass(Class<?> clazz) {
            //            return false;
            //        }
            //    })
            .create();

    private final Type type;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    protected byte[] mBody = null;
    private final Gson mParsonGson;
    private Map<String, String> mResponseHeaders;

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, type, headers, null, null, listener, errorListener, gson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener, Gson parseGson) {
        this(method, url, type, headers, null, null, listener, errorListener, parseGson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, Map<String, String> responseHeaders, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, type, headers, null, responseHeaders, listener, errorListener, gson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, Map<String, String> responseHeaders, Listener<T> listener, ErrorListener errorListener, Gson parseGson) {
        this(method, url, type, headers, null, responseHeaders, listener, errorListener, parseGson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, byte[] body, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, type, headers, body, null, listener, errorListener, gson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, byte[] body, Listener<T> listener, ErrorListener errorListener, Gson parseGson) {
        this(method, url, type, headers, body, null, listener, errorListener, parseGson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, byte[] body, Map<String, String> responseHeaders, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, type, headers, body, responseHeaders, listener, errorListener, gson);
    }

    public GsonRequest(int method, String url, Type type, Map<String, String> headers, byte[] body, Map<String, String> responseHeaders, Listener<T> listener, ErrorListener errorListener, Gson parseGson) {
        super(method, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        this.mBody = body;
        this.mResponseHeaders = responseHeaders;
        this.mParsonGson = parseGson;
        Log.v("GsonRequest", url);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mBody != null ? mBody : super.getBody();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            if (mResponseHeaders != null) {
                mResponseHeaders.putAll(response.headers);
            }
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (type == UserResponse.class) {
                json = json.replace("\"address\":[],", "");
            }
            if (BuildConfig.DEBUG) {
                String log = json;
                while (log.length() > 0) {
                    if (log.length() > 4000) {
                        Log.v("parseNetworkResponse", log.substring(0, 4000));
                        log = log.substring(4000);
                    } else {
                        Log.i("parseNetworkResponse", log);
                        break;
                    }
                }
            }
            return (Response<T>) Response.success(mParsonGson.fromJson(json, type), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}