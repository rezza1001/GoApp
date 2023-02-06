package com.vma.goapp.ui.component;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

public class OptionChooserImageDialog extends MyDialog {

    public enum Option {
        CAMERA,FILE_BROWSER
    }

    private CardView card_body;
    private MaterialRippleLayout mrly_camera,mrly_file;

    public OptionChooserImageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.report_dialog_optionchooserdialog;
    }

    @Override
    protected void initLayout(View view) {
        card_body = view.findViewById(R.id.card_body);
        card_body.setVisibility(View.INVISIBLE);

        mrly_camera = view.findViewById(R.id.mrly_camera);
        mrly_file = view.findViewById(R.id.mrly_file);

        RelativeLayout rvly_root = view.findViewById(R.id.rvly_root);
        rvly_root.setOnClickListener(view1 -> dismiss());

        initListener();
    }

    protected void initListener(){
        mrly_camera.setOnClickListener(view -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(Option.CAMERA);
            }
            dismiss();
        });
        mrly_file.setOnClickListener(view -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(Option.FILE_BROWSER);
            }
            dismiss();
        });
    }

    @Override
    public void show() {
        super.show();
        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener{
        void onSelected(Option option);
    }
}