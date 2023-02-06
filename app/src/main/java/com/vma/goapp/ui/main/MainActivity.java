package com.vma.goapp.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.vma.goapp.R;
import com.vma.goapp.VmaApplication;
import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.database.table.DirectionDB;
import com.vma.goapp.dom.MenuHolder;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.service.AuthService;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.component.LoginDialog;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.floating.FloatingSystem;
import com.vma.goapp.ui.master.MyActivity;
import com.vma.goapp.ui.profile.ProfileActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends MyActivity {

    DateWidget widget_time;
    FrameLayout frame_compass;
    MenuView menu_bottom_1, menu_bottom_0;
    VmaButton bbtn_login;
    RelativeLayout rvly_profile;
    TextView txvw_ship,txvw_owner;
    CircleImageView imvw_kapal;

    private boolean canPause = false;

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initLayout() {
        widget_time = findViewById(R.id.widget_time);
        widget_time.create();

        frame_compass = findViewById(R.id.frame_compass);
        addCompass();

        menu_bottom_1 = findViewById(R.id.menu_bottom);
        menu_bottom_0 = findViewById(R.id.menu_bottom2);
        rvly_profile = findViewById(R.id.rvly_profile);
        txvw_ship = findViewById(R.id.txvw_ship);
        txvw_owner = findViewById(R.id.txvw_owner);
        imvw_kapal = findViewById(R.id.imvw_kapal);

        createMenu();

        bbtn_login = findViewById(R.id.bbtn_login);
        bbtn_login.create("Login",0);

        keepAlive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VmaConstants.VMA_TIMER_TASK);
        intentFilter.addAction(VmaConstants.NOTIFY_RELOAD);
        registerReceiver(receiver,intentFilter);

        stopService(new Intent(mActivity, FloatingSystem.class));

        if (Utility.isRoot("su")){
           ConfirmDialog confirmDialog = new ConfirmDialog(mActivity);
           confirmDialog.show("Root Notice","Device terdeteksi sudah dilakukan rooting");
           confirmDialog.setOnActionListener(confirm -> mActivity.finish());
        }
    }

    @Override
    protected void initData() {
        checkAlwaysOnLocation();

        rvly_profile.clearAnimation();
        bbtn_login.clearAnimation();
        Glide.with(mActivity).load(R.drawable.default_kapal).into(imvw_kapal);

        rvly_profile.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
        rvly_profile.setVisibility(View.INVISIBLE);
        bbtn_login.setVisibility(View.GONE);
        txvw_ship.setText(mAccountDB.shipName);
        txvw_owner.setText(mAccountDB.imei);
        if (!mAccountDB.image.isEmpty()){
            Glide.with(mActivity).load(ApiConfig.PATH_IMAGE +mAccountDB.image).signature(new ObjectKey(VmaApplication.PROFILE_VERSION)) .into(imvw_kapal);
        }

        startService(new Intent(mActivity, AuthService.class));

    }

    @Override
    protected void initListener() {
        rvly_profile.setOnClickListener(view -> startActivity(new Intent(mActivity, ProfileActivity.class)));
        findViewById(R.id.rvly_logout).setOnClickListener(view -> logout());
    }

    protected void addCompass(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = CompassFragment.newInstance();
        fragmentTransaction.replace(frame_compass.getId(), fragment,"compass");
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    private void createMenu(){
        menu_bottom_0.create(true);
        menu_bottom_0.addMenu(new MenuHolder(MenuHolder.MENU.FISH_MAP,"Map",R.drawable.menu_map, createDrawable(R.drawable.menu_fishmap)));
        menu_bottom_0.addMenu(new MenuHolder(MenuHolder.MENU.SOS,"SOS",R.drawable.ic_sos, createDrawable(R.drawable.menu_sos)));
        menu_bottom_0.addMenu(new MenuHolder(MenuHolder.MENU.SETTING,getResources().getString(R.string.setting),R.drawable.ic_settings, createDrawable(R.drawable.menu_setting)));
//        menu_bottom_0.addMenu(new MenuHolder(MenuHolder.MENU.WEATHER,getResources().getString(R.string.weather),R.drawable.ic_weather, createDrawable(R.drawable.menu_weather)));
//
//        menu_bottom_1.create(true);
//        menu_bottom_1.addMenu(new MenuHolder(MenuHolder.MENU.DPI,"DPI",R.drawable.ic_dpi, createDrawable(R.drawable.menu_message)));
//        menu_bottom_1.addMenu(new MenuHolder(MenuHolder.MENU.LOCATION,"Lokasi",R.drawable.ic_pin_save, createDrawable(R.drawable.menu_sos)));

        menu_bottom_0.setOnDirectListener(mClass -> canPause = false);
        menu_bottom_1.setOnDirectListener(mClass -> canPause = false);
    }

    private Drawable createDrawable(int res){
        return ResourcesCompat.getDrawable(getResources(),res,null);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            reloadAccount();
            initData();
        }
    }

    private void logout(){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.are_you_sure_logout));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                mAccountDB.clearData(mActivity);
                reloadAccount();
                initData();
            }
        });
    }

    @SuppressLint("BatteryLife")
    private void keepAlive(){
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            canPause = false;
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.VMA_TIMER_TASK))     {
                Log.d(TAG,"BroadcastReceiver VMA_TIMER_TASK ");
                if (intent.getIntExtra(VmaConstants.SERVICE_DATA,0) == 10){
                    checkGPS();
                }
            }
            else if (intent.getAction().equals(VmaConstants.NOTIFY_RELOAD)){
                reloadAccount();
                initData();
            }
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        minimizeMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
       if (canPause){
           minimizeMode();
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        canPause = true;
    }

    @Override
    protected void onReloadAccount() {
        if (!mAccountDB.image.isEmpty()){
            Glide.with(mActivity).load(ApiConfig.PATH_IMAGE +mAccountDB.image).signature(new ObjectKey(VmaApplication.PROFILE_VERSION)) .into(imvw_kapal);
        }
    }

    private void minimizeMode(){
        DirectionDB db = new DirectionDB();
        if (db.getAll(mActivity).size() >= 1){
            if (!isMyServiceRunning()){
                startService(new Intent(mActivity, FloatingSystem.class));
            }
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FloatingSystem.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void checkGPS(){
//        GpsUtils gpsUtils = new GpsUtils(mActivity);
//        gpsUtils.turnGPSOn(isGPSEnable -> {
//
//        });
    }


    private void checkAlwaysOnLocation(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            String[]   permission = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            boolean hasPermission = Utility.checkPermission(mActivity,permission);
            if (!hasPermission ){
                ConfirmDialog dialog = new ConfirmDialog(mActivity);
                dialog.show(getResources().getString(R.string.permission),getResources().getString(R.string.backround_access_info));
                dialog.setOnActionListener(confirm -> {
                    if (confirm){
                        mActivity.requestPermissions(permission, 12);
                    }
                });

            }

        }

    }


}