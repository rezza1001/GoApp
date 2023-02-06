package com.vma.goapp.ui.floating;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.vma.goapp.R;
import com.vma.goapp.ui.component.ConfirmDialog;

public class Permission {

    public static void requestOverlayDisplay(Activity mActivity){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(mActivity.getResources().getString(R.string.permission),mActivity.getResources().getString(R.string.floating_access_request));
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
                mActivity.startActivityForResult(intent, Activity.RESULT_OK);
            }
        });
    }

    public static boolean checkOverlayDisplayPermission(Activity mActivity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(mActivity);
        } else {
            return true;
        }
    }
}
