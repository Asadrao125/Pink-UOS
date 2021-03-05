package com.gexton.pink_uos.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gexton.pink_uos.R;
import com.gexton.pink_uos.api.ApiManager;


/**
 * Created by HP on 1/20/2017.
 */

public class Dialog_CustomProgress extends Dialog {

    private LinearLayout ll_progress_dialog;
    private Activity activity;
    private ProgressBar progressBar2;

    public Dialog_CustomProgress(Activity a) {
        super(a);
        this.activity = a;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        //setCancelable(false);
        setContentView(R.layout.custom_progress_dialog_style);

        initUI();

    }

    private void initUI() {
        ll_progress_dialog = (LinearLayout) findViewById(R.id.ll_progress_dialog);
        /*ll_progress_dialog.setBackground(AppData.changeViewColorAndRadius(this.activity,
                this.activity.getResources().getColor(R.color.app_black_color),
                this.activity.getResources().getColor(R.color.app_golden_color),
                20,
                2));*/

        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void showProgressDialog() {
        if (this != null) {
            if (this.isShowing()) {
                return;
            }
        }
        if (!ApiManager.shouldShowPD) {
            ApiManager.shouldShowPD = true;
        } else {
            show();
        }
    }

    public void dismissProgressDialog() {
        dismiss();
    }

}
