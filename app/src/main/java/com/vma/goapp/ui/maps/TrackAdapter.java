package com.vma.goapp.ui.maps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.AdapterView>{

    private final ArrayList<TrackHolder> mList;
    private Context mContext;

    public TrackAdapter(Context context, ArrayList<TrackHolder> list){
        this.mList = list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_adapter_track, parent, false);
        return new AdapterView(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final TrackHolder data = mList.get(position);
        String no = position + 1 +".";
        holder.txvw_no.setText(no);
        holder.txvw_title.setText(data.getName());

        long difference = data.getTime();
        long seconds = (difference / 1000) % 60;
        long minutes = (difference / (1000 * 60)) % 60;
        long hours = (difference / (1000 * 60 * 60)) % 24;
        String totalTime = seconds+" s";
        if (hours > 0){
            totalTime = hours+" j  "+minutes+" m  "+ seconds+" s";
        }
        else if (minutes > 0){
            totalTime = minutes+" m  "+ seconds+" s";
        }

        String timeValue = mContext.getResources().getString(R.string.time)+" : "+ totalTime;
        holder.txvw_time.setText(timeValue);

        DistanceUnit distanceUnit = new DistanceUnit(mContext);
        distanceUnit.calcDistance(data.getPointStart(),data.getPointEnd());
        data.setDistance((float) distanceUnit.getNm());

        holder.txvw_distance.setText(distanceUnit.getDisplay());
        holder.txvw_distance.setBackground(Utility.getRectBackground("FFED05", Utility.dpToPx(mContext,12)));

        String sDate =  Utility.getDateString(data.getEndDate(),"dd MMMM yyyy HH:mm:ss");
        holder.txvw_date.setText(sDate);

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
        RelativeLayout lnly_selectMenu;
        TextView txvw_no,txvw_date,txvw_title, txvw_time,txvw_distance;

        public AdapterView(@NonNull View itemView) {
            super(itemView);
            lnly_selectMenu = itemView.findViewById(R.id.lnly_selectMenu);
            txvw_no = itemView.findViewById(R.id.txvw_no);
            txvw_title = itemView.findViewById(R.id.txvw_title);
            txvw_date = itemView.findViewById(R.id.txvw_date);
            txvw_time = itemView.findViewById(R.id.txvw_time);
            txvw_distance = itemView.findViewById(R.id.txvw_distance);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(TrackHolder menu);
    }
}
