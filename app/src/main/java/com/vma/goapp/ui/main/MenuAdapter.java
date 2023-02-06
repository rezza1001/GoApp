package com.vma.goapp.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.dom.MenuHolder;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.AdapterView>{

    private final ArrayList<MenuHolder> mList;
    private Context mContext;

    public MenuAdapter(Context context, ArrayList<MenuHolder> list){
        this.mList = list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menu, parent, false);
        return new AdapterView(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final MenuHolder data = mList.get(position);
        holder.txvw_menu.setText(data.name);
        holder.imvw_iconMenu.setImageResource(data.icon);
        holder.imvw_iconMenu.setColorFilter(Color.WHITE);
        holder.rvly_body.setBackground(data.background);

        holder.lnly_selectMenu.setOnClickListener(view -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_body;
        RelativeLayout lnly_selectMenu;
        ImageView imvw_iconMenu;
        TextView txvw_menu;

        public AdapterView(@NonNull View itemView) {
            super(itemView);

            rvly_body = itemView.findViewById(R.id.rvly_body);
            imvw_iconMenu = itemView.findViewById(R.id.imvw_iconMenu);
            lnly_selectMenu = itemView.findViewById(R.id.lnly_selectMenu);
            txvw_menu = itemView.findViewById(R.id.txvw_menu);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(MenuHolder menu);
    }
}
