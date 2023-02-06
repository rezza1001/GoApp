package com.vma.goapp.ui.dpi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.database.table.DirectionDB;
import com.vma.goapp.database.table.DpiDB;
import com.vma.goapp.dom.DpiHolder;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.component.option.OptionDialog;
import com.vma.goapp.ui.maps.LocationHolder;
import com.vma.goapp.ui.maps.MainMapActivity;
import com.vma.goapp.ui.master.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class DpiActivity extends MyActivity {

    LinearLayout lnly_empty;
    TextView txvw_update;
    DpiAdapter adapter;

    ArrayList<DpiHolder> listDpi = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_dpi;
    }

    @Override
    protected void initLayout() {
        txvw_update = findViewById(R.id.txvw_update);
        lnly_empty = findViewById(R.id.lnly_empty);

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new DpiAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        listDpi.clear();

        double lon = 0;
        double lat = 0;
        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA));
            lon = gpsLast.getDouble("longitude");
            lat = gpsLast.getDouble("latitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Utility.isNetworkConnected(mActivity)){
            loadFormDB(lon, lat);
            adapter.notifyDataSetChanged();
            return;
        }

        PostManager post = new PostManager(mActivity, ApiConfig.POST_DPI);
        post.addParam("longitude",lon);
        post.addParam("latitude",lat);
        post.addParam("distance",80);
        post.addParam("date",Utility.getDateString(new Date(),"yyyy-MM-dd"));
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                loadData(obj);
            }
            if (listDpi.size() == 0){
                lnly_empty.setVisibility(View.VISIBLE);
            }
            else {
                lnly_empty.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void initListener() {
        adapter.setOnSelectedListener(menu -> {
            LocationHolder holder = new LocationHolder();
            holder.setLongitude(menu.getLongitude());
            holder.setLatitude(menu.getLatitude());
            direct(holder);
        });
    }

    private void loadData(JSONObject obj){
        DpiDB mDPI = new DpiDB();
        mDPI.clearData(mActivity);

        try {
            JSONArray data = obj.getJSONArray("data");
            for (int i=0; i<data.length(); i++){
                JSONObject jo = data.getJSONObject(i);

                DpiHolder holder = new DpiHolder();
                holder.setDate(Utility.getDate(jo.getString("date"),"yyyy-MM-dd"));
                holder.setLatitude(jo.getDouble("latitude"));
                holder.setLongitude(jo.getDouble("longitude"));

                double distance = jo.getDouble("distance") / 1.852;
                holder.setDistance(distance);

                DpiDB db = new DpiDB();
                db.name = jo.getString("date");
                db.date = jo.getString("date");
                db.longitude = jo.getDouble("longitude");
                db.latitude = jo.getDouble("latitude");
                db.insert(mActivity);

                if (i ==0 ){
                    Date date = Utility.getDate(jo.getString("date"),"yyyy-MM-dd");
                    txvw_update.setText(Utility.getDateString(date,"dd MMM yyyy"));
                }
                listDpi.add(holder);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFormDB(double longitude, double latitude){
        DpiDB db = new DpiDB();
        ArrayList<DpiDB> dpiDBS = db.getAll(mActivity);
        for (DpiDB dpidb : dpiDBS){
            DpiHolder holder = new DpiHolder();
            holder.setDate(Utility.getDate(dpidb.date,"yyyy-MM-dd"));
            holder.setLatitude(dpidb.latitude);
            holder.setLongitude(dpidb.longitude);

            DistanceUnit unit = new DistanceUnit(mActivity);
            unit.calcDistance(dpidb.latitude,dpidb.longitude,latitude,longitude);
            holder.setDistance(unit.getNm());

            if (listDpi.size() == 0){
                Date date = Utility.getDate(dpidb.date,"yyyy-MM-dd");
                txvw_update.setText(Utility.getDateString(date,"dd MMM yyyy"));
            }

            listDpi.add(holder);
        }
    }



    private void direct(LocationHolder holder){
        DirectionDB db = new DirectionDB();
        if (db.getAll(mActivity).size() >= 1){
            directionOption(holder.getLongitude(), holder.getLatitude());
            return;
        }
        db.id = db.getNextID(mActivity);
        db.longitude = holder.getLongitude();
        db.latitude = holder.getLatitude();
        db.isFinish = false;
        db.insert(mActivity);
        notifyDirection();
    }

    private void directionOption(double lng, double lat){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addChooser(getResources().getString(R.string.move_location));
        dialog.addChooser(getResources().getString(R.string.add_location));
        dialog.setOnSelectListener((bundle, value) -> {
            DirectionDB db = new DirectionDB();
            if (value.equals(getResources().getString(R.string.move_location))){
                db.clearData(mActivity);
            }
            db.id = db.getNextID(mActivity);
            db.longitude = lng;
            db.latitude = lat;
            db.isFinish = false;
            db.insert(mActivity);
            notifyDirection();
        });
    }

    private void notifyDirection(){
        mActivity.sendBroadcast(new Intent(VmaConstants.NOTIFY_DIRECTION));
        mActivity.startActivity(new Intent(mActivity, MainMapActivity.class));
    }
}
