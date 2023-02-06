package com.vma.goapp.libs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

import com.vma.goapp.VmaApplication;

import java.util.Locale;

public class VmaLanguage {
    public static final String LANGUAGE = "vmaLanguage";
    public static final String CHANGE_LANGUAGE = "change_language";

    public static final String INDONESIA = "us";
    public static final String ENGLISH = "en";

    public static String getString(Context context, int res){
        return context.getResources().getString(res);
    }

    public static void init(Context context){

        if (getLanguage(context).isEmpty() || getLanguage(context).equalsIgnoreCase(INDONESIA)){
            changeToIndonesia(context);
        }
        else {
            changeToEnglish(context);
        }
    }

    public static String getLanguage(Context context){
        String language = VmaPreferences.get(context,LANGUAGE);
        Log.d(LANGUAGE,"getLanguage "+ language);
        return language;
    }

    public static String getCountry(Context context){
        String code = getLanguage(context);
        if (code.equals(INDONESIA)){
            return "Indonesia";
        }
        else {
            return "English";
        }
    }

    public static void changeToEnglish(Context context){
        VmaPreferences.save(context, LANGUAGE, ENGLISH);
        Locale locale = new Locale(ENGLISH);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, null);
        String newConfig = getLanguage(context);

        Log.d(LANGUAGE,"changeToEnglish from "+ VmaApplication.language+" to "+newConfig);
        if (!newConfig.equals(VmaApplication.language)){
            VmaApplication.language = ENGLISH;
            new Handler().postDelayed(() -> context.sendBroadcast(new Intent(CHANGE_LANGUAGE)),100);
        }

    }
    public static void changeToIndonesia(Context context){

        VmaPreferences.save(context, LANGUAGE, INDONESIA);
        Locale locale = new Locale(INDONESIA);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, null);
        String newConfig = getLanguage(context);

        Log.d(LANGUAGE,"changeToIndonesia from "+VmaApplication.language+" to "+newConfig);
        if (!newConfig.equals(VmaApplication.language)){
            VmaApplication.language = INDONESIA;
            new Handler().postDelayed(() -> context.sendBroadcast(new Intent(CHANGE_LANGUAGE)),100);
        }
    }
}
