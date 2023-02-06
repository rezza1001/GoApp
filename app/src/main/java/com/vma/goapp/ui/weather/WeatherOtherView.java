package com.vma.goapp.ui.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class WeatherOtherView extends MyView {


    TextView txvw_date,txvw_status,txvw_windDir,txvw_wind,txvw_wavehight,txvw_wave;
    ImageView imvw_iconOther;

    public WeatherOtherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setLayout() {
        return R.layout.weather_view_others;
    }

    @Override
    protected void initLayout() {
        txvw_date = findViewById(R.id.txvw_dateOther);
        txvw_status = findViewById(R.id.txvw_statusOther);

        txvw_date.setText(Utility.getDateString(new Date(),"EEEE, dd MMMM yyyy"));

        txvw_windDir = findViewById(R.id.txvw_windDirOther);
        txvw_wind = findViewById(R.id.txvw_windOther);
        txvw_wavehight = findViewById(R.id.txvw_wavehightOther);
        txvw_wave = findViewById(R.id.txvw_waveOther);
        imvw_iconOther = findViewById(R.id.imvw_iconOther);

        try {
            setData(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initListener() {

    }

    public void create(){
        CardView card_main = findViewById(R.id.card_other);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            card_main.setOutlineSpotShadowColor(ContextCompat.getColor(mActivity, R.color.primary));
        }
    }



    int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void setDataTomorrow() {
        create();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        txvw_date.setText(Utility.getDateString(calendar.getTime(),"EEEE, dd MMMM yyyy"));
    }
    public void setAftrTomorrow() {
        create();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        txvw_date.setText(Utility.getDateString(calendar.getTime(),"EEEE, dd MMMM yyyy"));
    }

    @SuppressLint("SetTextI18n")
    public void setData(JSONObject data) throws JSONException {
        txvw_windDir.setText(" - ");
        txvw_wavehight.setText(" - ");
        txvw_wave.setText(" - ");
        txvw_wind.setText(" - ");
        imvw_iconOther.setImageLevel(0);
        if (data == null){
            return;
        }
        String cuaca = data.getString("cuaca");
        cuaca = cuaca.equals("null")  ? "-": cuaca;
        txvw_status.setText(cuaca);
        imvw_iconOther.setImageLevel(getIcon(cuaca.trim()));

        String[] winDir = data.getString("a_angin").split("-");
        String[] winSpeed = data.getString("kec_angin").split("-");
        String[] wave = data.getString("gelombang").split("-");

        if (winDir.length == 2){
            txvw_windDir.setText(winDir[0] +" - "+winDir[1]);
        }
        if (winSpeed.length == 2){
            txvw_wind.setText(winSpeed[0] +" - "+winSpeed[1]);
        }
        if (wave.length == 2){
            txvw_wavehight.setText(wave[0] +" - "+wave[1]+" meter");
            txvw_wave.setText(getWaveCategory(Double.parseDouble(wave[0]),Double.parseDouble(wave[1])));
        }
    }

    private String getWaveCategory(double from, double to){
        if ( (from >= 0.1 && from <0.5 ) || (to >= 0.1 && to <0.5 )){
            return "Tenang";
        }
        if ( (from >= 0.5 && from <1.25 ) || (to >= 0.5 && to <1.25 )){
            return "Rendah";
        }
        if ( (from >= 1.25 && from <2.50 ) || (to >= 1.25 && to <2.50 )){
            return "Tinggi";
        }
        if ( (from >= 4.0 && from <6.0 ) || (to >= 4.0 && to <6.0 )){
            return "Tinggi";
        }
        if ( (from >= 6.0 && from <9.0 ) || (to >= 6.0 && to <9.0 )){
            return "Ekstrem";
        }
        return "Sangat Ekstrem";
    }

    public int getIcon(String cuaca){
        if (cuaca.equalsIgnoreCase("cerah")){
            return 0;
        }
        if (cuaca.equalsIgnoreCase("Cerah berawan")){
            return 1;
        }
        if (cuaca.equalsIgnoreCase("berawan")){
            return 2;
        }
        if (cuaca.equalsIgnoreCase("berawan tebal")){
            return 3;
        }
        if (cuaca.equalsIgnoreCase("Hujan")){
            return 4;
        }
        if (cuaca.equalsIgnoreCase("Hujan ringan")){
            return 5;
        }
        if (cuaca.equalsIgnoreCase("Hujan sedang")){
            return 6;
        }
        if (cuaca.equalsIgnoreCase("Hujan Lebat")){
            return 7;
        }
        return 0;
    }
}
