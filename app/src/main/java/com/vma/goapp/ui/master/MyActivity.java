package com.vma.goapp.ui.master;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vma.goapp.VmaApplication;
import com.vma.goapp.database.table.AccountDB;
import com.vma.goapp.dom.VmaConstants;

public abstract class MyActivity extends AppCompatActivity {

    protected String TAG = "MyActivity";
    protected Activity mActivity;
    private VmaApplication vmaApplication;

    protected AccountDB mAccountDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        mActivity = this;
        vmaApplication = (VmaApplication) getApplication();

        mAccountDB = new AccountDB();
        mAccountDB.loadData(mActivity);

        TAG = mActivity.getClass().getSimpleName();
        registerReceiver(receiver, new IntentFilter(VmaConstants.NOTIFY_ACCOUNT));

        initLayout();
        initData();
        initListener();
    }

    protected void reloadAccount(){
        mAccountDB = new AccountDB();
        mAccountDB.loadData(mActivity);

        onReloadAccount();
    }

    protected void onReloadAccount(){

    }

    protected abstract int setLayout();
    protected abstract void initLayout();
    protected abstract void initData();
    protected abstract void initListener();

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.NOTIFY_ACCOUNT)){
                reloadAccount();
            }
        }
    };

}
