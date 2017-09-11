package com.aten.yunpaysdk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * project:SDKDemo
 * package:com.aten.yunpaysdk
 * Created by Zhang JinCheng on 2017/9/8.
 * e-mail : 774222004@qq.com
 */


public class SsoHandler {

    protected Activity mAuthActivity;
    protected Fragment mAuthFragment;
    protected static final String OAUTH2_BASE_URL = "http://test-api.ipaye.cn/";
    public static final int REQUEST_CODE = 6666;
    public static final int CANCLE_CODE = 7777;
    public static String client_id;
    public static String client_secret="no";
    public static String redirect_uri;
    public Context context;

    public SsoHandler(Activity activity) {
        this.mAuthActivity = activity;
        context = activity;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    public SsoHandler(Fragment fragment) {
        this.mAuthFragment = fragment;
        context = fragment.getContext();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


    //验证客户端授权
    public void authRegister(){
        if (client_id == null){
            Toast.makeText(context, "请配置client_id", Toast.LENGTH_SHORT).show();
            return;
        }

        if (redirect_uri == null){
            Toast.makeText(context, "请配置redirect_uri", Toast.LENGTH_SHORT).show();
            return;
        }

        long time = System.currentTimeMillis() * 1000 + 600000;
        String stringSignTemp= client_id+"&"+redirect_uri+"&"+time+"&1";
        OkHttpUtils
                .post()
                .url(OAUTH2_BASE_URL+"api/v2/auth/login")
                .addHeader("User-Agent","OS_Android")
                .addParams("client_id", "lkfjsoerrj_klrlks")
                .addParams("redirect_uri", redirect_uri)
                .addParams("state", "1")
                .addParams("time", ""+time)
                .addParams("sign", stringSignTemp)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                Log.e("=====>", "=====>error"+e.getMessage());
                        Toast.makeText(context, "验证失败，请检查配置参数是否正确Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                Log.e("=====>", "=====>"+response);
                        if (mAuthActivity != null){
                            SdkWebActivity.jump(mAuthActivity, response);
                        }else if (mAuthFragment != null){
                            SdkWebActivity.jump(mAuthFragment, response);
                        }

                    }
                });
    }


    //获取刷新令牌
    public void getToken(String userName, String code, String state, StringCallback callback){
//        Log.e("====>34", client_id+";"+redirect_uri);
        OkHttpUtils
                .post()
                .url(OAUTH2_BASE_URL+"api/v2/auth/accessToken")
                .addHeader("User-Agent","OS_Android")
                .addParams("client_id", client_id)
                .addParams("redirect_uri", redirect_uri)
                .addParams("grant_type", "authorization_code")
                .addParams("username", userName)
                .addParams("code", code)
//                .addParams("refresh_token", "123")
                .addParams("client_secret", client_secret)
                .addParams("nonce", ""+(System.currentTimeMillis() * 1000))
                .build()
                .execute(callback);
    }


    //获取用户信息
    public void getUserInfo(String loginName,String token,StringCallback callback){
        Log.e("====>", MD5.getMessageDigest((token+"&"+loginName).getBytes()));
        OkHttpUtils
                .post()
                .url(OAUTH2_BASE_URL+"api/v2/auth/getUserInfo")
                .addHeader("User-Agent","OS_Android")
                .addHeader("Authorization", "Bearer "+MD5.getMessageDigest((token+"&"+loginName).getBytes()))
                .addParams("client_id", "lkfjsoerrj_klrlks")
                .addParams("loginName", loginName)
                .build()
                .execute(callback);
    }


    //取消授权
    public void cancleSso(String loginName,StringCallback callback){
        OkHttpUtils
                .post()
                .url(OAUTH2_BASE_URL+"api/v2/auth/refreshToken")
                .addHeader("User-Agent","OS_Android")
                .addParams("client_id", client_id)
                .addParams("loginName", loginName)
                .build()
                .execute(callback);
    }
}
