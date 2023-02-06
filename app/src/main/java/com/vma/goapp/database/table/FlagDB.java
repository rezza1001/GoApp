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

public class FlagDB extends MasterDB {

    public static final String TAG          = "FLAG_DB";
    public static final String TABLE_NAME   = "FLAG_DB";

    public static final String ID   = "_id";
    public static final String FLAG = "_flag";
    public static final String VALUE        = "_value";

    public int id = 0;
    public String  value = "";
    public String flag = "";

    @Override
    protected ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(VALUE, value);
        contentValues.put(FLAG, flag);
        return contentValues;
    }

    @Override
    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " integer default 0," +
                " " + FLAG + " varchar(2) NULL," +
                " " + VALUE    + " integer default 0" +
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
    protected FlagDB build(Cursor res) {
        FlagDB flagDB = new FlagDB();
        flagDB.id = res.getInt(res.getColumnIndex(ID));
        flagDB.value = res.getString(res.getColumnIndex(VALUE));
        flagDB.flag = res.getString(res.getColumnIndex(FLAG));

        return flagDB;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
        this.flag = res.getString(res.getColumnIndex(FLAG));
        this.value = res.getString(res.getColumnIndex(VALUE));
    }

    public String getFlag(Context context, String mFLag){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME +" where "+FLAG+" = '"+mFLag+"'", null);
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

        return value;
    }

    public void setFlag(Context context, String flag, String value){
        delete(context,FLAG+"='"+flag+"'");
        this.flag = flag;
        this.value = value;
        Log.d(TAG,"setFlag("+flag+" , "+value+")");
        insert(context);
    }
    public void resetFlag(Context context, String flag){
        delete(context,FLAG+"='"+flag+"'");
        this.flag = flag;
        this.value = "";
        Log.d(TAG,"setFlag("+flag+" , "+value+")");
        insert(context);
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
