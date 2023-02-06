package com.vma.goapp.ui.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.vma.goapp.R;
import com.vma.goapp.ui.maps.SaveTrackActivity;
import com.vma.goapp.ui.maps.ConfirmSaveTrackDialog;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.master.MyView;
import com.vma.goapp.service.TrackRecordService;

public class RecordButtonView extends MyView {

    RelativeLayout rvly_record;
    ImageView imvw_record;
    ConfirmSaveTrackDialog confirmSaveTrackDialog;

    public RecordButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_recordbutton;
    }

    @Override
    protected void initLayout() {
        rvly_record = findViewById(R.id.rvly_record);
        imvw_record = findViewById(R.id.imvw_record);

    }

    public void create(){
        if (TrackRecordService.isRecording){
            imvw_record.setImageResource(R.drawable.navi_stop_record);
            new Handler().postDelayed(() -> {
                if (onRecordListener != null){
                    onRecordListener.onStart();
                }
            },500);
        }
        else {
            imvw_record.setImageResource(R.drawable.navi_start_record);
        }
    }

    @Override
    protected void initListener() {
        rvly_record.setOnClickListener(view -> record());
    }

    private void record(){
        if (!TrackRecordService.isRecording){
            imvw_record.setImageResource(R.drawable.navi_stop_record);
            startService();
        }
        else {
            showConfirm();
        }
    }

    private void showConfirm(){

        confirmSaveTrackDialog = new ConfirmSaveTrackDialog(mActivity);
        confirmSaveTrackDialog.show();
        confirmSaveTrackDialog.setOnActionListener(new ConfirmSaveTrackDialog.OnActionListener() {
            @Override
            public void OnSaveAction() {
                imvw_record.setImageResource(R.drawable.navi_start_record);
                stopService();
                Intent intent = new Intent(mActivity, SaveTrackActivity.class);
                mActivity.startActivity(intent);
            }

            @Override
            public void OnNoAction() {
                stopAndClearData();
            }

            @Override
            public void OnCancelAction() {

            }
        });
    }

    private void stopAndClearData(){
        imvw_record.setImageResource(R.drawable.navi_start_record);
        stopService();

        String path = FileProcessing.getRootPath(mActivity)+ TrackRecordService.folderName;
        FileProcessing.DeleteFile(path,TrackRecordService.fileName);

        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (onRecordListener != null){
            onRecordListener.onFinish();
        }
    }

    private void startService(){
        mActivity.registerReceiver(receiver, new IntentFilter(VmaConstants.TRACK_RECORD_SAVE));

        Intent service = new Intent(mActivity.getApplicationContext(), TrackRecordService.class);
        mActivity.getApplicationContext().startService(service);
        if (onRecordListener != null){
            onRecordListener.onStart();
        }
    }

    private void stopService(){
        try {
            Context context = mActivity.getApplicationContext();
            Intent service = new Intent( context, TrackRecordService.class);
            context.stopService(service);
            if (onRecordListener != null){
                onRecordListener.onStop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private OnRecordListener onRecordListener;
    public void setOnRecordListener(OnRecordListener onRecordListener){
        this.onRecordListener = onRecordListener;
    }
    public interface OnRecordListener{
        void onStart();
        void onStop();
        void onFinish();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(VmaConstants.TRACK_RECORD_SAVE)){
                if (intent.getIntExtra("status",-1) == 0){
                    confirmSaveTrackDialog.dismiss();
                }
                new Handler().postDelayed(() -> stopAndClearData(),500);
            }
        }
    };



}
