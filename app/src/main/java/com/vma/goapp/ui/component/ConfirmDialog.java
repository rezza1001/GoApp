package com.vma.goapp.ui.component;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vma.goapp.R;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyDialog;

public class ConfirmDialog extends MyDialog {

    CardView card_body;
    TextView txvw_title,txvw_body;
    VmaButton bbtn_no,bbtn_yes;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_comfirm;
    }

    @Override
    protected void initLayout(View view) {
        card_body = view.findViewById(R.id.card_body);
        card_body.setVisibility(View.INVISIBLE);

        txvw_title = view.findViewById(R.id.txvw_title);
        txvw_body = view.findViewById(R.id.txvw_body);

        bbtn_no = view.findViewById(R.id.bbtn_no);
        bbtn_no.create("Batal",0);
        bbtn_no.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        bbtn_yes = view.findViewById(R.id.bbtn_yes);
        bbtn_yes.create("Ya",0);
        bbtn_yes.setButtonType(VmaButton.ButtonType.STANDARD);

        bbtn_yes.setOnActionListener(view1 -> {
            if (onActionListener!= null){
                onActionListener.onAction(true);
                dismiss();
            }
        });

        bbtn_no.setOnActionListener(view1 -> {
            if (onActionListener!= null){
                onActionListener.onAction(false);
                dismiss();
            }
        });
    }

    public void show(String title, String value) {
        super.show();
        txvw_title.setText(title);
        txvw_body.setText(value);
        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
    }

    public void setTextAction(String yes, String no){
        bbtn_no.create(no,0);
        bbtn_yes.create(yes,0);
    }

    public void show(String title, SpannableString value) {
        super.show();
        txvw_title.setText(title);
        txvw_body.setText(value);
        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));
    }

    public void show( String value) {
        show(mActivity.getResources().getString(R.string.confirm), value);
    }

    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }
    public interface OnActionListener{
        void onAction(boolean confirm);
    }
}
