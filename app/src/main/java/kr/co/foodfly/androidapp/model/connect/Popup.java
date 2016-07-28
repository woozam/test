package kr.co.foodfly.androidapp.model.connect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import io.realm.RealmObject;
import kr.co.foodfly.androidapp.R;
import kr.co.foodfly.androidapp.common.PreferenceUtils;
import kr.co.foodfly.androidapp.network.VolleySingleton;

/**
 * Created by woozam on 2016-07-16.
 */
public class Popup extends RealmObject {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_WEB_VIEW = 2;

    @SerializedName("id")
    private String mId;
    @SerializedName("content")
    private String mContent;
    @SerializedName("redirect_url")
    private String mRedirectUrl;
    @SerializedName("type")
    private int mType;
    @SerializedName("force_redirect")
    private boolean mForceRedirect;

    public Popup() {
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public boolean isForceRedirect() {
        return mForceRedirect;
    }

    public void setForceRedirect(boolean forceRedirect) {
        mForceRedirect = forceRedirect;
    }

    public void show(Context context) {
        if (PreferenceUtils.getLongValue(getId(), 0) < System.currentTimeMillis() - 86400000) {
            final String id = getId();
            MaterialDialog.Builder builder = new Builder(context);
            View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_popup, null);
            builder.customView(popupView, true);
            final MaterialDialog dialog = builder.positiveText("닫기").neutralText("하루동안 보지 않기").positiveColorRes(R.color.textColorHint).onNeutral(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    PreferenceUtils.setValue(id, System.currentTimeMillis());
                }
            }).show();
            final TextView text = (TextView) popupView.findViewById(R.id.popup_text);
            final NetworkImageView image = (NetworkImageView) popupView.findViewById(R.id.popup_image);
            final WebView webView = (WebView) popupView.findViewById(R.id.popup_web_view);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            switch (getType()) {
                case TYPE_TEXT:
                    text.setVisibility(View.VISIBLE);
                    text.setText(getContent());
                    break;
                case TYPE_IMAGE:
                    image.setVisibility(View.VISIBLE);
                    image.setImageUrl(getContent(), VolleySingleton.getInstance(context).getImageLoader());
                    image.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getRedirectUrl() != null) {
                                try {
                                    Uri uri = Uri.parse(URLDecoder.decode(getRedirectUrl(), "utf-8"));
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(uri);
                                    v.getContext().startActivity(intent);
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    break;
                case TYPE_WEB_VIEW:
                case 3:
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(getContent());
                    break;
            }
            ((ViewGroup) ((ViewGroup) dialog.getView()).getChildAt(1)).getChildAt(0).setPadding(0, 0, 0, 0);
            popupView.setPadding(0, 0, 0, 0);
        }
    }
}
