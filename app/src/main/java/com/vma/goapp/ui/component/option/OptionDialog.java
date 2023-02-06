package com.vma.goapp.ui.component.option;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.ui.master.MyDialog;

import java.util.ArrayList;

public class OptionDialog extends MyDialog {

    private OnSelectListener onSelectListener;

    private TextView txvw_title;
    private RecyclerView rcvw_menu;
    private CardView card_body;
    private OptionAdapter adapter;
    private ArrayList<Bundle> listOption = new ArrayList<>();

    public OptionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.component_dialog_option;
    }

    @Override
    protected void initLayout(View view) {
        view.findViewById(R.id.rvly_close).setOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.rvly_dialog).setOnClickListener(view1 -> dismiss());

        card_body = findViewById(R.id.card_body);
        card_body.setVisibility(View.INVISIBLE);

        rcvw_menu = view.findViewById(R.id.rcvw_menu);
        rcvw_menu.setLayoutManager(new LinearLayoutManager(mActivity));

        txvw_title = view.findViewById(R.id.txvw_title);
        txvw_title.setVisibility(View.GONE);

    }

    @Override
    public void show() {
        super.show();
        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));

        listOption = new ArrayList<>();
        adapter = new OptionAdapter(mActivity, listOption);
        rcvw_menu.setAdapter(adapter);

        adapter.setOnSelectedListener(data -> {
            if (onSelectListener!= null){
                onSelectListener.onSelect(data, data.getString("value"));
                dismiss();
            }
        });
    }

    public void addChooser(String value){
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        listOption.add(bundle);
        adapter.notifyItemInserted(listOption.size());
    }
    public void addChooser(String key, String value){
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        bundle.putString("key", key);
        listOption.add(bundle);
        adapter.notifyItemInserted(listOption.size());
    }
    public void addChooser(int key, String value){
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        bundle.putInt("key", key);
        listOption.add(bundle);
        adapter.notifyItemInserted(listOption.size());
    }
    public void addChooser( Bundle bundle ){
        listOption.add(bundle);
        adapter.notifyItemInserted(listOption.size());
    }

    public void setNameTitle(String title){
        txvw_title.setVisibility(View.VISIBLE);
        txvw_title.setText(title);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener){
        this.onSelectListener = onSelectListener;
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    public interface OnSelectListener{
        void onSelect(Bundle bundle, String value);
    }
}
