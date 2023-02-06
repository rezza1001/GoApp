package com.vma.goapp.libs;

import android.content.Context;

public class SpeedUnit {

    private Context context;
    int speedNm = 0;
    private String unit;
    public SpeedUnit(Context context, int speedNm){
        this.context = context;
        this.speedNm = speedNm;
        this.unit = VmaGlobalConfig.getSpeedUnit(context);
    }

    public SpeedUnit(Context context){
        this.context = context;
        this.unit = VmaGlobalConfig.getSpeedUnit(context);
    }

    public void setSpeed(int speedNm){
        this.speedNm = speedNm;
    }

    public double getSpeed(){
        double speed;
        if (unit.equalsIgnoreCase("KMH")){
            speed = speedNm * 1.852;
        }
        else if (unit.equalsIgnoreCase("MPH")){
            speed = speedNm * 1.15078;
        }
        else {
            speed = speedNm;
        }

        return speed;
    }

    public String getUnitSpeed(){
        return unit;
    }
}
