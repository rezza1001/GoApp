package com.vma.goapp.service;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.vma.goapp.R;
import com.vma.goapp.VmaApplication;
import com.vma.goapp.dom.VMA_COMMAND;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.logbook.FishHolder;
import com.vma.goapp.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {
    private static final String TAG = "MainService";

    public static Location lastLocation = null;
    public static boolean permission = true;
    public static  ArrayList<Bundle> listDummy = new ArrayList<>();

    private Timer timer;
    private String lastLocationStr = "";

    public static HashMap<Integer, FishHolder> MAP_FISH = new HashMap<>();


    private int indexDummy = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startNotificationListener();
        super.onCreate();
        Log.d(TAG, "onCreate");
//        loadDummy();

        sender();
        loadFish();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return  START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopped");
    }

    public void checkGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        LocationManager  mlocManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mlocManager.getProviders(true);
        Location bestLocation = null;
        Log.d("GPS_SERVICE","providers "+providers.size());
        for (String provider : providers) {
            Location l = mlocManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        if (bestLocation != null){
            sendLocationUpdate(bestLocation,true);
        }
        else {
            Log.e(TAG,"DATA GPS FAILED");
        }

    }


    private void sendLocationUpdate(Location location, boolean status){

        if (lastLocation == null){
            lastLocation = location;
            Log.e(TAG,"LOCATION NULL OR NOT FOUND");
            return;
        }
        String note = "Provider";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (location.isMock()){
                note = "FAKE GPS";
            }
        }else {
            if ( location.isFromMockProvider()){
                note = "FAKE GPS";
            }
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put(VmaApiConstant.RF_ITEM_CMD, VMA_COMMAND.GNRMC.getValue());
            jo.put(VmaApiConstant.GPS_ITEM_LON, location.getLongitude());
            jo.put(VmaApiConstant.GPS_ITEM_LAT, location.getLatitude());
            jo.put(VmaApiConstant.GPS_NOTE, note);

            int speedKm = 0;
            if (location.hasSpeed()){
                speedKm =(int) ((location.getSpeed()*3600)/1000); // KM/H
            }
            double speedNm = speedKm/1.852;  // Nautical Mile (NM)

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(location.getTime());
            DateFormat format= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("id"));

            jo.put(VmaApiConstant.GPS_ITEM_STATUS, status);
            jo.put(VmaApiConstant.GPS_ITEM_SPEED, Math.floor(speedNm));
            if (!status){
                jo.put(VmaApiConstant.GPS_ITEM_SPEED, 0);
            }
            jo.put(VmaApiConstant.GPS_ITEM_DATE, format.format(location.getTime()));
            float bearing = lastLocation.bearingTo(location);
            if (bearing == 0){
                bearing = VmaApplication.LastBearing;
            }

            jo.put(VmaApiConstant.GPS_ITEM_BEARING,bearing);

            Log.d(TAG,"DATA GPS "+jo);
            lastLocationStr = jo.toString();
            lastLocation = location;
        }catch (JSONException e){
            e.printStackTrace();
        }
        VmaPreferences.save(getApplicationContext(),VmaApiConstant.GPS_LSAT_DATA, jo.toString()); // SAVE LAST LOCATION

        if (lastLocationStr.isEmpty()){
            Log.d(TAG,"sendBroadcast is empty");
            return;
        }
        Intent intent = new Intent(VmaConstants.VMA_GPS);
        intent.putExtra(VmaConstants.SERVICE_DATA, lastLocationStr);
        sendBroadcast(intent);
        Log.d(TAG,"sendBroadcast "+lastLocationStr);
    }

    private void sender(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (permission) {
                    checkGPSLocation();
                }

            }
        },0,1000);
    }


    private void loadDummy(){
        String dummy = Utility.getStringFromAssets(getApplicationContext(),"gps.txt");
        if (dummy == null){
            return;
        }
        String[] line = dummy.split("\n");
        for (String s : line){
            String sLat = s.split("~")[0];
            String sLon = s.split("~")[1];
            String sBear = s.split("~")[2].trim();

            Bundle bundle = new Bundle();
            bundle.putDouble(VmaApiConstant.GPS_ITEM_LON, Double.parseDouble(sLon));
            bundle.putDouble(VmaApiConstant.GPS_ITEM_LAT, Double.parseDouble(sLat));
            bundle.putInt(VmaApiConstant.GPS_ITEM_BEARING, 0);
            listDummy.add(bundle);
        }

    }

    public void startNotificationListener() {
        new Thread(this::ShowNotification).start();
    }

    public void ShowNotification(){
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(MainService.this);
        builder.setSmallIcon(R.drawable.ic_dpi);
        builder.setContentTitle("GoApp APP");
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_MAX);
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "com.vma.goapp";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "GoApp",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(0, notification);

    }

    private void loadFish(){
        MAP_FISH.clear();
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader(getAssets().open("masterikan.csv")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                FishHolder holder = new FishHolder();
                String []value = mLine.split(",");
                holder.setId(Integer.parseInt(value[0]));
                holder.setName(value[1]);
                holder.setImageName(value[2]);
                holder.setType(value[3]);
                MAP_FISH.put(holder.getId(), holder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
