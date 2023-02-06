package com.vma.goapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.database.table.LogbookDB;
import com.vma.goapp.database.table.LogbookUploadDB;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.service.MainService;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.master.MyActivity;

import java.util.ArrayList;

public class LogbookActivity extends MyActivity {

    private LinearLayout lnly_empty;
    LogbookAdapter adapter;

    ArrayList<LogbookHolder> listDpi = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.activity_logbook;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        lnly_empty = findViewById(R.id.lnly_empty);
        lnly_empty.setVisibility(View.VISIBLE);

        adapter = new LogbookAdapter(mActivity,listDpi);
        rcvw_data.setAdapter(adapter);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        listDpi.clear();
        loadDB();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.fab_add).setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, AddLogbookActivity.class);
            startActivityForResult(intent,1);
        });

        adapter.setOnSelectedListener(this::confirmToDelete);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void loadDB(){
        listDpi.clear();
        LogbookDB db = new LogbookDB();
        for (LogbookDB logbookDB : db.getAll(mActivity)){
            LogbookHolder holder = new LogbookHolder();
            FishHolder fishHolder = MainService.MAP_FISH.get(logbookDB.fishId);
            if (fishHolder  == null){
                continue;
            }

            holder.setId(logbookDB.id);
            holder.setFishId(fishHolder.getId());
            holder.setFishName(fishHolder.getName());
            holder.setType(UnitType.find(logbookDB.unit));
            holder.setFishImage(fishHolder.getImageName());
            holder.setLongitude(logbookDB.longitude);
            holder.setLatitude(logbookDB.latitude);
            holder.setQty(logbookDB.qty);
            String sTime = logbookDB.date +" "+ logbookDB.time;

            holder.setTime(Utility.getDate(sTime,"dd/MM/YYYY HH:mm"));
            listDpi.add(holder);
        }
        adapter.notifyDataSetChanged();
        lnly_empty.setVisibility(View.VISIBLE);
        if (listDpi.size() > 0){
            lnly_empty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            loadDB();
        }
    }

    private void confirmToDelete(LogbookHolder holder){
        String sValue = holder.getFishName() + " pada tanggal "+ Utility.getDateString(holder.getTime(),"dd MMM yyyy HH:mm");
        String info = getResources().getString(R.string.are_you_sure_to_delete_logbook).replace("#X1",sValue);

        int start   = info.indexOf(sValue);
        int end     = start+ (sValue.length());

        SpannableString spanInfo = Utility.BoldText(mActivity, info,start,end,"#F79E46");

        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.delete_logbook),spanInfo);
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                LogbookDB db = new LogbookDB();
                db.delete(mActivity, holder.getId());
                loadDB();

                LogbookUploadDB dbUpload = new LogbookUploadDB();
                dbUpload.deleteByLogbook(mActivity, holder.getId());
            }
        });
    }
}
