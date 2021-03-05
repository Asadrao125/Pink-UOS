package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.gexton.pink_uos.model.UserBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements ApiCallback {
    ImageView imgBack;
    Button btnSignUp;
    ApiCallback apiCallback;
    CircleImageView profile_image;
    final int REQUEST_CODE_GALLERY = 999;
    SharedPreferences.Editor editor;
    File op;
    String MY_PREFS_NAME = "pink-uos";
    String image_path;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    final int CUSTOM_REQUEST_CODE = 987;
    EditText edtFirstName, edtLastName, edtPhone, edtEmail, edtPassword, edtDepartment;
    EditText edtConfirmPassword, edtRollNo, edtEnrolYear, edtEmergency, edtFatherName, edtCNIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUserInfo();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void registerUserInfo() {
        String first_name = edtFirstName.getText().toString().trim();
        String last_name = edtLastName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm_password = edtConfirmPassword.getText().toString().trim();
        String department = edtDepartment.getText().toString().trim();
        String roll_no = edtRollNo.getText().toString().trim();
        String enroll_year = edtEnrolYear.getText().toString().trim();
        String emergency = edtEmergency.getText().toString().trim();
        String father_name = edtFatherName.getText().toString().trim();
        String cnic = edtCNIC.getText().toString().trim();

        if (image_path == null) {
            Toast.makeText(SignupActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(first_name)) {
            edtFirstName.setError("Empty");
            edtFirstName.requestFocus();
        } else if (TextUtils.isEmpty(last_name)) {
            edtLastName.setError("Empty");
            edtLastName.requestFocus();
        } else if (TextUtils.isEmpty(roll_no)) {
            edtRollNo.setError("Empty");
            edtRollNo.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Empty");
            edtPhone.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Empty");
            edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(enroll_year)) {
            edtEnrolYear.setError("Empty");
            edtEnrolYear.requestFocus();
        } else if (TextUtils.isEmpty(department)) {
            edtDepartment.setError("Empty");
            edtDepartment.requestFocus();
        } else if (TextUtils.isEmpty(emergency)) {
            edtEmergency.setError("Empty");
            edtEmergency.requestFocus();
        } else if (TextUtils.isEmpty(father_name)) {
            edtFatherName.setError("Empty");
            edtFatherName.requestFocus();
        } else if (TextUtils.isEmpty(cnic)) {
            edtCNIC.setError("Empty");
            edtCNIC.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Empty");
            edtPassword.requestFocus();
        } else if (TextUtils.isEmpty(confirm_password)) {
            edtConfirmPassword.setError("Empty");
            edtConfirmPassword.requestFocus();
        } else {

            RequestParams requestParams = new RequestParams();
            requestParams.put("first_name", first_name);
            requestParams.put("email", email);
            requestParams.put("password", password);
            requestParams.put("password_confirmation", confirm_password);
            requestParams.put("last_name", last_name);
            requestParams.put("roll_no", roll_no);
            requestParams.put("mobile_no", phone);
            requestParams.put("enroll_year", enroll_year);
            requestParams.put("father_name", father_name);
            requestParams.put("department", department);
            requestParams.put("emergency_contact", emergency);
            requestParams.put("cnic", cnic);
            try {
                Log.d("image_path", "image path: " + image_path);
                File file = new File(image_path);
                requestParams.put("profile_image", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ApiManager apiManager = new ApiManager(SignupActivity.this, "post", ApiManager.API_REGISTER_USER, requestParams, apiCallback);
            apiManager.loadURL();
        }
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        pickPhoto();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void init() {
        apiCallback = SignupActivity.this;
        imgBack = findViewById(R.id.imgBack);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtCNIC = findViewById(R.id.edtCnic);
        edtDepartment = findViewById(R.id.edtDepartment);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtRollNo = findViewById(R.id.edtRollNo);
        edtEnrolYear = findViewById(R.id.edtEnrolYear);
        edtEmergency = findViewById(R.id.edtEmergencyContact);
        edtFatherName = findViewById(R.id.edtFatherName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        profile_image = findViewById(R.id.profile_image);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("signup_api_response", "onApiResponce: " + apiResponce + apiName + httpStatusCode + successOrFail);

        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            String msg = jsonObject.getString("msg");
            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            String response = jsonObject.getString("data");
            System.out.println(" -- Signup Api Response " + response);
            System.out.println(" -- Signup Api Name " + apiName);
            System.out.println(" -- Signup Api Httpstatus code " + httpStatusCode);
            System.out.println(" -- Signup Api success ofr fail " + successOrFail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    protected void onStart() {
        super.onStart();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CUSTOM_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null) {
                photoPaths = new ArrayList<Uri>();
                photoPaths.addAll(dataList);

                try {
                    image_path = ContentUriUtils.INSTANCE.getFilePath(SignupActivity.this, photoPaths.get(0));
                    if (image_path != null) {
                        op = new File(image_path);
                        Picasso.get().load(op).into(profile_image);
                        System.out.println("-- file path " + op.getAbsolutePath());
                        Log.d("image_path", "onActivityResult: " + op.getAbsolutePath());
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
}