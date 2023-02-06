package com.vma.goapp.ui.sos;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.vma.goapp.R;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 29/03/2022
 */
public class MainSosActivity extends MyActivity {

    private CircularProgressIndicator cpi_sos;
    RelativeLayout rvly_sos,rvly_run;
    ImageView imvw_message,imvq_successFrame1,imvq_successFrame2;
    VmaButton bbtn_cancel;
    SosItemView itvw_date,itvw_location,itvw_time;

    private TextView txvw_counter,txvw_title,txvw_description;

    private Timer timerTask;
    private int task = 0;



    @Override
    protected int setLayout() {
        return R.layout.sos_activity_main;
    }

    @Override
    protected void initLayout() {;
        txvw_counter        = findViewById(R.id.txvw_counter);
        txvw_description    = findViewById(R.id.txvw_description);
        imvq_successFrame1  = findViewById(R.id.imvq_successFrame1);
        imvq_successFrame2  = findViewById(R.id.imvq_successFrame2);

        cpi_sos         = findViewById(R.id.cpi_sos);
        rvly_sos        = findViewById(R.id.rvly_sos);
        rvly_run        = findViewById(R.id.rvly_run);
        txvw_title      = findViewById(R.id.txvw_title);
        imvw_message    = findViewById(R.id.imvw_message);
        bbtn_cancel     = findViewById(R.id.bbtn_cancel);
        itvw_date       = findViewById(R.id.itvw_date);
        itvw_location   = findViewById(R.id.itvw_location);
        itvw_time       = findViewById(R.id.itvw_time);

        imvw_message.setVisibility(View.INVISIBLE);
        rvly_sos.setVisibility(View.VISIBLE);
        rvly_run.setVisibility(View.INVISIBLE);
        cpi_sos.setVisibility(View.INVISIBLE);
        cpi_sos.setMax(50);

        CardView crvw_sos = findViewById(R.id.crvw_sos);
        CardView card_info = findViewById(R.id.card_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            crvw_sos.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primary));
            card_info.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primary));
        }

        bbtn_cancel.create(getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_cancel.setVisibility(View.GONE);


        itvw_date.create(R.drawable.ic_calendar_month_24,getResources().getString(R.string.date), Utility.getDateString(new Date(),"dd MMMM yyyy"));
        setLocation();
        itvw_location.create(R.drawable.ic_baseline_location,getResources().getString(R.string.location), getResources().getString(R.string.unknown));
        itvw_time.create(R.drawable.ic_access_time_24,getResources().getString(R.string.time), Utility.getDateString(new Date(),"HH:mm"));

        restartUI();
    }

    @Override
    protected void initData() {
        itvw_time.setValue( Utility.getDateString(new Date(),"HH:mm"));
        itvw_date.setValue( Utility.getDateString(new Date(),"dd MMMM yyyy"));
    }

    @Override
    protected void initListener() {
        rvly_sos.setOnClickListener(view -> {
            cpi_sos.clearAnimation();
            rvly_run.clearAnimation();
            bbtn_cancel.clearAnimation();
            cpi_sos.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.animsos));
            rvly_run.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.animsos));
            rvly_run.setVisibility(View.VISIBLE);
            bbtn_cancel.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
            bbtn_cancel.setVisibility(View.VISIBLE);
            cpi_sos.setVisibility(View.VISIBLE);
            rvly_sos.setVisibility(View.INVISIBLE);
            txvw_description.setVisibility(View.INVISIBLE);
            run();
        });

        bbtn_cancel.setOnActionListener((VmaButton.OnClickListener) view -> {
            timerTask.cancel();
            restartUI();
        });
    }


    private void restartUI(){
        txvw_title.setText(getResources().getString(R.string.AreYouInEmergency));
        if (bbtn_cancel.getVisibility() == View.VISIBLE){
            bbtn_cancel.clearAnimation();
            bbtn_cancel.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_down_out));
            bbtn_cancel.setVisibility(View.INVISIBLE);
        }

        task = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cpi_sos.setProgress(0, true);
        }
        txvw_counter.setVisibility(View.VISIBLE);
        rvly_sos.setVisibility(View.VISIBLE);
        txvw_description.setVisibility(View.VISIBLE);

        imvw_message.clearAnimation();
        imvw_message.setVisibility(View.INVISIBLE);
        rvly_run.setVisibility(View.INVISIBLE);
        cpi_sos.setVisibility(View.INVISIBLE);
        imvq_successFrame2.setVisibility(View.INVISIBLE);
        imvq_successFrame1.setVisibility(View.INVISIBLE);
    }

    private void run(){
        cpi_sos.setProgress(0);
        task = 0;
        txvw_counter.setText(String.valueOf(task));

        timerTask = new Timer();
        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                task ++;
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cpi_sos.setProgress(task,true);
                    }
                    int count = task % 10;
                    if (count == 0){
                        txvw_counter.setText(String.valueOf(task/10));
                    }
                    if (task == 50){
                        new Handler().postDelayed(() -> onFinishTimer(),800);
                        this.cancel();
                    }
                });
            }
        },0,100);
    }


    private void setLocation(){

        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA));
            double lon = gpsLast.getDouble("longitude");
            double lat = gpsLast.getDouble("latitude");

            LocationConverter converter = new LocationConverter(mActivity, lon,lat);
            runOnUiThread(() -> itvw_location.setValue(converter.getLatitudeDisplay()+"  "+ converter.getLongitudeDisplay()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("SetTextI18n")
    private void onFinishTimer(){
        bbtn_cancel.clearAnimation();
        bbtn_cancel.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_down_out));
        bbtn_cancel.setVisibility(View.INVISIBLE);

        txvw_counter.setVisibility(View.INVISIBLE);
        imvw_message.setVisibility(View.VISIBLE);
        txvw_title.setText(getResources().getString(R.string.sending)+"...");
        imvw_message.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.flyin));

        new Handler().postDelayed(this::onSuccess,2000);
    }

    @SuppressLint("SetTextI18n")
    private void onSuccess(){
        txvw_title.setText("SOS "+getResources().getString(R.string.sent)+" !");
        imvw_message.setVisibility(View.INVISIBLE);
        rvly_run.setVisibility(View.INVISIBLE);
        cpi_sos.setVisibility(View.INVISIBLE);

        imvq_successFrame1.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.sos_zoom_out));
        imvq_successFrame1.setVisibility(View.VISIBLE);
        imvq_successFrame2.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.sos_zoom_in));
        imvq_successFrame2.setVisibility(View.VISIBLE);

        new Handler().postDelayed(this::restartUI,5000);
    }

}
