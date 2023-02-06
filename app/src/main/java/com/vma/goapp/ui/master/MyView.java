package com.vma.goapp.ui.master;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public abstract class MyView extends RelativeLayout {

    protected Activity mActivity;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(setLayout(),this,true);
        mActivity = scanForActivity(context);
        initLayout();
        initListener();
        initData();
    }

    public void create(){
        mActivity = scanForActivity(getContext());
    }



    protected abstract int setLayout();
    protected abstract void initLayout();
    protected abstract void initListener();

    protected void initData(){

    }

    protected Activity scanForActivity(Context context) {
        if (context == null)
            return null;
        else if (context instanceof Activity)
            return (Activity)context;
        else if (context instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)context).getBaseContext());

        return null;
    }
}