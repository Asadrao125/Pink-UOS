package com.gexton.pink_uos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutAppActivity extends AppCompatActivity {
    ImageView imgBack;
    TextView tvAboutApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        imgBack = findViewById(R.id.imgBack);
        tvAboutApp = findViewById(R.id.tvAboutApp);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*  tvAboutApp.setText(Html.fromHtml("<h5 align=\"center\">Xpendings is an app that avails you to manage your income and expenses.</h5>\n" +
                "<p>Xpendings is the most facile & most convenient way to keep track of your incomes and expenses. You can integrate variants of categories with an astounding accumulation of colors and images.</p>\n" +
                "<br/>\n" +
                "<p>App provides the following features:</p>\n" +
                "<br/>\n" +
                "<li>You can integrate wallet by integrating name and amplitude of wallet.</li>\n" +
                "<br/>\n" +
                "<li>This app sanctions you to make transactions utilizing the wallet.</li>\n" +
                "<br/>\n" +
                "<li>Your wallet will be managed on the substructure of your income and expenses.</li>\n" +
                "<br/>\n" +
                "<li>You can filter transactions (All time and Daily).</li>\n" +
                "<br/>\n" +
                "<li>You can manage transactions by editing them.</li>\n" +
                "<br/>\n" +
                "<li>This app sanctions you to visually perceive all the transactions visually and you can apportion it as well.</li>\n" +
                "<br/>\n" +
                "<H6 align=\"center\">Xpendings is Developed by Gexton INC.</h6>"));*/

        tvAboutApp.setText("This app is specifically designed for the female students, faculty members and admin staff of the University of Sindh, Jamshoro to report any harrasment incident to the authorities in real time. This app will help to make campus safe for all the females and Study and work with respect and confidence.");


    }
}