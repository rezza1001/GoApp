package com.vma.goapp.libs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.vma.goapp.R;


public class MyDevice {

    private TelephonyManager mTelephone;
    private Context mContext;

    public MyDevice(Context pContext) {
        mContext = pContext;
        if (mContext != null) {
            mTelephone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        }
    }

    @SuppressLint("HardwareIds")
    public String getImei() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            dialogAccessDenied();
            return "Access Dined";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return mTelephone.getImei();
            } catch (Exception e) {
                return "Undefined";
            }
        } else {
            try {
                return mTelephone.getDeviceId();
            } catch (Exception e) {
                return "Undefined";
            }
        }
    }

    @SuppressLint("HardwareIds")
    public String getPhoneNumber() {
        if (mContext == null) {
            return "Access Dined";
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "Access Dined";
        }
        try {
            return mTelephone.getLine1Number();
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressLint("HardwareIds")
    public String getACCID() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "Access Dined";
        }
        try {
            return mTelephone.getSimSerialNumber();
        } catch (Exception e) {
            return "Undefined";
        }
    }

    @SuppressLint("HardwareIds")
    public String getDeviceID() {
        if (mContext == null) {
            return "Access Dined";
        }
        return Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void dialogAccessDenied() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.app_name) + " cannot access your imei number, Please check your device or sim card ");
        alertDialogBuilder.setPositiveButton("yes",
                (arg0, arg1) -> ((Activity) mContext).finish());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }
    public int getVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionCode;
    }

    public int getMCC() {
        String networkOperator = mTelephone.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return Integer.parseInt(networkOperator.substring(0, 3));
        } else {
            return 0;
        }
    }

    public int getMNC() {
        String networkOperator = mTelephone.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return Integer.parseInt(networkOperator.substring(3));
        } else {
            return 0;
        }
    }

    public int getLAC() {
        int lac = -1;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return -99;
        }
        try {
            GsmCellLocation collect = (GsmCellLocation) mTelephone.getCellLocation();
            lac = collect.getLac();
        }catch (Exception e){e.printStackTrace();}
        return lac;
    }

    public int getCID() {
        int cid = -1;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return -99;
        }
        try {
            GsmCellLocation collect = (GsmCellLocation) mTelephone.getCellLocation();
            cid = collect.getCid();
        }catch (Exception e){e.printStackTrace();}
        return cid ;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public String getOs() {
        return Build.VERSION.RELEASE;
    }
}