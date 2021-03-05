package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.util.TextUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetAndChangePasswordActivity extends AppCompatActivity implements ApiCallback {
    EditText edtPassword, edtOldPassword, edtConfirmPassword;
    Button btnChangePassword, btnForgetPassword;
    ApiCallback apiCallback;
    ImageView imgBack;
    String val;
    LinearLayout forgetPasswordLayout, changePasswordLayout;
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_and_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        apiCallback = ForgetAndChangePasswordActivity.this;
        imgBack = findViewById(R.id.imgBack);
        forgetPasswordLayout = findViewById(R.id.forgetPasswordLayout);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        edtEmail = findViewById(R.id.edtEmail);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);

        val = getIntent().getStringExtra("val");
        if (!TextUtils.isEmpty(val)) {
            if (val.equals("cp")) {
                changePasswordLayout.setVisibility(View.VISIBLE);
                forgetPasswordLayout.setVisibility(View.GONE);
            } else if (val.equals("fp")) {
                forgetPasswordLayout.setVisibility(View.VISIBLE);
                changePasswordLayout.setVisibility(View.GONE);
            }
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = edtPassword.getText().toString().trim();
                String confirmPass = edtConfirmPassword.getText().toString().trim();
                String oldPass = edtOldPassword.getText().toString().trim();

                if (TextUtils.isEmpty(oldPass)) {
                    edtOldPassword.setError("Empty");
                    edtOldPassword.requestFocus();
                } else if (TextUtils.isEmpty(pass)) {
                    edtPassword.setError("Empty");
                    edtPassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmPass)) {
                    edtConfirmPassword.setError("Empty");
                    edtConfirmPassword.requestFocus();
                } else {
                    if (pass.equals(confirmPass)) {
                        changePassword(oldPass, pass, confirmPass);
                    } else {
                        edtPassword.setError("Not Matched");
                        edtConfirmPassword.setError("Not Matched");
                        edtPassword.requestFocus();
                        edtConfirmPassword.requestFocus();
                    }
                }
            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                RequestParams requestParams = new RequestParams();
                requestParams.put("email", email);
                ApiManager apiManager = new ApiManager(ForgetAndChangePasswordActivity.this, "post", ApiManager.API_FORGET_PASSWORD, requestParams, apiCallback);
                apiManager.loadURL();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void changePassword(String oldPass, String pass, String confirmPass) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("old_password", oldPass);
        requestParams.put("password", pass);
        requestParams.put("password_confirmation", confirmPass);
        ApiManager apiManager = new ApiManager(ForgetAndChangePasswordActivity.this, "post", ApiManager.API_CHANGE_PASSWORD, requestParams, apiCallback);
        apiManager.loadURLPanicBuzz();
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("logout_api_response", "onApiResponce: " + apiResponce);
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            String msg = jsonObject.getString("msg");
            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}