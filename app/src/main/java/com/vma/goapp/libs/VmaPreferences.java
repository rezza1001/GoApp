package com.vma.goapp.libs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

//  VMA Configuration
public class VmaPreferences {

    public static final String BRIGHTNESS = "BRIGHTNESS";
    public static final String BRIGHTNESS_MODE = "BRIGHTNESS_MODE";
    public static final String IMAGE_PROFILE = "IMAGE_PROFILE";



    public static void save(Context context, String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }

    public static void save(Context context, String key, int value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }
    public static void save(Context context, String key, long value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }
    public static void save(Context context, String key, float value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
        Log.d("VMA_PREF","save "+key+" = "+ value);
    }

    public static String get(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPref.getString(key,"");
        Log.d("VMA_PREF","get "+key+" = "+ (value.isEmpty() ?"<<EMPTY>>": value) );
        return value;
    }
    public static int getInt(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sharedPref.getInt(key,-1);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }
    public static long getLong(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        long value = sharedPref.getLong(key,0);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }
    public static float getFloat(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        float value = sharedPref.getFloat(key,0);
        Log.d("VMA_PREF","get "+key+" = "+value);
        return value;
    }

    public static void delete(Context context, String key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().remove(key).apply();
    }


}
