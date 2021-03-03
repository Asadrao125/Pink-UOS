package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.ContentUriUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.utils.DialogClass;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements ApiCallback {
    double lat, lng;
    TextView tvLocation;
    TextView tvName;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    static final int CAMERA_REQUEST = 1888;
    static final int MY_CAMERA_PERMISSION_CODE = 100;
    Button btnShareCredentials, btnPlayRingtone, btnProfile;
    ImageView imageSelected;
    Button btnCaptureImage;
    String imgFilePathTemp = "";//Jugaaar
    Uri outputFileUri;
    ApiCallback apiCallback;
    final int REQUEST_IMAGE_CAPTURE = 9766;
    String MY_PREFS_NAME = "pink-uos";
    SharedPreferences.Editor editor;
    GPSTracker gpsTracker;
    String address1;
    File op;
    final int CUSTOM_REQUEST_CODE = 987;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    String image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        btnPlayRingtone = findViewById(R.id.btnPlayRingtone);
        btnShareCredentials = findViewById(R.id.btnShareCredentials);
        gpsTracker = new GPSTracker(this);
        tvLocation = findViewById(R.id.tvLocation);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        apiCallback = HomeActivity.this;
        gpsTracker = new GPSTracker(HomeActivity.this);
        btnProfile = findViewById(R.id.btnProfile);

        //Setting device volume to Max
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        mediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.alert_tone);
        btnPlayRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    btnPlayRingtone.setText("Buzz Now");
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                } else {
                    btnPlayRingtone.setText("Stop Buzz");
                    mediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.alert_tone);
                    mediaPlayer.start();

                    if (gpsTracker.canGetLocation()) {
                        checkPermission();
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("address", address1);
                        requestParams.put("lat", gpsTracker.getLatitude());
                        requestParams.put("lng", gpsTracker.getLongitude());
                        requestParams.setUseJsonStreamer(true);
                        ApiManager apiManager = new ApiManager(HomeActivity.this, "post", ApiManager.API_PANIC_BUZZ, requestParams, apiCallback);
                        apiManager.loadURLPanicBuzz();
                    }
                }
            }
        });

        btnShareCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                openDialog();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ViewProfile.class));
            }
        });

    }

    private void openDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.message_dialog, null);

        tvName = view.findViewById(R.id.tvName);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        EditText edtMessage = view.findViewById(R.id.edtMessage);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        ImageView imgCross = view.findViewById(R.id.imgCross);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        imageSelected = view.findViewById(R.id.imageSelected);

        tvLocation.setText(address1);
        tvName.setText("Name");
        tvPhone.setText("Phone No");

        alertDialogBuilder.setView(view);
        AlertDialog alertDialog = alertDialogBuilder.create();
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
                //dispatchTakePictureIntent();
                pickPhoto();
            }
        });

        if (TextUtils.isEmpty(image_path)) {
            image_path = "";
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Lat: " + gpsTracker.getLatitude() + "\nLng: " + gpsTracker.getLongitude(), Toast.LENGTH_SHORT).show();
                RequestParams requestParams = new RequestParams();
                requestParams.put("address", address1);
                requestParams.put("lat", gpsTracker.getLatitude());
                requestParams.put("lng", gpsTracker.getLongitude());
                requestParams.put("msg", edtMessage.getText().toString().trim());
                requestParams.setUseJsonStreamer(true);
                try {
                    File file = new File(image_path);
                    requestParams.put("image", file);
                    Log.d("image_path", "onClick: " + image_path);
                    requestParams.setUseJsonStreamer(false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ApiManager apiManager = new ApiManager(HomeActivity.this, "post", ApiManager.API_PANIC_REPORT, requestParams, apiCallback);
                apiManager.loadURLPanicBuzz();
                Toast.makeText(HomeActivity.this, "Msg: " + edtMessage.getText().toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            getCurrentLocation();
                            try {
                                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                                List<Address> addresses1 = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
                                address1 = addresses1.get(0).getAddressLine(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Please provide permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void getCurrentLocation() {
        gpsTracker = new GPSTracker(HomeActivity.this);
        if (gpsTracker.canGetLocation()) {
            lat = gpsTracker.getLatitude();
            lng = gpsTracker.getLongitude();
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(lat, lng, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String addr = city + " " + " " + state + " " + country + " " + postalCode + " " + knownName;
                //openDialog(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gpsTracker.enableLocationPopup();
        }
    }

    /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //DATA.isImageCaptured = true;
            //DATA.imagePath = getRealPathFromURI(outputFileUri);
            if (outputFileUri == null) {
                System.out.println(" outputFileUri: " + outputFileUri);
                return;
            }
            //DATA.imagePath = ImageFilePath.getPath(activity, outputFileUri);//GM commented.. file provider issue android O
            //String imagePath = imgFilePathTemp;// temp solution....GM
            System.out.println("-- output image path: " + imgFilePathTemp);
            System.out.println("-- output file uri: " + outputFileUri);

            btnCaptureImage.setVisibility(View.GONE);
            imageSelected.setVisibility(View.VISIBLE);
            imageSelected.setImageBitmap(BitmapFactory.decodeFile(imgFilePathTemp));
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Toast.makeText(HomeActivity.this, "" + apiResponce, Toast.LENGTH_SHORT).show();
        Log.d("panic_buzz", "onApiResponce: " + apiResponce);
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
                    image_path = ContentUriUtils.INSTANCE.getFilePath(HomeActivity.this, photoPaths.get(0));
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

    private void dispatchTakePictureIntent() {

        long tim = System.currentTimeMillis();
        String imgName = "image_" + tim + ".jpg";

        //note this is lagacy storage  shut down after android 30- GM
        /*File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File op = new File(dir, imgName);*/
        //note this is lagacy storage  shut down after android 30- GM

        //note this is scopped storage - GM
        File dir = getApplicationContext().getFilesDir();
        File folder = new File(dir.getAbsolutePath() + "/images");
        if (!folder.exists()) {
            folder.mkdir();
        }
        op = new File(folder, imgName);
        //note this is scopped storage - GM

        //outputFileUri = Uri.fromFile(op);//Android O
        outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", op);
        imgFilePathTemp = op.getAbsolutePath();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}