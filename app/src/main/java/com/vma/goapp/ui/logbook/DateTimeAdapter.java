package com.vma.goapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class DateTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<Bundle> mList;
    private Context mContext;

    public DateTimeAdapter(Context context, ArrayList<Bundle> list){
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.logbook_adapter_datetime, parent, false);
        return new AdapterView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parent, int position) {
        if (parent instanceof  AdapterView){
            final Bundle data = mList.get(position);
            AdapterView  holder = (AdapterView) parent;
            if (data.getBoolean("select")){
                holder.rvly_root.setBackground(Utility.getRectBackground("F3D5D5", Utility.dpToPx(mContext,15)));
            }
            else {
                holder.rvly_root.setBackground(Utility.getRectBackground("ffffff", Utility.dpToPx(mContext,15)));
            }
            holder.txvw_item.setText(data.getString("name"));


            holder.rvly_action.setOnClickListener(view -> {
                int idx = 0;
                for (Bundle b : mList){
                    if (b.getBoolean("select")){
                        b.putBoolean("select", false);
                        notifyItemChanged(idx);
                    }
                    idx ++;
                }

                mList.get(position).putBoolean("select",true);
                notifyItemChanged(position);
                if (onSelectedListener != null){
                    onSelectedListener.onSelected(data);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);

    }

    public static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_action,rvly_root;
        TextView txvw_item;

        public AdapterView(@NonNull View itemView) {
            super(itemView);

            rvly_action = itemView.findViewById(R.id.rvly_action);
            rvly_root = itemView.findViewById(R.id.rvly_root);
            txvw_item = itemView.findViewById(R.id.txvw_item);
        }
    }
    public static class EndOfView extends RecyclerView.ViewHolder{
        public EndOfView(@NonNull View itemView) {
            super(itemView);
        }
    }


    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle menu);
    }
}
