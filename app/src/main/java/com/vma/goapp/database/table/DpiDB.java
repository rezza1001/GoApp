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

public class DpiDB extends MasterDB {

    public static final String TAG          = "DPI_DB";
    public static final String TABLE_NAME   = "DPI_DB";

    public static final String ID   = "_id";
    public static final String NAME = "_name";
    public static final String LONGITUDE = "_longitude";
    public static final String LATITUDE = "_latitude";
    public static final String DATE = "_date";

    public int id = 0;
    public String name = "";
    public String date = "";
    public double longitude = 0;
    public double latitude = 0;

    @Override
    protected ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(LATITUDE, latitude);
        contentValues.put(NAME, name);
        contentValues.put(DATE, date);
        return contentValues;
    }

    @Override
    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " integer default 0," +
                " " + NAME + " varchar(2) NULL," +
                " " + DATE + " varchar(20) NULL," +
                " " + LATITUDE + " varchar(20) ," +
                " " + LONGITUDE + " varchar(20) " +
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
    protected DpiDB build(Cursor res) {
        DpiDB flagDB = new DpiDB();
        flagDB.id        = res.getInt(res.getColumnIndex(ID));
        flagDB.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        flagDB.latitude  = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        flagDB.name      = res.getString(res.getColumnIndex(NAME));
        flagDB.date      = res.getString(res.getColumnIndex(DATE));

        return flagDB;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id        = res.getInt(res.getColumnIndex(ID));
        this.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        this.latitude  = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        this.name      = res.getString(res.getColumnIndex(NAME));
        this.date      = res.getString(res.getColumnIndex(DATE));
    }

    @Override
    public boolean insert(Context context) {
        id = getNextID(context);
        return super.insert(context);

    }

    public void update(Context context){
        delete(context,ID+"="+id);
        insert(context);
    }
    public void delete(Context context, int pId){
        delete(context,ID+"="+pId);
    }

    public ArrayList<DpiDB> getAll(Context context){
        ArrayList<DpiDB> list = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" order by "+ID +" DESC ", null);
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

    public ArrayList<DpiDB> get(Context context, int id){
        ArrayList<DpiDB> list = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" where "+ID +"="+ id, null);
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
                max    = res.getInt(res.getColumnIndex("IDMAX")) + 1;
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return max;
    }
}
