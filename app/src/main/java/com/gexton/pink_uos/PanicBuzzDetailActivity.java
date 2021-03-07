package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gexton.pink_uos.adapters.NotificationAdapter;
import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.NotificationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PanicBuzzDetailActivity extends AppCompatActivity implements ApiCallback, OnMapReadyCallback {
    String id;

    ApiCallback apiCallback;
    ImageView imgBack;
    TextView tvUsername, createdAt;
    CircleImageView imageUser;
    String address;
    TextView reportLocation;
    private GoogleMap mMap;
    String first_name, last_name, imageUserUrl, lat, lng, created_at;
    String latt, lngg;
    String notif_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_buzz_detail);

        apiCallback = PanicBuzzDetailActivity.this;
        imgBack = findViewById(R.id.imgBack);
        tvUsername = findViewById(R.id.tvUsername);
        createdAt = findViewById(R.id.tvCreatedAt);
        imageUser = findViewById(R.id.imageUser);
        id = getIntent().getStringExtra("id");
        reportLocation = findViewById(R.id.reportLocation);

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
        Log.d("panic_buzz_detail", "onApiResponce: " + apiResponce);

        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data").getJSONObject("buzz");
            Log.d("lets_see", "onApiResponce: " + jsonObject1);

            created_at = jsonObject1.getString("created_at");
            address = jsonObject1.getString("address");
            latt = jsonObject1.getString("lat");
            lngg = jsonObject1.getString("lng");

            first_name = jsonObject1.getJSONObject("user").getString("first_name");
            last_name = jsonObject1.getJSONObject("user").getString("last_name");
            imageUserUrl = jsonObject1.getJSONObject("user").getString("image_url");

            double lattt = Double.parseDouble(latt);
            double lnggg = Double.parseDouble(lngg);

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
            Picasso.get().load(imageUserUrl).placeholder(R.drawable.placeholder).into(imageUser);
            createdAt.setText(commentDate);

            if (!TextUtils.isEmpty(address)) {
                reportLocation.setText("Address not found");
            } else {
                reportLocation.setText(address);
            }

            Log.d("data_data", "onApiResponce: " + latt + " " + lngg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestParams requestParams = new RequestParams();
        requestParams.put("notification_id", notif_id);
        ApiManager apiManager = new ApiManager(PanicBuzzDetailActivity.this, "post", ApiManager.API_VIEW_PANIC_BUZZ + id, requestParams, apiCallback);
        apiManager.loadURLPanicBuzz();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}