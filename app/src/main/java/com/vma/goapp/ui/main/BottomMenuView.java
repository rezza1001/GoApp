package com.vma.goapp.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyView;

public class BottomMenuView extends MyView {

    ImageView imvw_menu;

    public BottomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_menu;
    }

    @Override
    protected void initLayout() {
        imvw_menu = findViewById(R.id.imvw_menu);
        imvw_menu.setImageLevel(0);
    }

    public void setSelected(int index){
        if (imvw_menu.getDrawable().getLevel() == index){
            return;
        }
        imvw_menu.setImageLevel(index);
        if (onSelectedListener != null){
            onSelectedListener.onMenuSelect(index+1);
        }
    }

    @Override
    protected void initListener() {
        findViewById(R.id.rvly_map).setOnClickListener(view -> {
            if (imvw_menu.getDrawable().getLevel() == 0){
                return;
            }
            imvw_menu.setImageLevel(0);
            if (onSelectedListener != null){
                onSelectedListener.onMenuSelect(1);
            }
        });
        findViewById(R.id.rvly_dpi).setOnClickListener(view -> {
            if (imvw_menu.getDrawable().getLevel() == 1){
                return;
            }
            imvw_menu.setImageLevel(1);
            if (onSelectedListener != null){
                onSelectedListener.onMenuSelect(2);
            }
        });
        findViewById(R.id.rvly_listLocation).setOnClickListener(view -> {
            if (imvw_menu.getDrawable().getLevel() == 2){
                return;
            }
            imvw_menu.setImageLevel(2);
            if (onSelectedListener != null){
                onSelectedListener.onMenuSelect(3);
            }
        });
        findViewById(R.id.rvly_tractLocation).setOnClickListener(view -> {
            if (imvw_menu.getDrawable().getLevel() == 3){
                return;
            }
            imvw_menu.setImageLevel(3);
            if (onSelectedListener != null){
                onSelectedListener.onMenuSelect(4);
            }
        });
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onMenuSelect(int position);
    }
}
