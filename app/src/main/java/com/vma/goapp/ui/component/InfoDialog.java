package com.vma.goapp.ui.component;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

public class InfoDialog extends MyDialog {

    RelativeLayout rvly_body;
    TextView txvw_title,txvw_body;

    public InfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_info;
    }

    @Override
    protected void initLayout(View view) {
        rvly_body = view.findViewById(R.id.rvly_body);
        rvly_body.setVisibility(View.INVISIBLE);

        txvw_title = view.findViewById(R.id.txvw_title);
        txvw_body = view.findViewById(R.id.txvw_body);

        view.findViewById(R.id.imvw_close).setOnClickListener(view1 -> dismiss());


    }

    public void show(String title, String value) {
        super.show();
        txvw_title.setText(title);
        txvw_body.setText(value);
        rvly_body.setVisibility(View.VISIBLE);
        rvly_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
    }


    public void show(String title, SpannableString value) {
        super.show();
        txvw_title.setText(title);
        txvw_body.setText(value);
        rvly_body.setVisibility(View.VISIBLE);
        rvly_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
    }

    public void show( String value) {
        show(mActivity.getResources().getString(R.string.information), value);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }
}
