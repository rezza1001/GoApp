package com.vma.goapp.libs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.vma.goapp.ui.component.Loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressLint("StaticFieldLeak")
public class MyFileReader extends AsyncTask<String, String, String> {

    Context mContext;
    String startData = "", endData = "";
    boolean hideLoading = false;

    public MyFileReader(Context context){
        mContext = context;
    }
    public MyFileReader(Context context, boolean hideLoading){
        mContext = context;
        this.hideLoading = hideLoading;
        if (!this.hideLoading ){

        }
    }

    public void readFile(String path){
        execute(path);
    }


    private OnReadListener onReadListener;
    public void setOnReadListener(OnReadListener onReadListener){
        this.onReadListener = onReadListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!this.hideLoading ){
            Loading.showLoading(mContext,"Load track...");
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String path = strings[0];
        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (startData.isEmpty()){
                    startData = line;
                }
                publishProgress(line);
                text.append(line);
                text.append('\n');
                endData = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (onReadListener != null){
            onReadListener.onLiveRead(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!this.hideLoading ){
            Loading.cancelLoading();
        }
        if (onReadListener != null){
            onReadListener.onFinish(startData, s, endData);
        }
    }

    public interface OnReadListener{
        void onLiveRead(String data);
        void onFinish(String start, String data, String end);
    }
}
