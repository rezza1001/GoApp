package com.vma.goapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.LoadSettings;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.vma.goapp.R;
import com.vma.goapp.database.table.TrackDB;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.MyFileReader;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.service.TrackRecordService;
import com.vma.goapp.ui.component.Loading;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.component.VmaEditext;
import com.vma.goapp.ui.master.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveTrackActivity extends MyActivity {

    private MapView mapvw_arcgis;
    private final double MAX_SCALE = 3000000;
    private final double MIN_SCALE = 5000;
    private final int [] mMapScale = {5000, 10000, 20000, 40000, 80000,160000,320000,640000,1280000,1500000,2000000,3000000};

    private VmaEditext edtx_name;
    private TextView txvw_date,txvw_startTime,txvw_speed,txvw_time,txvw_endTime,txvw_distance;
    private VmaButton bbtn_save,bbtn_cancel;
    private  String mStartPosition = "";
    private  String mEndPosition = "";
    private Date startDate;
    private Date endDate;
    private float calSpeed = 0;
    ArrayList<Integer> listSpeed = new ArrayList<>();
    PointCollection recordPosition = new PointCollection(SpatialReferences.getWgs84());
    private SimpleLineSymbol mRecordLine;
    private GraphicsOverlay graphicsOverlayRecordTrack,graphicsOverlayTrackMarker;

    private TrackDB dbExisting;

    @Override
    protected int setLayout() {
        return R.layout.fishmap_activity_save_track;
    }

    @Override
    protected void initLayout() {

        mapvw_arcgis = findViewById(R.id.map_static);

        edtx_name = findViewById(R.id.edtx_name);
        txvw_date = findViewById(R.id.txvw_no);
        bbtn_save = findViewById(R.id.bbtn_save);
        bbtn_cancel = findViewById(R.id.bbtn_cancel);
        txvw_startTime = findViewById(R.id.txvw_startTime);
        txvw_speed = findViewById(R.id.txvw_speed);
        txvw_time = findViewById(R.id.txvw_time);
        txvw_endTime = findViewById(R.id.txvw_endTime);
        txvw_distance = findViewById(R.id.txvw_distance);
        String trackName = getResources().getString(R.string.track_name);
        edtx_name.create(trackName,trackName);
        edtx_name.setRightIcon(R.drawable.ic_baseline_edit_24, Color.parseColor("#00B9FF"));

        txvw_date.setText(Utility.getDateString(new Date(),"EEEE, dd MMMM yyyy"));
        bbtn_save.create(getResources().getString(R.string.save),0);
        bbtn_cancel.create(getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        //  Recording Track Overlay
        graphicsOverlayRecordTrack = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayRecordTrack);
        //  Recording Track Overlay
        graphicsOverlayTrackMarker = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayTrackMarker);

        //  Record Track Line
        mRecordLine  = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#aa00ff"), 2);


    }

    @Override
    protected void initData() {
        loadFileTrack();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.rvly_back).setOnClickListener(view -> onBackPressed());
        bbtn_save.setOnActionListener(view -> save());
        bbtn_cancel.setOnActionListener(view -> mActivity.finish());
    }

    private void save(){
        if (edtx_name.getValue().isEmpty()){
            Utility.showToastError(mActivity,"Please input Track Name");
            return;
        }
        if (dbExisting != null){ // Update
            dbExisting.name  = edtx_name.getValue();
            dbExisting.update(mActivity);
            notifyUI();
            return;
        }
        String fromPath = FileProcessing.getRootPath(mActivity)+ TrackRecordService.folderName+"/"+TrackRecordService.fileName;
        String toPath = FileProcessing.getRootPath(mActivity) + TrackRecordService.folderName+"/"+System.currentTimeMillis()+".txt";
        try {
            FileProcessing.Copy(new File(fromPath), new File(toPath));
            TrackDB db = new TrackDB();
            db.name          = edtx_name.getValue();
            db.startTime     = Utility.getDateString(startDate,"dd-MM-yyyy HH:mm:ss");
            db.endTime       = Utility.getDateString(endDate,"dd-MM-yyyy HH:mm:ss");
            db.startPosition = mStartPosition;
            db.endPosition   = mEndPosition;
            db.speed         = calSpeed;
            db.distance      = VmaPreferences.getFloat(mActivity, VmaConstants.TRACKING_DISTANCE);
            db.filePath      = toPath;

            db.insert(mActivity);
            notifyUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyUI(){
        Utility.showToastSuccess(mActivity,"Success");
        Intent intent = new Intent(VmaConstants.TRACK_RECORD_SAVE);
        intent.putExtra("status",0);
        sendBroadcast(intent);

        mActivity.finish();
    }

    private void loadFileTrack(){
        String recordPath = FileProcessing.getRootPath(mActivity)+TrackRecordService.folderName+"/"+TrackRecordService.fileName;
        int id =  getIntent().getIntExtra("id",0);
        if (id > 0){
            dbExisting = new TrackDB();
            dbExisting.getData(mActivity, id);
            recordPath = dbExisting.filePath;
            VmaPreferences.save(mActivity, VmaConstants.TRACKING_DISTANCE, dbExisting.distance);
            edtx_name.setValue(dbExisting.name);
        }

        MyFileReader fileReader = new MyFileReader(mActivity);
        fileReader.setOnReadListener(new MyFileReader.OnReadListener() {
            @Override
            public void onLiveRead(String data) {
                try {
                    JSONObject jo = new JSONObject(data);
                    listSpeed.add(jo.getInt("speed"));
                    LocationConverter converter = new LocationConverter(mActivity,jo.getString("longitude"),jo.getString("latitude"));
                    recordPosition.add(converter.getPoint());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish(String start, String data, String end) {
                mEndPosition = end;
                mStartPosition = start;

                Log.d(TAG,"mStartPosition "+mStartPosition);

                try {
                    JSONObject joStart = new JSONObject(start);
                    startDate = Utility.getDate(joStart.getString("date"),"dd-MM-yyyy HH:mm:ss"); // 11-05-2022 10:07:53

                    JSONObject joEnd = new JSONObject(end);
                    endDate = Utility.getDate(joEnd.getString("date"),"dd-MM-yyyy HH:mm:ss");

                    long difference = endDate.getTime() - startDate.getTime();
                    long seconds = (difference / 1000) % 60;
                    long minutes = (difference / (1000 * 60)) % 60;
                    long hours = (difference / (1000 * 60 * 60)) % 24;
                    String totalTime = hours+" : "+minutes+" : "+ seconds;
                    txvw_time.setText(totalTime);

                    for (Integer speed : listSpeed){
                        calSpeed = calSpeed + speed;
                    }
                    calSpeed = calSpeed/listSpeed.size();
                    DecimalFormat format = new DecimalFormat("0.00");
                    txvw_speed.setText(format.format(calSpeed));

                    txvw_startTime.setText(Utility.getDateString(startDate,"HH:mm:ss"));
                    txvw_endTime.setText(Utility.getDateString(endDate,"HH:mm:ss"));
                    txvw_distance.setText(VmaPreferences.getFloat(mActivity,VmaConstants.TRACKING_DISTANCE)+" NM");

                    initMap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        fileReader.readFile(recordPath);
    }

    private void initMap(){
        try {
            String licenseKey = "runtimelite,1000,rud8679770455,none,ZZ0RJAY3FLJ1H2RGJ062";
            ArcGISRuntimeEnvironment.setLicense(licenseKey);
        }catch (Exception e){
            e.printStackTrace();
        }

        File f = new File(mActivity.getCacheDir()+ "/"+VmaConstants.MAPS_CONFIG);

        MobileMapPackage  mapPackage = new MobileMapPackage(f.getAbsolutePath());
        mapPackage.loadAsync();
        mapPackage.addDoneLoadingListener(() -> {
            Loading.cancelLoading();
            if (mapPackage.getLoadStatus() == LoadStatus.LOADED && !mapPackage.getMaps().isEmpty()) {
                ArcGISMap map= mapPackage.getMaps().get(0);
                //  map view setting
                map.setMinScale(MAX_SCALE);
                map.setMaxScale(MIN_SCALE);

                LoadSettings loadSettings = new LoadSettings();
                loadSettings.setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
                loadSettings.setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
                loadSettings.setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
                map.setLoadSettings(loadSettings);
                mapvw_arcgis.setMap(map);

                showLineTrack();
                showMarker();

                new Handler().postDelayed(() -> {
                    try {
                        JSONObject   jo = new JSONObject(mStartPosition);
                        LocationConverter converter = new LocationConverter(mActivity,jo.getString("longitude"),jo.getString("latitude"));
                        setZoom();
                        mapvw_arcgis.setViewpointCenterAsync(converter.getPoint());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },500);

            }
        });
    }

    private void showLineTrack(){
        Polyline polyline       = new Polyline(recordPosition);
        Graphic graphicRoute    = new Graphic(polyline, mRecordLine);

        ListenableList<Graphic> graphicList = graphicsOverlayRecordTrack.getGraphics();
        graphicList.clear();
        graphicList.add(graphicRoute);
    }

    private void showMarker(){

        try {
            JSONObject joStart = new JSONObject(mStartPosition);
            LocationConverter convStart = new LocationConverter(mActivity, joStart.getString("longitude"),joStart.getString("latitude"));

            Graphic startGraphic = new Graphic(convStart.getPoint(), getWayPointMarker(R.drawable.track_start));
            graphicsOverlayTrackMarker.getGraphics().add(startGraphic);

            JSONObject joEnd = new JSONObject(mEndPosition);
            LocationConverter convEnd = new LocationConverter(mActivity, joEnd.getString("longitude"),joEnd.getString("latitude"));
            Graphic endGraphic = new Graphic(convEnd.getPoint(), getWayPointMarker(R.drawable.track_end));
            graphicsOverlayTrackMarker.getGraphics().add(endGraphic);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private PictureMarkerSymbol getWayPointMarker(int resource){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, resource);
        PictureMarkerSymbol mSymbolTargetPos = new PictureMarkerSymbol(bitmapDrawable);
        mSymbolTargetPos.setHeight(24);
        mSymbolTargetPos.setWidth(24);
        mSymbolTargetPos.loadAsync();
        return mSymbolTargetPos;
    }

    private void setZoom(){
        float distance = VmaPreferences.getFloat(mActivity,VmaConstants.TRACKING_DISTANCE);
        int zoom = 10;
        if (distance <= 2){
            zoom = 0;
        }
        else if (distance <= 4){
            zoom = 2;
        }
        else if (distance <= 6){
            zoom = 3;
        }
        else if (distance <= 8){
            zoom = 4;
        }
        else if (distance <= 10){
            zoom = 5;
        }
        else if (distance <= 12){
            zoom = 6;
        }
        else if (distance <= 15){
            zoom = 7;
        }
        else if (distance <= 20){
            zoom = 8;
        }

        long scele = mMapScale[zoom];
        Log.d(TAG,"SCELE "+zoom+" = "+ scele);
        mapvw_arcgis.setViewpointScaleAsync(scele);
    }


}
