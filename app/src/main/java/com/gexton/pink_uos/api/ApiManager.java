package com.gexton.pink_uos.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.security.KeyStore;

import cz.msebera.android.httpclient.entity.mime.Header;

import static android.content.Context.MODE_PRIVATE;

public class ApiManager {
    final int DEFAULT_TIMEOUT = 1000000000;
    Activity activity;
    String getOrPost;
    String apiName;
    RequestParams params;
    ApiCallback apiCallback;
    String baseURL = "https://usms.edu.pk/pink_safety/public/api/";
    public static final String API_REGISTER_USER = "register";
    public static final String API_LOGIN_USER = "login";
    public static final String API_PANIC_BUZZ = "panic_buzz";
    public static final String API_UPDATE_PROFILE = "update_profile";
    public static final String API_PANIC_REPORT = "panic_report";
    public static final String API_VIEW_PANIC_BUZZ = "guiders/panic_buzz/view/";
    public static final String API_GUIDERS_NOTIFICATIONS = "guiders/notifications";
    public static final String API_VIEW_PANIC_REPORT = "guiders/panic_report/view/";
    String MY_PREFS_NAME = "pink-uos";
    SharedPreferences prefs;
    String fcm_token;
    String jwd_token;

    public ApiManager(Activity activity, String getOrPost, String apiName, RequestParams params, ApiCallback apiCallback) {
        this.activity = activity;
        this.getOrPost = getOrPost;
        this.apiName = apiName;
        this.params = params;
        this.apiCallback = apiCallback;
        prefs = activity.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        fcm_token = prefs.getString("fcm_token", "");
        jwd_token = prefs.getString("jwd_token", "");

        System.out.println("-- Req URL : " + baseURL + apiName);
        System.out.println("-- Params : " + params.toString());
    }

    public void loadURL() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        if (!TextUtils.isEmpty(fcm_token)) {
            client.addHeader("Platform", "android");
            client.addHeader("Content-Type", "application/json");
            client.addHeader("Accept", "application/json");
            client.addHeader("Devicetoken", fcm_token);

            client.post(baseURL + apiName, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            try {
                                String content = new String(responseBody);
                                apiCallback.onApiResponce(statusCode, 1, apiName, content);
                                Log.d("onSuccess", "onFailure: " + content);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            try {
                                String content = new String(responseBody);
                                Log.d("onFailure", "onFailure: " + content);
                                apiCallback.onApiResponce(statusCode, 0, apiName, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } else {
            Toast.makeText(activity, "Null Token", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadURLPanicBuzz() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(DEFAULT_TIMEOUT);

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(sf);
        } catch (Exception e) {
            System.out.println("-- exception ");
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(fcm_token) && !TextUtils.isEmpty(jwd_token)) {
            client.addHeader("Platform", "android");
            client.addHeader("Content-Type", "application/json");
            client.addHeader("Accept", "application/json");
            client.addHeader("Devicetoken", fcm_token);
            client.addHeader("Authorization", "Bearer" + jwd_token);

            client.post(baseURL + apiName, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            try {
                                String content = new String(responseBody);
                                apiCallback.onApiResponce(statusCode, 1, apiName, content);
                                Log.d("onSuccess", "onFailure: " + content);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            try {
                                String content = new String(responseBody);
                                Log.d("onFailure", "onFailure: " + content);
                                apiCallback.onApiResponce(statusCode, 0, apiName, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } else {
            Toast.makeText(activity, "Null FCM or JWD Token", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadGetRequests() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        if (!TextUtils.isEmpty(fcm_token) && !TextUtils.isEmpty(jwd_token)) {
            client.addHeader("Platform", "android");
            client.addHeader("Content-Type", "application/json");
            client.addHeader("Accept", "application/json");
            client.addHeader("Devicetoken", fcm_token);
            client.addHeader("Authorization", "Bearer" + jwd_token);

            client.get(baseURL + apiName + fcm_token, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            try {
                                String content = new String(responseBody);
                                apiCallback.onApiResponce(statusCode, 1, apiName, content);
                                Log.d("onSuccess", "onSuccess: " + content);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            try {
                                String content = new String(responseBody);
                                Log.d("onFailure", "onFailure: " + content);
                                apiCallback.onApiResponce(statusCode, 0, apiName, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } else {
            Toast.makeText(activity, "Null Token", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadNotifications() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        if (!TextUtils.isEmpty(fcm_token) && !TextUtils.isEmpty(jwd_token)) {
            client.addHeader("Platform", "android");
            client.addHeader("Content-Type", "application/json");
            client.addHeader("Accept", "application/json");
            client.addHeader("Devicetoken", fcm_token);
            client.addHeader("Authorization", "Bearer" + jwd_token);

            client.get(baseURL + apiName, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                            try {
                                String content = new String(responseBody);
                                apiCallback.onApiResponce(statusCode, 1, apiName, content);
                                Log.d("onSuccess", "onSuccess: " + content);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            try {
                                String content = new String(responseBody);
                                Log.d("onFailure", "onFailure: " + content);
                                apiCallback.onApiResponce(statusCode, 0, apiName, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } else {
            Toast.makeText(activity, "Null Token", Toast.LENGTH_SHORT).show();
        }
    }

}
