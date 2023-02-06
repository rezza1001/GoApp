package com.vma.goapp.ui.maps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.vma.goapp.R;
import com.vma.goapp.database.table.DirectionDB;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.VmaUtils;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.master.MyView;

import java.util.ArrayList;
import java.util.Locale;

public class DirectionStatusView extends MyView {

    private TextView txvw_dirTo,txvw_directiondistance,txvw_distanceUnit,txvw_dstBearing;
    private MaterialRippleLayout mry_stopDir;

    private Point mPoint;
    private DirectionDB directionDB;
    private ArrayList<DirectionDB> listDirection = new ArrayList<>();

    private Point mCurrPoint;
    private float bearing =0;
    private boolean isDirection= false;

    private GraphicsOverlay overLayDirection,graphicsOverlayStreet;
    private OnDirectionListener onDirectionListener;
    private PictureMarkerSymbol mSymbolTargetPos, mFinishSymbol;
    private SimpleLineSymbol mRouteLine, mStreetLine;
    private FinishDirectionDialog finishInfoDialog;

    public DirectionStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_direction_status;
    }

    @Override
    protected void initLayout() {
        setVisibility(GONE);

        txvw_dirTo = findViewById(R.id.txvw_dirTo);
        txvw_directiondistance = findViewById(R.id.txvw_directiondistance);
        txvw_distanceUnit = findViewById(R.id.txvw_distanceUnit);
        txvw_dstBearing = findViewById(R.id.txvw_dstBearing);
        mry_stopDir = findViewById(R.id.mry_stopDir);

    }

    @Override
    protected void initListener() {
        mry_stopDir.setOnClickListener(view -> {
            ConfirmDialog dialog = new ConfirmDialog(mActivity);
            dialog.show("Anda akan membatalkan tujuan anda?");
            dialog.setOnActionListener(confirm -> {
                if (confirm){
                    listDirection.clear();
                    directionDB.clearData(mActivity);
                    load();
                }
            });
        });
    }

    public void create(MapView map) {
        super.create();

        overLayDirection = new GraphicsOverlay(GraphicsOverlay.RenderingMode.STATIC);
        map.getGraphicsOverlays().add(overLayDirection);

        graphicsOverlayStreet = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        map.getGraphicsOverlays().add(graphicsOverlayStreet);

        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, R.drawable.flag_center);
            assert bitmapDrawable != null;
            mSymbolTargetPos = new PictureMarkerSymbol(bitmapDrawable);
            mSymbolTargetPos.setHeight(28);
            mSymbolTargetPos.setWidth(28);
            mSymbolTargetPos.setOffsetY(15);
            mSymbolTargetPos.loadAsync();
        }
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, R.drawable.flag_finish);
            assert bitmapDrawable != null;
            mFinishSymbol = new PictureMarkerSymbol(bitmapDrawable);
            mFinishSymbol.setHeight(28);
            mFinishSymbol.setWidth(28);
            mFinishSymbol.setOffsetY(15);
            mFinishSymbol.loadAsync();
        }

        finishInfoDialog = new FinishDirectionDialog(mActivity);

        load();
    }

    private void load(){
        listDirection.clear();
        directionDB = new DirectionDB();
        listDirection.addAll(directionDB.getAll(mActivity));
        overLayDirection.getGraphics().clear();
        graphicsOverlayStreet.getGraphics().clear();

        if (listDirection.size() > 0){
            isDirection = true;
            show();
        }
        else {
            isDirection = false;
            hide();
        }
    }

    public void show(){
        setVisibility(VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(mActivity,R.anim.zoom_in));

        DirectionDB db = new DirectionDB();
        db = db.getCurrentDir(mActivity);

        if (db == null){
            return;
        }

        LocationConverter converter = new LocationConverter(mActivity, db.longitude,db.latitude);
        mPoint = converter.getPoint();

        String strLat = converter.getLatitudeDisplay();
        String strLon = converter.getLongitudeDisplay();
        String strPos = String.format(Locale.ENGLISH, "%s\n%s", strLat, strLon );
        txvw_dirTo.setText(strPos);

        if (onDirectionListener != null){
            onDirectionListener.onDirection(getPoint());
        }

        mStreetLine  = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.parseColor("#aa00ff"), 2);
        mRouteLine  = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.BLUE, 2);

//        onReceiveGPS(converter);
        setStreetRoot();
    }

    public void hide(){
        overLayDirection.getGraphics().clear();
        graphicsOverlayStreet.getGraphics().clear();

        txvw_directiondistance.setText(null);
        txvw_dstBearing.setText(null);
        txvw_distanceUnit.setText(null);
        txvw_dirTo.setText(null);
        setVisibility(GONE);
    }

    public void onReceiveGPS(LocationConverter converter){
        mCurrPoint = converter.getPoint();
        if (listDirection.size() == 0){
            return;
        }

        if (onDirectionListener != null){
            onDirectionListener.onLive(getPoint());
        }

        for (DirectionDB valDB : listDirection){
            LocationConverter locDestConverter = new LocationConverter(mActivity,valDB.longitude,valDB.latitude);
            if (!valDB.isFinish){
                mPoint = locDestConverter.getPoint();
                break;
            }
        }
        DistanceUnit distanceUnit = new DistanceUnit(mActivity);
        distanceUnit.calcDistance(converter.getPoint(),getPoint());
        double distance = distanceUnit.getDistance();

        txvw_distanceUnit.setText(distanceUnit.getUnit());
        if (distance >= 100){
            distance = Math.round(distance);
            String dist = String.format(Locale.ENGLISH, "%.0f", distance);
            String dist2 = dist.replace('.', ',');
            txvw_directiondistance.setText(dist2);
        }
        else {
            String dist = String.format(Locale.ENGLISH, "%.2f", distance);
            String dist2 = dist.replace('.', ',');
            txvw_directiondistance.setText(dist2);
        }

        double bearing = VmaUtils.bearing(converter.getPoint().getY(), converter.getPoint().getX(), getPoint().getY(), getPoint().getX());
        String bear = String.format(Locale.ENGLISH, "%d°", (int)bearing);
        txvw_dstBearing.setText(bear);
        this.bearing = (float) bearing;


        ListenableList<Graphic> graphicList = overLayDirection.getGraphics();
        graphicList.clear();

        for (DirectionDB holder : listDirection) {
            LocationConverter locDestConverter = new LocationConverter(mActivity, holder.longitude,holder.latitude);
            Graphic destGraphic = new Graphic(locDestConverter.getPoint(), mSymbolTargetPos);
            if (holder.isFinish){
                destGraphic = new Graphic(locDestConverter.getPoint(), mFinishSymbol);
            }
            graphicList.add(destGraphic);
        }

        //  Draw Line
        PointCollection pointCollection = new PointCollection(VmaUtils.WGS84);
        pointCollection.add(converter.getPoint());
        pointCollection.add(getPoint());

        Polyline polyline       = new Polyline(pointCollection);
        Graphic graphicRoute    = new Graphic(polyline, mRouteLine);

        graphicList.add(graphicRoute);
        checkDirectionStatus();
    }

    private void setStreetRoot(){
        ListenableList<Graphic> graphicList = graphicsOverlayStreet.getGraphics();
        graphicList.clear();
        if (listDirection.size() == 1){
            return;
        }

        PointCollection pointCollection = new PointCollection(VmaUtils.WGS84);

        for (DirectionDB directionHolder : listDirection){
            if (!directionHolder.isFinish){
                LocationConverter locDestConverter = new LocationConverter(mActivity, directionHolder.longitude,directionHolder.latitude);
                pointCollection.add(locDestConverter.getPoint());
            }
        }

        Polyline polyline       = new Polyline(pointCollection);
        Graphic graphicRoute    = new Graphic(polyline, mStreetLine);
        graphicList.add(graphicRoute);
    }

    private void checkDirectionStatus(){
        Point destPoint = getPoint();
        int index = 0;
        int mID = 0;
        for (DirectionDB directionHolder : listDirection){
            if (!directionHolder.isFinish){
                LocationConverter locDestConverter = new LocationConverter(mActivity, directionHolder.longitude,directionHolder.latitude);
                destPoint = locDestConverter.getPoint();
                mID = directionHolder.id;
                break;
            }
            if (listDirection.size() > (index+1)){
                index ++;
            }
        }
        DistanceUnit distanceUnit = new DistanceUnit(mActivity);
        distanceUnit.calcDistance(destPoint, mCurrPoint);
        double distanceMetre = distanceUnit.getNm() * 1852;
        if (distanceMetre <= 50 && listDirection.size() > 0){
            listDirection.get(index).isFinish = true;
            directionDB.setFinishById(mActivity, mID);
            setStreetRoot(); // Rebuild Street line
            if (index == listDirection.size()-1){
                new Handler().postDelayed(() -> {
                    if (!finishInfoDialog.isShowing() ){
                        finishInfoDialog.show();
                    }
                    directionDB.clearData(mActivity);
                },500);
            }
        }
    }


    public void setDirection(){
        load();
    }


    public Point getPoint() {
        return mPoint;
    }

    public float getBearing(){
        String bear = String.format(Locale.ENGLISH, "%d", (int)bearing);
        return Float.parseFloat(bear);
    }

    public boolean isDirection() {
        return isDirection;
    }

    public void setOnDirectionListener(OnDirectionListener  onDirectionListener){
        this.onDirectionListener = onDirectionListener;
    }

    public interface OnDirectionListener{
        void onDirection(Point point);
        void onLive(Point point);
    }
}
