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

public class LogbookUploadDB extends MasterDB {

    public static final String TAG          = "LOGBOOK_PUSH DB";
    public static final String TABLE_NAME   = "LOGBOOK_PUSH";

    public static final String ID = "_id";
    public static final String LOGBOOK = "_logbook_id";

    public int id = 0;
    public int logboook = 0;


    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " INTEGER DEFAULT 0," +
                " " + LOGBOOK + " INTEGER DEFAULT 0" +
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
    protected LogbookUploadDB build(Cursor res) {
        LogbookUploadDB jp = new LogbookUploadDB();
        jp.id = res.getInt(res.getColumnIndex(ID));
        jp.logboook = res.getInt(res.getColumnIndex(LOGBOOK));
        return jp;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
        this.logboook = res.getInt(res.getColumnIndex(LOGBOOK));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(LOGBOOK, logboook);
        Log.d(TAG,"ContentValues = "+ contentValues);
        return contentValues;
    }


    public void delete(Context context, int id) {
        super.delete(context, ID +"= "+id+"");
    }
    public void deleteByLogbook(Context context, int logbook) {
        super.delete(context, LOGBOOK +"= "+logbook+"");
    }
    public void delete(Context context) {
        super.delete(context, ID +"= "+ logboook +"");
    }

    @Override
    public boolean insert(Context context) {
        this.id = getNextID(context);
        return super.insert(context);
    }



    public ArrayList<LogbookUploadDB> getAll(Context context){
        ArrayList<LogbookUploadDB> list = new ArrayList<>();

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
