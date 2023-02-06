package com.vma.goapp.libs;

import android.content.Context;

import com.vma.goapp.R;

/**
 * Created by Mochamad Rezza Gumilang on 29/03/2022
 */
public class VmaGlobalConfig {
    private static final String FORMAT_COORDINATE = "CoordinateFormat";
    public static final int FORMAT_COORDINATE_SECONDS = 3;
    public static final int FORMAT_COORDINATE_MINUTE = 2;
    public static final int FORMAT_COORDINATE_DEGREE = 1;

    private static final String DISTANCE_UNIT = "distance_unit";
    private static final String SPEED_UNIT = "speed_unit";


    public static void setCoordinateFormat(Context context, int format){
        VmaPreferences.save(context, FORMAT_COORDINATE, format);
    }

    public static int getCoordinateFormat(Context context){
        int format = VmaPreferences.getInt(context, FORMAT_COORDINATE);
        if (format == -1){
            return FORMAT_COORDINATE_DEGREE;
        }
        return VmaPreferences.getInt(context, FORMAT_COORDINATE);
    }

    public static String getCoordinateFormatName(Context context){
        int format = getCoordinateFormat(context);
        if (format == FORMAT_COORDINATE_SECONDS){
            return context.getResources().getString(R.string.decimal_seconds);
        }
        if (format == FORMAT_COORDINATE_MINUTE){
            return context.getResources().getString(R.string.decimal_minutes);
        }
        else {
            return context.getResources().getString(R.string.decimal_degree);
        }
    }

    public static void setDistanceUnit(Context context,  String unit){
        VmaPreferences.save(context, DISTANCE_UNIT, unit);
    }

    public static String  getDistanceUnit(Context context){
        String unit = "Nm";
        if (!VmaPreferences.get(context,DISTANCE_UNIT).isEmpty() ){
            unit = VmaPreferences.get(context,DISTANCE_UNIT);
        }

        return unit;
    }

    public static void setSpeedUnit(Context context,  String unit){
        VmaPreferences.save(context, SPEED_UNIT, unit);
    }

    public static String  getSpeedUnit(Context context){
        String unit = "Knot";
        if (!VmaPreferences.get(context,SPEED_UNIT).isEmpty() ){
            unit = VmaPreferences.get(context,SPEED_UNIT);
        }

        return unit;
    }
}
