package com.vma.goapp.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.vma.goapp.R;
import com.vma.goapp.VmaApplication;
import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.FormPost;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.component.OptionChooserImageDialog;
import com.vma.goapp.ui.master.MyActivity;

import java.io.File;
import java.io.IOException;

public class EditPictureActivity extends MyActivity {

    private ImageView imvw_photo;
    private MaterialRippleLayout mrly_gallery;

    @Override
    protected int setLayout() {
        return R.layout.activity_edit_picture;
    }

    @Override
    protected void initLayout() {
        imvw_photo = findViewById(R.id.imvw_photo);
        mrly_gallery = findViewById(R.id.mrly_gallery);
    }

    @Override
    protected void initData() {

        if (!mAccountDB.image.isEmpty()){
            Glide.with(mActivity).load(ApiConfig.PATH_IMAGE +mAccountDB.image).signature(new ObjectKey(VmaApplication.PROFILE_VERSION)) .into(imvw_photo);
        }
        else {
            imvw_photo.setImageResource(0);
        }
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back).setOnClickListener(view -> onBackPressed());
        mrly_gallery.setOnClickListener(view -> showOptionImage());
    }


    private void showOptionImage(){
        OptionChooserImageDialog dialog = new OptionChooserImageDialog(mActivity);
        dialog.show();
        dialog.setOnSelectedListener(option -> {
            if (option == OptionChooserImageDialog.Option.CAMERA){
                openCamera();
            }
            else {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT+"/"+FileProcessing.TEMP);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent.createChooser(intent, "Choose File to Upload..");
        mActivity.startActivityForResult(intent,FileProcessing.REQUEST_OPEN_GALLERY);

    }
    private void openCamera() {
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (!Utility.hasPermission(this,permission)){
            return;
        }

        FileProcessing.createFolder(this,FileProcessing.ROOT+"/"+FileProcessing.TEMP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                return;
            }
        }

        String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath()+"/"+FileProcessing.ROOT+"/"+FileProcessing.TEMP;
        String file =mediaPath+"/shiptmp.jpg";
        File newfile = new File(file);
        try {
            if (newfile.exists()){
                boolean deleted =  newfile.delete();
            }
            boolean created = newfile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputFileUri = FileProcessing.getUriFormFile(this, newfile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mActivity.startActivityForResult(intent, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            String mediaPath = FileProcessing.getMainPath(this).getAbsolutePath() + "/" + FileProcessing.ROOT + "/" + FileProcessing.TEMP;
            String file = mediaPath + "/shiptmp.jpg";
            Glide.with(mActivity).load(new File(file))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imvw_photo);
            imvw_photo.setTag(file);
            checkFile();

        } else {
            if (data == null) {
                Toast.makeText(this, "Data is error", Toast.LENGTH_LONG).show();
                return;
            }
            if (resultCode != RESULT_OK) {
                return;
            }
            FileProcessing fileProcessing = new FileProcessing();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                String pathFile = "/"+FileProcessing.ROOT+"/"+FileProcessing.TEMP;
                fileProcessing.saveToTmpReal(mActivity,bitmap, pathFile,"shiptmp1.jpg");
                fileProcessing.setOnSavedListener((path, name) -> {
                    String newFile = FileProcessing.getMainPath(mActivity).getAbsolutePath() +path+name;
                    Glide.with(mActivity).load(new File(newFile))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imvw_photo);
                    imvw_photo.setTag(newFile);
                    checkFile();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkFile(){
        FormPost post = new FormPost(mActivity,ApiConfig.DELETE_FILE);
        post.addParam("collection","ref_kapal");
        post.addParam("reffId",""+mAccountDB.mKey);
        post.execute();
        post.setOnReceiveListener((obj, code, success, message) -> uploadPhoto());

    }

    private void uploadPhoto(){
        FormPost post = new FormPost(mActivity, ApiConfig.UPLOAD_FILE);
        post.addParam("collection","ref_kapal");
        post.addParam("reffId",""+mAccountDB.mKey);
        post.addImage("file",imvw_photo.getTag().toString());
        post.execute();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                clearGlide();
            }

        });
    }

    private void clearGlide(){
        VmaApplication.PROFILE_VERSION =  VmaApplication.PROFILE_VERSION+1;
        VmaPreferences.save(mActivity, VmaPreferences.IMAGE_PROFILE, (VmaApplication.PROFILE_VERSION));
        sendBroadcast(new Intent(VmaConstants.NOTIFY_ACCOUNT));
        new Handler().postDelayed(() -> {
            Utility.showToastSuccess(mActivity,"Success !");
            setResult(RESULT_OK);
            mActivity.finish();
        },200);
    }


}
