package com.vma.goapp.ui.component.find;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class FindDialog extends MyDialog {

    private RelativeLayout rvly_header_00;
    private EditText edtx_search_00;
    private MaterialRippleLayout mrly_clear_00;
    private RecyclerView rcvw_data_00;
    private ArrayList<Bundle> allData = new ArrayList<>();
    private ArrayList<Bundle> filterData = new ArrayList<>();
    private FindAdapter adapter;

    public FindDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_find_dialog;
    }

    @Override
    protected void initLayout(View view) {
        rvly_header_00 = view.findViewById(R.id.rvly_body_00);
        rvly_header_00.setVisibility(View.INVISIBLE);

        edtx_search_00 = view.findViewById(R.id.edtx_search_00);
        mrly_clear_00 = view.findViewById(R.id.mrly_clear_00);
        mrly_clear_00.setVisibility(View.INVISIBLE);

        rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setVisibility(View.INVISIBLE);
    }

    public void show(String title){
        edtx_search_00.setHint(title);
        show();
    }
    @Override
    public void show() {
        super.show();
        rvly_header_00.setVisibility(View.VISIBLE);
        rvly_header_00.setAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_down_in));
        rcvw_data_00.setVisibility(View.VISIBLE);
        rcvw_data_00.setAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.push_up_in));

        edtx_search_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0){
                    mrly_clear_00.setVisibility(View.INVISIBLE);
                }
                else {
                    mrly_clear_00.setVisibility(View.VISIBLE);
                }
                filter(s.toString());
            }
        });

        mrly_clear_00.setOnClickListener(v -> edtx_search_00.setText(null));
        findViewById(R.id.mrly_back_00).setOnClickListener(v -> dismiss());

        allData = new ArrayList<>();
        filterData = new ArrayList<>();
        adapter = new FindAdapter(mActivity, filterData);
        rcvw_data_00.setAdapter(adapter);
        adapter.setOnSelectedListener(data -> {
            Utility.hideKeyboard(mActivity);
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
                dismiss();
            }
        });
    }

    public void setData(ArrayList<Bundle> data){
        if (data.size() >= 10){
            new Handler().postDelayed(() -> Utility.showKeyboard(mActivity, edtx_search_00),700);
        }
        allData.addAll(data);
        filter("");
    }

    public void setData(String sData){
        try {
            JSONArray data = new JSONArray(sData);
            for (int i=0; i<data.length(); i++) {
                JSONObject jo =  data.getJSONObject(i);
                Bundle bundle = new Bundle();
                String value = jo.getString("value");
                bundle.putString("value", value);
                bundle.putInt("index", i);
                if (jo.has("key")){
                    String key = jo.getString("key");
                    bundle.putString("key", key);
                }
                else {
                    bundle.putString("key", value);
                }
                allData.add(bundle);
            }
            filter("");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void filter(String search){
        filterData.clear();
        adapter.setKey(search);
        if (search.isEmpty()){
            filterData.addAll(allData);
        }
        else {
            for (Bundle bundle : allData){
                String value = Objects.requireNonNull(bundle.getString("value")).toLowerCase();
                if (value.contains(search.toLowerCase())){
                    filterData.add(bundle);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }


    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    private OnSelectedListener onSelectedListener;
    public interface OnSelectedListener{
        void onSelected(Bundle bundle);
    }
}