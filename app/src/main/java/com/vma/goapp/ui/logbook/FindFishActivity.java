package com.vma.goapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.component.SearchRZView;
import com.vma.goapp.ui.master.MyActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FindFishActivity extends MyActivity {

    private SearchRZView srvw_rz;
    private CardView card_body;

    private final ArrayList<FishHolder> listFishFilter = new ArrayList<>();
    private final ArrayList<FishHolder> listFish = new ArrayList<>();
    private FishAdapter fishAdapter;

    @Override
    protected int setLayout() {
        return R.layout.activity_logbook_fish;
    }

    @Override
    protected void initLayout() {
        srvw_rz = findViewById(R.id.srvw_rz);
        srvw_rz.create(mActivity);
        srvw_rz.setHint(getResources().getString(R.string.find_fish));
        srvw_rz.setImageSearchColor(Color.WHITE);

        card_body = findViewById(R.id.card_body);

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        fishAdapter = new FishAdapter(mActivity, listFishFilter);
        rcvw_data.setAdapter(fishAdapter);
    }

    @Override
    protected void initData() {
        loadFish();
    }

    @Override
    protected void initListener() {
        srvw_rz.setOnActionListener(new SearchRZView.OnActionListener() {
            @Override
            public void onShow() {
                card_body.setRadius(0);
            }

            @Override
            public void onHide() {
                card_body.setRadius(Utility.dpToPx(mActivity, 15));
            }

            @Override
            public void onClear() {
                filter("");
            }

            @Override
            public void onTextChange(String text) {
                filter(text);
            }
        });

        fishAdapter.setOnSelectedListener(new FishAdapter.OnSelectedListener() {
            @Override
            public void onSelected(FishHolder menu) {
                Intent intent = new Intent();
                intent.putExtra("data", menu);
                setResult(RESULT_OK, intent);
                mActivity.finish();
            }

            @Override
            public void onFinishLoad() {

            }
        });
    }

    private void loadFish(){
        listFish.clear();
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader(getAssets().open("masterikan.csv")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                FishHolder holder = new FishHolder();
                String []value = mLine.split(",");
                holder.setId(Integer.parseInt(value[0]));
                holder.setName(value[1]);
                holder.setImageName(value[2]);
                holder.setType(value[3]);
                listFish.add(holder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        filter("");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filter(String value){
        listFishFilter.clear();
        fishAdapter.search(value);
        if (value.isEmpty()){
            listFishFilter.addAll(listFish);
        }
        else {
            for (FishHolder holder : listFish){
                if (holder.getName().toLowerCase().contains(value.toLowerCase())){
                    listFishFilter.add(holder);
                }
            }
        }
        fishAdapter.notifyDataSetChanged();
    }
}
