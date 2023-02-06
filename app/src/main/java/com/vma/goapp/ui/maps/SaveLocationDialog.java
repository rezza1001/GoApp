package com.vma.goapp.ui.maps;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.esri.arcgisruntime.geometry.Point;
import com.vma.goapp.R;
import com.vma.goapp.database.table.LocationDB;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.component.CoordinateInput;
import com.vma.goapp.ui.component.EditTextForm;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.ui.master.MyDialog;

public class SaveLocationDialog extends MyDialog {

    private CardView card_body;
    private EditTextForm edtx_name;
    private CoordinateInput input_latitude,input_longitude;
    private VmaButton bbtn_ok,bbtn_cancel;
    private OnSaveListener onSaveListener;

    private int pId = 0;

    public SaveLocationDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.maps_dialog_savelocation;
    }

    @Override
    protected void initLayout(View view) {
        view.findViewById(R.id.rvly_dialog).setOnClickListener(view1 -> dismiss());

        card_body = view.findViewById(R.id.card_body);
        card_body.setVisibility(View.INVISIBLE);

        edtx_name = view.findViewById(R.id.edtx_name);
        edtx_name.setRequired();
        edtx_name.create(mActivity.getResources().getString(R.string.location_name));
        edtx_name.setTitleColor(mActivity.getResources().getColor(R.color.text_standard));

        input_latitude = view.findViewById(R.id.input_latitude);
        input_latitude.create();
        input_latitude.setTypeLatitude();

        input_longitude = view.findViewById(R.id.input_longitude);
        input_longitude.create();
        input_longitude.setTypeLongitude();

        bbtn_ok = findViewById(R.id.bbtn_ok);
        bbtn_ok.create(mActivity.getResources().getString(R.string.save_location),0);
        bbtn_ok.setButtonType(VmaButton.ButtonType.STANDARD);

        bbtn_cancel = findViewById(R.id.bbtn_cancel);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);

        bbtn_cancel.setOnActionListener(view1 -> dismiss());
        bbtn_ok.setOnActionListener(view1 -> save());
    }

    public void show(Point point) {
        super.show();

        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));

        if (point == null){
            return;
        }
        input_longitude.setValue(point.getX());
        input_latitude.setValue(point.getY());


    }

    public void showAsEdit(double longitude, double latitude, int id){
        super.show();

        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));

        input_longitude.setValue(longitude);
        input_latitude.setValue(latitude);

        LocationDB db = new LocationDB();
        db.get(mActivity, id);
        edtx_name.setValue(db.name);

        pId = id;
    }



    private void save(){
        LocationDB db = new LocationDB();
        if (pId == 0){
            db.id = db.getNextID(mActivity);
        }
        else {
            db.id = pId;
        }
        db.latitude = input_latitude.getValue();
        db.longitude = input_longitude.getValue();
        db.name = edtx_name.getValue();

        if (db.name.isEmpty()){
            Utility.showToastError(mActivity,"Name required");
            return;
        }
        if (db.latitude == 0){
            Utility.showToastError(mActivity,"Latitude required");
            return;
        }
        if (db.longitude == 0){
            Utility.showToastError(mActivity,"Longitude required");
            return;
        }

        db.update(mActivity);
        Utility.showToastSuccess(mActivity,"Success");
        dismiss();

        if (onSaveListener != null){
            onSaveListener.onSave(db);
        }
    }

    public void setOnSaveListener(OnSaveListener onSaveListener){
        this.onSaveListener = onSaveListener;
    }

    public interface OnSaveListener{
        void onSave(LocationDB db);
    }
}
