package com.vma.goapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.ui.dpi.DpiActivity;
import com.vma.goapp.ui.location.LocationActivity;
import com.vma.goapp.ui.logbook.LogbookActivity;
import com.vma.goapp.ui.maps.MainMapActivity;
import com.vma.goapp.ui.setting.SettingActivity;
import com.vma.goapp.ui.sos.MainSosActivity;
import com.vma.goapp.ui.weather.WeatherActivity;
import com.vma.goapp.dom.MenuHolder;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mochamad Rezza Gumilang on 14/02/2022
 */

public class MenuView extends MyView {

    private MenuAdapter adapter;
    ArrayList<MenuHolder> allMenu = new ArrayList<>();

    private String cameraPackage  = "";
    private String galleryPackage = "";
    private String settingPackage = "";

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_view_menu;
    }

    @Override
    protected void initLayout() {
    }

    @Override
    protected void initListener() {
    }

    public void create(boolean isFullMenu){
        RecyclerView rcvw_menu = findViewById(R.id.rcvw_menu);
        rcvw_menu.setNestedScrollingEnabled(false);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity,3){
            @Override
            protected boolean isLayoutRTL() {
                if (!isFullMenu){
                    return true;
                }
                else {
                    return super.isLayoutRTL();
                }
            }
        };
        rcvw_menu.setLayoutManager(layoutManager);

        allMenu = new ArrayList<>();
        adapter = new MenuAdapter(mActivity, allMenu);
        rcvw_menu.setAdapter(adapter);

        adapter.setOnSelectedListener(this::directTo);

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = mActivity.getPackageManager().queryIntentActivities( mainIntent, 0);
        for (ResolveInfo menu : pkgAppsList){
            String name = menu.loadLabel(mActivity.getPackageManager()).toString();
            if (name.toLowerCase().contains("camera")){
                cameraPackage = menu.activityInfo.packageName;
            }
            if (name.toLowerCase().startsWith("gallery")){
                galleryPackage = menu.activityInfo.packageName;
            }
            if (name.toLowerCase().startsWith("setting")){
                settingPackage = menu.activityInfo.packageName;
            }
        }
    }

    public void addMenu(MenuHolder holder){
        allMenu.add(holder);
        adapter.notifyItemInserted(allMenu.size()-1);
    }

    private void directTo(MenuHolder holder){
        if (holder.menu == MenuHolder.MENU.DPI){
            if (listener!= null){
                listener.onDirect(DpiActivity.class);
            }
            startToActivity(new Intent(mActivity, DpiActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.LOGBOOK){
            if (listener!= null){
                listener.onDirect(LogbookActivity.class);
            }
            startToActivity(new Intent(mActivity, LogbookActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.LOCATION){
            if (listener!= null){
                listener.onDirect(LocationActivity.class);
            }
            startToActivity(new Intent(mActivity, LocationActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.WEATHER){
            if (listener!= null){
                listener.onDirect(WeatherActivity.class);
            }
            startToActivity(new Intent(mActivity, WeatherActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.FISH_MAP){
            if (listener!= null){
                listener.onDirect(MainMapActivity.class);
            }
            startToActivity(new Intent(mActivity, MainMapActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.SOS){
            if (listener!= null){
                listener.onDirect(MainSosActivity.class);
            }
            startToActivity(new Intent(mActivity, MainSosActivity.class));
        }
        else if (holder.menu == MenuHolder.MENU.CAMERA){
            if (listener!= null){
                listener.onDirect(LogbookActivity.class);
            }
            if (cameraPackage.isEmpty()){
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               mActivity.startActivityForResult(camera_intent, 1);
            }
            else {
                if (listener!= null){
                    listener.onDirect(LogbookActivity.class);
                }
                Intent launchIntent = mActivity.getPackageManager().getLaunchIntentForPackage(cameraPackage);
                mActivity.startActivity( launchIntent );
            }
        }
//        else if (holder.menu == MenuHolder.MENU.GALLERY){
//            if (galleryPackage.isEmpty()){
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setType("image/*");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mActivity.startActivity(intent);
//            }
//            else {
//                Intent launchIntent = mActivity.getPackageManager().getLaunchIntentForPackage(galleryPackage);
//                mActivity.startActivity( launchIntent );
//            }
//        }
        else if (holder.menu == MenuHolder.MENU.SETTING){
            if (listener!= null){
                listener.onDirect(SettingActivity.class);
            }
//            Intent launchIntent = mActivity.getPackageManager().getLaunchIntentForPackage(settingPackage);
            Intent intent = new Intent(mActivity, SettingActivity.class);
            mActivity.startActivity( intent );
        }
        else {
            Utility.showToastError(mActivity,"Under Development!");
        }
    }

    private void startToActivity(Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(0, 0);
    }


    private OnDirectListener listener;
    public void setOnDirectListener(OnDirectListener listener){
        this.listener = listener;
    }
    public interface OnDirectListener{
        void onDirect(Class<?> mClass);
    }
}
