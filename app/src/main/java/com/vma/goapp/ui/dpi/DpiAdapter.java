package com.vma.goapp.ui.dpi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.dom.DpiHolder;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;

import java.util.ArrayList;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class DpiAdapter extends RecyclerView.Adapter<DpiAdapter.AdapterView>{

    private final ArrayList<DpiHolder> mList;
    private Context mContext;

    public DpiAdapter(Context context, ArrayList<DpiHolder> list){
        this.mList = list;
        this.mContext = context;
    }


    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dpi, parent, false);
        return new AdapterView(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final DpiHolder data = mList.get(position);
        String no = position + 1 +".";
        holder.txvw_no.setText(no);
        holder.txvw_title.setText(Utility.getDateString(data.getDate(),"dd-MM-yyyy"));
        LocationConverter converter = new LocationConverter(mContext,data.getLongitude(), data.getLatitude());
        String location = converter.getLatitude() +" | "+ converter.getLongitude();
        holder.txvw_location.setText(location);
        String heading = mContext.getResources().getString(R.string.heading)+" : "+ converter.getBearingStr(mContext);
        holder.txvw_course.setText(heading);

        DistanceUnit distanceUnit = new DistanceUnit(mContext,data.getDistance());
        holder.txvw_distance.setText(distanceUnit.getDisplay());
        holder.txvw_distance.setBackground(Utility.getRectBackground("FFED05", Utility.dpToPx(mContext,12)));

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
        TextView txvw_no,txvw_location,txvw_title,txvw_course,txvw_distance;

        public AdapterView(@NonNull View itemView) {
            super(itemView);
            lnly_selectMenu = itemView.findViewById(R.id.lnly_selectMenu);
            txvw_no = itemView.findViewById(R.id.txvw_no);
            txvw_title = itemView.findViewById(R.id.txvw_title);
            txvw_location = itemView.findViewById(R.id.txvw_location);
            txvw_course = itemView.findViewById(R.id.txvw_course);
            txvw_distance = itemView.findViewById(R.id.txvw_distance);
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(DpiHolder menu);
    }
}
