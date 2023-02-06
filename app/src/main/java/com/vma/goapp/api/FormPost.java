package com.vma.goapp.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vma.goapp.ui.component.Loading;
import com.vma.goapp.libs.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class FormPost extends AsyncTask<String, Integer, String> {
    private static String TAG = "FormPost";

    private JSONObject mData         = new JSONObject();
    private JSONObject mDataImage    = new JSONObject();
    private JSONObject existImage    = new JSONObject();

    boolean isSingleFile = false;
    boolean showLoading = true;
    private String mSuburl;
    private Date datestart;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public FormPost(Context context, String subUrl){
        mSuburl = subUrl;
        this.context = context;
    }


    public void addParam(String key, JSONArray objectAPI){
        try {
            mData.put(key, objectAPI);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParam(String key, String value){
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addParam(String key, int value){
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(JSONObject data){
        mData = data;
    }
    public void setImages(JSONObject data){
        mDataImage = data;
        isSingleFile = true;
    }

    public JSONObject getData(){
        return mData;
    }

    public JSONObject getImages(){
        return mDataImage;
    }

    public void addImage(String key, ArrayList<String> mPaths){
        try {
            JSONArray ja = new JSONArray();
            for (String s: mPaths){
                ja.put(s);
            }
            mDataImage.put(key, ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParams(String key, ArrayList<String>  value){
        try {
            int index = 0;
            for (String s: value){
                mData.put(key+"#"+index, s);
                index ++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addImage(String key, String mPaths){
        try {
            isSingleFile = true;
            if (mPaths.isEmpty()){
                return;
            }
            mDataImage.put(key, mPaths);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showLoading(boolean show){
        showLoading = show;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Loading.showLoading(context,"Please wait");
    }
    @Override
    protected String doInBackground(String... strings) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);

        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(ApiConfig.PATH_IMAGE + mSuburl);

//
//        post.addHeader("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
        post.addHeader("token", "token");
        post.addHeader("userid", "0");
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        Log.d(TAG,"PATH : "+ mSuburl);
        for (int i=0; i<post.getAllHeaders().length; i++){
            Log.d(TAG,"Add Header : "+ post.getAllHeaders()[i]);
        }
        Log.d(TAG,"PARAM JSON : "+mData.toString());
        Log.d(TAG,"PARAM IMAGE : "+mDataImage.toString());
        Iterator<String> iter = mData.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                String value = mData.getString(key);
                if (key.contains("#")){
                    key = key.split("#")[0];
                    key = key+"[]";
                }
                entityBuilder.addTextBody(key, value);
            } catch (JSONException e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
        HttpResponse response = null;

        int index = 0;
        Iterator<String> iterImage = mDataImage.keys();
        if (!isSingleFile){
            while (iterImage.hasNext()) {
                String key = iterImage.next();
                try {
                    JSONArray value = mDataImage.getJSONArray(key);
                    for (int x=0; x<value.length(); x++) {
                        File bin = new File(value.getString(x));
                        existImage.put(key,bin.exists());
                        FileBody bin1 = new FileBody(bin);
                        entityBuilder.addPart(key+"[]",bin1 );
                    }
                } catch (JSONException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }

            }
        }
        else {
            while (iterImage.hasNext()) {
                String key = iterImage.next();
                try {
                    if (mDataImage.get(key) instanceof String){
                        File bin = new File(mDataImage.getString(key));
                        FileBody bin1 = new FileBody(bin);
                        Log.d(TAG,key+" : "+ mDataImage.getString(key));
                        entityBuilder.addPart(key,bin1);
                    }
                    else {
                        JSONArray jaImage = mDataImage.getJSONArray(key);
                        for (int i=0; i<jaImage.length(); i++){
                            String path = jaImage.getString(i);
                            Log.d(TAG,key+"[] : "+path);
                            File bin = new File(path);
                            FileBody bin1 = new FileBody(bin);
                            entityBuilder.addPart(key+"[]",bin1);
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }

        }

        HttpEntity entity = entityBuilder.build();
        post.setEntity(entity);
        try {
            response = client.execute(post);
            index ++;
            publishProgress(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject objResponse = new JSONObject();
        if(response != null){
            int header_status = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            Log.d(TAG,"HEADER : "+header_status);
            try {
                objResponse.put("CODE", header_status);
                objResponse.put("DATA", EntityUtils.toString(httpEntity));
                return objResponse.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return ErrorCode.FAILED+"";
            }
        }

        return ErrorCode.FAILED+"";
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.w(TAG,s);
        if (showLoading){
            Loading.cancelLoading();
        }
        if (s.equals(""+ErrorCode.FAILED)){
            mReceiveListener.onReceive(null, ErrorCode.FAILED,false, "Undefined Error");
            return;
        }

        try {
            JSONObject obj = new JSONObject(s);
            String results = obj.getString("DATA");
            JSONObject objData;
            if (results.startsWith("[")){
                objData = new JSONObject();
                objData.put("data", new JSONArray(results));
            }
            else {
                objData   = new JSONObject(results);
            }

            int status = objData.getInt("code");
            boolean success = objData.getBoolean("success");
            String message  = objData.has("message") ? objData.getString("message"): results;
            Log.d(TAG,"Code : "+ status+" | Message : "+ message+" | Data : "+ objData.toString());
            if (mReceiveListener != null){
                mReceiveListener.onReceive(objData, status,success, message);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            mReceiveListener.onReceive(null, ErrorCode.UNDIFINED_ERROR,false, e.getMessage());
            sendError(e);
        }
    }


    private onReceiveListener mReceiveListener;
    public void setOnReceiveListener(onReceiveListener mReceiveListener){
        this.mReceiveListener = mReceiveListener;
    }
    public interface onReceiveListener{
        void onReceive(JSONObject obj, int code,boolean success, String message);
    }

    public void sendError(Exception e) {
//     
    }
}