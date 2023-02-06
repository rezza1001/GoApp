package com.vma.goapp.ui.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.ui.component.LoginDialog;
import com.vma.goapp.ui.master.MyActivity;
import com.vma.goapp.ui.profile.ProfileActivity;

import java.util.ArrayList;

public class SettingActivity extends MyActivity {
    enum Setting {PROFILE,BRIGHTNESS, DISPLAY, ABOUT_VMA, VERSION_INFO, TERMS}

    ArrayList<Bundle> listSetting = new ArrayList<>();
    SettingAdapter mAdapter;

    @Override
    protected int setLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_setting = findViewById(R.id.rcvw_data);
        rcvw_setting.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new SettingAdapter(mActivity, listSetting);
        rcvw_setting.setAdapter(mAdapter);


    }

    @Override
    protected void initData() {
        registerReceiver(receiver, new IntentFilter(VmaConstants.NOTIFY_RELOAD));
        createSetting();
    }

    @Override
    protected void initListener() {

    }


    private void createSetting(){
        listSetting.clear();
        addSetting(Setting.PROFILE, getResources().getString(R.string.account), R.drawable.ic_manage_accounts_24);
        addSetting(Setting.BRIGHTNESS, getResources().getString(R.string.brightness), R.drawable.ic_brightness);
        addSetting(Setting.DISPLAY, getResources().getString(R.string.display), R.drawable.ic_setting_theme);
        addSetting(Setting.ABOUT_VMA, getResources().getString(R.string.about_vma), R.drawable.ic_setting_aboutvma);
        addSetting(Setting.VERSION_INFO, getResources().getString(R.string.version_info), R.drawable.ic_setting_version);
        addSetting(Setting.TERMS, getResources().getString(R.string.term_to_use), R.drawable.ic_setting_term);

        mAdapter.setOnSelectedListener(menu -> {
            Setting setting = (Setting) menu.getSerializable("setting");
            switch (setting){
                case PROFILE:
                    checkAccount();
                    break;
                case BRIGHTNESS:
                    startActivity(new Intent(mActivity, BrightnessActivity.class));
                    break;
                case DISPLAY:
                    startActivity(new Intent(mActivity, DisplayActivity.class));
                    break;
                case ABOUT_VMA:
//                    startActivity(new Intent(mActivity, AboutVmaActivity.class));
                    break;
                case VERSION_INFO:
                        versionInfo();
//                    VersionAppController controller = new VersionAppController(mActivity);
//                    controller.checkVersion();
                    break;
                case TERMS:
                    break;
            }
        });
    }

    private void addSetting(Setting setting, String name, int resource){
        Bundle bundle = new Bundle();
        bundle.putSerializable("setting", setting);
        bundle.putString("name", name);
        bundle.putInt("icon", resource);
        listSetting.add(bundle);
        mAdapter.notifyItemInserted(listSetting.size());
        Log.d(TAG,"addSetting "+ bundle);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.NOTIFY_RELOAD)){
                mActivity.finish();
            }
        }
    };

    private void checkAccount(){
        if (mAccountDB.imei.isEmpty()){
            showLogin();
        }
        else {
            startActivity(new Intent(mActivity, ProfileActivity.class));
        }
    }

    private void showLogin(){
        LoginDialog dialog = new LoginDialog(mActivity);
        dialog.show();
        dialog.setOnActionListener(new LoginDialog.OnActionListener() {
            @Override
            public void onRegister() {

            }

            @Override
            public void onLogin() {
                reloadAccount();
                new Handler().postDelayed(() -> sendBroadcast(new Intent(VmaConstants.NOTIFY_RELOAD)),100);
            }
        });
    }

    private void versionInfo(){
        VersionDialog dialog = new VersionDialog(mActivity);
        dialog.show();
        dialog.setonCheckListener(this::checkVersionFromFB);
    }

    private void checkVersionFromFB(){
        VersionController controller = new VersionController(mActivity);
        controller.checkVersion();

    }
}
