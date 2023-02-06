package com.vma.goapp.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vma.goapp.database.DatabaseManager;
import com.vma.goapp.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class LogbookDB extends MasterDB {

    public static final String TAG          = "LOGBOOK DB";
    public static final String TABLE_NAME   = "LOGBOOK";

    public static final String ID = "_id";
    public static final String FISHID = "_fish_id";
    public static final String DATE = "_date";
    public static final String TIME = "_time";
    public static final String UNIT = "_unit";
    public static final String QTY = "_qty";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public int id = 0;
    public int fishId = 0;
    public String date;
    public String time;
    public String unit;
    public int qty;
    public double longitude = 0;
    public double latitude = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " INTEGER DEFAULT 0," +
                " " + FISHID + " INTEGER DEFAULT 0," +
                " " + DATE + " varchar(20) NULL," +
                " " + TIME + " varchar(20) NULL," +
                " " + UNIT + " varchar(20) NULL," +
                " " + QTY + " INTEGER DEFAULT 0," +
                " " + LONGITUDE + " varchar(20) NULL," +
                " " + LATITUDE + " varchar(20) NULL" +
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
    protected LogbookDB build(Cursor res) {
        LogbookDB jp = new LogbookDB();
        jp.id = res.getInt(res.getColumnIndex(ID));
        jp.fishId = res.getInt(res.getColumnIndex(FISHID));
        jp.date = res.getString(res.getColumnIndex(DATE));
        jp.time = res.getString(res.getColumnIndex(TIME));
        jp.unit = res.getString(res.getColumnIndex(UNIT));
        jp.qty = res.getInt(res.getColumnIndex(QTY));
        jp.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        jp.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        return jp;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
        this.fishId = res.getInt(res.getColumnIndex(FISHID));
        this.date = res.getString(res.getColumnIndex(DATE));
        this.time = res.getString(res.getColumnIndex(TIME));
        this.unit = res.getString(res.getColumnIndex(UNIT));
        this.qty = res.getInt(res.getColumnIndex(QTY));
        this.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        this.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(FISHID, fishId);
        contentValues.put(DATE, date);
        contentValues.put(TIME, time);
        contentValues.put(UNIT, unit);
        contentValues.put(QTY, qty);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(LATITUDE, latitude);
        Log.d(TAG,"ContentValues = "+ contentValues);
        return contentValues;
    }


    public void delete(Context context, int id) {
        super.delete(context, ID +"= "+id+"");
    }
    public void delete(Context context) {
        super.delete(context, ID +"= "+ id +"");
    }

    @Override
    public boolean insert(Context context) {
        this.id = getNextID(context);
        return super.insert(context);
    }



    public ArrayList<LogbookDB> getAll(Context context){
        ArrayList<LogbookDB> list = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" order by "+ ID +" DESC ", null);
        try {
            while (res.moveToNext()){
                list.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return list;
    }

    public void get(Context context, int id){

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" WHERE "+ID+" ="+id, null);
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


    @SuppressLint("Range")
    public int getNextID(Context context){
        int max = 0;
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select MAX("+ID+") as IDMAX  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                max    = res.getInt(res.getColumnIndex("IDMAX"));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }

        return max+1;
    }



}
