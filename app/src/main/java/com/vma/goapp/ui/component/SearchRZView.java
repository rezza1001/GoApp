package com.vma.goapp.ui.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.ferfalk.simplesearchview.utils.DimensUtils;
import com.ferfalk.simplesearchview.utils.SimpleAnimationUtils;
import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;


public class SearchRZView extends RelativeLayout {

    private RelativeLayout mrly_search_00, mrly_clear_00, mrly_back_00;
    private FrameLayout frame_search_00;
    private EditText edtx_search_00;
    private View hideview = null;
    private int realHightview = 0;
    private Activity mActivity;

    public SearchRZView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_view_searchrz, this, true);

        mrly_search_00 = findViewById(R.id.mrly_search_00);
        frame_search_00 = findViewById(R.id.frame_search_00);
        edtx_search_00 = findViewById(R.id.edtx_search_00);
        mrly_clear_00 = findViewById(R.id.mrly_clear_00);
        mrly_back_00 = findViewById(R.id.mrly_back_00);
        RelativeLayout rvly_backg_00 = findViewById(R.id.rvly_backg_00);

        frame_search_00.setVisibility(INVISIBLE);
        initListener();
        rvly_backg_00.setBackgroundColor(Color.WHITE);
    }

    public void create(Activity activity){
        mActivity = activity;
    }
    private void initListener() {
        frame_search_00.setOnClickListener(null);
        mrly_search_00.setOnClickListener(v -> {
            if (onActionListener != null){
                onActionListener.onShow();
            }
            SimpleAnimationUtils.revealOrFadeIn(frame_search_00, 500, animationListener, getRevealAnimationCenter()).start();
            edtx_search_00.setText("");
            edtx_search_00.requestFocus();
            Utility.showKeyboard(mActivity, edtx_search_00);
            if (hideview != null){
                SimpleAnimationUtils.verticalSlideView(hideview, hideview.getHeight(), 0, 500).start();
            }
        });

        mrly_back_00.setOnClickListener(v -> {
            Utility.hideKeyboard(mActivity);
            if (onActionListener != null){
                onActionListener.onHide();
            }
            close();
        });

        mrly_clear_00.setOnClickListener(v -> {
            edtx_search_00.setText("");
            if (onActionListener != null){
                onActionListener.onClear();
            }
        });

        edtx_search_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onActionListener != null){
                    onActionListener.onTextChange(s.toString());
                }
            }
        });
    }

    SimpleAnimationUtils.AnimationListener animationListener = new SimpleAnimationUtils.AnimationListener() {
        @Override
        public boolean onAnimationStart(View view) {
            return false;
        }

        @Override
        public boolean onAnimationCancel(View view) {
            return false;
        }

        @Override
        public boolean onAnimationEnd(@NonNull View view) {
            return false;
        }
    };

    public void setHideView(View view){
        hideview = view;
        realHightview = view.getHeight();
    }

    public void setImageSearchColor(int color){
        ImageView imvw = (ImageView) mrly_search_00.getChildAt(0);
        imvw.setColorFilter(color);
    }


    public Point getRevealAnimationCenter() {
        int centerX = frame_search_00.getWidth() - DimensUtils.convertDpToPx(26, mActivity);
        int centerY = frame_search_00.getHeight() / 2;

        return new Point(centerX, centerY);
    }

    public void close(){
        edtx_search_00.setText("");
        edtx_search_00.clearFocus();
        Utility.hideKeyboard(mActivity);
        try {
            SimpleAnimationUtils.hideOrFadeOut(frame_search_00, 500, animationListener, getRevealAnimationCenter()).start();
            if (hideview != null){
                SimpleAnimationUtils.verticalSlideView(hideview, 0, realHightview, 500).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setHint(String hint){
        edtx_search_00.setHint(hint);
    }

    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }
    public interface OnActionListener{
        void onShow();
        void onHide();
        void onClear();
        void onTextChange(String text);
    }

}