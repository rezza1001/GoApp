package com.vma.goapp.ui.setting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vma.goapp.R;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.libs.VmaGlobalConfig;
import com.vma.goapp.ui.master.MyDialog;

import java.util.ArrayList;

public class CoordinateFormatDialog extends MyDialog {

    ArrayList<Bundle> menuItems = new ArrayList<>();
    DisplayAdapter adapter;
    RecyclerView rcvw_data;
    CardView rvly_body;

    public CoordinateFormatDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected int setLayout() {
        return R.layout.component_dialog_option;
    }

    @Override
    protected void initLayout(View view) {
        rvly_body = view.findViewById(R.id.card_body);

        rcvw_data = view.findViewById(R.id.rcvw_menu);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data.setNestedScrollingEnabled(false);

        view.findViewById(R.id.rvly_dialog).setOnClickListener(view1 -> dismiss());
    }

    @Override
    public void show() {
        super.show();
        rvly_body.setVisibility(View.VISIBLE);
        rvly_body.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
        menuItems = new ArrayList<>();
        adapter = new DisplayAdapter();
        rcvw_data.setAdapter(adapter);

//        addOption(VmaGlobalConfig.FORMAT_COORDINATE_SECONDS,mActivity.getResources().getString(R.string.decimal_seconds));
//        addOption(VmaGlobalConfig.FORMAT_COORDINATE_MINUTE,mActivity.getResources().getString(R.string.decimal_minutes));
        addOption(VmaGlobalConfig.FORMAT_COORDINATE_DEGREE,mActivity.getResources().getString(R.string.decimal_degree));
    }

    public void addOption(int id, String value){
        Bundle bundle = new Bundle();
        bundle.putString("title", value);
        bundle.putInt("id", id);
        menuItems.add(bundle);
        adapter.notifyItemInserted(menuItems.size());
    }


    class DisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_setting_coordinate, parent, false);
            return new AdapterView(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            AdapterView view = (AdapterView) holder;
            Bundle data = menuItems.get(position);
            view.txvw_title.setText(data.getString("title"));
            if (data.getInt("id") == VmaGlobalConfig.FORMAT_COORDINATE_SECONDS){
                view.txvw_degree.setVisibility(View.VISIBLE);
                view.txvw_minutes.setVisibility(View.VISIBLE);
                view.txvw_seconds.setVisibility(View.VISIBLE);
            }
            else if (data.getInt("id") == VmaGlobalConfig.FORMAT_COORDINATE_MINUTE){
                view.txvw_degree.setVisibility(View.VISIBLE);
                view.txvw_minutes.setVisibility(View.VISIBLE);
                view.txvw_seconds.setVisibility(View.GONE);
            }
            else if (data.getInt("id") == VmaGlobalConfig.FORMAT_COORDINATE_DEGREE){
                view.txvw_degree.setVisibility(View.VISIBLE);
                view.txvw_minutes.setVisibility(View.GONE);
                view.txvw_seconds.setVisibility(View.GONE);
            }

            view.lnly_item.setOnClickListener(view1 -> {
                if (onSelectedListener != null){
                    onSelectedListener.onSelected(data,position);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }

        class AdapterView extends RecyclerView.ViewHolder{
            LinearLayout lnly_item;
            TextView txvw_title,txvw_degree,txvw_minutes,txvw_seconds;

            public AdapterView(@NonNull View itemView) {
                super(itemView);

                lnly_item      = itemView.findViewById(R.id.lnly_item);
                txvw_title     = itemView.findViewById(R.id.txvw_title);
                txvw_degree    = itemView.findViewById(R.id.txvw_degree);
                txvw_minutes   = itemView.findViewById(R.id.txvw_minutes);
                txvw_seconds   = itemView.findViewById(R.id.txvw_seconds);
            }
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle bundle, int index);
    }
}
