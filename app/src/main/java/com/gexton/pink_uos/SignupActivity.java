package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.gexton.pink_uos.api.ApiCallback;

public class SignupActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    EditText edtName, edtPhone, edtEmail, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        imgBack = findViewById(R.id.imgBack);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String phone = edtName.getText().toString().trim();
                String email = edtName.getText().toString().trim();
                String password = edtName.getText().toString().trim();
                signUpUser(name, phone, email, password);
            }
        });

    }

    private void signUpUser(String name, String phone, String email, String password) {
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {

    }
}