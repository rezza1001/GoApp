package com.vma.goapp.ui.setting;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vma.goapp.R;
import com.vma.goapp.libs.MyDevice;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyDialog;

public class VersionDialog extends MyDialog {
    private static final String TAG = "VersionDialog";

    private CardView card_body;
    private VmaButton bbtn_check;
    private TextView txvw_version,txvw_lastUpdate;
    private OnCheckListener onCheckListener;

    public VersionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_setting_version;
    }

    @Override
    protected void initLayout(View view) {
        card_body = view.findViewById(R.id.card_body);
        card_body.setVisibility(View.INVISIBLE);

        bbtn_check = view.findViewById(R.id.bbtn_check);
        bbtn_check.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_check.create("Check Version",0);

        txvw_version = view.findViewById(R.id.txvw_version);
        txvw_lastUpdate = view.findViewById(R.id.txvw_lastUpdate);

        view.findViewById(R.id.rvly_root).setOnClickListener(view1 -> dismiss());

    }

    @Override
    public void show() {
        super.show();

        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
        card_body.setVisibility(View.VISIBLE);

        MyDevice device = new MyDevice(mActivity);
        String version = "Version "+device.getVersion();
        txvw_version.setText(version);

        bbtn_check.setOnActionListener(view -> {
            if (onCheckListener != null){
                onCheckListener.onCheckVersion();
                dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }



    public void setonCheckListener(OnCheckListener onCheckListener){
        this.onCheckListener = onCheckListener;
    }
    public interface OnCheckListener {
        void onCheckVersion();
    }
}
