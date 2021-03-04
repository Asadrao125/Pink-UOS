package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gexton.pink_uos.adapters.NotificationAdapter;
import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PanicBuzzDetailActivity extends AppCompatActivity implements ApiCallback {
    String buzz_id, panic_id;
    ApiCallback apiCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_buzz_detail);

        apiCallback = PanicBuzzDetailActivity.this;


    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("panic_buzz_detail", "onApiResponce: " + apiResponce);
        Toast.makeText(getApplicationContext(), "" + apiResponce, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        buzz_id = getIntent().getStringExtra("buzz_id");
        panic_id = getIntent().getStringExtra("panic_id");

        if (!TextUtils.isEmpty(panic_id)) {
            RequestParams requestParams = new RequestParams();
            ApiManager apiManager = new ApiManager(PanicBuzzDetailActivity.this, "get", ApiManager.API_VIEW_PANIC_BUZZ + buzz_id, requestParams, apiCallback);
            apiManager.loadGetRequests();
        }

        if (!TextUtils.isEmpty(buzz_id)) {
            RequestParams requestParams = new RequestParams();
            ApiManager apiManager = new ApiManager(PanicBuzzDetailActivity.this, "get", ApiManager.API_VIEW_PANIC_REPORT + panic_id, requestParams, apiCallback);
            apiManager.loadGetRequests();
        }
    }
}