package com.vma.goapp.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;

public class MyToast extends RelativeLayout {

     ImageView imvw_icon_00;
     TextView txvw_note_00;
     Toast toast;

    public MyToast(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_mytoast,this,true);

        RelativeLayout rvly_root_00 = findViewById(R.id.rvly_root_00);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 32, getResources().getDisplayMetrics());
        rvly_root_00.setBackground(Utility.getRectBackground("6d6770",px,px,px,px));

        imvw_icon_00    = findViewById(R.id.imvw_icon_00);
        txvw_note_00    = findViewById(R.id.txvw_note_00);

        toast = new Toast(context);
        toast.setView(this);
    }

    public void show(int duration){
        toast.setDuration(duration);
        toast.show();
    }

    public void show(int duration, int gravity, int x, int y){
        toast.setDuration(duration);
        toast.setGravity(gravity,x,y);
        toast.show();
    }
    public void show(int duration, int gravity){
        toast.setDuration(duration);
        toast.setGravity(gravity,0,0);
        toast.show();
    }


    public void setIcon(int resource, int color){
        imvw_icon_00.setImageResource(resource);
        imvw_icon_00.setColorFilter(color);
    }

    public void setMessage(String message){
        txvw_note_00.setText(message);
    }

}