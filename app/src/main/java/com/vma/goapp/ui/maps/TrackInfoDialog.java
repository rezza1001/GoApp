package com.vma.goapp.ui.maps;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.vma.goapp.R;
import com.vma.goapp.database.table.TrackDB;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class TrackInfoDialog extends MyDialog {
    ImageView imvw_icon;
    TextView txvw_title,txvw_name,txvw_point;
    VmaButton bbtn_direct,bbtn_close,bbtn_cancel;
    private double longitude, latitude;

    public TrackInfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.map_dialog_track_info;
    }

    @Override
    protected void initLayout(View view) {
        bbtn_direct = view.findViewById(R.id.bbtn_direct);
        bbtn_direct.create(mActivity.getResources().getString(R.string.goto_location),0);
        bbtn_direct.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_direct.setOnActionListener(view1 -> {
            if (onActionListener != null){
                onActionListener.onAction(ActionType.DIRECTION, longitude,latitude);
            }
            closeDialog();
        });

        bbtn_close = view.findViewById(R.id.bbtn_close);
        bbtn_close.create(mActivity.getResources().getString(R.string.close_route),0);
        bbtn_close.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_close.setOnActionListener(view1 -> {
            if (onActionListener != null){
                onActionListener.onAction(ActionType.CLEAR_TRACK, longitude,latitude);
            }
            closeDialog();
        });

        bbtn_cancel = view.findViewById(R.id.bbtn_cancel);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_cancel.setOnActionListener(view1 -> closeDialog());


        imvw_icon = findViewById(R.id.imvw_back);
        txvw_title = findViewById(R.id.txvw_title);
        txvw_name = findViewById(R.id.txvw_name);
        txvw_point = findViewById(R.id.txvw_point);

        findViewById(R.id.rvly_root).setOnClickListener(view1 -> closeDialog());
    }

    enum ActionType {DIRECTION, CLEAR_TRACK}



    public void show(TrackDB roomTrack, boolean isStart) {
        show();
        String title = isStart ? mActivity.getResources().getString(R.string.starting_point) :mActivity.getResources().getString(R.string.end_point);
        int icon = isStart ? R.drawable.track_start: R.drawable.track_end;

        try {
            JSONObject  joStart = new JSONObject(roomTrack.startPosition);
            LocationConverter convStart = new LocationConverter(mActivity,joStart.getString("longitude"),joStart.getString("latitude"));
            String startPos = convStart.getLatitudeDisplay() +" | "+convStart.getLongitudeDisplay();

            JSONObject  joEnd = new JSONObject(roomTrack.endPosition);
            LocationConverter convEnd = new LocationConverter(mActivity, joEnd.getString("longitude"),joEnd.getString("latitude"));
            String endPos = convEnd.getLatitudeDisplay() +" | "+convEnd.getLongitudeDisplay();

            String position = isStart ? startPos : endPos;
            if (isStart){
                longitude = convStart.getLongitude();
                latitude = convStart.getLatitude();
            }
            else {
                longitude = convEnd.getLongitude();
                latitude = convEnd.getLatitude();
            }

            imvw_icon.setImageResource(icon);
            txvw_title.setText(title);
            txvw_name.setText(roomTrack.name);
            txvw_point.setText(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }



//
//        if (!roomTrack.getStart().contains("E") && !roomTrack.getStart().contains("S") && !roomTrack.getStart().contains("W")&& !roomTrack.getStart().contains("N")  ){
//            try {//- 6.22347
//                if (isStart){
//                    String start = roomTrack.getStart();
//                    start = start.replace("- ","-").replace("-  ","-").replaceAll("°","").replaceAll(" +"," ");
//                    if (start.startsWith("-")){
//                        String temp = start.replace("-","").trim();
//                        start = "-"+temp;
//                    }
//                    double lat = Double.parseDouble(start.split(" ")[0]);
//                    double lon = Double.parseDouble(start.split(" ")[1]);
//
//                    String sLat  = VmaUtils.mapLatToString(lat, formatLoc);
//                    String sLon  = VmaUtils.mapLonToString(lon, formatLoc);
//                    position = sLat+ " "+ sLon;
//                }
//                else {
//                    String stop = roomTrack.getStop();
//                    stop = stop.replace("- ","-").replace("-  ","-").replaceAll("°","").replaceAll(" +"," ");
//                    if (stop.startsWith("-")){
//                        String temp = stop.replace("-","").trim();
//                        stop = "-"+temp;
//                    }
//
//                    double lat = Double.parseDouble(stop.split(" ")[0]);
//                    double lon = Double.parseDouble(stop.split(" ")[1]);
//
//                    String sLat  = VmaUtils.mapLatToString(lat, formatLoc);
//                    String sLon  = VmaUtils.mapLonToString(lon, formatLoc);
//                    position = sLat+ " "+ sLon;
//                }
//            }catch (Exception e){
//                Log.e("VMA_TAG","Error "+roomTrack.getStart()+" "+roomTrack.getStop());
//                e.printStackTrace();
//            }
//
//        }



    }

    public void closeDialog(){
        dismiss();
    }


    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }
    public interface OnActionListener{
        void onAction(ActionType actionType, double lon, double lat);
    }


}
