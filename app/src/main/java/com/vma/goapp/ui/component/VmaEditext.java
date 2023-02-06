package com.vma.goapp.ui.component;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyView;

public class VmaEditext extends MyView {
    public static final int INPUT_DROP_DOWN = 99;

    private TextView txvw_edtxTitle;
    private EditText edtx_value;
    private RelativeLayout rvly_readOnly;
    private ImageView imvw_dropDown,imvw_right;

    private int inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;

    public VmaEditext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_view_editext;
    }

    @Override
    protected void initLayout() {
        txvw_edtxTitle = findViewById(R.id.txvw_edtxTitle);
        edtx_value = findViewById(R.id.edtx_value);
        rvly_readOnly = findViewById(R.id.rvly_readOnly);
        imvw_dropDown = findViewById(R.id.imvw_dropDown);
        imvw_right = findViewById(R.id.imvw_right);
        rvly_readOnly.setVisibility(GONE);
        imvw_dropDown.setVisibility(GONE);
        imvw_right.setVisibility(GONE);

    }

    @Override
    protected void initListener() {

    }

    public void setInputType(int inputType){
        this.inputType = inputType;
        if (inputType != INPUT_DROP_DOWN){
            edtx_value.setInputType(inputType);
        }
        else {
            if (imvw_dropDown.getVisibility() == VISIBLE){
                imvw_dropDown.setVisibility(VISIBLE);
            }
            else {
                imvw_dropDown.setVisibility(INVISIBLE);
            }
            edtx_value.setEnabled(false);
            rvly_readOnly.setVisibility(VISIBLE);
            rvly_readOnly.setOnClickListener(view -> {
                edtx_value.requestFocus();
                if (onSelectedListener != null){
                    onSelectedListener.onSelected();
                }
            });
            edtx_value.setOnFocusChangeListener((view, b) -> {
                if (b){
                    Utility.hideKeyboard(mActivity);
                }
            });
        }
    }

    public void setInputRealNumber(){
        setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_VARIATION_NORMAL);
        setDigit("0123456789");
    }

    public void setTextSize(int size){
        edtx_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
    }

    public void setMaxLength(int length){
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(length);
        edtx_value.setFilters(fArray);
    }

    public void setDigit(String digits){
        edtx_value.setKeyListener(DigitsKeyListener.getInstance(digits));
    }

    public void setValue(String value){
        edtx_value.setText(value);
    }

    public String getValue(){
        return edtx_value.getText().toString();
    }

    public void setChooserIcon(int res){
        imvw_dropDown.setVisibility(VISIBLE);
        imvw_dropDown.setImageResource(res);
    }

    public void setRightIcon(int icon, int color){
        imvw_right.setVisibility(VISIBLE);
        imvw_right.setImageResource(icon);
        if (color != 0){
            imvw_right.setColorFilter(color);
        }
    }

    public void create(String title, String hint){
        txvw_edtxTitle.setText(title);
        edtx_value.setHint(hint);
    }

    public void setReadOnly(){
        if (imvw_dropDown.getVisibility() == VISIBLE){
            imvw_dropDown.setVisibility(VISIBLE);
        }
        else {
            imvw_dropDown.setVisibility(INVISIBLE);
        }
        edtx_value.setEnabled(false);
        rvly_readOnly.setVisibility(VISIBLE);
        rvly_readOnly.setOnClickListener(view -> {

        });
    }



    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected();
    }

}
