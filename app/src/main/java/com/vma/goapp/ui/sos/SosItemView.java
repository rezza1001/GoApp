package com.vma.goapp.ui.sos;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyView;

/**
 * Created by Mochamad Rezza Gumilang on 30/03/2022
 */

public class SosItemView extends MyView {

    private ImageView imvw_icon;
    private TextView txvw_key,txvw_value;

    public SosItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.sos_view_item;
    }

    @Override
    protected void initLayout() {
        imvw_icon   = findViewById(R.id.imvw_icon);
        txvw_key    = findViewById(R.id.txvw_key);
        txvw_value  = findViewById(R.id.txvw_value);
    }

    @Override
    protected void initListener() {

    }

    public void create(int icon, String key, String value){
        imvw_icon.setImageResource(icon);
        txvw_key.setText(key);
        txvw_value.setText(value);
    }

    public void setValue(String value){
        txvw_value.setText(value);
    }
}
