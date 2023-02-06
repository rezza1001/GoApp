package com.vma.goapp.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vma.goapp.database.DatabaseManager;
import com.vma.goapp.database.MasterDB;
import com.vma.goapp.libs.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TrackDB extends MasterDB {

    public static final String TAG          = "TrackDB";
    public static final String TABLE_NAME   = "TrackDB";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String START_POSITION = "start_position";
    public static final String END_POSITION = "end_position";
    public static final String DISTANCE = "distance";
    public static final String FILE_PATH = "file_path";
    public static final String SPEED = "speed";

    public int pId = 0;
    public String name = "";
    public String startTime = ""; // dd-MM-yyyy HH:mm
    public String endTime = ""; // dd-MM-yyyy HH:mm
    public String startPosition = "";
    public String endPosition = "";
    public String filePath = "";
    public float speed = 0;
    public float distance = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " INTEGER DEFAULT 0," +
                " " + NAME + " varchar(50) NULL," +
                " " + START_DATE + " varchar(20) NULL," +
                " " + END_DATE + " varchar(20) NULL," +
                " " + START_POSITION + " varchar(200) NULL," +
                " " + END_POSITION + " varchar(200) NULL," +
                " " + DISTANCE + " varchar(20) NULL," +
                " " + SPEED + " varchar(20) NULL," +
                " " + FILE_PATH + " varchar(200) NULL" +
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
    protected TrackDB build(Cursor res) {
        TrackDB jp = new TrackDB();
        jp.pId          = res.getInt(res.getColumnIndex(ID));
        jp.name         = res.getString(res.getColumnIndex(NAME));
        jp.startTime = res.getString(res.getColumnIndex(START_DATE));
        jp.endTime = res.getString(res.getColumnIndex(END_DATE));
        jp.startPosition = res.getString(res.getColumnIndex(START_POSITION));
        jp.endPosition  = res.getString(res.getColumnIndex(END_POSITION));
        jp.filePath  = res.getString(res.getColumnIndex(FILE_PATH));
        jp.distance     = Float.parseFloat(res.getString(res.getColumnIndex(DISTANCE)));
        jp.speed     = Float.parseFloat(res.getString(res.getColumnIndex(SPEED)));
        Log.d(TAG,"Name "+jp.name);
        return jp;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.pId          = res.getInt(res.getColumnIndex(ID));
        this.name         = res.getString(res.getColumnIndex(NAME));
        this.startTime = res.getString(res.getColumnIndex(START_DATE));
        this.endTime = res.getString(res.getColumnIndex(END_DATE));
        this.startPosition = res.getString(res.getColumnIndex(START_POSITION));
        this.endPosition  = res.getString(res.getColumnIndex(END_POSITION));
        this.filePath  = res.getString(res.getColumnIndex(FILE_PATH));
        this.distance     = Float.parseFloat(res.getString(res.getColumnIndex(DISTANCE)));
        this.speed     = Float.parseFloat(res.getString(res.getColumnIndex(SPEED)));

        Log.d(TAG,"pId "+ pId+" | name "+name+" | filePath "+filePath);
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, pId);
        contentValues.put(NAME, name);
        contentValues.put(START_DATE, startTime);
        contentValues.put(END_DATE, endTime);
        contentValues.put(START_POSITION, startPosition);
        contentValues.put(END_POSITION, endPosition);
        contentValues.put(DISTANCE, distance);
        contentValues.put(FILE_PATH, filePath);
        contentValues.put(SPEED, speed);
        Log.d(TAG,"ContentValues = "+ contentValues);
        return contentValues;
    }


    public void delete(Context context, int id) {
        super.delete(context, ID +"= "+id+"");
    }
    public void delete(Context context) {
        super.delete(context, ID +"= "+pId+"");
    }

    @Override
    public boolean insert(Context context) {
        pId = getNextID(context);
        return super.insert(context);
    }

    public void update(Context context){
        delete(context, pId);
        super.insert(context);
    }


    public void getData(Context context, int id){
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        String query = "select * from " + TABLE_NAME +" WHERE "+ID+" = "+id+"";
        Log.d(TAG,"query "+query);
        Cursor res = db.rawQuery(query , null);
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

    public ArrayList<TrackDB> getAll(Context context){
        ArrayList<TrackDB> list = new ArrayList<>();

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

    public String getDate(String format, String sDate){

        String value = "-";
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = format1.parse(sDate);
            if (date != null){
                value = dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"getDate "+sDate+" --> "+value);
        return value;
    }

    public String getDuration(){
        Date startDate = Utility.getDate(startTime,"dd-MM-yyyy HH:mm:ss");

        Date  endDate = Utility.getDate(endTime,"dd-MM-yyyy HH:mm:ss");

        long difference = endDate.getTime() - startDate.getTime();
        long seconds = (difference / 1000) % 60;
        long minutes = (difference / (1000 * 60)) % 60;
        long hours = (difference / (1000 * 60 * 60)) % 24;
        return hours+" : "+minutes+" : "+ seconds;
    }

}
