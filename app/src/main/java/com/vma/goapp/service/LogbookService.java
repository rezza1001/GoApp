package com.vma.goapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.database.table.AccountDB;
import com.vma.goapp.database.table.LogbookDB;
import com.vma.goapp.database.table.LogbookUploadDB;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */
public class LogbookService extends Service {

    private Timer timer;
    public static boolean isRunning = false;

    private AccountDB accountDB;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        accountDB = new AccountDB();
        accountDB.loadData(getApplicationContext());

        isRunning = true;
        Log.d("LogbookService","onCreate");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkLogbook();
            }
        }, 0, 2000);
    }


    @Override
    public void onDestroy() {
        Log.d("LogbookService","onDestroy");
        isRunning = false;
        timer.cancel();
        super.onDestroy();
    }



    private void checkLogbook(){
        LogbookUploadDB db = new LogbookUploadDB();
        ArrayList<LogbookUploadDB> uploadDBS = db.getAll(getApplicationContext());

        if (uploadDBS.size() == 0){
            stopSelf();
            return;
        }
        LogbookUploadDB uploadDB =   uploadDBS.get(uploadDBS.size() -1);
        LogbookDB logbookDB = new LogbookDB();
        logbookDB.get(getApplicationContext(), uploadDB.logboook);
        sendToAPI(logbookDB,uploadDB);
    }

    private void sendToAPI(LogbookDB logbookDB, LogbookUploadDB uploadDB){
        if (!isNetworkConnected() ){
            Log.e("LogbookService","Network Failed");
            return;
        }
        if (accountDB.imei.isEmpty()){
            Log.e("LogbookService","No Auth");
            return;
        }

        String sTime = logbookDB.date +" "+ logbookDB.time;
        Date activityDate = Utility.getDate(sTime,"dd/MM/YYYY HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activityDate);

        PostManager post = new PostManager(getApplicationContext(), ApiConfig.POST_SAVE_LOGBOOK);
        post.addParam("imei", accountDB.imei);
        post.addParam("masterkey", accountDB.mKey);
        post.addParam("longitude", String.valueOf(logbookDB.longitude));
        post.addParam("latitude", String.valueOf(logbookDB.latitude));
        post.addParam("waktulokal", Utility.getDateString(new Date(),"yyyy-MM-dd HH:mm:ss"));
        post.addParam("activityDate", Utility.getDateString(activityDate,"yyyy-MM-dd HH:mm:ss"));
        post.addParam("year", calendar.get(Calendar.YEAR));
        post.addParam("month", calendar.get(Calendar.MONTH)+1);
        post.addParam("dateOfMonth", calendar.get(Calendar.DAY_OF_MONTH));
        post.addParam("timezone", "WIB");
        post.addParam("fishCode", logbookDB.fishId);
        post.addParam("kg", logbookDB.unit.equals("K")  ? logbookDB.qty : 0);
        post.addParam("tail", logbookDB.unit.equals("E")  ? logbookDB.qty : 0);
        post.addParam("unit", logbookDB.unit);
        post.showloading(false);
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                uploadDB.delete(getApplicationContext(),uploadDB.id);
                Log.d("LogbookService","Logbook save Success");
            }
            else {
                Log.d("LogbookService","Logbook save "+ message);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }



}
