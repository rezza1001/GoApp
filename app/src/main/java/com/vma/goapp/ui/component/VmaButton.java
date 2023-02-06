package com.vma.goapp.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyView;

public class VmaButton extends MyView {
    public enum ButtonType {STANDARD,BLUE_GREY};

    RelativeLayout rvly_button;
    ImageView imvw_bbtnIcon;
    View view_bbtnSeparator;
    TextView txvw_bbtnTitile;

    boolean isClick  = false;

    public VmaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_view_button;
    }

    @Override
    protected void initLayout() {
        rvly_button = findViewById(R.id.rvly_button);
        imvw_bbtnIcon = findViewById(R.id.imvw_bbtnIcon);
        view_bbtnSeparator = findViewById(R.id.view_bbtnSeparator);
        txvw_bbtnTitile = findViewById(R.id.txvw_bbtnTitile);
        setButtonType(ButtonType.BLUE_GREY);
    }

    @Override
    protected void initListener() {
        rvly_button.setOnClickListener(view -> {
            if (onClickListener != null){
                if (isClick){
                    return;
                }
                isClick = true;
                onClickListener.onVmaClick(this);
                new Handler().postDelayed(() -> isClick = false,500);
            }
        });
    }

    public void create(String sTitle, int icon){
        txvw_bbtnTitile.setText(sTitle);
        imvw_bbtnIcon.setImageResource(icon);
        txvw_bbtnTitile.setVisibility(VISIBLE);
        imvw_bbtnIcon.setVisibility(VISIBLE);
        if (sTitle.isEmpty()){
            txvw_bbtnTitile.setVisibility(GONE);
            view_bbtnSeparator.setVisibility(GONE);
        }
        else if (icon == 0) {
            imvw_bbtnIcon.setVisibility(GONE);
            view_bbtnSeparator.setVisibility(GONE);
        }
    }

    public void setButtonType(ButtonType buttonType){
        if (buttonType == ButtonType.STANDARD){
            txvw_bbtnTitile.setTextColor(Color.WHITE);
            rvly_button.setBackgroundResource(R.drawable.button_standard);
            imvw_bbtnIcon.setColorFilter(Color.parseColor("#ffffff"));
        }
        if (buttonType == ButtonType.BLUE_GREY){
            txvw_bbtnTitile.setTextColor(Color.parseColor("#0064FE"));
            imvw_bbtnIcon.setColorFilter(Color.parseColor("#0064FE"));
            rvly_button.setBackgroundResource(R.drawable.button_standard_bluegrey);
        }
    }

    private OnClickListener onClickListener;
    public void setOnActionListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener{
        void onVmaClick(VmaButton view);
    }
}
