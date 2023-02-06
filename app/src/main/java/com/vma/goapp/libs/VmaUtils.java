package com.vma.goapp.libs;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.vma.goapp.dom.VmaConstants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class VmaUtils {
    public static final SpatialReference WGS84 = SpatialReferences.getWgs84();
    public static int DEGFORMAT = VmaConstants.VMA_GPSFORMAT_DEG;


    public static String mapLatToString(double geoPos, int show){
        String latSign;
        String format = "%s %s";
        if ( show == VmaConstants.VMA_GPSFORMAT_DEG){
            format = "%s%s";
        }

        if(geoPos < 0.0) {
            latSign  = show == VmaConstants.VMA_GPSFORMAT_DEG ? "-" : "S";
            geoPos *= -1.0;
        } else
            latSign  = show == VmaConstants.VMA_GPSFORMAT_DEG ? "": "N";

        return String.format(Locale.ENGLISH, format, latSign, posToString(geoPos, show).trim());
    }

    public static String mapLonToString(double geoPos, int show){
        String latSign;
        String format = "%s %s";
        if ( show == VmaConstants.VMA_GPSFORMAT_DEG){
            format = "%s%s";
        }

        if(geoPos < 0.0) {
            latSign = show == VmaConstants.VMA_GPSFORMAT_DEG ? "-" :  "W";
            geoPos *= -1.0;
        } else
            latSign = show == VmaConstants.VMA_GPSFORMAT_DEG ? "" : "E";

        return String.format(Locale.ENGLISH, format, latSign, posToString(geoPos, show).trim());
    }

    public static String posToString(double geoPos, int show){
        geoPos = convertDoubleF(geoPos,6);
        String  strPos;
        int     deg, min;
        double  dmin, sec;

        switch(show) {
            case VmaConstants.VMA_GPSFORMAT_MIN:            // 도,분
                deg = (int)geoPos;
                dmin = (geoPos-deg) * 60.0;
                strPos = String.format(Locale.ENGLISH, "%d°%6.3f´",
                        deg, dmin);
                break;

            case VmaConstants.VMA_GPSFORMAT_DEG:            // 도
                strPos = String.format(Locale.ENGLISH, "%9.5f°", geoPos);
                break;

            case VmaConstants.VMA_GPSFORMAT_SEC:            // 도,분,초
            default:
                deg = (int)geoPos;
                dmin = (geoPos-deg) * 60.0;
                min = (int)dmin;
                sec = (geoPos-deg-(min/60.0)) * 3600.0;

                if (sec > 59.9999 ){
                    sec = 0;
                    min = min + 1;
                }
                if (dmin > 59.9999) {
                    min = 0;
                    deg = deg + 1;
                }

                strPos = String.format(Locale.ENGLISH, "%d°%d´%4.1f˝",
                        deg, min, sec);
                break;
        }
        return strPos;
    }

    public static double convertDoubleF(double data, int digitAfterComma){
        StringBuilder sFormat = new StringBuilder("#.");
        for (int i=0; i<digitAfterComma; i++){
            sFormat.append("#");
        }
        DecimalFormat df = new DecimalFormat(sFormat.toString(), new DecimalFormatSymbols(Locale.US));
        String sLat = df.format(data);
        data = Double.parseDouble(sLat);
        return data;
    }

    public static double bearing(double lat1, double long1, double lat2, double long2){
        double theta;

        try {
            double degToRad = Math.PI / 180.0;
            double phi1 = lat1 * degToRad;
            double phi2 = lat2 * degToRad;
            double lam1 = long1 * degToRad;
            double lam2 = long2 * degToRad;
            theta = Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2), Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)) * 180 / Math.PI;
            if(theta < 0.0)
                theta += 360.0;
            return theta;
        }catch (Exception e) {
            return 0.0;
        }
    }
    public static double bearing(Point point1, Point point2){
        double theta;

        try {
            double degToRad = Math.PI / 180.0;
            double phi1 = point1.getY() * degToRad;
            double phi2 = point1.getX() * degToRad;
            double lam1 = point2.getY() * degToRad;
            double lam2 = point2.getX() * degToRad;
            theta = Math.atan2(Math.sin(lam2 - lam1) * Math.cos(phi2), Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1)) * 180 / Math.PI;
            if(theta < 0.0)
                theta += 360.0;
            return theta;
        }catch (Exception e) {
            return 0.0;
        }
    }
}
