package com.vma.goapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.vma.goapp.R;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.SpeedUnit;
import com.vma.goapp.ui.master.MyView;

public class CoordinateView extends MyView {

    private TextView txvw_longitude,txvw_latitude,txvw_speed,txvw_bearing,txvw_speedUnit,txvw_latUnt,txvw_lonUnt;

    private SpeedUnit speedUnit;
    public CoordinateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_coordinate;
    }

    @Override
    protected void initLayout() {
        txvw_longitude = findViewById(R.id.txvw_longitude);
        txvw_latitude = findViewById(R.id.txvw_latitude);
        txvw_speed = findViewById(R.id.txvw_speed);
        txvw_bearing = findViewById(R.id.txvw_bearing);
        txvw_speedUnit = findViewById(R.id.txvw_speedUnit);
        txvw_latUnt = findViewById(R.id.txvw_latUnt);
        txvw_lonUnt = findViewById(R.id.txvw_lonUnt);
    }

    public void create(){
        super.create();
        speedUnit = new SpeedUnit(mActivity);
        CardView card_coordinate = findViewById(R.id.card_coordinate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            card_coordinate.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primaryDark));
        }
        setBearing(0);
    }

    @Override
    protected void initListener() {

    }

    public void setLocation(String longitude, String latitude){
        LocationConverter locationConverter = new LocationConverter(mActivity, longitude, latitude);
        txvw_longitude.setText(locationConverter.getLongitudeDisplay());
        txvw_latitude.setText(locationConverter.getLatitudeDisplay());
    }

    public void setLocation(LocationConverter location){
        txvw_longitude.setText(location.getLongitudeDisplay());
        txvw_latitude.setText(location.getLatitudeDisplay());
        txvw_lonUnt.setText(location.getUnitLon());
        txvw_latUnt.setText(location.getUnitLat());
    }

    @SuppressLint("SetTextI18n")
    public void setSpeed(double speed){
        speedUnit.setSpeed((int) speed);
        txvw_speed.setText(""+speedUnit.getSpeed());
        txvw_speedUnit.setText(speedUnit.getUnitSpeed());
    }

    public void setBearing(float bearing){
        txvw_bearing.setText(String.valueOf(bearing));
    }
}
