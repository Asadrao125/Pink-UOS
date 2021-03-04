package com.gexton.pink_uos.model;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {

    public String id;
    public String type;
    public String notifiable_type;
    public float notifiable_id;
    //@SerializedName("data")
    public Data data;
    public String read_at = null;
    public String created_at;
    public String updated_at;

    public class Data {
        public String link;
        public String route;
        public String id;
        public String type;
        public String icon;
        public String color;
        public String msg;
    }
}
