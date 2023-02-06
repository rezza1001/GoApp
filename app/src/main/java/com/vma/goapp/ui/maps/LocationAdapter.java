package com.vma.goapp.ui.maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.arcgisruntime.geometry.Point;
import com.vma.goapp.R;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.libs.VmaUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mochamad Rezza Gumilang on 15/02/2022
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.AdapterView>{

    private final ArrayList<LocationHolder> mList;
    private Context mContext;
    private Point point;

    public LocationAdapter(Context context, ArrayList<LocationHolder> list){
        this.mList = list;
        this.mContext = context;

        String prefData = VmaPreferences.get(context, VmaApiConstant.GPS_LSAT_DATA);
        try {
            JSONObject jo = new JSONObject(prefData);
            LocationConverter converter = new LocationConverter(mContext, jo.getString("longitude"),jo.getString("latitude"));
            point = converter.getPoint();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_adapter_locationi, parent, false);
        return new AdapterView(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final LocationHolder data = mList.get(position);
        String no = position + 1 +".";
        holder.txvw_no.setText(no);
        holder.txvw_title.setText(data.getName());
        LocationConverter converter = new LocationConverter(mContext,data.getLongitude(), data.getLatitude());
        String location = converter.getLatitude() +" | "+ converter.getLongitude();
        holder.txvw_location.setText(location);

        double bearing = VmaUtils.bearing(point.getY(), point.getX(), converter.getPoint().getY(), converter.getPoint().getX());
        String bear = mContext.getResources().getString(R.string.heading)+" : "+String.format(Locale.ENGLISH, "%d°", (int)bearing);
        holder.txvw_course.setText(bear);

        DistanceUnit distanceUnit = new DistanceUnit(mContext);
        distanceUnit.calcDistance(converter.getPoint(),point);

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
        void onSelected(LocationHolder menu);
    }
}
