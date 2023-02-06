package com.vma.goapp.ui.logbook;

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
public class LogbookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static int END_VIEW = 9999;

    private final ArrayList<LogbookHolder> mList;
    private Context mContext;

    public LogbookAdapter(Context context, ArrayList<LogbookHolder> list){
        this.mList = list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == END_VIEW){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_logbook_endview, parent, false);
            return new EndOfView(itemView);
        }
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_logbook, parent, false);
        return new AdapterView(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder parent, int position) {
        final LogbookHolder data = mList.get(position);
        if (parent instanceof AdapterView){
            AdapterView  holder = (AdapterView) parent;
            holder.rvly_fish.setBackground(Utility.getRectBackground("E7F0FF", Utility.dpToPx(mContext,6)));

            holder.txvw_fishName.setText(data.getFishName());
            String sType = data.getType().getValue();
            String value = data.getQty()+" ("+ sType+")";
            holder.txvw_size.setText(value);

            String time = Utility.getDateString(data.getTime(),"HH:mm");
            String date = Utility.getDateString(data.getTime(),"dd MMMM yyyy");
            String sTime = date +"\n" + time;
            holder.txvw_time.setText(sTime);

            Glide.with(mContext)
                    .load(Uri.parse("file:///android_asset/fish/"+data.getFishImage()))
                    .into(holder.imvw_fish);

            holder.rvly_delete.setOnClickListener(view -> {
                if (onSelectedListener != null){
                    onSelectedListener.onDelete(data);
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
        if (mList.get(position).getFishId() == -1){
            return END_VIEW;
        }
        return super.getItemViewType(position);
    }

    public static class AdapterView extends RecyclerView.ViewHolder{
        RelativeLayout rvly_root,rvly_fish,rvly_delete;
        ImageView imvw_fish;
        TextView txvw_fishName,txvw_size,txvw_time;

        public AdapterView(@NonNull View itemView) {
            super(itemView);

            rvly_root = itemView.findViewById(R.id.rvly_root);
            rvly_fish = itemView.findViewById(R.id.rvly_fish);
            imvw_fish = itemView.findViewById(R.id.imvw_fish);
            txvw_fishName = itemView.findViewById(R.id.txvw_fishName);
            txvw_size = itemView.findViewById(R.id.txvw_size);
            txvw_time = itemView.findViewById(R.id.txvw_time);
            rvly_delete = itemView.findViewById(R.id.rvly_delete);
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
        void onDelete(LogbookHolder menu);
    }
}
