package com.vma.goapp.ui.profile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyView;

public class ItemView extends MyView {

    private TextView txvw_key,txvw_value;

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.profile_view_item;
    }

    @Override
    protected void initLayout() {
        txvw_key = findViewById(R.id.txvw_key);
        txvw_value = findViewById(R.id.txvw_value);
    }

    @Override
    protected void initListener() {

    }

    public void create(String key, String value) {
        super.create();

        txvw_key.setText(key);
        txvw_value.setText(value);
        if (value.isEmpty()){
            txvw_value.setText("-");
        }
    }


}
