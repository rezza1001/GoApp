package com.vma.goapp.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.database.table.AccountDB;
import com.vma.goapp.database.table.BlackBoxDB;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class TimerService extends Service {

    private LocationManager locationManager;

    private Timer timer;
    int second = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                second++;
                buildLocation();
                if (second % 10 == 0) {
                    checkBlackBox();
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 10);
                    sendBroadcast(intent);
                }
                if (second % 30 == 0) {
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 30);
                    sendBroadcast(intent);
                }
                if (second % 60 == 0) {
                    Intent intent = new Intent(VmaConstants.VMA_TIMER_TASK);
                    intent.putExtra(VmaConstants.SERVICE_DATA, 60);
                    sendBroadcast(intent);

                    second = 0;
                }

            }
        }, 0, 1000);
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return  START_STICKY;
    }

    private void buildLocation() {
        Location location = getLastKnownLocation();
        if (location == null){
            Log.e("GPS_SERVICE","LOCATION NULL");
            return;
        }

        Log.d("GPS_SERVICE","location "+ location.getLatitude()+", "+location.getLongitude());
    }


    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        Log.d("GPS_SERVICE","providers "+providers.size());
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void checkBlackBox(){
        if (isNetworkConnected()){
            BlackBoxDB blackBoxDB = new BlackBoxDB();
            ArrayList<BlackBoxDB> list =  blackBoxDB.getUnUpload(getApplicationContext());
            if (list.size() > 0){
                sendApi(list.get(0));
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void sendApi(BlackBoxDB db){
        AccountDB accountDB = new AccountDB();
        accountDB.loadData(getApplicationContext());

        PostManager post = new PostManager(getApplicationContext(), ApiConfig.POST_SAVE_BLACKBOX);
        post.addParam("imei", accountDB.imei);
        post.addParam("longitude", db.longitude);
        post.addParam("latitude", db.latitude);
        post.addParam("speed", db.speed);
        post.addParam("course", db.course);
        post.addParam("status", "Offline");
        post.addParam("note", "Out of service network");
        post.addParam("time", db.time);
        post.showloading(false);
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                Log.d("TimerService","sendLocationData Success");
                db.isUpload = true;
                db.update(getApplicationContext());

                checkBlackBox();
            }
            else {
                Log.d("TimerService","sendLocationData Failed "+ message);
            }
        });
    }
}
