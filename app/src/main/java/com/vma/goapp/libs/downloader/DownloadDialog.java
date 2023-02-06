package com.vma.goapp.libs.downloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

public class DownloadDialog extends MyDialog {

    private TextView txvw_downloading,txvw_description,txvw_percent,txvw_count,txvw_status;
    private ProgressBar progrss_download;

    public DownloadDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int setLayout() {
        return R.layout.libs_downloader_dilaog;
    }

    @Override
    protected void initLayout(View view) {
        txvw_downloading = view.findViewById(R.id.txvw_downloading);
        txvw_description = view.findViewById(R.id.txvw_description);
        txvw_percent = view.findViewById(R.id.txvw_percent);
        txvw_count = view.findViewById(R.id.txvw_count);
        progrss_download = view.findViewById(R.id.progrss_download);
        txvw_status = view.findViewById(R.id.txvw_status);
    }


    public void show(String description) {
        show("",description);
    }

    @SuppressLint("SetTextI18n")
    public void show(String title, String description) {
        super.show();
        if (!title.isEmpty()){
            txvw_downloading.setText("Downloading...");
        }
        txvw_description.setText(description);
        progrss_download.setProgress(0);
        txvw_count.setText("0/0");
        txvw_percent.setText("0%");
        txvw_status.setText("0 B");
    }

    @SuppressLint("SetTextI18n")
    public void updateProgress(float percent, long downloaded, long total){
        int prc = Math.round(percent);
        txvw_percent.setText(prc+"%");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progrss_download.setProgress(prc, true);
        }

        long downloadKb = convertKB(downloaded);
        String sDownload=  downloadKb+" KB";
        txvw_status.setVisibility(View.INVISIBLE);
        if (downloadKb > 1000){
            sDownload = convertMB(downloadKb) +" MB";
            txvw_status.setVisibility(View.VISIBLE);
            txvw_status.setText(downloadKb+" KB");
        }

        long totalKb = convertKB(total);
        String sTotal =  totalKb+" KB";
        if (totalKb > 1000){
            sTotal = convertMB(totalKb) +" MB";
        }

        String info = sDownload+" / "+sTotal;
        txvw_count.setText(info);
    }

    private long convertMB(long fileSizeInKB){
        return fileSizeInKB / 1024;
    }
    private long convertKB(long fileSizeInB){
        return fileSizeInB / 1024;
    }
}
