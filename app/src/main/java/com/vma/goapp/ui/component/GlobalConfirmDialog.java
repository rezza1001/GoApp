package com.vma.goapp.ui.component;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyDialog;

/**
 * Created by Mochamad Rezza Gumilang on 29/03/2022
 */
public class GlobalConfirmDialog extends MyDialog {
    public enum Action {YES,CANCEL}

    RelativeLayout rvly_body;
    TextView txvw_title,txvw_description;
    VmaButton bbtn_yes,bbtn_cancel;

    public GlobalConfirmDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int setLayout() {
        return R.layout.dialog_confirm_global;
    }

    @Override
    protected void initLayout(View view) {
        rvly_body = view.findViewById(R.id.rvly_body);
        int round = Utility.dpToPx(mActivity, 15);
        rvly_body.setBackground(Utility.getRectBackground(mActivity.getResources().getColor(R.color.dialogBackground), round,round,0,0));
        rvly_body.setVisibility(View.INVISIBLE);

        txvw_title = view.findViewById(R.id.txvw_title);
        txvw_description = view.findViewById(R.id.txvw_description);
        bbtn_yes = view.findViewById(R.id.bbtn_yes);
        bbtn_cancel = view.findViewById(R.id.bbtn_cancel);

        bbtn_yes.create(mActivity.getResources().getString(R.string.yes),0);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        bbtn_yes.setOnActionListener((VmaButton.OnClickListener) view1 -> {
            if (onActionListener != null){
                onActionListener.onAction(Action.YES);
                dismiss();
            }
        });
        bbtn_cancel.setOnActionListener((VmaButton.OnClickListener) view1 -> {
            if (onActionListener != null){
                onActionListener.onAction(Action.CANCEL);
                dismiss();
            }
        });
    }

    public void show(String title, String description){
        txvw_title.setText(title);
        txvw_description.setText(description);
        show();
    }

    public void setAction(String positive, String negative){
        bbtn_yes.create(positive,0);
        bbtn_cancel.create(negative,0);
    }

    @Override
    public void show() {
        super.show();
        rvly_body.setVisibility(View.VISIBLE);
        rvly_body.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
    }

    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }
    public interface OnActionListener{
        void onAction(Action action);
    }
}
