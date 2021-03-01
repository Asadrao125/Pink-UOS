package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.gexton.pink_uos.utils.DialogClass;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.android.gms.location.LocationSettingsStates;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements ApiCallback {
    String message;
    double lat, lng;
    TextView tvLocation;
    GPSTracker gpsTracker;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    Button btnShareCredentials, btnPlayRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        btnPlayRingtone = findViewById(R.id.btnPlayRingtone);
        btnShareCredentials = findViewById(R.id.btnShareCredentials);
        gpsTracker = new GPSTracker(this);
        tvLocation = findViewById(R.id.tvLocation);

        //Setting device volume to Max
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        //Create Media Player
        mediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.alert_tone);

        btnPlayRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    btnPlayRingtone.setText("Play Panic Sound");
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                } else {
                    btnPlayRingtone.setText("Pause Panic Sound");
                    mediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.alert_tone);
                    mediaPlayer.start();
                }
            }
        });

        btnShareCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void openDialog(String address) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.message_dialog, null);

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        EditText edtMessage = view.findViewById(R.id.edtMessage);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        ImageView imgCross = view.findViewById(R.id.imgCross);

        tvLocation.setText(address);
        message = edtMessage.getText().toString().trim();

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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            getCurrentLocation();
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
        GPSTracker gps = new GPSTracker(HomeActivity.this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();

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
                openDialog(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gps.enableLocationPopup();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (states.isNetworkLocationPresent() && states.isGpsPresent() && states.isLocationPresent() && states.isGpsUsable()) {
            Log.d("gps_tag", "onActivityResult: RESULT_OK");
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("gps_tag", "onActivityResult: RESULT_CANCELED");
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

    }
}