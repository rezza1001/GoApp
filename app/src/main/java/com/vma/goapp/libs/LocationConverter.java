package com.vma.goapp.libs;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.vma.goapp.dom.VmaApiConstant;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Mochamad Rezza Gumilang on 30/03/2022
 */
public class LocationConverter {
    private static final String TAG = "LocationConverter";
    public static final SpatialReference WGS84 = SpatialReferences.getWgs84();

    public enum Type {LONGITUDE, LATITUDE}

    String mLongitude;
    String mLatitude;
    int format = Location.FORMAT_DEGREES;

    public LocationConverter(Context context, String longitude, String latitude){
        mLongitude = longitude;
        mLatitude = latitude;
        format = VmaGlobalConfig.getCoordinateFormat(context);
    }
    public LocationConverter(Context context, double longitude, double latitude){
        mLongitude = longitude+"";
        mLatitude = latitude+"";
        format = VmaGlobalConfig.getCoordinateFormat(context);
    }

    public String getDisplayDegree(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_DEGREES);
        String strLatitude = Location.convert(latitude, Location.FORMAT_DEGREES);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public String getDisplayMinute(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_MINUTES);
        String strLatitude = Location.convert(latitude, Location.FORMAT_MINUTES);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public String getDisplaySecond(Type type){
        double longitude = Double.parseDouble(mLongitude);
        double latitude = Double.parseDouble(mLatitude);

        String strLongitude = Location.convert(longitude, Location.FORMAT_SECONDS);
        String strLatitude = Location.convert(latitude, Location.FORMAT_SECONDS);
        if (type == Type.LONGITUDE){
            return strLongitude;
        }
        else {
            return strLatitude;
        }
    }

    public double getLongitude(){
        return Double.parseDouble(mLongitude);
    }
    public double getLatitude(){
        return Double.parseDouble(mLatitude);
    }

    public String getLongitudeDisplay(){

        Log.d(TAG,"getLongitudeDisplay format "+format);
        if (format == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            return getDisplayDegree(Type.LONGITUDE);
        }
        else  if (format == VmaGlobalConfig.FORMAT_COORDINATE_MINUTE){
            return getDisplayMinute(Type.LONGITUDE);
        }
        else
            return getDisplaySecond(Type.LONGITUDE);
    }

    public String getLatitudeDisplay(){
        Log.d(TAG,"getLatitudeDisplay format "+format);
        if (format == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            return getDisplayDegree(Type.LATITUDE);
        }
        else  if (format == VmaGlobalConfig.FORMAT_COORDINATE_MINUTE){
            return getDisplayMinute(Type.LATITUDE);
        }
        else
            return getDisplaySecond(Type.LATITUDE);
    }

    public String getUnitLon(){
        double longitude = Double.parseDouble(mLongitude);
        if (longitude < 0){
            return "W";
        }
        else {
            return "E";
        }
    }
    public String getUnitLat(){
        double latitude = Double.parseDouble(mLatitude);
        if (latitude < 0){
            return "S";
        }
        else {
            return "N";
        }
    }

    public double getBearing(Context context){
        double theta;

        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(context, VmaApiConstant.GPS_LSAT_DATA));
            double lon = gpsLast.getDouble("longitude");
            double lat = gpsLast.getDouble("latitude");

            double degToRad = Math.PI / 180.0;
            double phi1 = lat * degToRad;
            double phi2 = getLatitude() * degToRad;
            double lam1 = lon * degToRad;
            double lam2 = getLongitude() * degToRad;
            theta = Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2), Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)) * 180 / Math.PI;
            if(theta < 0.0)
                theta += 360.0;
            return theta;
        }catch (Exception e) {
            return 0.0;
        }
    }

    public String getBearingStr(Context context){
        DecimalFormat df = new DecimalFormat("0");
        df.setRoundingMode(RoundingMode.UP);
        double bearing = getBearing(context);

        return df.format(bearing);
    }

    public Point getPoint(){
        return new Point(getLongitude(), getLatitude(), WGS84);
    }
}
