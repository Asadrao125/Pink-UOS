package com.gexton.pink_uos;

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
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.LoginResponse;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

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
    String login_response;

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
    }

    private void init() {
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        signupLayout = findViewById(R.id.signupLayout);
        apiCallback = LoginActivity.this;
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        login_response = prefs.getString("Login_Response", "");
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
            String jwd_token = jsonObject.getJSONObject("data").get("token").toString();
            int is_active = jsonObject.getJSONObject("data").getJSONObject("user").getInt("is_active");
            int userId = jsonObject.getJSONObject("data").getJSONObject("user").getInt("id");
            int is_student = jsonObject.getJSONObject("data").getJSONObject("user").getInt("is_student");
            String verified_at = jsonObject.getJSONObject("data").getJSONObject("user").getString("verified_at");
            String deleted_at = jsonObject.getJSONObject("data").getJSONObject("user").getString("deleted_at");
            String updated_at = jsonObject.getJSONObject("data").getJSONObject("user").getString("updated_at");
            String created_at = jsonObject.getJSONObject("data").getJSONObject("user").getString("created_at");
            
            String email = jsonObject.getJSONObject("data").getJSONObject("user").getString("email");
            String first_name = jsonObject.getJSONObject("data").getJSONObject("user").getString("first_name");
            String last_name = jsonObject.getJSONObject("data").getJSONObject("user").getString("last_name");
            String roll_no = jsonObject.getJSONObject("data").getJSONObject("user").getString("roll_no");
            String mobile_no = jsonObject.getJSONObject("data").getJSONObject("user").getString("mobile_no");
            String enroll_year = jsonObject.getJSONObject("data").getJSONObject("user").getString("enroll_year");
            String father_name = jsonObject.getJSONObject("data").getJSONObject("user").getString("father_name");
            String department = jsonObject.getJSONObject("data").getJSONObject("user").getString("department");
            String emergency_contact = jsonObject.getJSONObject("data").getJSONObject("user").getString("emergency_contact");
            String cnic = jsonObject.getJSONObject("data").getJSONObject("user").getString("cnic");
            String image_url = jsonObject.getJSONObject("data").getJSONObject("user").getString("image_url");

            if (!TextUtils.isEmpty(jwd_token)) {
                Log.d("jwd_token", "onApiResponce: " + jwd_token);
                editor.putString("jwd_token", jwd_token);
                editor.apply();

                LoginResponse loginResponse = new LoginResponse(userId, is_active, is_student, verified_at, deleted_at, updated_at,
                        created_at, email, first_name, last_name, roll_no, mobile_no, enroll_year, father_name, department,
                        emergency_contact, cnic, image_url);
                Gson gson = new Gson();
                String json = gson.toJson(loginResponse);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("Login_Response", json);
                editor.apply();
                Toast.makeText(this, "Login Response Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Token Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkUserExistance() {
        if (!TextUtils.isEmpty(login_response)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExistance();
    }
}