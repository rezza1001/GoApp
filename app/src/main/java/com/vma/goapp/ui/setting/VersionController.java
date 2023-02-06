package com.vma.goapp.ui.setting;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.vma.goapp.R;
import com.vma.goapp.database.table.VersionDB;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.MyDevice;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.component.InfoDialog;
import com.vma.goapp.ui.component.Loading;

import java.io.File;
import java.util.Date;

public class VersionController {
    private static final String TAG = "VersionController";

    private Activity mActivity;

    public VersionController(Activity activity){
        mActivity = activity;
    }

    public void checkVersion(){
        Loading.showLoading(mActivity,mActivity.getResources().getString(R.string.check_version_app));
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(VmaConstants.COLLECTION_VERSION).whereEqualTo("status",true)
                .addSnapshotListener((value, error) -> {
                    Loading.cancelLoading();
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (value == null){
                        Log.e(TAG,"Not found data");
                        return;
                    }
                    for (QueryDocumentSnapshot doc : value) {
                        String versionName = doc.getString("name");
                        String description = doc.getString("description");
                        String url = doc.getString("url");
                        Date updated = doc.getDate("updated");
                        long code = doc.getLong("code");

                        VersionDB db = new VersionDB();
                        db.name = versionName;
                        db.code = (int) code;
                        db.description = description;
                        db.url = url;
                        db.date = Utility.getDateString(updated,"dd-MM-yyyy HH:mm:ss");
                        db.insert(mActivity);
                    }

                    processCheck();
                });
    }

    private void processCheck(){
        VersionDB db = new VersionDB();
        db.getAll(mActivity);

        MyDevice myDevice = new MyDevice(mActivity);
        if (db.code != myDevice.getVersionCode()){
            newUpdate(db);
        }
        else {
            String message = mActivity.getResources().getString(R.string.version_app_uptodate);
            message = message.replace("#X1",db.name);
            InfoDialog dialog = new InfoDialog(mActivity);
            dialog.show(message);
        }
    }

    private void newUpdate(VersionDB db){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        String description =mActivity.getResources().getString(R.string.new_version_app).replace("#X1",db.name).replace("#X2", db.description);
        dialog.show(mActivity.getResources().getString(R.string.new_version),description);
        dialog.setTextAction("Download",mActivity.getResources().getString(R.string.cancel));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                mActivity.runOnUiThread(() -> download(db.url));
            }
        });
    }

    private void download(String link){
        DialogDownload download = new DialogDownload(mActivity);
        download.show(link,"smartfishing.apk");
        download.setOnDownloadListener((isDownload, file) -> {
            if (isDownload){
               mActivity.runOnUiThread(() ->   onFinishDialog(file));
            }
            Log.d(TAG,"Download finish "+isDownload+" "+file);
        });

    }

    private void onFinishDialog(String path){
        String desc = mActivity.getResources().getString(R.string.download_apk_desc).replace("#X1",path);

        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(mActivity.getResources().getString(R.string.download_finish),desc);
        dialog.setTextAction("Install", mActivity.getResources().getString(R.string.cancel));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                installApk(path);
            }
        });
    }

    public void installApk(String path){
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        File apkFile = new File(path);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext().getPackageName() + ".provider", apkFile);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            mActivity.getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in opening the file!");
        }
    }
}
