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

public class BlackBoxDB extends MasterDB {

    public static final String TAG          = "BLACKBOX DB";
    public static final String TABLE_NAME   = "BLACKBOX";

    public static final String ID = "_id";
    public static final String LONGITUDE = "_longitude";
    public static final String LATITUDE = "_latitude";
    public static final String SPEED = "_speed";
    public static final String COURSE = "_course";
    public static final String TIME = "_time";
    public static final String UPLOAD = "_isUpload";

    public int id = 0;
    public double longitude = 0;
    public double latitude = 0;
    public double speed = 0;
    public double course = 0;
    public String time = "";
    public boolean isUpload = false;


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " INTEGER DEFAULT 0," +
                " " + LATITUDE + " varchar(20) NULL," +
                " " + LONGITUDE + " varchar(20) NULL," +
                " " + COURSE + " varchar(20) NULL," +
                " " + TIME + " varchar(20) NULL," +
                " " + SPEED + " varchar(20) NULL," +
                " " + UPLOAD + " INTEGER DEFAULT 0" +
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
    protected BlackBoxDB build(Cursor res) {
        BlackBoxDB jp = new BlackBoxDB();
        jp.id = res.getInt(res.getColumnIndex(ID));
        jp.isUpload = res.getInt(res.getColumnIndex(UPLOAD)) == 1;
        jp.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        jp.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        jp.course = Double.parseDouble(res.getString(res.getColumnIndex(COURSE)));
        jp.speed = Double.parseDouble(res.getString(res.getColumnIndex(SPEED)));
        jp.time = res.getString(res.getColumnIndex(TIME));
        return jp;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
        this.isUpload = res.getInt(res.getColumnIndex(UPLOAD)) == 1;
        this.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        this.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        this.course = Double.parseDouble(res.getString(res.getColumnIndex(COURSE)));
        this.speed = Double.parseDouble(res.getString(res.getColumnIndex(SPEED)));
        this.time = res.getString(res.getColumnIndex(TIME));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(UPLOAD, isUpload ? 1 : 0);
        contentValues.put(LATITUDE, latitude);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(COURSE, course);
        contentValues.put(SPEED, speed);
        contentValues.put(TIME, time);
        Log.d(TAG,"ContentValues = "+ contentValues);
        return contentValues;
    }


    public void delete(Context context, int id) {
        super.delete(context, ID +"= "+id+"");
    }
    public void update(Context context, int id) {
        super.delete(context, ID +"= "+id+"");
        insert(context);
    }
    public void update(Context context) {
        super.delete(context, ID +"= "+id+"");
        insert(context);
    }

    public void delete(Context context) {
        super.delete(context, ID +"= "+ longitude +"");
    }

    @Override
    public boolean insert(Context context) {
        this.id = getNextID(context);
        return super.insert(context);
    }



    public ArrayList<BlackBoxDB> getAll(Context context){
        ArrayList<BlackBoxDB> list = new ArrayList<>();

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
    public ArrayList<BlackBoxDB> getUnUpload(Context context){
        ArrayList<BlackBoxDB> list = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" WHERE "+UPLOAD+" =0  order by "+ ID +" DESC ", null);
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
