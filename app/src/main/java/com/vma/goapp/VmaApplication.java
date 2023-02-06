package com.vma.goapp;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.VmaLanguage;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.libs.VmaTheme;
import com.vma.goapp.service.GpsService;
import com.vma.goapp.service.LogbookService;
import com.vma.goapp.service.MainService;
import com.vma.goapp.service.TimerService;

public class VmaApplication extends Application {
    private static final String TAG = "VmaApplication";
    public static String language = "id";

    public static float LastBearing = 0;

    public static int mPosFormat = VmaConstants.VMA_GPSFORMAT_DEG;
    public static int PROFILE_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        language = VmaLanguage.INDONESIA;

        VmaTheme.applyTheme(this);
        startService(new Intent(this, TimerService.class));

        startService(new Intent(this, MainService.class));
//        startService(new Intent(this, GpsService.class));
        VmaLanguage.changeToIndonesia(this);

        new Handler().postDelayed(() -> startService(new Intent(this, LogbookService.class)),5000);

        initSettingVMA();

        PROFILE_VERSION = VmaPreferences.getInt(getApplicationContext(),VmaPreferences.IMAGE_PROFILE);

        // Check Root
    }

    private void initSettingVMA(){
        int brightness = VmaPreferences.getInt(this,VmaPreferences.BRIGHTNESS);
        if (brightness == -1) {
            brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
            VmaPreferences.save(this,VmaPreferences.BRIGHTNESS, brightness);
        }

        int brightMode = VmaPreferences.getInt(this,VmaPreferences.BRIGHTNESS_MODE);
        if (brightMode == -1){
            brightMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            VmaPreferences.save(this,VmaPreferences.BRIGHTNESS_MODE, brightMode);
        }

    }


}
