package com.vma.goapp.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mochamad Rezza Gumilang on 11/02/2022
 */

public class LocationService {
    private static final String TAG = "LocationService";
    FusedLocationProviderClient mFusedLocationClient;
    Context mActivity;
    LocationRequest locationRequest;
    LocationManager locationManager;

    private ArrayList<String> dummyGPS = new ArrayList<>();
    private int indexDummy = 0;

    private static final int FAST_INTERVAL_TIME = 1000;
    private static final int INTERVAL_TIME = 10000;

    public LocationService(Context pActivity) {
        mActivity = pActivity;
    }

    public void create() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        createLocationRequest();
        locationManager = (LocationManager) mActivity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

//        initDummyTrack(mActivity);

    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL_TIME);
        locationRequest.setFastestInterval(FAST_INTERVAL_TIME);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(mActivity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
        mFusedLocationClient.getLastLocation().addOnFailureListener(e -> {
            Log.e(TAG, "Failed get location " +e.getMessage());
        });

    }

    public void destroy(){
        mFusedLocationClient.removeLocationUpdates(callback);
    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (onLocationChangeListener != null){
                Location location = locationResult.getLastLocation();
                if (dummyGPS.size() > 0){
                    location = appendDummy(locationResult.getLastLocation());
                }
                onLocationChangeListener.onChange(location, locationResult.getLocations());
            }
        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            if (onLocationChangeListener != null){
                onLocationChangeListener.onChangeStatus(locationAvailability.isLocationAvailable());
            }
        }

    };

    private OnLocationChangeListener onLocationChangeListener;
    public void setOnLocationChangeListener( OnLocationChangeListener onLocationChangeListener){
        this.onLocationChangeListener = onLocationChangeListener;
    }
    public interface OnLocationChangeListener{
        void onChange(Location lastLocation, List<Location> locations);
        void onChangeStatus(boolean available);
    }


    private Location appendDummy(Location location){
        if ((indexDummy + 1) == dummyGPS.size()){
            indexDummy = 0;
        }
        if (dummyGPS.size() > 0){
            String dummyData = dummyGPS.get(indexDummy);
            String[] splitter = dummyData.split("\\|");
            String sLong =splitter[2];
            String sLat = splitter[1];
            float speed = Float.parseFloat(splitter[3]);
            float bearing = Float.parseFloat(splitter[4]);
            speed = speed * 1.852f;
            location.setLongitude(Double.parseDouble(sLong));
            location.setLatitude(Double.parseDouble(sLat));
            location.setSpeed(speed);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                location.setBearingAccuracyDegrees(bearing);
                Log.d("DUMMY_GPS","Location  "+location.getBearing() + " : "+ location.getBearingAccuracyDegrees());
            }
            indexDummy ++;
        }

        return location;
    }
}
