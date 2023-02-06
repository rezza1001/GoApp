package com.vma.goapp.ui.weather;

import com.vma.goapp.R;
import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.dom.VmaApiConstant;
import com.vma.goapp.libs.VmaPreferences;
import com.vma.goapp.ui.master.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends MyActivity {

    WeatherTodayView weather_today;
    WeatherOtherView weather_tmr,weather_aftrtmr;

    @Override
    protected int setLayout() {
        return R.layout.weather_activity_main;
    }

    @Override
    protected void initLayout() {
        weather_today = findViewById(R.id.weather_today);
        weather_tmr = findViewById(R.id.weather_tmr);
        weather_aftrtmr = findViewById(R.id.weather_aftrtmr);

        weather_tmr.setDataTomorrow();
        weather_aftrtmr.setAftrTomorrow();
    }

    @Override
    protected void initData() {
        try {
            JSONObject gpsLast = new JSONObject(VmaPreferences.get(mActivity, VmaApiConstant.GPS_LSAT_DATA));
            PostManager post = new PostManager(mActivity, ApiConfig.POST_WEATHER);
            post.addParam("longitude",gpsLast.getDouble("longitude"));
            post.addParam("latitude",gpsLast.getDouble("latitude"));
            post.exPost();
            post.setOnReceiveListener((obj, code, success, message) -> {
                if (success){
                    try {
                        JSONObject data = obj.getJSONObject("data");
                        JSONObject today = data.getJSONObject("today");
                        weather_today.setData(today);

                        JSONObject tomorrow = data.getJSONObject("tomorrow");
                        weather_tmr.setData(tomorrow);

                        JSONObject afterTmr = data.getJSONObject("afterTmr");
                        weather_aftrtmr.setData(afterTmr);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void initListener() {

    }
}
