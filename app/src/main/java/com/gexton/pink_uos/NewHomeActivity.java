package com.gexton.pink_uos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.fragments.FragmentHome;
import com.gexton.pink_uos.utils.GPSTracker;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ApiCallback {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    Toolbar toolbar;
    SharedPreferences prefs;
    String MY_PREFS_NAME = "pink-uos";
    NavigationView navigationView;
    SharedPreferences.Editor editor;
    GPSTracker gpsTracker;
    ApiCallback apiCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        dl = (DrawerLayout) findViewById(R.id.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.menu);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        gpsTracker = new GPSTracker(this);
        apiCallback = NewHomeActivity.this;

        t = new ActionBarDrawerToggle(this, dl, toolbar, R.string.Open, R.string.Close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
               /* if (!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("si")) {
                    contentFrame.setTranslationX(-slideOffset * drawerView.getWidth());
                    dl.bringChildToFront(drawerView);
                    dl.requestLayout();
                } else {
                    contentFrame.setTranslationX(slideOffset * drawerView.getWidth());
                    dl.bringChildToFront(drawerView);
                    dl.requestLayout();
                }*/
            }
        };

        replaceFragment(new FragmentHome());
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        if (!dl.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed();
        } else {
            dl.closeDrawers();
        }
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            //ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

     /* private void checkUser() {
        checkPermission();
        int is_student = prefs.getInt("panenl_value", 10000);
        if (is_student == 0) {
            //imgBell.setVisibility(View.VISIBLE);
        } else {
            //imgBell.setVisibility(View.GONE);
        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        checkLocation();
    }

    private void checkLocation() {
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.enableLocationPopup();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homebtn:
                replaceFragment(new FragmentHome());
                break;
            case R.id.profilebtn:
                startActivity(new Intent(getApplicationContext(), ViewProfile.class));
                break;
            case R.id.aboutbtn:
                startActivity(new Intent(getApplicationContext(), AboutAppActivity.class));
                break;
            case R.id.sharebtn:
                shareIntent();
                break;
            case R.id.logoutbtn:
                logout();
                break;
            case R.id.changePasswordbtn:
                Intent intent = new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class);
                intent.putExtra("val", "cp");
                startActivity(intent);
                break;
        }
        navigationView.getMenu().getItem(0).setChecked(true);
        dl.closeDrawers();
        return false;
    }

    public void shareIntent() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Extra Subject");
            String shareMessage = "Let me recommend you Pink Safety application ";
            shareMessage = shareMessage + "Playstore Url";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, ""));
        } catch (Exception e) {
            e.toString();
        }
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void logout() {
        RequestParams requestParams = new RequestParams();
        ApiManager apiManager = new ApiManager(NewHomeActivity.this, "post", ApiManager.API_LOGOUT, requestParams, apiCallback);
        apiManager.loadURLPanicBuzz();

        editor.remove("first_name");
        editor.remove("last_name");
        editor.remove("mobile_no");
        editor.remove("image_url");
        editor.remove("jwd_token");
        editor.remove("fcm_token");
        editor.remove("panenl_value");
        editor.remove("hash_id");
        editor.remove("uniLat");
        editor.remove("uniLng");
        editor.remove("radius");
        editor.remove("apply_limits");
        editor.apply();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("logout_api_response", "onApiResponce: " + apiResponce);
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            String msg = jsonObject.getString("msg");
            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}