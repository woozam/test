package kr.co.foodfly.androidapp.app.activity.order;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;

import kr.co.foodfly.androidapp.app.activity.etc.AbsWebViewActivity;
import kr.co.foodfly.androidapp.model.order.Payment;
import kr.co.foodfly.androidapp.model.order.PaymentParams;

/**
 * Created by woozam on 2016-07-31.
 */
public class PayActivity extends AbsWebViewActivity {

    public static final int REQ_CODE_PAY = PayActivity.class.hashCode() & 0x000000ff;
    public static final String EXTRA_PAYMENT = "extra_payment";

    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private static String DIALOG_CARDNM = "";

    public static void createInstance(Activity activity, Payment payment) {
        Intent intent = new Intent(activity, PayActivity.class);
        intent.putExtra(EXTRA_PAYMENT, payment);
        activity.startActivityForResult(intent, REQ_CODE_PAY);
    }

    private Payment mPayment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        mPayment = (Payment) intent.getSerializableExtra(EXTRA_PAYMENT);
        mWebView.setWebViewClient(new InicisWebViewClient());
        mWebView.setWebChromeClient(new myWebChromeClient());

        String body = "";
        PaymentParams params = mPayment.getParams();
        Field[] fields = PaymentParams.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (body.length() > 0) body += "&";
                Object a = field.get(params);
                if (a != null) {
                    body += field.getName() + "=" + URLEncoder.encode(String.valueOf(a), mPayment.getEncoding());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Log.d("<INIPAYMOBILE>", "Params : " + body);
        mWebView.postUrl(mPayment.getUrl(), body.getBytes());
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this).title("결제 취소").content("결제를 취소하시겠습니까?").positiveText("네").negativeText("아니오").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        }).show();
    }

    private void showCardInstallAlertDialog(final String coCardNm) {
        final Hashtable<String, String> cardNm = new Hashtable<>();
        cardNm.put("HYUNDAE", "현대 앱카드");
        cardNm.put("SAMSUNG", "삼성 앱카드");
        cardNm.put("LOTTE", "롯데 앱카드");
        cardNm.put("SHINHAN", "신한 앱카드");
        cardNm.put("KB", "국민 앱카드");
        cardNm.put("HANASK", "하나SK 통합안심클릭");
        //cardNm.put("SHINHAN_SMART",  "Smart 신한앱");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE", "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("LOTTEAPPCARD", "market://details?id=com.lcacApp");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK", "market://details?id=com.ilk.visa3d");
        //cardInstallUrl.put("SHINHAN_SMART",  "market://details?id=com.shcard.smartpay");//여기 수정 필요!!2014.04.01

        new MaterialDialog.Builder(this).title("알림").content(cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.").positiveText("설치").onPositive(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String installUrl = cardInstallUrl.get(coCardNm);
                Uri uri = Uri.parse(installUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                Log.d("<INIPAYMOBILE>", "Call : " + uri.toString());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    Toast.makeText(PayActivity.this, cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        }).negativeText("취소").onNegative(new SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Toast.makeText(PayActivity.this, "(-1)결제를 취소 하셨습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).show();
    }

    private void showAlertDialog(int id) {
        switch (id) {
            case DIALOG_ISP:
                new MaterialDialog.Builder(this).title("알림").content("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.").positiveText("설치").onPositive(new SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                        mWebView.loadUrl(ispUrl);
                        finish();
                    }
                }).negativeText("취소").onNegative(new SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(PayActivity.this, "(-1)결제를 취소 하셨습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).show();
                break;
            case DIALOG_CARDAPP:
                showCardInstallAlertDialog(DIALOG_CARDNM);
                break;
        }
    }

    class InicisWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /*
             * URL별로 분기가 필요합니다. 어플리케이션을 로딩하는것과
	    	 * WEB PAGE를 로딩하는것을 분리 하여 처리해야 합니다.
	    	 * 만일 가맹점 특정 어플 URL이 들어온다면
	    	 * 조건을 더 추가하여 처리해 주십시요.
	    	 */

            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                Intent intent;

                Log.d("<INIPAYMOBILE>", "intent url : " + url);

                if (url.startsWith("foodfly")) {
                    intent = new Intent();
                    intent.setData(Uri.parse(url));
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }

                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    Log.d("<INIPAYMOBILE>", "intent getDataString : " + intent.getDataString());
                    Log.d("<INIPAYMOBILE>", "intent getPackage : " + intent.getPackage());

                } catch (URISyntaxException ex) {
                    Log.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);


                try {
                    startActivity(intent);
                    /*가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다.
	    			    삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에
	    			    조건을 걸어 종료하도록 하였습니다.*/
                    if (url.startsWith("ispmobile://")) {
                        finish();
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url);
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, uri.getScheme()" + intent.getDataString());

                    //ISP
                    if (url.startsWith("ispmobile://")) {
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_ISP);
                        return false;
                    }

                    //현대앱카드
                    else if (intent.getDataString().startsWith("hdcardappcardansimclick://")) {
                        DIALOG_CARDNM = "HYUNDAE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 현대앱카드설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //신한앱카드
                    else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://")) {
                        DIALOG_CARDNM = "SHINHAN";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 신한카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //삼성앱카드
                    else if (intent.getDataString().startsWith("mpocket.online.ansimclick://")) {
                        DIALOG_CARDNM = "SAMSUNG";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 삼성카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //롯데 모바일결제
                    else if (intent.getDataString().startsWith("lottesmartpay://")) {
                        DIALOG_CARDNM = "LOTTE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데모바일결제 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //롯데앱카드(간편결제)
                    else if (intent.getDataString().startsWith("lotteappcard://")) {
                        DIALOG_CARDNM = "LOTTEAPPCARD";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데앱카드 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //KB앱카드
                    else if (intent.getDataString().startsWith("kb-acp://")) {
                        DIALOG_CARDNM = "KB";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, KB카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //하나SK카드 통합안심클릭앱
                    else if (intent.getDataString().startsWith("hanaansim://")) {
                        DIALOG_CARDNM = "HANASK";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 하나카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showAlertDialog(DIALOG_CARDAPP);
                        return false;
                    }

	    			/*
	    			//신한카드 SMART신한 앱
	    			else if( intent.getDataString().startsWith("smshinhanansimclick://"))
	    			{
	    				DIALOG_CARDNM = "SHINHAN_SMART";
	    				Log.e("INIPAYMOBILE", "INIPAYMOBILE, Smart신한앱설치");
	    				view.loadData("<html><body></body></html>", "text/html", "euc-kr");
	    				showDialog(DIALOG_CARDAPP);
				        return false;
	    			}
	    			*/

                    /**
                     > 현대카드 안심클릭 droidxantivirusweb://
                     - 백신앱 : Droid-x 안드로이이드백신 - NSHC
                     - package name : net.nshc.droidxantivirus
                     - 특이사항 : 백신 설치 유무는 체크를 하고, 없을때 구글마켓으로 이동한다는 이벤트는 있지만, 구글마켓으로 이동되지는 않음
                     - 처리로직 : intent.getDataString()로 하여 droidxantivirusweb 값이 오면 현대카드 백신앱으로 인식하여
                     하드코딩된 마켓 URL로 이동하도록 한다.
                     */

                    //현대카드 백신앱
                    else if (intent.getDataString().startsWith("droidxantivirusweb")) {
                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: ");
                        /*************************************************************************************/

                        Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
                        hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
                        startActivity(hydVIntent);

                    }

                    //INTENT:// 인입될시 예외 처리
                    else if (url.startsWith("intent://")) {

                        /**

                         > 삼성카드 안심클릭
                         - 백신앱 : 웹백신 - 인프라웨어 테크놀러지
                         - package name : kr.co.shiftworks.vguardweb
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 신한카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 농협카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 외환카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 씨티카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 하나SK카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 하나카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 롯데카드
                         - 백신이 설치되어 있지 않아도, 결제페이지로 이동

                         */

                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "Custom URL (intent://) 로 인입될시 마켓으로 이동되는 예외 처리: ");
                        /*************************************************************************************/

                        try {

                            Intent excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            String packageNm = excepIntent.getPackage();

                            Log.d("<INIPAYMOBILE>", "excepIntent getPackage : " + packageNm);

                            excepIntent = new Intent(Intent.ACTION_VIEW);
                            excepIntent.setData(Uri.parse("market://search?q=" + packageNm));

                            startActivity(excepIntent);

                        } catch (URISyntaxException e1) {
                            Log.e("<INIPAYMOBILE>", "INTENT:// 인입될시 예외 처리  오류 : " + e1);
                        }
                    }
                }
            } else {
                view.loadUrl(url);
                return false;
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showProgressDialog();
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            dismissProgressDialog();
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            view.loadData("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" +
                    "</head><body>" + "요청실패 : (" + errorCode + ")" + description + "</body></html>", "text/html", "utf-8");
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
}