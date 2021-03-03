package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.model.LoginResponse;
import com.gexton.pink_uos.model.UserBean;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewProfile extends AppCompatActivity {
    ImageView imgBack;
    String MY_PREFS_NAME = "pink-uos";
    CircleImageView profileImage;
    Button btnUpdateProfile;
    TextView tvName, tvEmail, tvMobile, tvFatherName, tvDepartment, tvEmergency, tvCnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        profileImage = findViewById(R.id.profileImage);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        tvFatherName = findViewById(R.id.tvFatherName);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvEmergency = findViewById(R.id.tvEmergency);
        tvCnic = findViewById(R.id.tvCnic);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
            }
        });

        SharedPreferences prefs1 = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String json = prefs1.getString("Login_Response", "");
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
        if (loginResponse != null) {
            String name = loginResponse.first_name + " " + loginResponse.last_name;
            Picasso.get().load(loginResponse.image_url).into(profileImage);
            tvName.setText(name);
            tvEmail.setText(loginResponse.email);
            tvMobile.setText(loginResponse.mobile_no);
            tvFatherName.setText(loginResponse.father_name);
            tvDepartment.setText(loginResponse.department);
            tvEmergency.setText(loginResponse.emergency_contact);
            tvCnic.setText(loginResponse.cnic);
        }
    }
}