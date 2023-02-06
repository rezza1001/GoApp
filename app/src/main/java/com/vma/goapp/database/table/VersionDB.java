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

public class VersionDB extends MasterDB {

    public static final String TAG          = "VERSION_DB";
    public static final String TABLE_NAME   = "VERSION_DB";

    public static final String CODE = "_code";
    public static final String NAME = "_name";
    public static final String DESCRIPTION = "_description";
    public static final String DATE = "_date";
    public static final String URL = "_url";

    public int code = 0;
    public String name = "";
    public String date = "";
    public String description = "";
    public String url = "";

    @Override
    protected ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CODE, code);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(NAME, name);
        contentValues.put(DATE, date);
        contentValues.put(URL, url);
        return contentValues;
    }

    @Override
    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + CODE + " integer default 0," +
                " " + NAME + " varchar(2) NULL," +
                " " + DATE + " varchar(20) NULL," +
                " " + URL + " varchar(255) NULL," +
                " " + DESCRIPTION + " varchar(200) " +
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
    protected VersionDB build(Cursor res) {
        VersionDB flagDB = new VersionDB();
        flagDB.code = res.getInt(res.getColumnIndex(CODE));
        flagDB.description = res.getString(res.getColumnIndex(DESCRIPTION));
        flagDB.name      = res.getString(res.getColumnIndex(NAME));
        flagDB.date      = res.getString(res.getColumnIndex(DATE));
        flagDB.url      = res.getString(res.getColumnIndex(URL));

        return flagDB;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.code = res.getInt(res.getColumnIndex(CODE));
        this.description = res.getString(res.getColumnIndex(DESCRIPTION));
        this.name      = res.getString(res.getColumnIndex(NAME));
        this.date      = res.getString(res.getColumnIndex(DATE));
        this.url      = res.getString(res.getColumnIndex(URL));
    }

    @Override
    public boolean insert(Context context) {
        clearData(context);
        return super.insert(context);

    }

    public void update(Context context){
        delete(context, CODE +"="+ code);
        insert(context);
    }
    public void delete(Context context, int pId){
        delete(context, CODE +"="+pId);
    }

    public void getAll(Context context){

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" order by "+ CODE +" DESC ", null);
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



}
