package com.vma.goapp.libs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.vma.goapp.ui.component.option.OptionDialog;

public class ShareLocation {

    Activity mActivity;

    double latitude = 0;
    double longitude = 0;


    public ShareLocation(Activity activity){
        mActivity = activity;
    }

    public void share(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
        share();
    }

    private void share(){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addChooser("2","Link Google Map");
        dialog.addChooser("3","Text Lokasi");
        dialog.addChooser("1","Buka Aplikasi Map");
        dialog.setOnSelectListener((bundle, value) -> {

            String key = bundle.getString("key");
            if (key.equals("1")){
                String uri = "geo:" + latitude + ","  +longitude + "?q=" + latitude    + "," + longitude;
                mActivity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
            }
            else  if (key.equals("2")){
                String uri = "http://maps.google.com/maps?saddr=" +latitude+","+longitude;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Smartfishing";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                mActivity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            else {
                String uri = latitude+","+longitude;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Smartfishing";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                mActivity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

    }
}
