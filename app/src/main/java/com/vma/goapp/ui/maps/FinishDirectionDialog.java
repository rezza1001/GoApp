package com.vma.goapp.ui.maps;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

public class FinishDirectionDialog extends MyDialog {

    private RelativeLayout rvly_body;
    private boolean isShowing = false;

    public FinishDirectionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.map_doalog_finish_direction;
    }

    @Override
    protected void initLayout(View view) {
        view.findViewById(R.id.imvw_close).setOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.rvly_root).setOnClickListener(view1 -> dismiss());

        rvly_body = view.findViewById(R.id.rvly_body);
        rvly_body.setVisibility(View.INVISIBLE);
    }

    @Override
    public void show() {
        super.show();
        isShowing = true;
        rvly_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_down_in));
        rvly_body.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        new Handler().postDelayed(() -> isShowing = false,60000);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }
}
