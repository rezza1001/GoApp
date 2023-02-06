package com.vma.goapp.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.vma.goapp.R;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.main.MainActivity;
import com.vma.goapp.ui.master.MyActivity;
import com.vma.goapp.service.MainService;

import java.util.ArrayList;

public class SplashActivity extends MyActivity {

    ArrayList<String> LIST_PERMISSION = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void initData() {
        LIST_PERMISSION.add(Manifest.permission.ACCESS_FINE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        LIST_PERMISSION.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LIST_PERMISSION.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_ADMIN);
//        LIST_PERMISSION.add(Manifest.permission.BLUETOOTH);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_NETWORK_STATE);
        LIST_PERMISSION.add(Manifest.permission.ACCESS_WIFI_STATE);
//        LIST_PERMISSION.add(Manifest.permission.CHANGE_WIFI_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("TAGRZ","ACCESS_BACKGROUND_LOCATION check");
//            LIST_PERMISSION.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            LIST_PERMISSION.add(Manifest.permission.BLUETOOTH_CONNECT);
            LIST_PERMISSION.add(Manifest.permission.FOREGROUND_SERVICE);
        }

        String[] PERMISSIONS =  new String[LIST_PERMISSION.size()];
        int idx = 0;
        for (String a : LIST_PERMISSION){
            PERMISSIONS[idx] = a;
            idx++;
        }
        boolean hasPermission = Utility.checkPermission(mActivity,PERMISSIONS);
        if (hasPermission){
            startActivity(new Intent(mActivity, MainActivity.class));
            MainService.permission = true;
            mActivity.finish();
        }
        else {
            requestPermission();
        }

    }

    @Override
    protected void initListener() {

    }

    private void requestPermission(){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.permission),getResources().getString(R.string.backround_access_info));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                configPermission();
            }
            else {
                mActivity.finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(mActivity, MainActivity.class));
            MainService.permission = true;
            mActivity.finish();
        } else {
            Utility.showToastError(mActivity,"Permission denied to");
        }
    }

    private void configPermission(){
        String[] PERMISSIONS =  new String[LIST_PERMISSION.size()];
        int position = 0;
        for (String a : LIST_PERMISSION ){
            PERMISSIONS[position] = a;
            position ++;
        }

//        ActivityCompat.requestPermissions(mActivity,PERMISSIONS,PackageManager.PERMISSION_GRANTED);

        boolean permissionOk = Utility.hasPermission(mActivity,PERMISSIONS);
        if (permissionOk){
            startActivity(new Intent(mActivity, MainActivity.class));
        }
    }
}
