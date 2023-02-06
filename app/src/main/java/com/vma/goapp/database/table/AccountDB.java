package com.vma.goapp.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vma.goapp.database.DatabaseManager;
import com.vma.goapp.database.MasterDB;

import java.util.Objects;

public class AccountDB extends MasterDB {

    public static final String TAG          = "ACCOUNT";
    public static final String TABLE_NAME   = "ACCOUNT";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SHIP_NAME = "shipName";
    public static final String OWNER = "owner";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String GT = "gt";
    public static final String SIPI = "sipi";
    public static final String IMAGE = "image";
    public static final String MKEY = "mkey";
    public static final String IMEI = "imei";
    public static final String REG_STATUS = "reg_status";

    public String username = "";
    public String password = "";
    public long mKey = 0;
    public String imei = "";
    public String shipName = "";
    public String owner = "";
    public String phone = "";
    public String address = "";
    public String sipi = "";
    public String image = "";
    public String gt = "";
    public boolean regStatus = false;

    @Override
    protected ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(PASSWORD, password);
        contentValues.put(MKEY, mKey);
        contentValues.put(IMEI, imei);
        contentValues.put(SHIP_NAME, shipName);
        contentValues.put(OWNER, owner);
        contentValues.put(PHONE, phone);
        contentValues.put(ADDRESS, address);
        contentValues.put(SIPI, sipi);
        contentValues.put(IMAGE, image);
        contentValues.put(GT, gt);
        contentValues.put(REG_STATUS, regStatus ? 1 : 0);
        return contentValues;
    }

    @Override
    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + USERNAME + " varchar(50) NULL," +
                " " + PASSWORD + " varchar(50) NULL," +
                " " + MKEY + " varchar(20) NULL," +
                " " + IMEI + " varchar(20) NULL," +
                " " + SHIP_NAME + " varchar(100) NULL," +
                " " + OWNER + " varchar(50) NULL," +
                " " + PHONE + " varchar(15) NULL," +
                " " + ADDRESS + " varchar(200) NULL ," +
                " " + SIPI + " varchar(20) NULL ," +
                " " + IMAGE + " varchar(200) NULL ," +
                " " + REG_STATUS + " Integer default 0 ," +
                " " + GT + " varchar(5) NULL " +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @SuppressLint("Range")
    @Override
    protected AccountDB build(Cursor res) {
        AccountDB resultDB = new AccountDB();
        resultDB.username = res.getString(res.getColumnIndex(USERNAME));
        resultDB.password = res.getString(res.getColumnIndex(PASSWORD));
        resultDB.mKey = Long.parseLong(res.getString(res.getColumnIndex(MKEY)));
        resultDB.imei = res.getString(res.getColumnIndex(IMEI));
        resultDB.shipName = res.getString(res.getColumnIndex(SHIP_NAME));
        resultDB.owner = res.getString(res.getColumnIndex(OWNER));
        resultDB.phone = res.getString(res.getColumnIndex(PHONE));
        resultDB.address = res.getString(res.getColumnIndex(ADDRESS));
        resultDB.sipi = res.getString(res.getColumnIndex(SIPI));
        resultDB.image = res.getString(res.getColumnIndex(IMAGE));
        resultDB.gt = res.getString(res.getColumnIndex(GT));
        resultDB.regStatus = res.getInt(res.getColumnIndex(REG_STATUS)) == 1;

        return resultDB;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.username = res.getString(res.getColumnIndex(USERNAME));
        this.password = res.getString(res.getColumnIndex(PASSWORD));
        this.mKey = Long.parseLong(res.getString(res.getColumnIndex(MKEY)));
        this.imei = res.getString(res.getColumnIndex(IMEI));
        this.shipName = res.getString(res.getColumnIndex(SHIP_NAME));
        this.owner = res.getString(res.getColumnIndex(OWNER));
        this.phone = res.getString(res.getColumnIndex(PHONE));
        this.address = res.getString(res.getColumnIndex(ADDRESS));
        this.sipi = res.getString(res.getColumnIndex(SIPI));
        this.image = res.getString(res.getColumnIndex(IMAGE));
        this.gt = res.getString(res.getColumnIndex(GT));
        this.regStatus = res.getInt(res.getColumnIndex(REG_STATUS)) == 1;
    }

    @Override
    public boolean insert(Context context) {
        delete(context,IMEI +"='"+imei+"'");
        return super.insert(context);
    }

    public void loadData(Context context){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                buildSingle(res);
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
    }
//
//    @SuppressLint("Range")
//    public int getNextID(Context context){
//        int max = 0;
//        DatabaseManager pDB = new DatabaseManager(context);
//        SQLiteDatabase db = pDB.getReadableDatabase();
//        Cursor res = db.rawQuery("select MAX("+ USERNAME +") as IDMAX  from " + TABLE_NAME , null);
//        try {
//            while (res.moveToNext()){
//                max    = res.getInt(res.getColumnIndex("IDMAX")) + 1;
//            }
//            pDB.close();
//        }catch (Exception e){
//            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
//        }
//        finally {
//            res.close();
//            pDB.close();
//        }
//
//        return max;
//    }
}
