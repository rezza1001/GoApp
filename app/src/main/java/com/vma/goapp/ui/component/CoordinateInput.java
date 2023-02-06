package com.vma.goapp.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.libs.VmaGlobalConfig;
import com.vma.goapp.ui.master.MyView;

public class CoordinateInput extends MyView {

    private TextView txvw_typeName;
    private RadioGroup rg_location;
    private RadioButton rb_negative, rb_positive;
    private EditTextForm edtx_degree;
    private LinearLayout lnly_degree,lnly_minute,lnly_second;
    private int type = 1;
    private int typeValue = -1;

    public CoordinateInput(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_view_coordinate_input;
    }

    @Override
    protected void initLayout() {
        txvw_typeName = findViewById(R.id.txvw_typeName);
        rg_location = findViewById(R.id.rg_location);
        rb_negative = findViewById(R.id.rb_latS);
        rb_positive = findViewById(R.id.rb_latN);
        edtx_degree = findViewById(R.id.edtx_degree);
        edtx_degree.create("");
        edtx_degree.hideTitle();
        edtx_degree.setType(EditTextForm.Type.NUMBER_DECIMAL);

        lnly_degree = findViewById(R.id.lnly_degree);
        lnly_degree.setVisibility(GONE);

        lnly_minute = findViewById(R.id.lnly_minute);
        lnly_minute.setVisibility(GONE);

        lnly_second = findViewById(R.id.lnly_second);
        lnly_second.setVisibility(GONE);
    }

    @Override
    protected void initListener() {
        rg_location.setOnCheckedChangeListener((radioGroup, i) -> {
            if (  rg_location.getCheckedRadioButtonId() == rb_negative.getId()){
                Log.d("TAGRZ","Selected Negative ");
                typeValue = -1;
            }
            else if (rg_location.getCheckedRadioButtonId() == rb_positive.getId()){
                Log.d("TAGRZ","Selected Positive ");
                typeValue = 1;
            }
        });
    }

    @Override
    public void create() {
        super.create();
        lnly_minute.setVisibility(GONE);
        lnly_second.setVisibility(GONE);
        lnly_degree.setVisibility(GONE);

        type = VmaGlobalConfig.getCoordinateFormat(mActivity);
//        type = VmaGlobalConfig.FORMAT_COORDINATE_MINUTE;

        if (type == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            lnly_degree.setVisibility(VISIBLE);
        }
        else if (type == VmaGlobalConfig.FORMAT_COORDINATE_SECONDS){
            lnly_minute.setVisibility(VISIBLE);
            buildMinute();
        }
        else {
            lnly_second.setVisibility(VISIBLE);
            buildSecond();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setTypeLatitude(){
        txvw_typeName.setText("Latitude");
        rb_negative.setText("S");
        rb_positive.setText("N");

        rb_negative.setChecked(true);
    }

    @SuppressLint("SetTextI18n")
    public void setTypeLongitude(){
        txvw_typeName.setText("Longitude");
        rb_negative.setText("W");
        rb_positive.setText("E");
        rb_positive.setChecked(true);
    }

    private void buildMinute(){
        EditTextForm deg = (EditTextForm) lnly_minute.getChildAt(0);
        deg.create("D");
        deg.hideTitle();
        deg.setType(EditTextForm.Type.NUMBER);

        EditTextForm min = (EditTextForm) lnly_minute.getChildAt(1);
        min.create("M");
        min.hideTitle();
        min.setType(EditTextForm.Type.NUMBER_DECIMAL);
    }
    private void buildSecond(){
        EditTextForm deg = (EditTextForm) lnly_second.getChildAt(0);
        deg.create("D");
        deg.hideTitle();
        deg.setType(EditTextForm.Type.NUMBER);

        EditTextForm min = (EditTextForm) lnly_second.getChildAt(1);
        min.create("M");
        min.hideTitle();
        min.setType(EditTextForm.Type.NUMBER);

        EditTextForm sec = (EditTextForm) lnly_second.getChildAt(2);
        sec.create("S");
        sec.hideTitle();
        sec.setType(EditTextForm.Type.NUMBER_DECIMAL);
    }

    public void setValue(double value){
        if (value < 0){
            rb_negative.setChecked(true);
            typeValue = -1;
        }
        else {
            rb_positive.setChecked(true);
            typeValue = 1;
        }

        if (type == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            edtx_degree.setValue(String.valueOf(value * typeValue));
        }
        else if (type == VmaGlobalConfig.FORMAT_COORDINATE_SECONDS){

        }
        else {

        }
    }

    public double getValue(){
        if (type == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
            if (edtx_degree.getValue().isEmpty()){
                return 0;
            }
            return Double.parseDouble(edtx_degree.getValue()) * typeValue;
        }
        else if (type == VmaGlobalConfig.FORMAT_COORDINATE_SECONDS){
            return 0;
        }
        else {
            return 0;
        }
    }


}
