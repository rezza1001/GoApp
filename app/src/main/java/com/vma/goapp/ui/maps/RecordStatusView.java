package com.vma.goapp.ui.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.ui.master.MyView;

public class RecordStatusView extends MyView {

    private TextView txvw_hour,txvw_minute,txvw_second;
    public RecordStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_recordstatus;
    }

    @Override
    protected void initLayout() {
        txvw_hour = findViewById(R.id.txvw_hour);
        txvw_minute = findViewById(R.id.txvw_minute);
        txvw_second = findViewById(R.id.txvw_second);
        setVisibility(GONE);
    }

    @Override
    protected void initListener() {

    }

    public void startRecording(){
        mActivity.registerReceiver(receiver, new IntentFilter(VmaConstants.TRACK_RECORD_TASK));
        setVisibility(VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));
    }
    public void stopRecording(){
        mActivity.unregisterReceiver(receiver);
        setVisibility(GONE);
    }

    private void showToUI(int hour, int minute, int second){
        String sHour = hour < 10 ? "0"+hour : hour+"";
        String sMinute = minute < 10 ? "0"+minute : minute+"";
        String sSecond = second < 10 ? "0"+second : second+"";
        txvw_hour.setText(sHour);
        txvw_minute.setText(sMinute);
        txvw_second.setText(sSecond);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.TRACK_RECORD_TASK)){
                int hour = intent.getIntExtra(VmaConstants.HOUR,0);
                int minute = intent.getIntExtra(VmaConstants.MINUTE,0);
                int second = intent.getIntExtra(VmaConstants.SECOND,0);
                mActivity.runOnUiThread(() -> showToUI(hour, minute, second));
            }
        }
    };
}
