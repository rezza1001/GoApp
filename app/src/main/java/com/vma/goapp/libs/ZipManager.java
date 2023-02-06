package com.vma.goapp.libs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.vma.goapp.ui.component.Loading;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipManager {
    public static final String TAG = "ZipManager";

    private Context mContext;
    public ZipManager(Context context){
        mContext = context;
    }
    public void extractPluginMaps(){
        String pathToSave = FileProcessing.getMainPath(mContext).getAbsolutePath()+"/"+FileProcessing.ROOT;

        String pluginPath = pathToSave+"/map.zip";
        if (!new File(pathToSave+"/plugin").exists()){
            unzip(pluginPath, pathToSave);
        }
        else {
            new Handler().postDelayed(() -> {
                if (onFinishListener != null){
                    onFinishListener.onFinish(pathToSave);
                }
            },500);
        }
    }


    public void unzip(String zipFile, String location) {
        UnzipTask task = new UnzipTask();
        task.execute(zipFile,location);
    }

    class UnzipTask extends AsyncTask<String, String, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Loading.showLoading(mContext,"Extract Maps Plugin");
        }

        @Override
        protected String doInBackground(String... strings) {
            String sZipFile = strings[0];
            String toLocation = strings[1];
            ZipInputStream zis = null;
            try {
                zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sZipFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[8192];
                while (true) {
                    assert zis != null;
                    if ((ze = zis.getNextEntry()) == null) break;
                    File file = new File(toLocation, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    assert dir != null;
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    try (FileOutputStream fout = new FileOutputStream(file)) {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert zis != null;
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,"finally");
            }
            return toLocation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute");
            Loading.cancelLoading();
            if (onFinishListener != null){
                onFinishListener.onFinish(s);
            }
        }
    }

    public OnFinishListener onFinishListener;
    public void setOnFinishListener(OnFinishListener onFinishListener){
        this.onFinishListener = onFinishListener;
    }
    public interface OnFinishListener{
        void onFinish(String path);
    }
}