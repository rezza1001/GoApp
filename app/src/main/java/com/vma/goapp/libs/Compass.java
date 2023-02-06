package com.vma.goapp.libs;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import com.vma.goapp.R;

/**
 * Created by Mochamad Rezza Gumilang on 14/02/2022
 */
public class Compass implements SensorEventListener {

    private final float[] mGravity = new float[3];
    private final float[] mGeomagnetic = new float[3];
    private float currentAzimuth = 0f;
    private float declination = 0;
    private SensorManager mSensorManager;
    private int lastAngel = 0;
    private Context activity;

    public void create(Context activity){
        this.activity = activity;
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this){
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha * mGravity[0] +  (1-alpha) * sensorEvent.values[0];
                mGravity[1] = alpha * mGravity[1] +  (1-alpha) * sensorEvent.values[1];
                mGravity[2] = alpha * mGravity[2] +  (1-alpha) * sensorEvent.values[2];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic[0] = alpha * mGeomagnetic[0] +  (1-alpha) * sensorEvent.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] +  (1-alpha) * sensorEvent.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] +  (1-alpha) * sensorEvent.values[2];
            }

            float [] R = new float[9];
            float [] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mGravity, mGeomagnetic);

            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R,orientation);
                float pAzimut = orientation[0] - declination;
                float azimuth = (float) Math.toDegrees(pAzimut);
                azimuth = (azimuth + 360) % 360;

                int angle = Math.round(azimuth * 100) / 100;
                if (lastAngel != angle){
                    if (onChangeListener != null){
                        onChangeListener.onChange(angle, azimuth,currentAzimuth,getDirection(angle)[0],getDirection(angle)[1]);
                    }
                    lastAngel = angle;
                }
                currentAzimuth = azimuth;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onStart() {
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    public void onDestroy() {
        mSensorManager.unregisterListener(this);
    }

    private String[] getDirection(int angle){
        String [] direction = new String[2];

        if (angle >= 350 || angle <= 10){
            direction[0] = activity.getString(R.string.wind_direction_n); // N
            direction[1] =  activity.getString(R.string.north);
        }
        if (angle < 350 && angle > 280){
            direction[0]  = activity.getString(R.string.wind_direction_nw); // NW
            direction[1] =  activity.getString(R.string.north_west);
        }
        if (angle <= 280 && angle > 260){
            direction[0]  = activity.getString(R.string.wind_direction_w); // W
            direction[1] =  activity.getString(R.string.west);
        }
        if (angle <= 260 && angle > 190){
            direction[0]  = activity.getString(R.string.wind_direction_sw); // SW"
            direction[1] =  activity.getString(R.string.south_west);
        }
        if (angle <= 190 && angle > 170){
            direction[0]  =  activity.getString(R.string.wind_direction_s);  // S
            direction[1] =  activity.getString(R.string.south);
        }
        if (angle <= 170 && angle > 100){
            direction[0]  = activity.getString(R.string.wind_direction_se); // SE"
            direction[1] =  activity.getString(R.string.south_east);
        }
        if (angle <= 100 && angle > 80){
            direction[0]  = activity.getString(R.string.wind_direction_e); // E
            direction[1] =  activity.getString(R.string.east);
        }
        if (angle <= 80 && angle > 10){
            direction[0]  = activity.getString(R.string.wind_direction_ne); // NE"
            direction[1] =  activity.getString(R.string.north_east);
        }


        return direction;
    }

    public void setLocation(Location location) {
        GeomagneticField geomagneticField = new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                System.currentTimeMillis());
        declination = (float) Math.toRadians(geomagneticField.getDeclination());
    }

    private OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }
    public interface OnChangeListener{
        void onChange(float degree, float azimuth, float currentAzimuth, String name, String description);
    }
}
