package com.vma.goapp.ui.setting;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.VmaGlobalConfig;
import com.vma.goapp.libs.VmaLanguage;
import com.vma.goapp.libs.VmaTheme;
import com.vma.goapp.ui.component.OptionDialog;
import com.vma.goapp.ui.master.MyActivity;

import java.util.ArrayList;

public class DisplayActivity extends MyActivity {

    enum Setting {LANGUAGE, THEME_COLOR, COORDINATE_FORMAT, DISTANCE_UNIT, SPEED_UNIT}

    ArrayList<Bundle> listSetting = new ArrayList<>();
    DisplayAdapter mAdapter;


    @Override
    protected int setLayout() {
        return R.layout.activity_setting_display;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_setting = findViewById(R.id.rcvw_setting);
        rcvw_setting.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new DisplayAdapter(mActivity, listSetting);
        rcvw_setting.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        createSetting();
    }

    @Override
    protected void initListener() {

    }

    private void createSetting() {
        listSetting.clear();
        mAdapter.notifyItemRangeRemoved(0,4);

        addSetting(Setting.LANGUAGE, getResources().getString(R.string.language), VmaLanguage.getCountry(mActivity));
        addSetting(Setting.THEME_COLOR, getResources().getString(R.string.theme_color), VmaTheme.getThemeName(mActivity));
        addSetting(Setting.COORDINATE_FORMAT, getResources().getString(R.string.coordinate_format),VmaGlobalConfig.getCoordinateFormatName(mActivity));
        addSetting(Setting.DISTANCE_UNIT, getResources().getString(R.string.distance_unit),VmaGlobalConfig.getDistanceUnit(mActivity));
        addSetting(Setting.SPEED_UNIT, getResources().getString(R.string.speed_unit),VmaGlobalConfig.getSpeedUnit(mActivity));

        mAdapter.setOnSelectedListener(menu -> {
            if (menu.getSerializable("setting") == Setting.LANGUAGE){
                showLanguage();
            }
            else if (menu.getSerializable("setting") == Setting.THEME_COLOR){
                showTheme();
            }
            else if (menu.getSerializable("setting") == Setting.COORDINATE_FORMAT){
                showFormat();
            }
            else if (menu.getSerializable("setting") == Setting.DISTANCE_UNIT){
                showDistanceUnit();
            }
            else if (menu.getSerializable("setting") == Setting.SPEED_UNIT){
                showSpeedUnit();
            }
        });
    }

    private void addSetting(Setting setting, String name, String description){
        Bundle bundle = new Bundle();
        bundle.putSerializable("setting", setting);
        bundle.putString("name", name);
        bundle.putString("description", description);
        listSetting.add(bundle);
        mAdapter.notifyItemInserted(listSetting.size());
    }

    private void showLanguage(){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addOption("id", "Indonesia");
//        dialog.addOption("en", "English");
        dialog.setOnSelectedListener((bundle, index) -> {
            String code = bundle.getString("id");
            if (code.equals("id")){
                VmaLanguage.changeToIndonesia(mActivity);
            }
            else {
                VmaLanguage.changeToEnglish(mActivity);
            }

            int position = 0;
            Bundle bundle1 = listSetting.get(position);
            bundle1.putString("description", VmaLanguage.getCountry(mActivity));
            listSetting.set(position, bundle1);
            mAdapter.notifyItemChanged(position);
        });
    }

    private void showTheme(){
        String automaticDescription = "Pukul 06.00 s/d 18.00= Terang\nPukul 18.00 s/d 06.00= Gelap";
        if (VmaLanguage.getLanguage(mActivity).equals("en")){
            automaticDescription = "06.00 s/d 18.00= Light\n18.00 s/d 06.00= Dark";
        }

        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addOption(VmaTheme.NIGHT_MODE, getResources().getString(R.string.dark_mode));
        dialog.addOption(VmaTheme.DAY_MODE, getResources().getString(R.string.light_mode));
//        dialog.addOption(VmaTheme.AUTO_MODE, getResources().getString(R.string.automatic),automaticDescription);
        dialog.setOnSelectedListener((bundle, index) -> {
            VmaTheme.setTheme(mActivity, bundle.getInt("id"));
            int position = 1;
            Bundle bundle1 = listSetting.get(position);
            bundle1.putString("description", VmaTheme.getThemeName(mActivity));
            listSetting.set(position, bundle1);
            mAdapter.notifyItemChanged(position);
        });
    }


    private void showFormat(){
        CoordinateFormatDialog dialog = new CoordinateFormatDialog(mActivity);
        dialog.show();
        dialog.setOnSelectedListener((bundle, index) -> {
            int format = bundle.getInt("id");
            VmaGlobalConfig.setCoordinateFormat(mActivity, format);

            int position = 2;
            Bundle bundle1 = listSetting.get(position);
            bundle1.putString("description", VmaGlobalConfig.getCoordinateFormatName(mActivity));
            listSetting.set(position, bundle1);
            mAdapter.notifyItemChanged(position);
        });
    }

    private void showDistanceUnit(){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addOption("Nm", "Nm (Nautical miles)");
        dialog.addOption("Km", "Km (Kilo Meter)");
        dialog.addOption("Mi", "Mi (Mile)");
        dialog.setOnSelectedListener((bundle, index) -> {
            String code = bundle.getString("id");
            VmaGlobalConfig.setDistanceUnit(mActivity, code);

            int position = 3;
            Bundle bundle1 = listSetting.get(position);
            bundle1.putString("description", code);
            listSetting.set(position, bundle1);
            mAdapter.notifyItemChanged(position);
        });
    }
    private void showSpeedUnit(){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.addOption("Knot", "Knot (Default)");
        dialog.addOption("Kmh", "Kmh (Kilo meter per hour)");
        dialog.addOption("Mph", "Mph (Mile per hour)");
        dialog.setOnSelectedListener((bundle, index) -> {
            String code = bundle.getString("id");
            VmaGlobalConfig.setSpeedUnit(mActivity, code);

            int position = 4;
            Bundle bundle1 = listSetting.get(position);
            bundle1.putString("description", code);
            listSetting.set(position, bundle1);
            mAdapter.notifyItemChanged(position);
        });
    }

}
