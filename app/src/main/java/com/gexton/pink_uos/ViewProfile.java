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
    TextView tvFirstName, tvPhone, tvLastName;

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
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvPhone = findViewById(R.id.tvPhone);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
            }
        });

        String first_name = prefs.getString("first_name", "");
        String last_name = prefs.getString("last_name", "");
        String mobile = prefs.getString("mobile_no", "");
        String image_url = prefs.getString("image_url", "");

        Picasso.get().load(image_url).into(profileImage);
        tvFirstName.setText(first_name);
        tvLastName.setText(last_name);
        tvPhone.setText(mobile);
        if (TextUtils.isEmpty(mobile)) {
            tvPhone.setText("No contact found");
        } else if (TextUtils.isEmpty(first_name)) {
            tvFirstName.setText("No first name found");
        } else if (TextUtils.isEmpty(first_name)) {
            tvLastName.setText("No last name found");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String first_name = prefs.getString("first_name", "");
        String last_name = prefs.getString("last_name", "");
        String mobile = prefs.getString("mobile_no", "");
        String image_url = prefs.getString("image_url", "");

        Picasso.get().load(image_url).into(profileImage);
        tvFirstName.setText(first_name);
        tvLastName.setText(last_name);
        tvPhone.setText(mobile);
        if (TextUtils.isEmpty(mobile)) {
            tvPhone.setText("No contact found");
        } else if (TextUtils.isEmpty(first_name)) {
            tvFirstName.setText("No first name found");
        } else if (TextUtils.isEmpty(first_name)) {
            tvLastName.setText("No last name found");
        }
    }
}