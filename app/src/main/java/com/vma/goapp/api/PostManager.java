package com.vma.goapp.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.vma.goapp.ui.component.Loading;
import com.vma.goapp.libs.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostManager extends AsyncTask<String, String, String> {

    private static final String TAG = "PostManager";
    private static final String SPARATOR = "M0C1-14";
    private String mainUrl = ApiConfig.MAIN;
    private String apiUrl;
    private JSONObject mData        = new JSONObject();
    private final ArrayList<Bundle> headerParams = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private boolean showLoading     = true;
    private Date dateStart;

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public PostManager(Context mContext, String apiUrl) {
        this.apiUrl = apiUrl;
        context = mContext;
    }


    public void exGet(){
        execute("GET");
    }

    public void exPost(){
        execute("POST");
    }

    public void exDelete(){
        execute("DELETE");
    }

    public void showloading(boolean show){
        showLoading = show;
    }

    public void setData(JSONObject jo) {
        mData = jo;
    }


    public void addHeaderParam(String key, String value){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        bundle.putString("value", value);
        headerParams.add(bundle);
    }


    public void addParam(String key, int value){
        if (context == null){
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addParam(String key, String value){
        if (context == null){
            return;
        }
        try {
            mData.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void addParam(String key, double value){
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
    public void addParam(String key, boolean value){
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

    public void addParam(String key, JSONObject data){
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getParamData(){
        return mData;
    }

    public void addParam(String key, JSONArray data){
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        try {
            mData.put(key, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        dateStart = new Date();
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        Utility.logDbug(TAG,"onPreExecute with loading : "+ showLoading);
        if (showLoading){
            Loading.showLoading(context,"Loading");
        }

        super.onPreExecute();
    }

    protected String doInBackground(String... arg0) {

        StringBuilder sbResponse = new StringBuilder();
        try {

            String type = arg0[0];
            if (type.equals("GET")) {
                StringBuilder param = new StringBuilder();
                if (headerParams.size() > 0){
                    param = new StringBuilder("&");
                }
                for (Bundle bundle : headerParams) {
                    param.append(bundle.getString("key")).append("=").append(bundle.getString("value")).append("&");
                }
                apiUrl = apiUrl + param;
            }
            Log.d(TAG,"API : "+mainUrl +apiUrl);
            URL url = new URL(mainUrl +apiUrl); //Enter URL here
            String host = url.getHost();
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod(type); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            if (type.equals("POST") || type.equals("PUT")){
                httpURLConnection.setDoOutput(true);
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.setRequestProperty("Accept", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            try {
                httpURLConnection.connect();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Utility.logDbug(TAG,type+ " "+url+" "+apiUrl);
            if (type.equals("POST")|| type.equals("PUT")){
                Utility.logDbug(TAG,"HOST : "+host+", InetAddress : "+address+", IP : "+ip);
                Utility.logDbug(TAG,"data "+ " : "+ mData.toString());
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(mData.toString());
                wr.flush();
                wr.close();
            }


            int responseCode = httpURLConnection.getResponseCode();
            Utility.logDbug(TAG,"RESPONSE CODE : "+ responseCode);
            sbResponse.append(responseCode).append(SPARATOR);
            BufferedReader in;
            if (responseCode == ErrorCode.OK_200 || responseCode == ErrorCode.CODE_201){
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            }
            else if (responseCode == ErrorCode.BLOCK){
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            }
            else {
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                Utility.logDbug(TAG,httpURLConnection.getResponseMessage());
            }


            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            sbResponse.append(response.toString());
            return sbResponse.toString();

        } catch (Exception e) {
            e.printStackTrace();
            sbResponse.append("Request time out");
            if (showLoading){
                Loading.cancelLoading();
            }
            return sbResponse.toString();
        }

    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    protected void onPostExecute(String presults) {
        Utility.logDbug(TAG,"onPostExecute "+ presults.length());
        if (context == null){
            Utility.logDbug(TAG,"Request Canceled !!!");
            return;
        }
        Date date1 = new Date();
        long diff = date1.getTime() - dateStart.getTime();
        Utility.logDbug(TAG,"TOTAL TIME : "+ diff+" Seconds");

        if (showLoading){
            Loading.cancelLoading();
        }
        try {

            String results = presults.split(SPARATOR)[1];
            int code    =  Integer.parseInt(presults.split(SPARATOR)[0]);

            if (results != null){
                Utility.logDbug(TAG,"TEXT RESPONSE "+this.apiUrl +" | "+ results +" | HEADER CODE : "+ code);
                if (results.equals("Request time out")){
                    Toast.makeText(context,"Request time out", Toast.LENGTH_SHORT).show();
                    mReceiveListener.onReceive(null, ErrorCode.TIME_OUT,false, "Time Out");
                    return;
                }
                try {
                    JSONObject jo;
                    if (results.startsWith("[")){
                        jo = new JSONObject();
                        jo.put("data", new JSONArray(results));
                    }
                    else {
                        jo   = new JSONObject(results);
                    }

                    code = jo.has("code") ? jo.getInt("code"): ErrorCode.UNDIFINED_ERROR;
                    String message  = jo.has("message") ? jo.getString("message"): "";
                    boolean success  = jo.has("success") && jo.getBoolean("success");

                    mReceiveListener.onReceive(jo, code,success,message);
                } catch (JSONException e) {
                    sendError(e,presults);
                    mReceiveListener.onReceive(null, ErrorCode.CODE_UNPROCESSABLE_ENTITY,false, "Undefined");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            sendError(e,presults);
            if (mReceiveListener != null){
                mReceiveListener.onReceive(null, ErrorCode.UNDIFINED_ERROR,false,"Error Connection "+ e.getMessage());
            }
            Toast.makeText(context, "Error Connection "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void sendError(Exception e, String responseData){
        if (dateStart == null){
            dateStart = new Date();
        }
        Log.d(TAG,"SEND Error ---- > "+e.getMessage());

    }

    private onReceiveListener mReceiveListener;
    public void setOnReceiveListener(onReceiveListener mReceiveListener){
        this.mReceiveListener = mReceiveListener;
    }
    public interface onReceiveListener{
        void onReceive(JSONObject obj, int code, boolean success, String message);
    }

}
