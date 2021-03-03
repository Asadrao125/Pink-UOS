package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.gexton.pink_uos.model.LoginResponse;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class UpdateProfile extends AppCompatActivity implements ApiCallback {
    String MY_PREFS_NAME = "pink-uos";
    ImageView imgBack;
    Button btnUpdate;
    CircleImageView profileImage;
    EditText edtFirstName, edtLastName, edtPhone;
    final int REQUEST_CODE_GALLERY = 999;
    String imgFilePathTemp = "";
    Uri outputFileUri;
    File op;
    ApiCallback apiCallback;
    String fName, lName, phoneNo;
    String emergency, fatherName;

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

        settingDataIntoFields();

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

                RequestParams requestParams = new RequestParams();
                requestParams.put("first_name", fName);
                requestParams.put("last_name", lName);
                requestParams.put("phone_no", phoneNo);
                requestParams.setUseJsonStreamer(true);
                try {
                    requestParams.put("profile_image", op);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                ApiManager apiManager = new ApiManager(UpdateProfile.this, "post", ApiManager.API_UPDATE_PROFILE, requestParams, apiCallback);
                apiManager.loadURLPanicBuzz();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void settingDataIntoFields() {
        SharedPreferences prefs1 = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String json = prefs1.getString("Login_Response", "");
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(json, LoginResponse.class);
        if (loginResponse != null) {
            String firstName = loginResponse.first_name;
            String lastName = loginResponse.last_name;
            String phone = loginResponse.mobile_no;

            emergency = loginResponse.emergency_contact;
            fatherName = loginResponse.father_name;

            Picasso.get().load(loginResponse.image_url).into(profileImage);
            edtFirstName.setText(firstName);
            edtLastName.setText(lastName);
            edtPhone.setText(phone);
        }
    }

    private void dispatchTakePictureIntent() {

        long tim = System.currentTimeMillis();
        String imgName = "image_" + tim + ".jpg";

        File dir = getApplicationContext().getFilesDir();
        File folder = new File(dir.getAbsolutePath() + "/images");
        if (!folder.exists()) {
            folder.mkdir();
        }
        op = new File(folder, imgName);

        outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", op);
        imgFilePathTemp = op.getAbsolutePath();

        Intent takePictureIntent = new Intent(Intent.ACTION_PICK);
        takePictureIntent.setType("image/*");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        //===============android19
        List<ResolveInfo> resolvedIntentActivities = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //context.revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //==============android19

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_GALLERY);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
            Log.d("destination", "onActivityResult: " + destination);

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("update_api_response", "onApiResponce: " + apiResponce);

        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
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

            LoginResponse loginResponse = new LoginResponse(userId, is_active, is_student, verified_at, deleted_at, updated_at,
                    created_at, email, fName, lName, roll_no, phoneNo, enroll_year, fatherName, department,
                    emergency, cnic, image_url);

            Gson gson = new Gson();
            String json = gson.toJson(loginResponse);
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("Login_Response", json);
            editor.apply();
            Toast.makeText(this, "Login Response Saved", Toast.LENGTH_SHORT).show();
            settingDataIntoFields();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}