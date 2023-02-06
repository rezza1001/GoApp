package com.vma.goapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class FishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<FishHolder> mList;
    private Context mContext;
    private String mSearch = "";

    public FishAdapter(Context context, ArrayList<FishHolder> list){
        this.mList = list;
        this.mContext = context;
    }

    public void search(String value){
        mSearch = value;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_logbook_fish, parent, false);
        return new AdapterView(itemView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parent, int position) {
        if (parent instanceof  AdapterView){
            final FishHolder data = mList.get(position);
            AdapterView  holder = (AdapterView) parent;

            int start   = data.getName().toUpperCase().indexOf(mSearch.toUpperCase());
            int end     = start+ (mSearch.length());

            holder.txvw_fishName.setText(Utility.BoldText(mContext, data.getName(),start,end,"#F79E46"));
            holder.txvw_latin.setText(data.getLatinName());

            Glide.with(mContext)
                    .load(Uri.parse("file:///android_asset/fish/"+data.getImageName()))
                    .into(holder.imvw_fish);

            if (position == mList.size()-1 && mList.size() > 0){
                if (onSelectedListener != null){
                    onSelectedListener.onFinishLoad();
                }
            }

            holder.rvly_root.setOnClickListener(view -> {
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
        RelativeLayout rvly_root;
        ImageView imvw_fish;
        TextView txvw_fishName, txvw_latin;

        public AdapterView(@NonNull View itemView) {
            super(itemView);

            rvly_root = itemView.findViewById(R.id.rvly_root);
            imvw_fish = itemView.findViewById(R.id.imvw_fish);
            txvw_fishName = itemView.findViewById(R.id.txvw_fishName);
            txvw_latin = itemView.findViewById(R.id.txvw_latin);
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
        void onSelected(FishHolder menu);
        void onFinishLoad();
    }
}
