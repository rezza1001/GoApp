package com.vma.goapp.libs;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.esri.arcgisruntime.geometry.Point;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Locale;

public class DistanceUnit {

    private double mDistance = 0;
    private String unit = "Nm";

    public DistanceUnit (Context context, double distanceKm){
        unit = VmaGlobalConfig.getDistanceUnit(context);
        mDistance = distanceKm;
    }

    public DistanceUnit(Context context){
        unit = VmaGlobalConfig.getDistanceUnit(context);
    }


    public double getDistance(){
        double distance = mDistance;
        if (unit.equalsIgnoreCase("KM")){
            distance = distance * 1.852;
        }
        else if (unit.equalsIgnoreCase("Mi")){
            distance = distance * 1.151;
        }
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.UP);
        distance =  Double.parseDouble(df.format(distance).replaceAll(",","."));
        return distance;
    }

    public String getDisplay(){
        return getDistance()+" "+unit;
    }

    public String getUnit(){
        return unit;
    }

    public void calcDistance(Point pos1, Point pos2 ) {
         calcDistance(pos1.getY(), pos1.getX(), pos2.getY(), pos2.getX());
    }

    //  calc distance
    public void calcDistance(double lat1, double lon1, double lat2, double lon2){
        double distance;

        Location point1 = new Location("locationA");
        point1.setLatitude(lat1);
        point1.setLongitude(lon1);

        Location point2 = new Location("locationB");
        point2.setLatitude(lat2);
        point2.setLongitude(lon2);

        distance = point1.distanceTo(point2);
        distance /= 1852.0;

        mDistance = distance;
    }

    public double getNm(){
        double distance = mDistance;
        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.UP);
        distance =  Double.parseDouble(df.format(distance).replaceAll(",","."));
        return distance;
    }


}
