package com.vma.goapp.libs;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class VmaNetwork {

    private final Activity mActivity;

    public VmaNetwork(Activity activity){
        mActivity = activity;
    }
    public boolean isNetworkConnected() {
        WifiManager wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED){
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public int getWifiLevel(){
        WifiManager wifiManager = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int numberOfLevels = 6;
        return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
    }


}
