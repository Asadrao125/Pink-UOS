package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.LoginResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity implements ApiCallback {
    Button btnLogin;
    EditText edtEmail, edtPassword;
    LinearLayout signupLayout;
    ApiCallback apiCallback;
    SharedPreferences.Editor editor;
    String MY_PREFS_NAME = "pink-uos";
    SharedPreferences prefs;
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                loginUser(email, password);
            }
        });

        signupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class);
                intent.putExtra("val", "fp");
                startActivity(intent);
            }
        });
    }

    private void init() {
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        signupLayout = findViewById(R.id.signupLayout);
        apiCallback = LoginActivity.this;
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void loginUser(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Empty");
            edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Empty");
            edtPassword.requestFocus();
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.put("email", email);
            requestParams.put("password", password);
            requestParams.setUseJsonStreamer(true);
            ApiManager apiManager = new ApiManager(LoginActivity.this, "post", ApiManager.API_LOGIN_USER, requestParams, apiCallback);
            apiManager.loadURL();
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);

            String msg = jsonObject.getString("msg");
            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();

            Log.d("login_api_response", "onApiResponce: " + apiResponce + apiName + httpStatusCode + successOrFail);
            String jwd_token = jsonObject.getJSONObject("data").get("token").toString();
            String image_url = jsonObject.getJSONObject("data").getJSONObject("user").getString("image_url");
            String first_name = jsonObject.getJSONObject("data").getJSONObject("user").getString("first_name");
            String last_name = jsonObject.getJSONObject("data").getJSONObject("user").getString("last_name");
            String mobile_no = jsonObject.getJSONObject("data").getJSONObject("user").getString("mobile_no");
            int is_student = jsonObject.getJSONObject("data").getJSONObject("user").getInt("is_student");
            String hash_id = jsonObject.getJSONObject("data").getJSONObject("user").getString("hashid");

            String uniLat = jsonObject.getJSONObject("data").getJSONObject("university_limits").getString("lat");
            String uniLng = jsonObject.getJSONObject("data").getJSONObject("university_limits").getString("lng");
            String radius = jsonObject.getJSONObject("data").getJSONObject("university_limits").getString("radius");
            String apply_limits = jsonObject.getJSONObject("data").getJSONObject("university_limits").getString("apply_limits");

            Log.d("uni_limits", "onApiResponce: " + uniLat + "\n" + uniLng + "\n" + radius);

            if (!TextUtils.isEmpty(jwd_token)) {
                Log.d("jwd_token", "onApiResponce: " + jwd_token);
                editor.putString("jwd_token", jwd_token);
                editor.putInt("panenl_value", is_student);
                editor.putString("first_name", first_name);
                editor.putString("last_name", last_name);
                editor.putString("image_url", image_url);
                editor.putString("mobile_no", mobile_no);
                editor.putString("hash_id", hash_id);
                editor.putString("uniLat", uniLat);
                editor.putString("uniLng", uniLng);
                editor.putString("radius", radius);
                editor.putString("apply_limits", apply_limits);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), NewHomeActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "JWD Token Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkUserExistance() {
        String jwd_token = prefs.getString("jwd_token", "");
        if (!TextUtils.isEmpty(jwd_token)) {
            startActivity(new Intent(getApplicationContext(), NewHomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExistance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("task_status", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        editor.putString("fcm_token", token);
                        editor.apply();
                        System.out.println("-- FCM Token : " + token);
                    }
                });
    }
}