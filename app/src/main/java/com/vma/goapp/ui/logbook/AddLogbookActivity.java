package com.vma.goapp.ui.logbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.vma.goapp.R;
import com.vma.goapp.database.table.LogbookDB;
import com.vma.goapp.database.table.LogbookUploadDB;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.service.LogbookService;
import com.vma.goapp.service.MainService;
import com.vma.goapp.ui.component.EditTextForm;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.component.option.OptionDialog;
import com.vma.goapp.ui.master.MyActivity;

import java.util.Date;

public class AddLogbookActivity extends MyActivity {

    private EditTextForm edtx_fish,edtx_date,edtx_time,edtx_typeQty,edtx_qty;
    private VmaButton bbtn_edit,bbtn_save,bbtn_cancel;

    @Override
    protected int setLayout() {
        return R.layout.activity_addlogbook;
    }

    @Override
    protected void initLayout() {
        edtx_fish = findViewById(R.id.edtx_fish);
        edtx_fish.setType(EditTextForm.Type.SELECT);
        edtx_fish.setRequired();
        edtx_fish.create(getResources().getString(R.string.fish_type),
                ContextCompat.getColor(mActivity,R.color.textHint), ContextCompat.getColor(mActivity,R.color.text_standard));

        edtx_date = findViewById(R.id.edtx_date);
        edtx_date.setType(EditTextForm.Type.SELECT);
        edtx_date.setRequired();
        edtx_date.create(getResources().getString(R.string.date),
                ContextCompat.getColor(mActivity,R.color.textHint), ContextCompat.getColor(mActivity,R.color.text_standard));

        edtx_time = findViewById(R.id.edtx_time);
        edtx_time.setType(EditTextForm.Type.SELECT);
        edtx_time.setRequired();
        edtx_time.create(getResources().getString(R.string.time),
                ContextCompat.getColor(mActivity,R.color.textHint), ContextCompat.getColor(mActivity,R.color.text_standard));

        edtx_typeQty = findViewById(R.id.edtx_typeQty);
        edtx_typeQty.setType(EditTextForm.Type.SELECT);
        edtx_typeQty.setRequired();
        edtx_typeQty.create(getResources().getString(R.string.unit),
                ContextCompat.getColor(mActivity,R.color.textHint), ContextCompat.getColor(mActivity,R.color.text_standard));

        edtx_qty = findViewById(R.id.edtx_qty);
        edtx_qty.setType(EditTextForm.Type.NUMBER);
        edtx_qty.setRequired();
        edtx_qty.create(getResources().getString(R.string.quantity),
                ContextCompat.getColor(mActivity,R.color.textHint), ContextCompat.getColor(mActivity,R.color.text_standard));

        bbtn_edit = findViewById(R.id.bbtn_edit);
        bbtn_edit.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_edit.create("", R.drawable.ic_baseline_edit_24);

        bbtn_save = findViewById(R.id.bbtn_save);
        bbtn_save.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_save.create(getResources().getString(R.string.save), R.drawable.ic_logbook);

        bbtn_cancel = findViewById(R.id.bbtn_cancel);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_cancel.create(getResources().getString(R.string.cancel), R.drawable.ic_clear_24);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        bbtn_edit.setOnActionListener(view -> showOptionDate());
        edtx_date.setOnSelectListener(this::showOptionDate);
        edtx_time.setOnSelectListener(this::showOptionDate);
        edtx_typeQty.setOnSelectListener(this::showOptionUnit);
    }

    @Override
    protected void initListener() {
        bbtn_save.setOnActionListener(view -> save());
        bbtn_cancel.setOnActionListener(view -> onBackPressed());

        edtx_fish.setOnSelectListener(() -> {
            startActivityForResult(new Intent(mActivity, FindFishActivity.class),1);
        });
    }

    private void showOptionDate(){
        DateTimeSelectorDialog dialog = new DateTimeSelectorDialog(mActivity);
        dialog.show();
        dialog.setOnSelectedListener((date, time) -> {
            edtx_date.setValue(date);
            edtx_time.setValue(time);
        });
    }

    private void showOptionUnit(){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.setNameTitle(getResources().getString(R.string.unit));
        for (UnitType type : UnitType.values() ){
            Bundle bundle = new Bundle();
            bundle.putString("value", type.value);
            bundle.putString("key", type.key);
            dialog.addChooser(bundle);
        }
        dialog.setOnSelectListener((bundle, value) -> edtx_typeQty.setValue(bundle.getString("key"),bundle.getString("value")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            assert data != null;
            FishHolder holder = (FishHolder) data.getSerializableExtra("data");
            edtx_fish.setValue(holder.getId()+"", holder.getName());
            UnitType type = UnitType.find(holder.getType());
            edtx_typeQty.setValue(type.key, type.value);
        }
    }

    private void save(){
        if (!edtx_fish.isValid()){
            return;
        }
        if (!edtx_date.isValid()){
            return;
        }
        if (!edtx_time.isValid()){
            return;
        }
        if (!edtx_typeQty.isValid()){
            return;
        }
        if (!edtx_qty.isValid()){
            return;
        }


        LogbookDB db = new LogbookDB();
        db.fishId = Integer.parseInt(edtx_fish.getKey());
        db.latitude = MainService.lastLocation.getLatitude();
        db.longitude = MainService.lastLocation.getLongitude();
        db.unit = edtx_typeQty.getKey();
        db.qty = Integer.parseInt(edtx_qty.getValue());
        db.time = edtx_time.getValue();

        String strDate = edtx_date.getValue();
        Date mDate = Utility.getDate(strDate,"dd MMM yyyy");
        db.date = Utility.getDateString(mDate,"dd/MM/yyyy");
        db.insert(mActivity);

        saveToStack(db.id);

        Utility.showToastSuccess(mActivity, getResources().getString(R.string.success));
        setResult(RESULT_OK);
        mActivity.finish();


    }

    private void saveToStack(int logbook){
        LogbookUploadDB db = new LogbookUploadDB();
        db.logboook = logbook;
        db.insert(mActivity);


        if (!LogbookService.isRunning){
            startService(new Intent(mActivity, LogbookService.class));
        }
    }
}
