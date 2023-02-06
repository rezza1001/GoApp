package com.vma.goapp.libs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileProcessing {

    public static final int REQUEST_OPEN_CAMERA  = 1001;
    public static final int REQUEST_OPEN_GALLERY = 1002;
    public static final int RESULT_OK = -1;
    public static final String ROOT = "VMAMOB";
    public static final String TEMP = "TEMP";
    public static final String DOWNLOAD = "download/";
    private OnSavedListener mOnSavedListener;

    public static void init(Context context){
        Log.d("FileProcessing","INIT");
        String[] permission = new String[2];
        permission[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permission[1] = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            permission[2] = Manifest.permission.MANAGE_EXTERNAL_STORAGE;
//
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }

        }

        boolean hasPermission = Utility.hasPermission((Activity) context, permission);
        Log.d("FileProcessing","Init permission "+hasPermission);
        if (hasPermission){
            FileProcessing.createFolder(context,FileProcessing.ROOT);
        }
        else {
            Utility.showToastError(context,"Can't access Permission for FileProcessing");
        }
    }

    public static String getRootPath(Context context){
        File mainFolder = getMainPath(context);
        return mainFolder.getAbsolutePath()+"/"+ROOT+"/";
    }

    public void processImage(Context mContext, Intent data, String path, String name){
        int request     = data.getIntExtra("REQ",0);
        int result      = data.getIntExtra("RES",0);
        Log.d("FileProcessing","Start processImage "+request+":"+result);
        if (request == REQUEST_OPEN_CAMERA && result == RESULT_OK){
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveToTmp(thumbnail, path, name);
        }
        else if (request == REQUEST_OPEN_GALLERY && result == RESULT_OK){
            Uri contentURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveToTmp(bitmap, path, name);
        }
    }

    public static File getMainPath(Context context){
        File mediaPath = Environment.getExternalStorageDirectory();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            mediaPath = context.getExternalFilesDir("");
        }
        return mediaPath;
    }

    public static Uri getUriFormFile(Context context,File file){
        return FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
    }


    public static String getPathDefault(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    public  void saveToTmp(Bitmap bitmap, String path, String name){
        if (!path.contains("/")){
            path = path +"/";
        }
        Log.d("FileProcessing",path+name );
        String mediaPath = Environment.getExternalStorageDirectory()+path+name;
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(Environment.getExternalStorageDirectory(), "/");
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create "+ROOT+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+ROOT+" Failed");
                }
            }

            file = new File(Environment.getExternalStorageDirectory(), path);
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+"/"+name);
        }

        assert file != null;
        Log.d("FileProcessing",file.getAbsolutePath()+"/"+name);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }

    public static File getDownloadDir(Context context){
        return  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }


    public  void saveToTmpReal(Context context, Bitmap bitmap, String path, String name){
        if (!name.contains("/")){
            name = "/"+ name ;
        }
        String mediaPath = getMainPath(context).getAbsolutePath() +path+name;
        Log.d("FileProcessing","saveToTmpReal : "+mediaPath);
        File media = new File(mediaPath);

        if (media.exists()){
            media.delete();
        }

        File file = null,f = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File root = new File(getMainPath(context).getAbsolutePath(), "/");
            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create "+ROOT+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+ROOT+" Failed");
                }
            }

            file = new File(getMainPath(context).getAbsolutePath(), path);
            if(!file.exists())
            {
                if(file.mkdirs()){
                    Log.d("FileProcessing","Create "+path+" Success");
                }
                else {
                    Log.d("FileProcessing","Create "+path+" Failed");
                }
            }
            f = new File(file.getAbsolutePath()+name);
        }

        assert file != null;
        Log.d("FileProcessing","Before created "+f);

        try {
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.flush();
            ostream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("NAME", name);
        bundle.putString("PATH", path);
        message.what =1 ;
        message.setData(bundle);
        handler.sendMessageDelayed(message,500);
    }

    public static boolean createFolder(Context context, String path){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File root = new File(getMainPath(context).getAbsolutePath(), "/"+ROOT);

            if (!root.exists()){
                if(root.mkdirs()){
                    Log.d("FileProcessing","Create root "+ROOT+" Success");
                    return create(context,path);
                }
                else {
                    Log.d("FileProcessing","Create root "+ROOT+" Failed");
                    return false;
                }
            }
            else {
                return create(context,path);
            }
        }
        else {
            Log.d("FileProcessing","MEDIA_MOUNTED NOT ACCESS");
            return false;
        }
    }

    private static boolean create(Context context, String path){
        File file;
        file = new File(getMainPath(context).getAbsolutePath(), path);
        if(!file.exists()) {
            if(file.mkdirs()){
                Log.d("FileProcessing","createFolder "+path+" Success");
                return true;
            }
            else {
                Log.d("FileProcessing","createFolder "+path+" Failed");
                return false;
            }
        }
        else {
            Log.d("FileProcessing","Folder exist "+path+"");
            return true;
        }
    }

    private static boolean create(String path){
        File file;
        file = new File(Environment.getExternalStorageDirectory(), path);
        if(!file.exists()) {
            if(file.mkdirs()){
                Log.d("FileProcessing","createFolder "+path+" Success");
                return true;
            }
            else {
                Log.d("FileProcessing","createFolder "+path+" Failed");
                return false;
            }
        }
        else {
            Log.d("FileProcessing","Folder exist "+path+"");
            return true;
        }
    }

    public static Bitmap openImage(String path, String name){
        File sd = Environment.getExternalStorageDirectory();
        File image = new File(sd.getAbsolutePath()+path+name);
        Log.d("FileProcessing",sd.getAbsolutePath()+path+name);
        if (!image.exists()){
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }
    public static Bitmap openImage(String url){
        File sd = Environment.getExternalStorageDirectory();
        File image = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }

    public int folderFilqQTY(String url){
        File sd = Environment.getExternalStorageDirectory();
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing",sd.getAbsolutePath()+url);
        if (!file.exists()){
            file.mkdirs();
        }

        return file.listFiles().length;
    }

    public File[] getAllfiles(String url){
        File sd = Environment.getExternalStorageDirectory();
        File file = new File(sd.getAbsolutePath()+url);
        Log.d("FileProcessing","getAllfiles : " +file.listFiles().length);
        return file.listFiles();
    }

    public static Bitmap openImageWthPath(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public static Bitmap openImageReal(String url){
        File image = new File(url);
        Log.d("FileProcessing",url);
        if (!image.exists()){
            Log.d("FileProcessing","IMAGE NOT FOUND");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    }
    public static boolean deleteImage(String path, String name){
        String mediaPath = Environment.getExternalStorageDirectory()+path+name;
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
           return media.delete();
        }
        else {
            return false;
        }
    }
    public static boolean deleteImage(String mediaPath){
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }
    public static void clearImage(Context context, String path){
        String mediaPath = getMainPath(context).getAbsolutePath()+path;
        File media = new File(mediaPath);
        String[] children = media.list();
        if (children != null){
            for (String child : children) {
               boolean delete = new File(media, child).delete();
               Log.d("FileProcessing","Deleted "+ media+" : "+ delete);
            }
        }
    }



    public void savefile(String fromPtah, String folder, String fileName) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String destinationFilename = Environment.getExternalStorageDirectory()+"/"+ROOT+""+folder+"/"+fileName;
        String root = Environment.getExternalStorageDirectory()+"/"+ROOT;
        String root1 = Environment.getExternalStorageDirectory()+"/"+ROOT+""+folder;

        File fileRoot = new File(root);
        File fileRoot1 = new File(root1);
        if (!fileRoot.exists()){
            fileRoot.mkdir();
        }
        if (!fileRoot1.exists()){
            fileRoot1.mkdir();
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(fromPtah));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            int status = bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOnSavedListener != null){
            mOnSavedListener.onSave(destinationFilename,fileName);
        }
    }

    public void savefileInternal(String fromPtah, String folder, String fileName) {
        String destinationFilename = folder+fileName;


        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(fromPtah));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            int status = bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOnSavedListener != null){
            mOnSavedListener.onSave(destinationFilename,fileName);
        }
    }

    public static void openFile(Activity activity, String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String fileName = file.getName();
        if (fileName.contains(".pdf")){
            pdfOpenintent.setDataAndType(uri, "application/pdf");
        }
        else if (fileName.contains(".jpg") || fileName.contains(".jpeg")){
            pdfOpenintent.setDataAndType(uri, "image/jpeg");
        }
        else if (fileName.contains(".txt")){
            pdfOpenintent.setDataAndType(uri, "text/plain");
        }
        else if (fileName.contains(".zip")){
            pdfOpenintent.setDataAndType(uri, "application/zip");
        }
        else if (fileName.contains(".rar")){
            pdfOpenintent.setDataAndType(uri, "application/x-rar-compressed");
        }
        else if (fileName.contains(".xls") || fileName.contains(".xlsx")){
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        else if (fileName.contains(".pptx") || fileName.contains(".ppt")){
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }
        else if (fileName.contains(".doc") || fileName.contains(".docx")){
            pdfOpenintent.setDataAndType(uri, "application/msword");
        }

        try {
            activity.startActivity(pdfOpenintent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler handler = new Handler(msg -> {
        if (msg.what == 1) {
            Log.d("FileProcessing", "RESULT " + msg.getData().getString("NAME") + "" + msg.getData().getString("PATH"));
            if (mOnSavedListener != null) {
                mOnSavedListener.onSave(msg.getData().getString("PATH"), msg.getData().getString("NAME"));
            }
        }
        return false;
    } );

    public static void WriteFileToLog(Context context, String folder, String fileName, String data) {

        File root = new File(getMainPath(context)+"/"+FileProcessing.ROOT+"/"+folder);
        if (!root.exists()){
            boolean created = root.mkdirs();
            Log.d("FileProcessing","Create Folder Log ("+root.getName()+") = "+created);
        }
        File logFile = new File(root.getAbsolutePath()+"/"+fileName);
        if (!logFile.exists()){
            try {
                boolean createFile =  logFile.createNewFile();
                Log.d("FileProcessing","Create new file Logs ("+logFile.getName()+") "+createFile);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FileProcessing","Create new file Logs ("+logFile.getName()+") failed ");
            }
        }
        else {
            Log.i("FileProcessing","Create new file Logs ("+logFile.getName()+") exists");
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(data);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean DeleteFile(String path, String name){
        String mediaPath = path+"/"+name;
        File media = new File(mediaPath);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }

    public static boolean DeleteFolder(String path){
        File media = new File(path);
        Log.d("FileProcessing","Deleted : "+ media.getPath()+" -> "+media.exists());
        if (media.exists()){
            return media.delete();
        }
        else {
            return false;
        }
    }
    public static void Copy(File src, File dst) throws IOException {
        Log.d("FileProcessing","Copy exists : "+src.getAbsolutePath()+" = "+src.exists()+" to "+dst.getAbsolutePath());
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public void setOnSavedListener(OnSavedListener onSavedListener){
        mOnSavedListener = onSavedListener;
    }
    public interface OnSavedListener{
        void onSave(String path, String name);
    }



}
