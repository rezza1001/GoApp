package com.vma.goapp.libs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;

import com.vma.goapp.R;

import java.util.Calendar;

public class VmaTheme {

    public static final String CHANGE_THEME = "ChangeTheme";
    private static final String VMA_THEME = "VmaTheme";
    private static final String VMA_NAVIGATION = "VmaNavigation";

    public static final int NIGHT_MODE = 1;
    public static final int DAY_MODE = 2;
    public static final int AUTO_MODE = 3;

    public static final int NAVIGATION_SHOW = 1;
    public static final int NAVIGATION_HIDE = 0;


    public static void setTheme(Context context, int theme){
        switch (theme){
            case NIGHT_MODE:setDarkMode(context); break;
            case DAY_MODE:setLightMode(context); break;
            case AUTO_MODE:setAutoMode(context); break;
        }
    }

    public static void applyTheme(Context context){
        int mode = VmaPreferences.getInt(context, VMA_THEME);
        if (mode == 0){
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
                mode = NIGHT_MODE;
            }
            else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO){
                mode = DAY_MODE;
            }
        }

        if (mode == DAY_MODE){
            setLightMode(context);
        }
        else if (mode == NIGHT_MODE){
            setDarkMode(context);
        }
        else {
            setAutoMode(context);
        }
    }

    public static void setDarkMode(Context context){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        int mode = VmaPreferences.getInt(context, VMA_THEME);
        if (mode == DAY_MODE ){
            new Handler().postDelayed(() -> context.sendBroadcast(new Intent(CHANGE_THEME)),500);
        }
        VmaPreferences.save(context, VMA_THEME, NIGHT_MODE);
    }
    public static void setLightMode(Context context){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        int mode = VmaPreferences.getInt(context, VMA_THEME);
        if (mode == NIGHT_MODE ){
            new Handler().postDelayed(() -> context.sendBroadcast(new Intent(CHANGE_THEME)),500);
        }
        VmaPreferences.save(context, VMA_THEME, DAY_MODE);
    }
    public static void setAutoMode(Context context){
        VmaPreferences.save(context, VMA_THEME, AUTO_MODE);
        int mode = VmaPreferences.getInt(context, VMA_THEME);
        if (mode == AUTO_MODE ){
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 6 && calendar.get(Calendar.HOUR_OF_DAY) < 18){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            new Handler().postDelayed(() -> context.sendBroadcast(new Intent(CHANGE_THEME)),500);
        }

    }

    public static String getThemeName(Context context){
        int mode = VmaPreferences.getInt(context, VMA_THEME);
        if (mode == 0){
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
                mode = NIGHT_MODE;
            }
            else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO){
                mode = DAY_MODE;
            }
        }
        switch (mode){
            case DAY_MODE:return context.getResources().getString(R.string.light_mode);
            case AUTO_MODE:return context.getResources().getString(R.string.automatic);
            default:return context.getResources().getString(R.string.dark_mode);
        }
    }

    public static void setNavigation(Context context, int visibility){
        VmaPreferences.save(context, VMA_NAVIGATION, visibility);
    }

    public static int getNavigation(Context context){
        return VmaPreferences.getInt(context, VMA_NAVIGATION);
    }

    public static String getNavigationName(Context context){
        int navigation = getNavigation(context);
        if (navigation == NAVIGATION_HIDE){
            return context.getResources().getString(R.string.hide);
        }
        else {
            return context.getResources().getString(R.string.show);
        }
    }

}
