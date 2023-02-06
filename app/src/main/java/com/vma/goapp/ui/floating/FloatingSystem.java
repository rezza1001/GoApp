package com.vma.goapp.ui.floating;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esri.arcgisruntime.geometry.Point;
import com.vma.goapp.R;
import com.vma.goapp.VmaApplication;
import com.vma.goapp.database.table.DirectionDB;
import com.vma.goapp.database.table.FlagDB;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.Compass;
import com.vma.goapp.libs.DistanceUnit;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.SpeedUnit;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.libs.VmaUtils;
import com.vma.goapp.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class FloatingSystem extends Service {

    private WindowManager windowManager;
    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParam;

    private TextView txvw_destDegree,txvw_distance,txvw_speed,txvw_courseValue,txvw_location;
    private ImageView imvw_compass;
    private SpeedUnit speedUnit;
    private RelativeLayout rvly_degree;
    Thread thread;
    boolean isRun = true;
    private Compass mCompass;

    private int LAYOUT_TYPE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        speedUnit = new SpeedUnit(getApplicationContext());
        mCompass = new Compass();
        mCompass.create(getApplicationContext());
        mCompass.onStart();

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        floatView = (ViewGroup) inflater.inflate(R.layout.floating_system, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }

        // translucency by PixelFormat.TRANSLUCENT
        floatWindowLayoutParam = new WindowManager.LayoutParams(
//                (int) (width * (0.5f)),
                ViewGroup.LayoutParams.WRAP_CONTENT,
//                (int) (height * (0.35f)),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        FlagDB flagDB = new FlagDB();
        int x = Integer.parseInt(flagDB.getFlag(getApplicationContext(), VmaConstants.SCREEN_X));
        int y = Integer.parseInt(flagDB.getFlag(getApplicationContext(), VmaConstants.SCREEN_Y));
        x = (int) (x * 0.2);
        y = (int) (y * 0.3);

        floatWindowLayoutParam.gravity = Gravity.CENTER;
        floatWindowLayoutParam.x = x;
        floatWindowLayoutParam.y = y;

        windowManager.addView(floatView, floatWindowLayoutParam);

        ImageView imvw_close = floatView.findViewById(R.id.imvw_close);
        ImageView imvw_maximize = floatView.findViewById(R.id.imvw_maximize);
        txvw_destDegree = floatView.findViewById(R.id.txvw_destDegree);
        txvw_distance = floatView.findViewById(R.id.txvw_distance);
        txvw_speed = floatView.findViewById(R.id.txvw_speed);
        imvw_compass = floatView.findViewById(R.id.imvw_compass);
        txvw_courseValue = floatView.findViewById(R.id.txvw_courseValue);
        rvly_degree = floatView.findViewById(R.id.rvly_degree);
        txvw_location = floatView.findViewById(R.id.txvw_location);


        imvw_close.setOnClickListener(view -> {
            stopSelf();
            windowManager.removeView(floatView);
            DirectionDB db = new DirectionDB();
            db.clearData(getApplicationContext());
        });

        imvw_maximize.setOnClickListener(view -> {
            stopSelf();
            windowManager.removeView(floatView);
            Intent backToHome = new Intent(FloatingSystem.this, MainActivity.class);
            backToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(backToHome);
        });

        floatView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        px = event.getRawX();
                        py = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                        break;
                }
                return false;
            }
        });

        initCompass();
        startDirection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        isRun = false;
        mCompass.onDestroy();
        stopSelf();
        windowManager.removeView(floatView);
    }

    private void startDirection(){
        thread = new Thread() {
            @Override
            public void run() {
                while (isRun) {
                    try {
                        mHandler.sendEmptyMessage(1);
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            calculate();
        }
    };

    @SuppressLint("SetTextI18n")
    private void calculate(){
        String prefData = VmaPreferences.get(getApplicationContext(), VmaApiConstant.GPS_LSAT_DATA);
        try {
            JSONObject jo = new JSONObject(prefData);
            LocationConverter converter = new LocationConverter(getApplicationContext(),jo.getString("longitude"),jo.getString("latitude"));
            double speed = jo.getDouble(VmaApiConstant.GPS_ITEM_SPEED);
            speedUnit.setSpeed((int) speed);
            txvw_location.setText(converter.getLatitudeDisplay() +" | "+converter.getLongitudeDisplay());
            String sSpeed = speedUnit.getSpeed()+ " "+speedUnit.getUnitSpeed();
            txvw_speed.setText(sSpeed);

            DirectionDB db = new DirectionDB();
            ArrayList<DirectionDB> listTrack = db.getAll(getApplicationContext());
            if (listTrack.size() == 0){
                return;
            }
            DirectionDB directionDB = listTrack.get(0);

            LocationConverter disConverter = new LocationConverter(getApplicationContext(), directionDB.longitude,directionDB.latitude);
            Point mPoint = disConverter.getPoint();

            DistanceUnit distanceUnit = new DistanceUnit(getApplicationContext());
            distanceUnit.calcDistance(converter.getPoint(),mPoint);
            double distance = distanceUnit.getDistance();

            double bearing = VmaUtils.bearing(converter.getPoint().getY(), converter.getPoint().getX(), mPoint.getY(), mPoint.getX());
            String bear = String.format(Locale.ENGLISH, "%d°", (int)bearing);
            txvw_destDegree.setText(bear);
            txvw_destDegree.setRotation((float) bearing);

            if (distance >= 100){
                distance = Math.round(distance);
                String dist = String.format(Locale.ENGLISH, "%.0f", distance);
                String dist2 = dist.replace('.', ',')+" "+distanceUnit.getUnit();
                txvw_distance.setText(dist2);
            }
            else {
                String dist = String.format(Locale.ENGLISH, "%.2f", distance);
                String dist2 = dist.replace('.', ',') +" "+distanceUnit.getUnit();
                txvw_distance.setText(dist2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void initCompass(){
        mCompass.setOnChangeListener((degree, azimuth, currentAzimuth, name, description) -> {
            VmaApplication.LastBearing = degree;

            Animation anim = new RotateAnimation(-currentAzimuth, -azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(500);
            anim.setRepeatCount(0);
            anim.setFillAfter(true);
            imvw_compass.startAnimation(anim);
            txvw_courseValue.setText(String.format("%.0f", degree)+"\u00B0");


            rvly_degree.setRotation(-azimuth);
        });
    }

}
