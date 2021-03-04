package com.gexton.pink_uos.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gexton.pink_uos.PanicBuzzDetailActivity;
import com.gexton.pink_uos.R;
import com.gexton.pink_uos.model.NotificationModel;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    Context context;
    List<NotificationModel> photosList;

    public NotificationAdapter(Context context, List<NotificationModel> photosList) {
        this.context = context;
        this.photosList = photosList;
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        NotificationModel photos = photosList.get(position);
        holder.tvReportAdded.setText(WordUtils.capitalize(photos.data.type.replace("_", " ")));
        holder.tvNewNotification.setText(photos.data.msg);

        //yyyy-MM-dd HH:mm:ss
        String commentDate = photos.created_at;//2021-03-04T09:16:36.000000Z      2020-02-26T16:01:34.000Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(commentDate);//"2020-2-31 11:30:19"
            commentDate = new SimpleDateFormat("dd, MMM yyyy @ hh:mm a").format(d);
            //commenceDate = commenceDate.replace(" ","\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvateTime.setText(commentDate);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photos.data.type.equals("panic_buzz")) {
                    String id = photos.data.id;
                    Intent intent = new Intent(context, PanicBuzzDetailActivity.class);
                    intent.putExtra("buzz_id", id);
                    context.startActivity(intent);
                } else if (photos.data.type.equals("panic_report")) {
                    String id = photos.data.id;
                    Intent intent = new Intent(context, PanicBuzzDetailActivity.class);
                    intent.putExtra("panic_id", id);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReportAdded, tvNewNotification, tvateTime;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tvReportAdded = view.findViewById(R.id.tvReportAdded);
            tvNewNotification = view.findViewById(R.id.tvNewNotification);
            tvateTime = view.findViewById(R.id.tvateTime);
        }
    }
}
