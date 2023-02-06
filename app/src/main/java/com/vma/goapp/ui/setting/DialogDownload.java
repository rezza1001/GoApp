package com.vma.goapp.ui.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Dialog : 다운로드 진행 창 - 다운로드 진행 중 표시됩니다.
 */
public class DialogDownload extends MyDialog {
    private final String TAG = "DialogDownload";
    private  Context mContext = null;
    private  String mApkUrl;
    private  String mApkName;

    public boolean bSucess = false;
    private TextView txvw_description,txvw_percent,txvw_count,txvw_status;
    private ProgressBar progrss_download;
    private final Thread thProgress = null;

    public DialogDownload(@NonNull Context context) {
        super(context);
        mContext = context;
    }


    @Override
    protected int setLayout() {
        return R.layout.setting_downloader_dilaog;
    }

    @Override
    protected void initLayout(View view) {
        progrss_download = view.findViewById(R.id.progrss_download);
        txvw_status      =  view.findViewById(R.id.txvw_status);
        txvw_count       =  view.findViewById(R.id.txvw_count);
        txvw_percent     =  view.findViewById(R.id.txvw_percent);
        txvw_description =  view.findViewById(R.id.txvw_description);


        setCancelable(false);
    }


    public void show(String url ,String fileNmae) {
        super.show();
        mApkUrl = url;
        mApkName = fileNmae;
        downloadApkFile();
    }

    @SuppressLint("SetTextI18n")
    private void downloadApkFile() {
        txvw_description.setText("File "+mApkName);
        Thread thread = new Thread() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try {
                    String apkurl = mApkUrl;
                    URL url = new URL(apkurl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(false);
                    connection.setDoInput(true);
                    connection.setReadTimeout( 3 * 60 * 1000);

                    int contentLength = connection.getContentLength();

                    if (contentLength == -1)
                        contentLength = 1000000;

                    progrss_download.setMax(contentLength);

                    File fileDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String getDownload = fileDownload.getPath();
                    String apkfileName = getDownload + "/" + mApkName;
                    String tempfileName = getDownload + "/download.temp";

                    int lenghtOfFile = connection.getContentLength();

                    File tempfile = new File(tempfileName);
                    InputStream is = connection.getInputStream();
                    OutputStream os = new FileOutputStream(tempfile);
                    byte[] buff = new byte[4096];
                    int len;
                    int progress = 0;

                    while ((len = is.read(buff)) > 0) {
                        os.write(buff, 0, len);
                        progress += len;
                        int percent = (int) (progress * 100L / lenghtOfFile);

                        int finalProgress = progress;
                        ((Activity)mContext).runOnUiThread(() -> {
                            txvw_percent.setText(percent+" %");
                            progrss_download.setProgress(finalProgress);

                            long downloadKb = convertKB(finalProgress);
                            String sDownload=  downloadKb+" KB";
                            txvw_status.setVisibility(View.INVISIBLE);
                            if (downloadKb > 1000){
                                sDownload = convertMB(downloadKb) +" MB";
                                txvw_status.setVisibility(View.VISIBLE);
                                txvw_status.setText(downloadKb+" KB");
                            }

                            long totalKb = convertKB(lenghtOfFile);
                            String sTotal =  totalKb+" KB";
                            if (totalKb > 1000){
                                sTotal = convertMB(totalKb) +" MB";
                            }

                            String info = sDownload+" / "+sTotal;
                            txvw_count.setText(info);
                        });
                    }

                    os.close();
                    is.close();
                    connection.disconnect();

                    File apkfile = new File(apkfileName);
                    tempfile.renameTo(apkfile);

                    if (onDownloadListener != null){
                        onDownloadListener.onFinishDownload(true,apkfile.getAbsolutePath());
                    }
                } catch (MalformedURLException e) {
                    Log.d(TAG, "MalformedURLException");
                } catch (IOException e) {
                    Log.d(TAG, "IOExcepttion: Read Timeout");
                } finally {
                    dismiss();
                }

            }
        };
        thread.start();
    }


    private long convertMB(long fileSizeInKB){
        return fileSizeInKB / 1024;
    }
    private long convertKB(long fileSizeInB){
        return fileSizeInB / 1024;
    }

    private OnDownloadListener onDownloadListener;
    public void setOnDownloadListener(OnDownloadListener onDownloadListener){
        this.onDownloadListener = onDownloadListener;
    }
    public interface OnDownloadListener{
        void onFinishDownload(boolean isDownload, String file);
    }

}
