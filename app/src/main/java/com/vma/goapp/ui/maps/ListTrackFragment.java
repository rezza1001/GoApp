package com.vma.goapp.ui.maps;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.database.table.TrackDB;
import com.vma.goapp.dom.VmaConstants;
import com.vma.goapp.libs.FileProcessing;
import com.vma.goapp.libs.LocationConverter;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.service.TrackRecordService;
import com.vma.goapp.ui.component.ConfirmDialog;
import com.vma.goapp.ui.component.option.OptionDialog;
import com.vma.goapp.ui.master.MyFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListTrackFragment extends MyFragment {

    private TrackAdapter trackAdapter;
    ArrayList<TrackHolder> listTrack = new ArrayList<>();

    public static ListTrackFragment newInstance() {
        Bundle args = new Bundle();
        ListTrackFragment fragment = new ListTrackFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    protected int setLayout() {
        return R.layout.map_fragment_track;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        trackAdapter = new TrackAdapter(mActivity, listTrack);
        rcvw_data.setAdapter(trackAdapter);

        mActivity.registerReceiver(receiver,new IntentFilter(VmaConstants.TRACK_RECORD_SAVE));

    }

    @Override
    protected void initListener() {
        trackAdapter.setOnSelectedListener(this::showOption);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        listTrack.clear();

        TrackDB db = new TrackDB();
        for (TrackDB trackDB: db.getAll(mActivity)){
            TrackHolder holder = new TrackHolder();
            holder.setId(trackDB.pId);
            holder.setName(trackDB.name);
            holder.setPath(trackDB.filePath);
            try {
                JSONObject joStart = new JSONObject(trackDB.startPosition);
                LocationConverter convStart = new LocationConverter(mActivity,joStart.getString("longitude"),joStart.getString("latitude"));
                holder.setPointStart(convStart.getPoint());

                JSONObject joEnd = new JSONObject(trackDB.endPosition);
                LocationConverter convEnd = new LocationConverter(mActivity, joEnd.getString("longitude"),joEnd.getString("latitude"));
                holder.setPointEnd(convEnd.getPoint());

                holder.setStartDate(Utility.getDate(trackDB.startTime,"dd-MM-yyyy HH:mm:ss"));
                holder.setEndDate(Utility.getDate(trackDB.endTime,"dd-MM-yyyy HH:mm:ss"));

                long difference = holder.getEndDate().getTime() - holder.getStartDate().getTime();
                holder.setTime(difference);

                listTrack.add(holder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        trackAdapter.notifyDataSetChanged();
    }

    private void showOption(TrackHolder holder){
        OptionDialog dialog = new OptionDialog(mActivity);
        dialog.show();
        dialog.setNameTitle(holder.getName());
        dialog.addChooser(getResources().getString(R.string.show));
        dialog.addChooser(getResources().getString(R.string.edit_name));
        dialog.addChooser(getResources().getString(R.string.delete));

        dialog.setOnSelectListener((bundle, value) -> {
            if (value.equals(getResources().getString(R.string.delete))){
               delete(holder);
            }
            else if (value.equals(getResources().getString(R.string.edit_name))){
                Intent intent = new Intent(mActivity, SaveTrackActivity.class);
                intent.putExtra("id", holder.getId());
                mActivity.startActivity(intent);
            }
            else {
                Intent intent = new Intent(VmaConstants.NOTIFY_SHOW_TRACK);
                intent.putExtra("id",holder.getId());
                mActivity.sendBroadcast(intent);
            }
        });
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    };

    private void delete(TrackHolder holder){
        String info = getResources().getString(R.string.are_you_sure_to_delete_track).replace("#X1",holder.getName());

        int start   = info.indexOf(holder.getName());
        int end     = start+ (holder.getName().length());

        SpannableString spanInfo = Utility.BoldText(mActivity, info,start,end,"#F79E46");

        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.show(getResources().getString(R.string.confirm),spanInfo);
        dialog.setOnActionListener(confirm -> {
            if (confirm){
                TrackDB db = new TrackDB();
                db.delete(mActivity, holder.getId());
                FileProcessing.DeleteFile(holder.getPath(), TrackRecordService.fileName);
                initData();
                Utility.showToastSuccess(mActivity, getResources().getString(R.string.success));
            }
        });
    }


}
