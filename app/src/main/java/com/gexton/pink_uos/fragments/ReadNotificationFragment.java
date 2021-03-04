package com.gexton.pink_uos.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gexton.pink_uos.LoginActivity;
import com.gexton.pink_uos.PanicBuzzDetailActivity;
import com.gexton.pink_uos.R;
import com.gexton.pink_uos.adapters.NotificationAdapter;
import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.NotificationModel;
import com.gexton.pink_uos.utils.RecyclerItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadNotificationFragment extends Fragment implements ApiCallback {
    View view;
    ApiCallback apiCallback;
    ArrayList<NotificationModel> notificationModelsList;
    RecyclerView rvNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read_notification, container, false);
        apiCallback = ReadNotificationFragment.this;
        notificationModelsList = new ArrayList<>();
        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setHasFixedSize(true);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        rvNotifications.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("Notif_Api_Response", "onApiResponce: " + apiResponce);
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            JSONArray read_notifications = jsonObject.getJSONObject("data").getJSONArray("read_notifications");

            /*for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectnew = jsonArray.getJSONObject(i);
                Log.d("lets_see", "onApiResponce: " + jsonObjectnew);
                String report_type = jsonObjectnew.getJSONObject("data").getString("type");
                String report_msg = jsonObjectnew.getJSONObject("data").getString("msg");
                String dtae_time = jsonObjectnew.getString("created_at");
                Log.d("report_type", "onApiResponce: " + report_type + "\n" + report_msg + "\n" + dtae_time);
                NotificationModel notificationModel = new NotificationModel(report_type, report_msg, dtae_time);
                notificationModelsList.add(notificationModel);
            }
            Log.d("read_notifications", "onApiResponce: " + jsonArray);
            Type listType = new TypeToken<List<NotificationModel>>(){}.getType(); */

            notificationModelsList = new Gson().fromJson(read_notifications + "", new TypeToken<List<NotificationModel>>() {
            }.getType());
            rvNotifications.setAdapter(new NotificationAdapter(getContext(), notificationModelsList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        RequestParams requestParams = new RequestParams();
        ApiManager apiManager = new ApiManager((Activity) getContext(), "get", ApiManager.API_GUIDERS_NOTIFICATIONS, requestParams, apiCallback);
        apiManager.loadNotifications();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadNotifications();
    }
}