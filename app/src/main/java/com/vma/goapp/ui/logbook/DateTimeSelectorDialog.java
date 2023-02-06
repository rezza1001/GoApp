package com.vma.goapp.ui.logbook;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeSelectorDialog extends MyDialog {

    LinearLayout lnly_root;
    VmaButton bbtn_save,bbtn_cancel;
    ArrayList<Bundle> lisDate = new ArrayList<>();
    ArrayList<Bundle> lisTime = new ArrayList<>();
    DateTimeAdapter adapterDate;
    DateTimeAdapter adapterTime;

    String dateSelect = "";
    String timeSelect = "";

    protected DateTimeSelectorDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.logbook_dialog_datetimeselector;
    }

    @Override
    protected void initLayout(View view) {
        view.findViewById(R.id.rvly_close).setOnClickListener(view1 -> dismiss());
        lnly_root = view.findViewById(R.id.lnly_root);
        lnly_root.setVisibility(View.GONE);
        int size = Utility.dpToPx(mActivity, 15);
        lnly_root.setBackground(Utility.getRectBackground(Color.WHITE,size,size,0,0 ));

        bbtn_save = view.findViewById(R.id.bbtn_save);
        bbtn_save.create(mActivity.getResources().getString(R.string.save),0);
        bbtn_save.setOnActionListener(view1 -> {
            if (dateSelect.isEmpty()){
                Utility.showToastError(mActivity,mActivity.getResources().getString(R.string.select_date));
                return;
            }
            if (timeSelect.isEmpty()){
                Utility.showToastError(mActivity,mActivity.getResources().getString(R.string.select_time));
                return;
            }
            if (onSelectedListener != null){
                onSelectedListener.onSelect(dateSelect, timeSelect);
                dismiss();
            }
        });

        bbtn_cancel = view.findViewById(R.id.bbtn_cancel);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_cancel.setOnActionListener(view12 -> dismiss());

       initDate(view);
       initTime(view);
    }

    @Override
    public void show() {
        super.show();
        lnly_root.setVisibility(View.VISIBLE);
        lnly_root.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private void initDate(View v){
        lisDate = new ArrayList<>();
        adapterDate = new DateTimeAdapter(mActivity, lisDate);
        RecyclerView rcvw_date = v.findViewById(R.id.rcvw_date);
        rcvw_date.setBackground(Utility.getShapeLine(mActivity,1,6,Color.parseColor("#CDD4D9"),0));
        rcvw_date.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_date.setAdapter(adapterDate);

        Calendar calendar = Calendar.getInstance();
        for (int i=0; i<14; i++){
            Bundle bundle = new Bundle();
            bundle.putBoolean("select", false);
            bundle.putString("name", Utility.getDateString(calendar.getTime(),"dd MMM yyyy"));
            lisDate.add(bundle);
            adapterDate.notifyItemInserted(lisDate.size());
            calendar.add(Calendar.DATE, -(i+1));
        }

        adapterDate.setOnSelectedListener(menu -> dateSelect = menu.getString("name"));
    }
    private void initTime(View v){
        lisTime = new ArrayList<>();
        adapterTime = new DateTimeAdapter(mActivity, lisTime);
        RecyclerView rcvw_time = v.findViewById(R.id.rcvw_time);
        rcvw_time.setBackground(Utility.getShapeLine(mActivity,1,6,Color.parseColor("#CDD4D9"),0));
        rcvw_time.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_time.setAdapter(adapterTime);

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        for (int i=0; i<30; i++){
            Bundle bundle = new Bundle();
            bundle.putBoolean("select", false);
            bundle.putString("name", dateFormat.format(calendar.getTime()));
            lisTime.add(bundle);
            adapterTime.notifyItemInserted(lisTime.size());
            calendar.add(Calendar.MINUTE, -(i+5));
        }
        adapterTime.setOnSelectedListener(menu -> timeSelect = menu.getString("name"));
    }


    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelect(String date, String time);
    }
}
