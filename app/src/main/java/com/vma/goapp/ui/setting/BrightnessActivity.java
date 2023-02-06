package com.vma.goapp.ui.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.vma.goapp.R;
import com.vma.goapp.database.table.FlagDB;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.master.MyActivity;

public class BrightnessActivity extends MyActivity {

    private SwitchCompat swtch_auto;
    private AppCompatSeekBar seek_brightness;
    private TextView txvw_value;

    private int brightness =0;

    @Override
    protected int setLayout() {
        return R.layout.activity_setting_brightness;
    }

    @Override
    protected void initLayout() {
        CardView card_automatic = findViewById(R.id.card_automatic);
        CardView card_status = findViewById(R.id.card_status);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            card_automatic.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primaryLight));
            card_status.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primaryLight));
        }

        swtch_auto = findViewById(R.id.swtch_auto);
        seek_brightness = findViewById(R.id.seek_brightness);
        txvw_value = findViewById(R.id.txvw_value);

    }

    @Override
    protected void initData() {
        boolean settingsCanWrite = Settings.System.canWrite(mActivity);
        Log.d(TAG,"settingsCanWrite "+settingsCanWrite);
        if (!settingsCanWrite){
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
            return;
        }

        int mode = VmaPreferences.getInt(mActivity,VmaPreferences.BRIGHTNESS_MODE);
        swtch_auto.setChecked(mode != 0);
        if (swtch_auto.isChecked()){
            seek_brightness.setEnabled(false);
        }
        brightness = VmaPreferences.getInt(mActivity,VmaPreferences.BRIGHTNESS);

        float x = (((float)brightness/(float)255) * (float)100);
        int percent = Math.round(x);
        Log.d(TAG," brightness = "+brightness +" X = "+x +" percent ="+percent +" ----> mode "+mode);
        seek_brightness.setProgress(percent);
        txvw_value.setText(String.valueOf(percent));
    }

    @Override
    protected void initListener() {
        seek_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                txvw_value.setText(""+progress);
                brightness = (int) (((float)progress/(float)100) * (float)255);
                VmaPreferences.save(mActivity,VmaPreferences.BRIGHTNESS, brightness);
                Log.d(TAG,"brightness change "+brightness +" progress "+progress);
                Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        swtch_auto.setOnCheckedChangeListener((compoundButton, b) -> {
            int mode = b ? 1 : 0;
            VmaPreferences.save(this,VmaPreferences.BRIGHTNESS_MODE, mode);
            Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            if (mode == 0){
                Settings.System.putInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                seek_brightness.setEnabled(true);
            }
            else {
                seek_brightness.setEnabled(false);
            }
        });
    }






}
