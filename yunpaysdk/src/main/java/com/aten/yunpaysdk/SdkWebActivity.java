package com.aten.yunpaysdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class SdkWebActivity extends AppCompatActivity implements AdvancedWebView.Listener{

    private AdvancedWebView mWebView;
    private String source;

    private SsoHandler mHandler;
    private JSONObject dataObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_web);

        mHandler = new SsoHandler(this);
        source = getIntent().getStringExtra("source");

        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.addJavascriptInterface(new postAuthCodeJS(), "yunpay");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
//                Toast.makeText(SdkWebActivity.this, "Finished loading", Toast.LENGTH_SHORT).show();
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                Toast.makeText(SdkWebActivity.this, title, Toast.LENGTH_SHORT).show();
            }

        });
        mWebView.addHttpHeader("X-Requested-With", "");
//        mWebView.loadUrl(TEST_PAGE_URL);
        mWebView.loadHtml(source);


        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(SsoHandler.CANCLE_CODE);
                finish();
            }
        });
    }

    public class postAuthCodeJS extends Object{
        @JavascriptInterface
        public void postAuthCode(String userName, String code, String state){
//            Log.e("=====>", "ooooooo"+userName+";"+code+";"+state);
            getToken(userName, code, state);
        }
    }

    private void getToken(final String userName, String code, String state){
        mHandler.getToken(userName, code, state,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                Log.e("=====>", "=====>"+e.getMessage());
                Toast.makeText(SdkWebActivity.this, "Error Code1:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
//                    {"expires_in":2591999956,"refresh_token":"f23f02631e4b1a3c7f22de676fe76f8d","access_token":"4108ae278253a5bb7e9cfe6d525686ef"}
                try {
                    final JSONObject object = new JSONObject(response);
                    final String access_token = object.getString("access_token");

                    dataObj.put("access_token", access_token);
                    dataObj.put("expires_in", object.getString("expires_in"));
                    dataObj.put("refresh_token", object.getString("refresh_token"));

                    mHandler.getUserInfo(userName, access_token, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
//                            Log.e("=====>", "======>"+e.getMessage());
                            Toast.makeText(SdkWebActivity.this, "Error Code2:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                                {"nickName":"云粉","headPic":"/commons/upload/touxiang.jpg","loginName":"15059156443"}
//                            Log.e("=====>", access_token+"=====>"+response);
                            JSONObject object = null;
                            try {
                                object = new JSONObject(response);
                                dataObj.put("nickName", object.getString("nickName"));
                                dataObj.put("headPic", object.getString("headPic"));
                                dataObj.put("loginName", object.getString("loginName"));
                                setResult(RESULT_OK, new Intent().putExtra("data", dataObj.toString()));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mWebView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
//        Toast.makeText(SdkWebActivity.this, "onPageError(errorCode = "+errorCode+",  description = "+description+",  failingUrl = "+failingUrl+")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
//        Toast.makeText(SdkWebActivity.this, "onDownloadRequested(url = "+url+",  suggestedFilename = "+suggestedFilename+",  mimeType = "+mimeType+",  contentLength = "+contentLength+",  contentDisposition = "+contentDisposition+",  userAgent = "+userAgent+")", Toast.LENGTH_LONG).show();

		/*if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
			// download successfully handled
		}
		else {
			// download couldn't be handled because user has disabled download manager app on the device
		}*/
    }

    @Override
    public void onExternalPageRequest(String url) {
//        Toast.makeText(SdkWebActivity.this, "onExternalPageRequest(url = "+url+")", Toast.LENGTH_SHORT).show();
    }


    public static void jump(Activity context, String source){
        Intent intent = new Intent(context, SdkWebActivity.class);
        intent.putExtra("source", source);
        context.startActivityForResult(intent, SsoHandler.REQUEST_CODE);
    }

    public static void jump(Fragment context, String source){
        Intent intent = new Intent(context.getActivity(), SdkWebActivity.class);
        intent.putExtra("source", source);
        context.startActivityForResult(intent, SsoHandler.REQUEST_CODE);
    }
}
