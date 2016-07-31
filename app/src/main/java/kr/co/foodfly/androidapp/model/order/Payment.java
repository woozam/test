package kr.co.foodfly.androidapp.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by woozam on 2016-07-28.
 */
public class Payment implements Serializable {

    @SerializedName("method")
    private String mMethod;
    @SerializedName("encoding")
    private String mEncoding;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("params")
    private PaymentParams mParams;
    @SerializedName("debug")
    private String mDebug;

    public Payment() {
    }

    public String getMethod() {
        return mMethod;
    }

    public void setMethod(String method) {
        mMethod = method;
    }

    public String getEncoding() {
        return mEncoding;
    }

    public void setEncoding(String encoding) {
        mEncoding = encoding;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public PaymentParams getParams() {
        return mParams;
    }

    public void setParams(PaymentParams params) {
        mParams = params;
    }

    public String getDebug() {
        return mDebug;
    }

    public void setDebug(String debug) {
        mDebug = debug;
    }
}