package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
    SharedPreferences prefs;
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
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
            }
        });

        String first_name = prefs.getString("first_name", "Name Not Found");
        String last_name = prefs.getString("last_name", "Name Not Found");
        String mobile = prefs.getString("mobile_no", "Phone No Not Found");
        String image_url = prefs.getString("image_url", "Image Not Found");
        Picasso.get().load(image_url).into(profileImage);
        tvName.setText(first_name + " " + last_name);
        if (TextUtils.isEmpty(mobile)){
            tvMobile.setText("Mobile No Not Found");
        } else {
            tvMobile.setText(mobile);
        }
    }
}