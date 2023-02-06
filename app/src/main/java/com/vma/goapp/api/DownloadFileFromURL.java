package com.vma.goapp.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.downloader.DownloadDialog;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * Created by Mochamad Rezza Gumilang on 25/Aug/2021.
 * Class Info :
 */

public class DownloadFileFromURL extends AsyncTask<String, Bundle, Integer> {
    public static final String DESCRIPTION = "DOWNLOAD_DESCRIPTION";
    public static final String URL = "DOWNLOAD_URL";
    public static final String TITLE = "DOWNLOAD_TITLE";
    public static final String FILE_NAME = "FILE_NAME";

    private final DownloadDialog loadingDialog;
    private String fileName = "";
    private String title = "";
    private String description = "";
    private String urlStr = "";

    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public DownloadFileFromURL(Context context, Intent intent){
        this.title = intent.getStringExtra(TITLE);
        this.description = intent.getStringExtra(DESCRIPTION);
        this.urlStr = intent.getStringExtra(URL);
        this.fileName = intent.getStringExtra(FILE_NAME);
        this.context = context;
        loadingDialog = new DownloadDialog(context);

    }

    public void startDownload(){
        loadingDialog.show(title,description);
        execute(urlStr);
    }
    /**
     * Before starting background thread Show Progress Bar Dialog
     * */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected Integer doInBackground(String... f_url) {
        int count;
        String locationFile = FileProcessing.getDownloadDir(context) +"/"+fileName;
        try {
            URL url = new URL(f_url[0]);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Keep-Alive", "timeout=5, max=100");
            connection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream

            OutputStream output = new FileOutputStream(locationFile);

            byte[] data = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                int progress = (int) (total * 100L / lenghtOfFile);

                Bundle bundle = new Bundle();
                bundle.putInt("progress",progress);
                bundle.putLong("current",total);
                bundle.putLong("total",lenghtOfFile);
                publishProgress(bundle);
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            return 0;
        } catch (Exception e) {
            Log.e("Download", "Error "+Objects.requireNonNull(e.getMessage()));
            return -1;
        }

    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(Bundle... progress) {
        Bundle bundle = progress[0];
        int percent = bundle.getInt("progress");
        long current = bundle.getLong("current");
        long total = bundle.getLong("total");
        loadingDialog.updateProgress(percent, current, total);
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(Integer status) {
        loadingDialog.dismiss();
        if (status == 0){
            if (onFinishDownloadListener != null){
                onFinishDownloadListener.onSuccess();
            }
        }
        else {
            if (onFinishDownloadListener != null){
                onFinishDownloadListener.onFailed();
            }
        }
    }


    private OnFinishDownloadListener onFinishDownloadListener;

    public void setOnFinishDownloadListener(OnFinishDownloadListener onFinishDownloadListener){
        this.onFinishDownloadListener = onFinishDownloadListener;
    }
    public interface OnFinishDownloadListener{
        void onSuccess();
        void onFailed();
    }
}
