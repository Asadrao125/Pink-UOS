package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.LoginResponse;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UpdateProfile extends AppCompatActivity implements ApiCallback {
    String MY_PREFS_NAME = "pink-uos";
    ImageView imgBack;
    Button btnUpdate;
    CircleImageView profileImage;
    EditText edtFirstName, edtLastName, edtPhone;
    final int REQUEST_CODE_GALLERY = 999;
    File op;
    ApiCallback apiCallback;
    String fName, lName, phoneNo;
    final int CUSTOM_REQUEST_CODE = 987;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    String image_path;
    String address, city, state, zipcode;
    GPSTracker gpsTracker;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        imgBack = findViewById(R.id.imgBack);
        btnUpdate = findViewById(R.id.btnUpdate);
        profileImage = findViewById(R.id.profileImage);
        edtPhone = findViewById(R.id.edtPhone);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        apiCallback = UpdateProfile.this;
        gpsTracker = new GPSTracker(this);
        prefs = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        settingDataIntoFields();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            zipcode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fName = edtFirstName.getText().toString().trim();
                lName = edtLastName.getText().toString().trim();
                phoneNo = edtPhone.getText().toString().trim();

                if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(phoneNo) && !TextUtils.isEmpty(image_path) && !TextUtils.isEmpty(address)) {
                    RequestParams requestParams = new RequestParams();
                    requestParams.put("first_name", fName);
                    requestParams.put("last_name", lName);
                    requestParams.put("address", address);
                    requestParams.put("city", city);
                    requestParams.put("state", state);
                    requestParams.put("zipcode", zipcode);
                    requestParams.put("phone_no", phoneNo);
                    try {
                        File file = new File(image_path);
                        requestParams.put("profile_image", file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ApiManager apiManager = new ApiManager(UpdateProfile.this, "post", ApiManager.API_UPDATE_PROFILE, requestParams, apiCallback);
                    apiManager.loadURLPanicBuzz();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });
    }

    private void settingDataIntoFields() {
        String first_name = prefs.getString("first_name", "Name Not Found");
        String last_name = prefs.getString("last_name", "Name Not Found");
        String mobile = prefs.getString("mobile_no", "Phone No Not Found");
        String image_url = prefs.getString("image_url", "Image Not Found");
        Picasso.get().load(image_url).into(profileImage);

        edtFirstName.setText(first_name);
        edtLastName.setText(last_name);
        edtPhone.setText(mobile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "You dont have permission to access file.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CUSTOM_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null) {
                photoPaths = new ArrayList<Uri>();
                photoPaths.addAll(dataList);

                try {
                    image_path = ContentUriUtils.INSTANCE.getFilePath(UpdateProfile.this, photoPaths.get(0));
                    if (image_path != null) {
                        op = new File(image_path);
                        Picasso.get().load(op).into(profileImage);
                        System.out.println("-- file path " + op.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void pickPhoto() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.ThemeOverlay_AppCompat_Dark)
                .setActivityTitle("Please select media")
                .setImageSizeLimit(5)
                .setVideoSizeLimit(10)
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.ic_camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, CUSTOM_REQUEST_CODE);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("update_api_response", "onApiResponce: " + apiResponce);
        Toast.makeText(this, "" + apiResponce, Toast.LENGTH_SHORT).show();
    }
}