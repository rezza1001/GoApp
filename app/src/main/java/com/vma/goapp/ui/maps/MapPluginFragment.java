//package com.vma.goapp.ui.maps;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import androidx.core.content.ContextCompat;
//
//import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
//import com.esri.arcgisruntime.data.ShapefileFeatureTable;
//import com.esri.arcgisruntime.geometry.GeometryEngine;
//import com.esri.arcgisruntime.geometry.Point;
//import com.esri.arcgisruntime.geometry.PointCollection;
//import com.esri.arcgisruntime.geometry.Polyline;
//import com.esri.arcgisruntime.geometry.SpatialReferences;
//import com.esri.arcgisruntime.layers.FeatureLayer;
//import com.esri.arcgisruntime.loadable.LoadStatus;
//import com.esri.arcgisruntime.mapping.ArcGISMap;
//import com.esri.arcgisruntime.mapping.LayerList;
//import com.esri.arcgisruntime.mapping.LoadSettings;
//import com.esri.arcgisruntime.mapping.MobileMapPackage;
//import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
//import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
//import com.esri.arcgisruntime.mapping.view.Graphic;
//import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
//import com.esri.arcgisruntime.mapping.view.LocationDisplay;
//import com.esri.arcgisruntime.mapping.view.MapView;
//import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
//import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
//import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
//import com.esri.arcgisruntime.symbology.SimpleRenderer;
//import com.esri.arcgisruntime.symbology.TextSymbol;
//import com.esri.arcgisruntime.util.ListenableList;
//import com.vma.goapp.R;
//import com.vma.goapp.ui.component.Loading;
//import com.vma.goapp.dom.VmaApiConstant;
//import com.vma.goapp.dom.VmaConstants;
//import com.vma.goapp.libs.FileProcessing;
//import com.vma.goapp.libs.LocationConverter;
//import com.vma.goapp.libs.MyFileReader;
//import com.vma.goapp.libs.Utility;
//import com.vma.goapp.libs.VmaPreferences;
//import com.vma.goapp.ui.master.MyFragment;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.Objects;
//
//public class MapPluginFragment extends MyFragment {
//
//    private JSONObject mapStyle = new JSONObject();
//    MapView mapvw_arcgis;
//    RelativeLayout rvly_zoomOut,rvly_zoomIn,rvly_compassAction;
//
//    private MobileMapPackage mapPackage;
//    private LocationDisplay mLocationDisplay;
//    private CoordinateView covw_coordinate;
//    private RecordStatusView rcvw_status;
//    private RecordButtonView rcbvw_record;
//    private DirectionStatusView dsvw_destination;
//    private CenterPointView cnvw_centerPoint;
//    private ImageView imvw_cursor;
//
//    private final double DEFAULT_LAT = -6.6037;
//    private final double DEFAULT_LON = 106.8589;
//    private final double MAX_SCALE = 3000000;
//    private final double MIN_SCALE = 5000;
//
//    private final int [] mMapScale = {5000, 10000, 20000, 40000, 80000,160000,320000,640000,1280000,1500000,2000000};
//
//    private double myLatitude = DEFAULT_LAT;
//    private double myLongitude = DEFAULT_LON;
//
//    private double myBearing = 0;
//    private int scalePosition = 10;
//    private boolean toMyLocation = true;
//    int mNavigationMode = 0;
//    private GraphicsOverlay graphicsOverlay,graphicsOverlayRecordTrack, graphicsOverlayTrackMarker, graphicsOverlayDirectTo;
//
//    private  Point mCurrPoint = new Point( DEFAULT_LON, DEFAULT_LAT, SpatialReferences.getWgs84());
//    PointCollection recordPosition = new PointCollection(SpatialReferences.getWgs84());
//    private SimpleLineSymbol mRecordLine;
//    private PictureMarkerSymbol myPointerMarkerSymbol;
//
//
//    private String mLoadPath = "";
//
//
//    public static MapPluginFragment newInstance() {
//        Bundle args = new Bundle();
//        MapPluginFragment fragment = new MapPluginFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//    @Override
//    protected int setLayout() {
//        return R.layout.fishmap_fragment_map;
//    }
//
//    @Override
//    protected void initLayout(View view) {
//        covw_coordinate = view.findViewById(R.id.covw_coordinate);
//        mapvw_arcgis = view.findViewById(R.id.mapvw_arcgis);
//        rvly_zoomOut = view.findViewById(R.id.rvly_zoomOut);
//        rvly_zoomIn = view.findViewById(R.id.rvly_zoomIn);
//        rvly_compassAction = view.findViewById(R.id.rvly_compassAction);
//        rcvw_status = view.findViewById(R.id.rcvw_status);
//        rcbvw_record = view.findViewById(R.id.rcbvw_record);
//        imvw_cursor = view.findViewById(R.id.imvw_cursor);
//        dsvw_destination = view.findViewById(R.id.dsvw_destination);
//        cnvw_centerPoint = view.findViewById(R.id.cnvw_centerPoint);
//
//        imvw_cursor.setImageLevel(1);
//        rcbvw_record.create();
//        covw_coordinate.create();
//
//
//        boolean settingsCanWrite = Settings.System.canWrite(mActivity);
//        if(!settingsCanWrite) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
//            mActivity.startActivity(intent);
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    protected void initListener() {
//        getmView().findViewById(R.id.rvly_myLocation).setOnClickListener(view -> {
//            toMyLocation = true;
//            myLocation();
//        });
//        rvly_zoomOut.setOnClickListener(view -> zoomOut());
//        rvly_zoomIn.setOnClickListener(view -> zoomIn());
//        rvly_compassAction.setOnClickListener(view -> {
//            if (mNavigationMode == 0){
//                mNavigationMode = 1;
//                imvw_cursor.setImageLevel(0);
//            }
//            else {
//                mNavigationMode = 0;
//                imvw_cursor.setImageLevel(1);
//            }
////            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
////            if (!mLocationDisplay.isStarted())
////                mLocationDisplay.startAsync();
//        });
//
//        rcbvw_record.setOnRecordListener(new RecordButtonView.OnRecordListener() {
//            @Override
//            public void onStart() {
//                clearRecordTrack();
//                rcvw_status.startRecording();
//                toMyLocation = true;
//                myLocation();
//            }
//
//            @Override
//            public void onStop() {
//                rcvw_status.stopRecording();
//            }
//
//            @Override
//            public void onFinish() {
//                clearRecordTrack();
//            }
//        });
//
//
//        mapvw_arcgis.setOnTouchListener(new DefaultMapViewOnTouchListener(mActivity,mapvw_arcgis){
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_MOVE){
//                    toMyLocation = false;
//                    cnvw_centerPoint.setTransparent(false);
//                }
//                return super.onTouch(view, event);
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                super.onLongPress(e);
//                Point   pressPoint = screenToWGS84(e.getX(), e.getY());
//                GeometryEngine.contains(pressPoint,pressPoint);
//                mapvw_arcgis.setViewpointCenterAsync(pressPoint);
//            }
//        });
//
//        cnvw_centerPoint.setOnCenterInfoListener(new CenterPointView.OnCenterInfoListener() {
//            @Override
//            public void onCenterInfo(Point cenPoint) {
//                graphicsOverlayDirectTo.getGraphics().clear();
//            }
//
//            @Override
//            public void onDirect(Point point, double lng, double lat) {
//                showDestinationLine(point);
//            }
//        });
//    }
//
//    @Override
//    protected void initData() {
//        IntentFilter intentFilter =  new IntentFilter(VmaConstants.SHOW_TRACK_MAP);
//        intentFilter.addAction(VmaConstants.VMA_GPS);
//        mActivity.registerReceiver(receiver,intentFilter);
//
//        Loading.showLoading(mActivity,"Load Maps");
//        new Handler().postDelayed(() -> mActivity.runOnUiThread(() -> {
//            initMap();
//            InitializeGISMap();
//        }),500);
//    }
//
//    protected void onReceiveMessageFormGps(String data) {
//        String prefData = VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA);
//        if (data.isEmpty()){
//            covw_coordinate.setSpeed(0);
//            return;
//        }
//        try {
//            JSONObject jo = new JSONObject(prefData);
//            if (jo.has(VmaApiConstant.GPS_ITEM_SNR)){
//                covw_coordinate.setSpeed(0);
//                return;
//            }
//            LocationConverter converter = new LocationConverter(jo.getString("longitude"),jo.getString("latitude"));
//            myLatitude = converter.getLatitude();
//            myLongitude = converter.getLongitude();
//            covw_coordinate.setLocation(converter);
//            double speed = jo.getDouble(VmaApiConstant.GPS_ITEM_SPEED);
//            myBearing = jo.getDouble(VmaApiConstant.GPS_ITEM_BEARING);
//            covw_coordinate.setSpeed(speed);
//
//            mCurrPoint = new Point( myLongitude, myLatitude, SpatialReferences.getWgs84());
//            //  Display Current Position
//            Graphic currGraphic = new Graphic(mCurrPoint, myPointerMarkerSymbol);
//            graphicsOverlay.getGraphics().set(0, currGraphic);
////            mapvw_arcgis.setViewpointCenterAsync(mCurrPoint);
//
//            setBearing();
//
//            boolean isRecording = VmaPreferences.getInt(mActivity, VmaConstants.TRACKING_RECORD) == 1;
//            if (isRecording){
//                recordPosition.add(mCurrPoint);
//                mActivity.runOnUiThread(this::showLineTrack);
//            }
//            else {
//                myLocation();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void initMap(){
//
//        Context context = mActivity.getApplicationContext();
//        boolean settingsCanWrite = Settings.System.canWrite(context);
//        if(!settingsCanWrite) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
//            mActivity.startActivity(intent);
//        }
//        try {
//            String licenseKey = "runtimelite,1000,rud8679770455,none,ZZ0RJAY3FLJ1H2RGJ062";
//            ArcGISRuntimeEnvironment.setLicense(licenseKey);
////            ArcGISRuntimeEnvironment.setApiKey("AAPKf70861c704834410b5da65dbc0c7763daZIywGZ2Rgvywl0Ea_4tpT1aMGxApT2NKZMEBq2VPivNcogB6Aj6-FY5Pm7Ty9Jb");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        String pathMap = FileProcessing.getMainPath(mActivity)+"/"+FileProcessing.ROOT+"/map.mmpk";
//        File fileMap = new File(pathMap);
//        mapPackage = new MobileMapPackage(fileMap.getAbsolutePath());
//        mapPackage.loadAsync();
//        mapPackage.addDoneLoadingListener(() -> {
//            Loading.cancelLoading();
//            if (mapPackage.getLoadStatus() == LoadStatus.LOADED && !mapPackage.getMaps().isEmpty()) {
//                ArcGISMap map= mapPackage.getMaps().get(0);
//                //  map view setting
//                map.setMinScale(MAX_SCALE);
//                map.setMaxScale(MIN_SCALE);
//
//                LoadSettings loadSettings = new LoadSettings();
//                loadSettings.setPreferredPointFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
//                loadSettings.setPreferredPolygonFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
//                loadSettings.setPreferredPolylineFeatureRenderingMode(FeatureLayer.RenderingMode.AUTOMATIC);
//                map.setLoadSettings(loadSettings);
//                mapvw_arcgis.setMap(map);
//
//                mLocationDisplay = mapvw_arcgis.getLocationDisplay();
//                //  Load Feature Layers
//                LayerList layerList = map.getOperationalLayers();
//                loadFetureLayers(layerList);
//
//
//                myLocation();
//                updateUIZoom();
//                if (!mLoadPath.isEmpty()){
//                    showTrackRecorded();
//                }
//                mapvw_arcgis.setViewpointCenterAsync(mCurrPoint);
//            }
//        });
//    }
//
//    private void myLocation(){
//        if (!toMyLocation){
//            return;
//        }
//        mapvw_arcgis.setViewpointCenterAsync(mCurrPoint);
//    }
//
//    private void zoomIn(){
//        if (scalePosition == 0){
//            return;
//        }
//        scalePosition --;
//        double scale  = mMapScale[scalePosition];
//        mapvw_arcgis.setViewpointScaleAsync(scale);
//        updateUIZoom();
//    }
//
//    private void zoomOut(){
//        if (scalePosition == mMapScale.length -1){
//            return;
//        }
//        scalePosition ++;
//        double scale  = mMapScale[scalePosition];
//        mapvw_arcgis.setViewpointScaleAsync(scale);
//        updateUIZoom();
//    }
//
//    private void updateUIZoom(){
//        if (scalePosition == (mMapScale.length -1) ){
//            rvly_zoomOut.setBackgroundResource(R.drawable.zoom_out_disable);
//        }
//        else {
//            rvly_zoomOut.setBackgroundResource(R.drawable.zoom_out_action);
//        }
//
//        if (scalePosition == 0){
//            rvly_zoomIn.setBackgroundResource(R.drawable.zoom_in_disable);
//        }
//        else {
//            rvly_zoomIn.setBackgroundResource(R.drawable.zoom_in_action);
//        }
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapvw_arcgis.pause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapvw_arcgis.resume();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapvw_arcgis.dispose();
//        try {
//            mActivity.unregisterReceiver(receiver);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(VmaConstants.SHOW_TRACK_MAP)){
//                mLoadPath = intent.getStringExtra("path");
//            }
//            if (intent.getAction().equals(VmaConstants.VMA_GPS)){
//                String gpsData = intent.getStringExtra(VmaConstants.SERVICE_DATA);
//                onReceiveMessageFormGps(gpsData);
//            }
//        }
//    };
//
//
//    private void showTrackRecorded(){
//        MyFileReader fileReader = new MyFileReader(mActivity);
//        fileReader.setOnReadListener(new MyFileReader.OnReadListener() {
//            @Override
//            public void onLiveRead(String data) {
//                try {
//                    JSONObject joStart = new JSONObject(data);
//                    double longitude = joStart.getDouble("longitude");
//                    double latitude = joStart.getDouble("latitude");
//                    Point mPoint = new Point( longitude, latitude, SpatialReferences.getWgs84());
//                    recordPosition.add(mPoint) ;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFinish(String start, String data, String end) {
//                try {
//                    graphicsOverlayTrackMarker   = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
//                    mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayTrackMarker);
//
//                    JSONObject joStart = new JSONObject(data);
//                    double longStart= joStart.getDouble("longitude");
//                    double latStart= joStart.getDouble("latitude");
//
//                    Point startPoint = new Point( longStart, latStart, SpatialReferences.getWgs84());
//                    Graphic startGraphic = new Graphic(startPoint, getWayPointMarker(R.drawable.track_start));
//                    graphicsOverlayTrackMarker.getGraphics().add(startGraphic);
//
//                    JSONObject joEnd = new JSONObject(end);
//                    double longEnd= joEnd.getDouble("longitude");
//                    double latEnd= joEnd.getDouble("latitude");
//
//                    Point endPoint = new Point( longEnd, latEnd, SpatialReferences.getWgs84());
//                    Graphic endGraphic = new Graphic(endPoint, getWayPointMarker(R.drawable.track_end));
//                    graphicsOverlayTrackMarker.getGraphics().add(endGraphic);
//
//                    mLoadPath = "";
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                showLineTrack();
//            }
//        });
//        fileReader.readFile(mLoadPath);
//    }
//
//    private void loadFetureLayers(LayerList layerList) {
//        String pluginDir = FileProcessing.getMainPath(mActivity)+"/" + FileProcessing.ROOT + "/plugin/";
//        getRootFile(pluginDir,layerList);
//    }
//
//    private void getRootFile(String path,LayerList layerList )  {
//        File file = new File(path);
//        if (file.list() == null){
//            Log.e(TAG,"EMPTY "+path);
//            return ;
//        }
//        String pathStyle = file+"/style.json";
//        File style = new File(pathStyle);
//        if (style.exists()){
//            try {
//                mapStyle =  new JSONObject(Objects.requireNonNull(Utility.getStringFromFile(style.getAbsolutePath())));
//                Log.d(TAG,"FOUND STYLE "+ mapStyle);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (String sPlugin : Objects.requireNonNull(file.list())){
//            sPlugin = file.getAbsolutePath()+"/"+sPlugin;
//            File folder = new File(sPlugin);
//
//            if (folder.list() == null){
//                String[] splitter  = folder.getName().split("\\.");
//                try {
//                    if (splitter.length != 2 || !splitter[1].equals("shp")){
//                        continue;
//                    }
//
//                    Log.d(TAG,"Load "+folder.getAbsolutePath()+" --> "+ mapStyle);
//                    String type = mapStyle.getString("type");
//                    if (type.equals("polygon")){
//                        String color1 = mapStyle.getString("color1");
//                        String color2 = mapStyle.getString("color2");
//                        float size = (float) mapStyle.getDouble("size");
//                        String filePath = folder.getAbsolutePath();
//                        FeatureLayer featureLayer = loadPolygon(filePath, Color.parseColor(color1), Color.parseColor(color2), size);
//                        if(featureLayer != null)
//                            layerList.add(featureLayer);
//                    }
//                    else {
//                        String color = mapStyle.getString("color");
//                        String filePath = folder.getAbsolutePath();
//                        String name = new File(filePath).getName().replaceAll(".shp","m");
//                        FeatureLayer featureLayer = loadLabel(filePath, name, Color.parseColor(color));
//                        if(featureLayer != null)
//                            layerList.add(featureLayer);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            else {
//                Log.d(TAG,"Load Folder "+sPlugin);
//                getRootFile(sPlugin,layerList);
//            }
//
//        }
//    }
//
//    private FeatureLayer loadPolygon(String path, int lineColor, int fillColor, float width){
//        try {
//            Log.d(TAG,"loadPolygon "+path);
//            ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);
//            FeatureLayer shapefileFeatureLayer = new FeatureLayer(shapefileFeatureTable);
//            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, lineColor, width);
//            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, fillColor, lineSymbol);
//            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
//            shapefileFeatureLayer.setRenderer(renderer);
//            return shapefileFeatureLayer;
//        } catch (Exception ep) {
//            Log.d(TAG,"Error "+ ep.getMessage());
//            ep.printStackTrace();
//            return null;
//        }
//    }
//
//    private FeatureLayer loadLabel(String path, String m, int color){
//        try {
//            Log.d(TAG,"loadLabel "+path);
//            ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path);
//            FeatureLayer shapefileFeatureLayer = new FeatureLayer(shapefileFeatureTable);
//            TextSymbol depth = new TextSymbol( 10, m, color, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
//            SimpleRenderer renderer = new SimpleRenderer(depth);
//            shapefileFeatureLayer.setRenderer(renderer);
//            return shapefileFeatureLayer;
//        } catch (Exception ep) {
//            Log.e(TAG,"loadLabel "+ ep.getMessage());
//            ep.printStackTrace();
//            return null;
//        }
//    }
//
//    private void InitializeGISMap() {
//        BackgroundGrid mainBackgroundGrid = new BackgroundGrid();
//        mainBackgroundGrid.setColor(Color.parseColor("#D2EFF8"));
//        mainBackgroundGrid.setGridLineColor(0xffffffff);
//        mainBackgroundGrid.setGridLineWidth(0);
//        mapvw_arcgis.setBackgroundGrid(mainBackgroundGrid);
//
//        createSymbolPosition();
//        graphicsOverlay = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
//        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlay);
//        Graphic currGraphic = new Graphic(mCurrPoint, myPointerMarkerSymbol);
//        graphicsOverlay.getGraphics().add(currGraphic);
//
//        //  Recording Track Overlay
//        graphicsOverlayRecordTrack = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
//        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayRecordTrack);
//
//        //  Direction Overlay
//        graphicsOverlayDirectTo = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
//        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayDirectTo);
//
//        //  Record Track Line
//        mRecordLine  = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.BLUE, 3);
//
//        //  set map for center view
//        cnvw_centerPoint.setMap(mapvw_arcgis);
//
//    }
//
//    private void showLineTrack(){
//        Polyline polyline       = new Polyline(recordPosition);
//        Graphic graphicRoute    = new Graphic(polyline, mRecordLine);
//
//        ListenableList<Graphic> graphicList = graphicsOverlayRecordTrack.getGraphics();
//        graphicList.clear();
//        graphicList.add(graphicRoute);
//        myLocation();
//    }
//
//    private void showDestinationLine(Point toPoint){
//        toMyLocation = true;
//        SimpleLineSymbol   line  = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.BLUE, 2);
//        PointCollection pointCollection = new PointCollection(SpatialReferences.getWgs84());
//        pointCollection.add(mCurrPoint);
//        pointCollection.add(toPoint);
//        Polyline polyline       = new Polyline(pointCollection);
//        Graphic graphicRoute    = new Graphic(polyline, line);
//
//        ListenableList<Graphic> graphicList = graphicsOverlayDirectTo.getGraphics();
//        graphicList.clear();
//        graphicList.add(graphicRoute);
//        myLocation();
//    }
//
//    private void createSymbolPosition(){
//        BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, R.drawable.ic_cursor_map);
//        assert pinStarBlueDrawable != null;
//        myPointerMarkerSymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
//
//        myPointerMarkerSymbol.setHeight(40);
//        myPointerMarkerSymbol.setWidth(40);
//        myPointerMarkerSymbol.setOffsetY(0);
//        myPointerMarkerSymbol.loadAsync();
//    }
//
//    private void setBearing(){
//        if (myPointerMarkerSymbol == null){
//            return;
//        }
//
//        if(mNavigationMode == 1){
//            //  Rotate Map
//            mapvw_arcgis.setViewpointRotationAsync(myBearing);
//            myPointerMarkerSymbol.setAngle((float)myBearing);
//        } else {
//            myPointerMarkerSymbol.setAngle((float)myBearing);
//            mapvw_arcgis.setViewpointRotationAsync(0.0);
//        }
//    }
//
//    private void clearRecordTrack(){
//        if (recordPosition != null){
//            recordPosition.clear();
//        }
//        if (graphicsOverlayRecordTrack != null){
//
//            graphicsOverlayRecordTrack.getGraphics().clear();
//        }
//        if (graphicsOverlayTrackMarker != null){
//            graphicsOverlayTrackMarker.getGraphics().clear();
//        }
//    }
//
//    private PictureMarkerSymbol getWayPointMarker(int resource){
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, resource);
//        PictureMarkerSymbol mSymbolTargetPos = new PictureMarkerSymbol(bitmapDrawable);
//        mSymbolTargetPos.setHeight(24);
//        mSymbolTargetPos.setWidth(24);
//        mSymbolTargetPos.loadAsync();
//        return mSymbolTargetPos;
//    }
//
//    private Point screenToWGS84(float x, float y){
//        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(x), Math.round(y));
//        Point mapPoint = mapvw_arcgis.screenToLocation(screenPoint);
//        return (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
//    }
//
//}
