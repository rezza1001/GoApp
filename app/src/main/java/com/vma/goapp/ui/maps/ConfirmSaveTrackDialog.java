package com.vma.goapp.ui.maps;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.vma.goapp.R;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyDialog;

public class ConfirmSaveTrackDialog extends MyDialog {

    private RelativeLayout rvly_body;
    public ConfirmSaveTrackDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int setLayout() {
        return R.layout.fishmap_dialog_confirm_save_track;
    }

    @Override
    protected void initLayout(View view) {
        rvly_body = view.findViewById(R.id.rvly_body);
        int radius = Utility.dpToPx(mActivity,16);
        int bgColor = ContextCompat.getColor(mActivity, R.color.dialogBackground);
        rvly_body.setBackground(Utility.getRectBackground(bgColor,radius,radius,0,0));
        rvly_body.setVisibility(View.INVISIBLE);

        VmaButton bbtn_yes = findViewById(R.id.bbtn_yes);
        bbtn_yes.create(mActivity.getResources().getString(R.string.yes),0);
        bbtn_yes.setButtonType(VmaButton.ButtonType.STANDARD);

        VmaButton bbtn_no = findViewById(R.id.bbtn_no);
        bbtn_no.create(mActivity.getResources().getString(R.string.no),0);
        bbtn_no.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        VmaButton bbtn_cancel = findViewById(R.id.bbtn_cancel);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        bbtn_cancel.setOnActionListener(view1 -> {
            if (onActionListener != null){
                onActionListener.OnCancelAction();
            }
            dismiss();
        });

        bbtn_no.setOnActionListener(view1 -> {
            if (onActionListener != null){
                onActionListener.OnNoAction();
            }
            dismiss();
        });

        bbtn_yes.setOnActionListener(view1 -> {
            if (onActionListener != null){
                onActionListener.OnSaveAction();
            }
        });

    }

    @Override
    public void show() {
        super.show();
        rvly_body.setVisibility(View.VISIBLE);
        rvly_body.startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_up_in));
    }


    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }

    public interface OnActionListener{
        void OnSaveAction();
        void OnNoAction();
        void OnCancelAction();
    }
}
