package com.vma.goapp.libs.downloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends AsyncTask<String, Bundle, String>{
    public static final String DESCRIPTION = "DOWNLOAD_DESCRIPTION";
    public static final String URL = "DOWNLOAD_URL";
    public static final String TITLE = "DOWNLOAD_TITLE";
    public static final String FILE_NAME = "FILE_NAME";

    // 10-10 19:14:32.618: D/DownloadService(1926): test cache：41234 32kb
    // 10-10 19:16:10.892: D/DownloadService(2069): test cache：41170 1kb
    // 10-10 19:18:21.352: D/DownloadService(2253): test cache：39899 10kb
    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";
    @SuppressLint("StaticFieldLeak")
    private final Activity mActivity;
    DownloadDialog downloadDialog;
    String fileName = "";

    public DownloadService(Activity activity) {
        this.mActivity = activity;
    }

    public void startDownload(Intent intent) {
        downloadDialog = new DownloadDialog(mActivity);
        String title = intent.getStringExtra(TITLE);
        String description = intent.getStringExtra(DESCRIPTION);
        String urlStr = intent.getStringExtra(URL);

        fileName = intent.getStringExtra(FILE_NAME);
        downloadDialog.show(title, description);
        execute(urlStr);

    }


    @Override
    protected String doInBackground(String... strings) {
        String urlStr = strings[0];
        Log.d("DownloadService","downloading "+urlStr);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(120 * 1000);
            urlConnection.setReadTimeout(120 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = FileProcessing.getDownloadDir(mActivity);
            String fileDownloadName = fileName;
            File apkFile = new File(dir, fileDownloadName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                if (progress != oldProgress) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("progress",progress);
                    bundle.putLong("current",bytesum);
                    bundle.putLong("total",bytetotal);
                    publishProgress(bundle);
                    Log.d("DownloadService","downloading progress "+progress);
                }
                oldProgress = progress;
            }

            Log.d("DownloadService","Finish download");
        } catch (Exception e) {
            Log.e(TAG, "download file error:" + e.getMessage());
            e.printStackTrace();
            return "Error";
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return "Success";
    }


    @Override
    protected void onProgressUpdate(Bundle... values) {
        super.onProgressUpdate(values);
        Bundle bundle = values[0];
        int progress = bundle.getInt("progress");
        long current = bundle.getLong("current");
        long total = bundle.getLong("total");
        downloadDialog.updateProgress(progress,current, total);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        downloadDialog.dismiss();
        if (s.equals("Error")){
            Utility.showToastError(mActivity,"Download Error");
        }
    }
}
