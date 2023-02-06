package com.vma.goapp.ui.component.option;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.vma.goapp.R;

import java.util.ArrayList;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.OptionView>{

    private final ArrayList<Bundle> data;
    private Context mContext;

    public OptionAdapter(Context context , ArrayList<Bundle> data ){
        this.data = data;
        mContext = context;
    }

    @NonNull
    @Override
    public OptionView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_chooser_adapter, parent, false);
        return new OptionView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionView view, int i) {
        Bundle menu = data.get(i);
        String value = menu.getString("value");

        view.txvw_value_00.setText(value);
        view.mrly_select_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(menu);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class OptionView extends RecyclerView.ViewHolder {

        private MaterialRippleLayout mrly_select_00;
        private TextView txvw_value_00;

        public OptionView(View itemView) {
            super(itemView);
            txvw_value_00 = itemView.findViewById(R.id.txvw_value_00);
            mrly_select_00 = itemView.findViewById(R.id.mrly_select_00);
        }
    }


    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data);
    }

}