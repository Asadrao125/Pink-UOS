package com.gexton.pink_uos.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gexton.pink_uos.NotificationsActivity;
import com.gexton.pink_uos.R;
import com.gexton.pink_uos.adapters.NotificationAdapter;
import com.gexton.pink_uos.api.ApiCallback;
import com.gexton.pink_uos.api.ApiManager;
import com.gexton.pink_uos.model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnreadNotificationFragment extends Fragment implements ApiCallback {
    View view;
    ApiCallback apiCallback;
    ArrayList<NotificationModel> notificationModelsList;
    RecyclerView rvNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_unread_notification, container, false);
        apiCallback = UnreadNotificationFragment.this;
        notificationModelsList = new ArrayList<>();
        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setHasFixedSize(true);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

    private void loadNotifications() {
        RequestParams requestParams = new RequestParams();
        ApiManager apiManager = new ApiManager((Activity) getContext(), "get", ApiManager.API_GUIDERS_NOTIFICATIONS, requestParams, apiCallback);
        apiManager.loadNotifications();
    }

    @Override
    public void onApiResponce(int httpStatusCode, int successOrFail, String apiName, String apiResponce) {
        Log.d("Notif_Api_Response", "onApiResponce: " + apiResponce);
        try {
            JSONObject jsonObject = new JSONObject(apiResponce);
            JSONArray read_notifications = jsonObject.getJSONObject("data").getJSONArray("unread_notifications");
            notificationModelsList = new Gson().fromJson(read_notifications + "", new TypeToken<List<NotificationModel>>() {
            }.getType());
            rvNotifications.setAdapter(new NotificationAdapter(getContext(), notificationModelsList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadNotifications();
    }
}