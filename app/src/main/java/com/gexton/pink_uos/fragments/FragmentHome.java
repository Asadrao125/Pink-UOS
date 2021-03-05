package com.gexton.pink_uos.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.HomeActivity;
import com.gexton.pink_uos.R;
import com.gexton.pink_uos.adapters.ViewPagerAdapter;
import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class FragmentHome extends Fragment implements ApiCallback {
    View view;
    ImageView imgReport, imgPlay;
    Button btnCaptureImage;
    MediaPlayer mediaPlayer;
    ApiCallback apiCallback;
    GPSTracker gpsTracker;
    TextView tvName;
    ImageView imageSelected;
    String image_path;
    String address;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    final int CUSTOM_REQUEST_CODE = 987;
    SharedPreferences prefs;
    String MY_PREFS_NAME = "pink-uos";
    LinearLayout layoutStudent;
    String first_name, last_name, phone;
    File op;
    RelativeLayout teacherLayout;

    TabLayout tabs;
    AlertDialog alertDialog;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    EditText edtMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        imgReport = view.findViewById(R.id.imgReport);
        imgPlay = view.findViewById(R.id.imgPlay);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alert_tone);
        gpsTracker = new GPSTracker(getContext());
        apiCallback = FragmentHome.this;
        prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        layoutStudent = view.findViewById(R.id.layoutStudent);

        teacherLayout = view.findViewById(R.id.teacherLayout);
        tabs = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);
        tabs.setupWithViewPager(viewPager);

        first_name = prefs.getString("first_name", "");
        last_name = prefs.getString("last_name", "");
        phone = prefs.getString("mobile_no", "");

        try {
            gpsTracker = new GPSTracker(getContext());

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses1 = null;
            addresses1 = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
            address = addresses1.get(0).getAddressLine(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setting device volume to Max
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        return view;
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("Home_Activity_Response", "onApiResponce: " + apiResponce);
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            String msg = jsonObject.getString("msg");
            Toast.makeText(getContext(), "" + msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.message_dialog, null);

        tvName = view.findViewById(R.id.tvName);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        edtMessage = view.findViewById(R.id.edtMessage);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        ImageView imgCross = view.findViewById(R.id.imgCross);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        imageSelected = view.findViewById(R.id.imageSelected);

        tvLocation.setText(address);
        tvName.setText(first_name + " " + last_name);
        tvPhone.setText(phone);

        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        if (TextUtils.isEmpty(image_path)) {
            image_path = "";
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*RequestParams requestParams = new RequestParams();
                requestParams.put("address", address);
                requestParams.put("lat", gpsTracker.getLatitude() + "");
                requestParams.put("lng", gpsTracker.getLongitude() + "");
                requestParams.put("msg", edtMessage.getText().toString().trim());
                try {
                    File file = new File(image_path);
                    requestParams.put("image", file);
                    Log.d("image_path", image_path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ApiManager apiManager = new ApiManager((Activity) getContext(), "post", ApiManager.API_PANIC_REPORT, requestParams, apiCallback);
                apiManager.loadURLPanicBuzz();
                alertDialog.dismiss();*/
                checkPermission2();
            }
        });
        alertDialog.show();
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

    private void checkUser() {
        int is_student = prefs.getInt("panenl_value", 10000);
        if (is_student == 0) {
            layoutStudent.setVisibility(View.GONE);
            teacherLayout.setVisibility(View.VISIBLE);
        } else {
            layoutStudent.setVisibility(View.VISIBLE);
            teacherLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CUSTOM_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            if (dataList != null) {
                photoPaths = new ArrayList<Uri>();
                photoPaths.addAll(dataList);
                try {
                    image_path = ContentUriUtils.INSTANCE.getFilePath(getContext(), photoPaths.get(0));
                    if (image_path != null) {
                        op = new File(image_path);
                        File file = new File(image_path);
                        Picasso.get().load(file).into(imageSelected);
                        btnCaptureImage.setVisibility(View.GONE);
                        imageSelected.setVisibility(View.VISIBLE);
                        System.out.println("-- file path " + op.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUser();
    }

    private void checkPermission2() {
        Dexter.withContext(getContext())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            RequestParams requestParams = new RequestParams();
                            requestParams.put("address", address);
                            requestParams.put("lat", gpsTracker.getLatitude() + "");
                            requestParams.put("lng", gpsTracker.getLongitude() + "");
                            requestParams.put("msg", edtMessage.getText().toString().trim());
                            try {
                                File file = new File(image_path);
                                requestParams.put("image", file);
                                Log.d("image_path", image_path);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            ApiManager apiManager = new ApiManager((Activity) getContext(), "post", ApiManager.API_PANIC_REPORT, requestParams, apiCallback);
                            apiManager.loadURLPanicBuzz();
                            alertDialog.dismiss();
                        } else {
                            gpsTracker.enableLocationPopup();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void checkPermission() {
        Dexter.withContext(getContext())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            if (mediaPlayer.isPlaying()) {
                                imgPlay.setImageResource(R.drawable.play123);
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                            } else {
                                imgPlay.setImageResource(R.drawable.pause);
                                mediaPlayer = MediaPlayer.create(getContext(), R.raw.alert_tone);
                                mediaPlayer.start();
                                if (gpsTracker.canGetLocation()) {

                                    gpsTracker = new GPSTracker(getContext());

                                    RequestParams requestParams = new RequestParams();
                                    requestParams.put("address", address);
                                    requestParams.put("lat", gpsTracker.getLatitude() + "");
                                    requestParams.put("lng", gpsTracker.getLongitude() + "");
                                    ApiManager apiManager = new ApiManager((Activity) getContext(), "post", ApiManager.API_PANIC_BUZZ, requestParams, apiCallback);
                                    apiManager.loadURLPanicBuzz();
                                }
                            }
                        } else {
                            gpsTracker.enableLocationPopup();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

}