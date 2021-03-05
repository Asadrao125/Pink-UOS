package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PanicReportDetailActivity extends AppCompatActivity implements ApiCallback, OnMapReadyCallback {
    ImageView imgBack;
    ApiCallback apiCallback;
    String address;
    CircleImageView imageUser;
    ImageView imageReport;
    TextView reportLocation, reportMessage;
    TextView tvUsername, createdAt;
    String imageReportUrl;
    String id;
    String notif_id;
    GPSTracker gpsTracker;
    String first_name, last_name, imageUserUrl, lat, lng, msg, created_at;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_report_detail);

        imgBack = findViewById(R.id.imgBack);
        apiCallback = PanicReportDetailActivity.this;
        tvUsername = findViewById(R.id.tvUsername);
        createdAt = findViewById(R.id.tvCreatedAt);
        imageUser = findViewById(R.id.imageUser);
        imageReport = findViewById(R.id.imageReport);
        reportLocation = findViewById(R.id.reportLocation);
        reportMessage = findViewById(R.id.reportMessage);
        id = getIntent().getStringExtra("id");
        gpsTracker = new GPSTracker(this);
        //logout neechy

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notif_id = getIntent().getStringExtra("notif_id");
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("panic_report_detail", "onApiResponce: " + apiResponce);

        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data").getJSONObject("report");
            Log.d("lets_see", "onApiResponce: " + jsonObject1);

            first_name = jsonObject1.getJSONObject("user").getString("first_name");
            last_name = jsonObject1.getJSONObject("user").getString("last_name");
            imageUserUrl = jsonObject1.getJSONObject("user").getString("image_url");

            lat = jsonObject1.getString("lat");
            address = jsonObject1.getString("address");
            lng = jsonObject1.getString("lng");
            imageReportUrl = jsonObject1.getString("image_url");
            msg = jsonObject1.getString("msg");
            created_at = jsonObject1.getString("created_at");

            if (TextUtils.isEmpty(imageReportUrl)) {
                imageReport.setVisibility(View.GONE);
            }


            double lattt = Double.parseDouble(lat);
            double lnggg = Double.parseDouble(lng);

            LatLng sydney = new LatLng(lattt, lnggg);
            mMap.addMarker(new MarkerOptions().position(sydney).title(first_name + " " + last_name));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            //yyyy-MM-dd HH:mm:ss
            String commentDate = created_at;//2021-03-04T09:16:36.000000Z      2020-02-26T16:01:34.000Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(commentDate);//"2020-2-31 11:30:19"
                commentDate = new SimpleDateFormat("dd, MMM yyyy @ hh:mm a").format(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvUsername.setText(first_name + " " + last_name);
            //picasso user
            Picasso.get().load(imageUserUrl).placeholder(R.drawable.ic_launcher_background).into(imageUser);
            //picasso report
            Picasso.get().load(imageReportUrl).placeholder(R.drawable.ic_launcher_background).into(imageReport);
            reportMessage.setText(msg);
            createdAt.setText(commentDate);
            reportLocation.setText(address);

            Log.d("data_data", "onApiResponce: " + lat + " " + lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            RequestParams requestParams = new RequestParams();
                            requestParams.put("notification_id", notif_id);
                            ApiManager apiManager = new ApiManager(PanicReportDetailActivity.this, "post", ApiManager.API_VIEW_PANIC_REPORT + id, requestParams, apiCallback);
                            apiManager.loadURLPanicBuzz();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}