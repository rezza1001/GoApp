package com.vma.goapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.VmaPreferences;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class TrackRecordService extends Service {
    private final IBinder mBinder = new MyBinder();

    private Timer timer;
    int second = 0;
    int minute = 0;
    int hour = 0;

    float distance = 0;

    double lastLongitude = 0, lastLatitude = 0;

    public static final String fileName = "tracking.txt";
    public static final String folderName = "track";

    public static boolean isRecording = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileProcessing.createFolder(getApplicationContext(),folderName);

        String path = FileProcessing.getRootPath(getApplicationContext())+ TrackRecordService.folderName;
        FileProcessing.DeleteFile(path,TrackRecordService.fileName);

        isRecording = true;
        distance = 0;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second++;
                if (second % 60 == 0) {
                    minute = minute + 1;
                    second = 0;
                }
                if (minute % 60 == 0 && minute > 0) {
                    hour = hour + 1;
                    minute = 0;
                }

                Intent intent = new Intent(VmaConstants.TRACK_RECORD_TASK);
                intent.putExtra(VmaConstants.HOUR, hour);
                intent.putExtra(VmaConstants.MINUTE, minute);
                intent.putExtra(VmaConstants.SECOND, second);
                sendBroadcast(intent);

                getLocation();

            }
        }, 0, 1000);
    }

    private void getLocation() {
        double longitude = 0, latitude = 0;
        String prefData = VmaPreferences.get(getApplicationContext(), VmaApiConstant.GPS_LSAT_DATA);
        if (prefData.isEmpty()){
            Log.d("TrackRecordService","GPS LAST DATA EMPTY");
            return;
        }

        try {
            JSONObject jo = new JSONObject(prefData);
            if (jo.has(VmaApiConstant.GPS_ITEM_SNR)){
                return;
            }
            LocationConverter converter = new LocationConverter(this, jo.getString("longitude"),jo.getString("latitude"));
            latitude = converter.getLatitude();
            longitude = converter.getLongitude();

            if (lastLatitude == 0 && lastLongitude == 0){
                lastLongitude = longitude;
                lastLatitude = latitude;
                FileProcessing.WriteFileToLog(getApplicationContext(),folderName,fileName,jo.toString());
            }
            else {
                if (converter.getLongitude() != 0.0 && converter.getLatitude() != 0.0){
                    Point point1 = new Point(lastLongitude, lastLatitude, SpatialReferences.getWgs84());
                    Point point2 = new Point(longitude, latitude, SpatialReferences.getWgs84());

                    DistanceUnit unit = new DistanceUnit(getApplicationContext());
                    unit.calcDistance(point1, point2);
                    double distanceMetre = unit.getNm() * 1852;
                    if (distanceMetre < 20){
                        return;
                    }
                    distance = (float) (distance + unit.getNm());
                    jo.remove("command");
                    jo.remove("status");
                    FileProcessing.WriteFileToLog(getApplicationContext(),folderName,fileName,jo.toString());

                    lastLongitude = longitude;
                    lastLatitude = latitude;
                }
                else {
                    Log.e("TrackRecordService","lastLatitude = 0 & lastLatitude = 0");
                }
            }


        }catch (Exception e){
            e.printStackTrace();
            Log.e("TrackRecordService","GPS DATA ERROR "+ e.getMessage());
        }
    }

    public class MyBinder extends Binder {
        public TrackRecordService getService() {
            return TrackRecordService.this;
        }
    }

    @Override
    public void onDestroy() {
        isRecording = false;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat format = new DecimalFormat("0.00", symbols);
        float xDistance = Float.parseFloat(format.format(distance));
        VmaPreferences.save(getApplicationContext(),VmaConstants.TRACKING_DISTANCE, xDistance);
        timer.cancel();
        super.onDestroy();
    }

}
