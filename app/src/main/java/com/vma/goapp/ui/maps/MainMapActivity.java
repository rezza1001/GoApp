package com.vma.goapp.ui.maps;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.vma.goapp.R;
import com.vma.goapp.database.table.FlagDB;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.floating.FloatingSystem;
import com.vma.goapp.ui.floating.Permission;
import com.vma.goapp.ui.main.BottomMenuView;
import com.vma.goapp.ui.master.MyActivity;

public class MainMapActivity extends MyActivity {

    FrameLayout frame_body,frame_body2;
    BottomMenuView menu_bottom;

    @Override
    protected int setLayout() {
        return R.layout.fishmap_activity_main;
    }

    @Override
    protected void initLayout() {
        frame_body = findViewById(R.id.frame_body);
        menu_bottom = findViewById(R.id.menu_bottom);
        frame_body2 = findViewById(R.id.frame_body2);
        frame_body2.setVisibility(View.GONE);

        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(VmaConstants.SHOW_TRACK);
        intentFilter.addAction(VmaConstants.NOTIFY_DIRECTION);
        intentFilter.addAction(VmaConstants.NOTIFY_SHOW_TRACK);
        registerReceiver(receiver,intentFilter);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        FlagDB flagDB = new FlagDB();
        int maxX = size.x;
        int maxY = size.y;
        flagDB.setFlag(mActivity, VmaConstants.SCREEN_X,maxX+"");
        flagDB.setFlag(mActivity, VmaConstants.SCREEN_X,maxY+"");

    }

    @Override
    protected void initData() {
        createMap();
        permission();
    }

    @Override
    protected void initListener() {
        menu_bottom.setOnSelectedListener(position -> {
            if (position == 1){
                frame_body2.setVisibility(View.INVISIBLE);
                frame_body.setVisibility(View.VISIBLE);
            }
            else {
                frame_body.setVisibility(View.INVISIBLE);
                frame_body2.setVisibility(View.VISIBLE);
                gotToPage(position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void gotToPage(int page){
        Fragment fragment = DpiFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (page == 3){
            fragment = LocationFragment.newInstance();
        }
        else if (page == 4){
            fragment = ListTrackFragment.newInstance();
        }
        fragmentTransaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out);
        fragmentTransaction.replace(frame_body2.getId(), fragment,"page_"+page);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    private void createMap(){
        Fragment fragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out);
        fragmentTransaction.replace(frame_body.getId(), fragment,"page_map");
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }


    private void permission() {
        String[] permission = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
        Utility.checkPermission(mActivity, permission);
        if (Permission.checkOverlayDisplayPermission(mActivity)) {
            Log.d(TAG, "Can showing overlay");
            return;
        }
        Permission.requestOverlayDisplay(mActivity);

    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(VmaConstants.NOTIFY_DIRECTION) ||
                    intent.getAction().equals(VmaConstants.NOTIFY_SHOW_TRACK) ){
                menu_bottom.setSelected(0);
            }

        }
    };





}
