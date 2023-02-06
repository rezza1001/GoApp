package com.vma.goapp.ui.component;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.vma.goapp.R;
import com.vma.goapp.libs.MyDevice;
import com.vma.goapp.ui.component.VmaButton;
import com.vma.goapp.api.ApiConfig;
import com.vma.goapp.api.PostManager;
import com.vma.goapp.database.table.AccountDB;
import com.vma.goapp.libs.Utility;
import com.vma.goapp.ui.master.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginDialog extends MyDialog {

    CardView card_body;
    VmaButton bbtn_login,bbtn_cancel;

    EditText edtx_username,edtx_password;
    MyDevice myDevice;

    public LoginDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.dialog_login;
    }

    @Override
    protected void initLayout(View view) {
        card_body = view.findViewById(R.id.card_body);
        card_body.setVisibility(View.VISIBLE);

        bbtn_login = view.findViewById(R.id.bbtn_login);
        bbtn_login.setButtonType(VmaButton.ButtonType.STANDARD);
        bbtn_login.create("LOGIN",0);

        bbtn_cancel = view.findViewById(R.id.bbtn_cancel);
        bbtn_cancel.setButtonType(VmaButton.ButtonType.BLUE_GREY);
        bbtn_cancel.create(mActivity.getResources().getString(R.string.cancel),0);

        edtx_username = findViewById(R.id.edtx_username);
        edtx_password = findViewById(R.id.edtx_password);

        view.findViewById(R.id.txvw_register).setOnClickListener(view1 -> {
            if (onActionListener != null){
                onActionListener.onRegister();
            }
            dismiss();
        });

    }

    @Override
    public void show() {
        super.show();
        myDevice = new MyDevice(mActivity);

        card_body.setVisibility(View.VISIBLE);
        card_body.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.zoom_in));

        bbtn_cancel.setOnActionListener(view -> dismiss());
        bbtn_login.setOnActionListener(view -> login());
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private void login(){
        String username = edtx_username.getText().toString().trim();
        String password = edtx_password.getText().toString().trim();

        if (username.isEmpty()){
            Utility.showToastError(mActivity,"Invalid Username !");
            return;
        }
        if (password.isEmpty()){
            Utility.showToastError(mActivity,"Invalid Password !");
            return;
        }

        PostManager post = new PostManager(mActivity, ApiConfig.LOGIN);
        post.addParam("userName", username);
        post.addParam("password", password);
        post.addParam("deviceId", myDevice.getDeviceID());
        post.addParam("deviceName", myDevice.getDeviceName());
        post.exPost();
        post.setOnReceiveListener((obj, code, success, message) -> {
            if (success){
                insertToDB(obj);
            }
            else {
                if (message.isEmpty()){
                    message = "Login Failed !";
                }
                Utility.showToastError(mActivity,message);
            }
        });
    }

    private void insertToDB(JSONObject obj)  {
        try {
            JSONObject data  = obj.getJSONObject("data");

            AccountDB db = new AccountDB();
            db.username = data.getString("username");
            db.password = edtx_password.getText().toString().trim();
            db.imei = data.getString("imei");
            db.mKey = data.getLong("mKey");
            db.shipName = data.getString("ship_name");
            db.owner = data.getJSONObject("owner").getString("name");
            db.phone = data.getJSONObject("owner").getString("phone");
            db.address = data.getJSONObject("owner").getString("address");
            db.sipi = data.getString("sipi");
            db.gt = data.getString("gt");
            db.image = data.getString("image");
            db.regStatus = data.getBoolean("register_status");
            db.insert(mActivity);
            if (onActionListener != null){
                Utility.showToastSuccess(mActivity,"Login Success !");
                onActionListener.onLogin();
                dismiss();
            }
        } catch (JSONException e) {
            Utility.showToastError(mActivity,"Login Failed ! "+e.getMessage());
            e.printStackTrace();
        }
    }

    private OnActionListener onActionListener;
    public void setOnActionListener(OnActionListener onActionListener){
        this.onActionListener = onActionListener;
    }
    public interface OnActionListener{
        void onRegister();
        void onLogin();
    }
}
