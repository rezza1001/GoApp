package com.vma.goapp.ui.maps;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.skyfishjy.library.RippleBackground;
import com.vma.goapp.R;
import com.vma.goapp.database.table.DirectionDB;
import com.vma.goapp.ui.component.option.OptionDialog;
import com.vma.goapp.ui.maps.LocationInfoDialog;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.master.MyView;

import org.json.JSONObject;

public class CenterPointView extends MyView {

    private ImageView imvw_centerInfo,imvw_centerPoint;
    MapView mapvw_arcgis;
    RippleBackground rple_center;
    GraphicsOverlay graphicsOverlayCenter;
    boolean isTransparent = false;

    public CenterPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.fishmap_view_centerpointview;
    }

    @Override
    protected void initLayout() {
        imvw_centerInfo = findViewById(R.id.imvw_centerInfo);
        rple_center = findViewById(R.id.rple_center);
        imvw_centerPoint = findViewById(R.id.imvw_centerPoint);
    }

    @Override
    protected void initListener() {
        imvw_centerInfo.setOnClickListener(view -> centerPosition());
    }

    public void setMap(MapView map){
        mapvw_arcgis = map;
        graphicsOverlayCenter = new GraphicsOverlay(GraphicsOverlay.RenderingMode.DYNAMIC);
        mapvw_arcgis.getGraphicsOverlays().add(graphicsOverlayCenter);
    }


    private void centerPosition(){
        graphicsOverlayCenter.getGraphics().clear();

        float centreX   = mapvw_arcgis.getX() + mapvw_arcgis.getWidth()  / 2f;
        float centreY   = mapvw_arcgis.getY() + mapvw_arcgis.getHeight() / 2.1f ;

        android.graphics.Point screenPoint = new android.graphics.Point(Math.round(centreX), Math.round(centreY));
        Point mapPoint = mapvw_arcgis.screenToLocation(screenPoint);
        Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(mActivity, R.drawable.marker_center);
        PictureMarkerSymbol mSymbolTargetPos = new PictureMarkerSymbol(bitmapDrawable);
        mSymbolTargetPos.setHeight(40);
        mSymbolTargetPos.setWidth(28);
        mSymbolTargetPos.setOffsetY(-5);
        mSymbolTargetPos.loadAsync();
        Graphic startGraphic = new Graphic(wgs84Point, mSymbolTargetPos);
        graphicsOverlayCenter.getGraphics().add(startGraphic);

        if (onCenterInfoListener != null){
            onCenterInfoListener.onCenterInfo(wgs84Point);
        }
        showInfo(wgs84Point);
        rple_center.startRippleAnimation();
        setTransparent(true);
        new Handler().postDelayed(() -> rple_center.stopRippleAnimation(),1200);
    }

    private void showInfo(Point point){
        String prefData = VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA);
        try {
            JSONObject jo = new JSONObject(prefData);
            if (jo.has(VmaApiConstant.GPS_ITEM_SNR)){
                return;
            }
            LocationConverter converter = new LocationConverter(mActivity, jo.getString("longitude"),jo.getString("latitude"));

            LocationInfoDialog dialog = new LocationInfoDialog(mActivity);
            dialog.show();
            dialog.setLocation(point, converter.getLatitude(), converter.getLongitude());
            dialog.setOnActonListener(new LocationInfoDialog.OnActonListener() {
                @Override
                public void onDirect(Point point, double lng, double lat) {
                    setDirection(lng, lat, point);
                }

                @Override
                public void onSave(Point point, double lng, double lat) {
                    saveLocation(point);
                }

                @Override
                public void onDismiss() {
                    graphicsOverlayCenter.getGraphics().clear();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Utility.showToastError(mActivity,"Error showing location "+ e.getMessage());
        }
    }


    public void setTransparent(boolean show){
        if (show == isTransparent){
            return;
        }
        isTransparent = show;
        imvw_centerPoint.clearAnimation();
        imvw_centerInfo.clearAnimation();

        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.3f);
        if (!show){
            animation1 = new AlphaAnimation(0.3f, 1.0f);
            animation1.setDuration(500);
        }
        else {
            animation1.setDuration(1000);
        }
        animation1.setStartOffset(500);
        animation1.setFillAfter(true);
        imvw_centerPoint.startAnimation(animation1);
        imvw_centerInfo.startAnimation(animation1);
    }

    private void saveLocation(Point point){
        SaveLocationDialog dialog = new SaveLocationDialog(mActivity);
        dialog.show(point);
    }

    public void setDirection(double lng, double lat, Point point){
        DirectionDB db = new DirectionDB();
        if (db.getAll(mActivity).size() >= 1){
            directionOption(lng, lat, point);
            return;
        }
        db.id = db.getNextID(mActivity);
        db.longitude = lng;
        db.latitude = lat;
        db.isFinish = false;
        db.insert(mActivity);

        if (onCenterInfoListener != null){
            onCenterInfoListener.onDirect(point, lng, lat);
        }
    }

    private void directionOption(double lng, double lat, Point point){
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

            if (onCenterInfoListener != null){
                onCenterInfoListener.onDirect(point, lng, lat);
            }

        });
    }

    private OnCenterInfoListener onCenterInfoListener;
    public void setOnCenterInfoListener(OnCenterInfoListener onCenterInfoListener){
        this.onCenterInfoListener = onCenterInfoListener;
    }
    public interface OnCenterInfoListener{
        void onCenterInfo(Point cenPoint);
        void onDirect(Point point, double lng, double lat);
    }
}
