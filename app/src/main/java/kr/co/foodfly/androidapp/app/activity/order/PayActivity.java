package kr.co.foodfly.androidapp.app.activity.order;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import kr.co.foodfly.androidapp.app.activity.etc.AbsWebViewActivity;
import kr.co.foodfly.androidapp.model.order.Payment;
import kr.co.foodfly.androidapp.model.order.PaymentParams;

/**
 * Created by woozam on 2016-07-31.
 */
public class PayActivity extends AbsWebViewActivity {

    public static final int REQ_CODE_PAY = PayActivity.class.hashCode() & 0x000000ff;
    public static final String EXTRA_PAYMENT = "extra_payment";

    public static void createInstance(Activity activity, Payment payment) {
        Intent intent = new Intent(activity, PayActivity.class);
        intent.putExtra(EXTRA_PAYMENT, payment);
        activity.startActivityForResult(intent, REQ_CODE_PAY);
    }

    private Payment mPayment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPayment = (Payment) intent.getSerializableExtra(EXTRA_PAYMENT);
        mWebView.setWebViewClient(new InicisWebViewClient(this));
        mWebView.setWebChromeClient(new myWebChromeClient());

        String body = "";
        PaymentParams params = mPayment.getParams();
        Field[] fields = PaymentParams.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (body.length() > 0) body += "&";
                Object a = field.get(params);
                body += field.getName() + "=" + URLEncoder.encode(String.valueOf(a), "UTF-8");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        mWebView.postUrl(mPayment.getUrl(), body.getBytes());
    }

    class InicisWebViewClient extends WebViewClient {

        private Activity activity;

        public InicisWebViewClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                Intent intent = null;

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                    Uri uri = Uri.parse(intent.getDataString());
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (URISyntaxException ex) {
                    return false;
                } catch (ActivityNotFoundException e) {
                    if (intent == null) return false;
                    if (handleNotFoundPaymentScheme(intent.getScheme())) return true;
                    String packageName = intent.getPackage();
                    if (packageName != null) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        return true;
                    }
                    return false;
                }
            }

            return false;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * @param scheme
         * @return 해당 scheme에 대해 처리를 직접 하는지 여부
         * <p/>
         * 결제를 위한 3rd-party 앱이 아직 설치되어있지 않아 ActivityNotFoundException이 발생하는 경우 처리합니다.
         * 여기서 handler되지않은 scheme에 대해서는 intent로부터 Package정보 추출이 가능하다면 다음에서 packageName으로 market이동합니다.
         */
        protected boolean handleNotFoundPaymentScheme(String scheme) {
            //PG사에서 호출하는 url에 package정보가 없어 ActivityNotFoundException이 난 후 market 실행이 안되는 경우
            if (PaymentScheme.ISP.equalsIgnoreCase(scheme)) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_ISP)));
                return true;
            } else if (PaymentScheme.BANKPAY.equalsIgnoreCase(scheme)) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PaymentScheme.PACKAGE_BANKPAY)));
                return true;
            }
            return false;
        }
    }

    class myWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(PayActivity.this, message, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public class PaymentScheme {

        public final static String ISP = "ispmobile";
        public final static String BANKPAY = "kftc-bankpay";
        public final static String HYUNDAI_APPCARD = "hdcardappcardansimclick"; //intent:hdcardappcardansimclick://appcard?acctid=201605092050048514902797477441#Intent;package=com.hyundaicard.appcard;end;

        public final static String PACKAGE_ISP = "kvp.jjy.MispAndroid320";
        public final static String PACKAGE_BANKPAY = "com.kftc.bankpay.android";

    }
}