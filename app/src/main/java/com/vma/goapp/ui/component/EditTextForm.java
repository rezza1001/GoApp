package com.vma.goapp.ui.component;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyView;

public class EditTextForm extends MyView {

    public enum Type{
        PASSWORD,TEXT,NUMBER,PHONE,ADDRESS,NUMBER_DECIMAL,SELECT
    }

    TextView txvw_title,txvw_required;
    EditText edtx_form;
    TextView txvw_select;
    RelativeLayout rvly_select;
    LinearLayout lnly_title;

    boolean isValid = true;

    public EditTextForm(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_editext_form;
    }

    @Override
    protected void initLayout() {
        txvw_title = findViewById(R.id.txvw_title);
        txvw_required = findViewById(R.id.txvw_required);
        edtx_form = findViewById(R.id.edtx_form);
        rvly_select = findViewById(R.id.rvly_select);
        txvw_select = findViewById(R.id.txvw_select);
        lnly_title = findViewById(R.id.lnly_title);

        txvw_required.setVisibility(INVISIBLE);
        rvly_select.setVisibility(GONE);
        setType(Type.TEXT);

        txvw_select.setTag("");
        edtx_form.setTag("");

    }

    @Override
    protected void initListener() {
        rvly_select.setOnClickListener(view -> {
            if (onSelectListener != null){
                onSelectListener.onSelect();
            }
        });
    }

    public void create(String title){
        super.create();
        if (title.isEmpty()){
            hideTitle();
            return;
        }
        txvw_title.setText(title);
        edtx_form.setHint("Input "+ title);
    }
    public void create(String title, int titleColor, int valueColor){
        super.create();
        if (title.isEmpty()){
            hideTitle();
            return;
        }

        txvw_title.setText(title);
        txvw_title.setTextColor(titleColor);
        edtx_form.setTextColor(valueColor);
        edtx_form.setHint("Input "+ title);
    }

    public void hideTitle(){
        lnly_title.setVisibility(GONE);
        txvw_required.setVisibility(GONE);
    }

    public void setRequired(){
        txvw_required.setVisibility(VISIBLE);
    }

    public String getValue(){
        isValid = true;
        String text = edtx_form.getText().toString().trim();
        if (edtx_form.getVisibility() == GONE){
            text = txvw_select.getText().toString().trim();
        }

        if (txvw_required.getVisibility() == VISIBLE){
            if (text.isEmpty()){
                showError();
                isValid = false;
                return  "";
            }
            return  text;
        }
        return  text;
    }

    public String getKey(){
        isValid = true;
        String text = "";
        if (edtx_form.getVisibility() == GONE){
            text = txvw_select.getTag().toString().trim();
        }
        else {
            text = edtx_form.getTag().toString().trim();
        }

        if (txvw_required.getVisibility() == VISIBLE){
            if (text.isEmpty()){
                showError();
                isValid = false;
                return  "";
            }
            return  text;
        }
        return  text;
    }

    public void showError(){
        String message = txvw_title.getText().toString() +" "+ getResources().getString(R.string.required);
        Utility.showToastError(mActivity, message);
    }

    public void setTitleColor(int color){
        txvw_title.setTextColor(color);
    }

    public void setValue(String value){
        if (edtx_form.getVisibility() == GONE){
            txvw_select.setText(value);
            txvw_select.setTag(value);
        }
        else {
            edtx_form.setText(value);
        }
    }
    public void setValue( String key,String value){
        if (edtx_form.getVisibility() == GONE){
            txvw_select.setText(value);
            txvw_select.setTag(key);
        }
        else {
            edtx_form.setText(value);
            edtx_form.setTag(key);
        }
    }

    public void setType(Type type){
        edtx_form.setLines(1);
        if (type == Type.NUMBER){
            edtx_form.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }
        else if (type == Type.TEXT){
            edtx_form.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }
        else if (type == Type.PASSWORD){
            edtx_form.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else if (type == Type.PHONE){
            edtx_form.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
        }
        else if (type == Type.ADDRESS){
            edtx_form.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            edtx_form.setMinLines(2);
        }
        else if (type == Type.NUMBER_DECIMAL){
            edtx_form.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edtx_form.setMinLines(2);
        }
        else if (type == Type.SELECT){
            rvly_select.setVisibility(VISIBLE);
            edtx_form.setVisibility(GONE);
        }
    }

    public boolean isValid() {
        getValue();
        return isValid;
    }

    private OnSelectListener onSelectListener;
    public void setOnSelectListener(OnSelectListener onSelectListener){
        this.onSelectListener = onSelectListener;
    }
    public interface OnSelectListener{
        void onSelect();
    }
}
