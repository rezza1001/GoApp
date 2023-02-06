package com.vma.goapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.database.table.AccountDB;
import com.vma.goapp.database.table.BlackBoxDB;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AuthService extends Service {

    private static final String TAG = "AuthService";
    Thread thread;
    private AccountDB accountDB;
    private int menit = 0;
    private boolean start = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Starting");
        writeLog("Starting Service XXXXXXXXXXXXXXX");
        start = true;
        accountDB = new AccountDB();
        accountDB.loadData(getApplicationContext());
        sendLocationData();
        startTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Stopped");
        writeLog("Stopped Service XXXXXXXXXXXXXXX");
    }


    private void startTimer(){
        RunnableImpl runnable = new RunnableImpl();
        thread = new Thread(runnable,"AUTH");

        thread.start();
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
//            Toast.makeText(getApplicationContext(),"Menit "+ menit, Toast.LENGTH_SHORT).show();
            sendLocationData();
        }
    };

    private void sendLocationData(){
        double lon = 0;
        double lat = 0;
        double speed = 0;
        double course = 0;
        String note = "";
        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(getApplicationContext(), VmaApiConstant.GPS_LSAT_DATA));
            lon = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_LON);
            lat = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_LAT);
            speed = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_SPEED);
            note = gpsLast.getString(VmaApiConstant.GPS_NOTE);
//            speed = ThreadLocalRandom.current().nextDouble(0, 11);
            course = gpsLast.getDouble(VmaApiConstant.GPS_ITEM_BEARING);
        } catch (JSONException e) {
            e.printStackTrace();
            writeLog("JSONException: "+e.getMessage());
        }

        Log.d(TAG,"SPEED :"+speed+" menit : "+menit);
        writeLog("Speed "+ speed+" | Menit : "+menit);
//        if (menit % 5 == 0 && speed >= 10){
//            Log.d(TAG,"Send in 5 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 10 == 0  && (speed >= 5 &&  speed < 10)){
//            Log.d(TAG,"Send in 10 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 30 == 0  && (speed >= 1 &&  speed < 5)){
//            Log.d(TAG,"Send in 30 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//        }
//        if (menit % 60 == 0 && speed < 1){
//            Log.d(TAG,"Send in 60 minute ("+speed+") -> "+accountDB.imei +" ("+lat+","+lon+")");
//            sendToAPI(lon, lat, speed, course);
//            menit = 0;
//        }

        if (menit % 2 == 0 && speed >= 5){
            sendToAPI(lon, lat, speed, course,note);
        }
        if (menit % 5 == 0  && (speed >= 3 &&  speed < 5)){
            sendToAPI(lon, lat, speed, course,note);
        }
        if (menit % 15 == 0  && (speed < 3)){
            sendToAPI(lon, lat, speed, course,note);
        }
        if (menit % 60 == 0  ){
            menit = 0;

            if (!isNetworkConnected()){
                saveToBlackBox(lon,lat, speed, course);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return  START_STICKY;
    }

    private void sendToAPI(double lon, double lat, double speed, double course, String note){
        if (!isNetworkConnected()){
            Log.e(TAG,"Network Failed");
            writeLog("Network Failed");
            return;
        }

        PostManager post = new PostManager(getApplicationContext(), ApiConfig.POST_SAVE_POSITION);
        post.addParam("imei", accountDB.imei);
        post.addParam("longitude", lon);
        post.addParam("latitude", lat);
        post.addParam("speed", speed);
        post.addParam("course", course);
        post.addParam("status", "Online");
        post.addParam("note", note );
        post.addParam("time", Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss"));
        post.showloading(false);
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            writeLog("Sending Location "+ success);
            if (success){
                Log.d(TAG,"sendLocationData Success");
            }
            else {
                Log.d(TAG,"sendLocationData Failed "+ message);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void saveToBlackBox(double lon, double lat, double speed, double course){
        BlackBoxDB db = new BlackBoxDB();
        db.longitude = lon;
        db.latitude = lat;
        db.speed = speed;
        db.course = course;
        db.isUpload = false;
        db.time = Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss");
        db.insert(getApplicationContext());
    }

    private void writeLog(String tag){
        tag = Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss") +"  |  "+ tag;
//        FileProcessing.WriteFileToLog(getApplicationContext(),"dbug","AuthService.txt", tag);
    }

    private class RunnableImpl implements Runnable {

        public void run() {
            while (start){
                try {
                    menit ++;
                    mHandler.sendEmptyMessage(1);
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
