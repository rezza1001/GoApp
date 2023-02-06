package com.vma.goapp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.ui.master.MyView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 10/02/2022
 */

public class DateWidget extends MyView {

    private TextView txvw_time,txvw_date;
    public DateWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected int setLayout() {
        return R.layout.wiget_datetime;
    }

    @Override
    protected void initLayout() {
        txvw_time = findViewById(R.id.txvw_time);
        txvw_date = findViewById(R.id.txvw_dateOther);
    }

    @Override
    protected void initListener() {

    }

    public void create(){
        mActivity.registerReceiver(receiver, new IntentFilter(VmaConstants.VMA_TIMER_TASK));
        refreshTime(60);
    }

    public void refreshTime(int second){
        if (second == 60){
            DateFormat formatTime = new SimpleDateFormat("HH:mm", new Locale("id"));
            DateFormat formatDate = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id"));
            txvw_time.setText(formatTime.format(new Date()));
            txvw_date.setText(formatDate.format(new Date()));
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.VMA_TIMER_TASK)){
                refreshTime(intent.getIntExtra(VmaConstants.SERVICE_DATA, 0));
            }
        }
    };
}
