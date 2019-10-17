package com.example.dinhthai.sendingandreceivingdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "EmotionalHealth.db";
    public static final String TABLE_NAME = "Measurement";
    public static final String COL_1 = "ID";
//    public static final String COL_2 = "NAME";
//    public static final String COL_3 = "AGE";
    public static final String COL_2 = "SKINCONDUCTANCE";
    public static final String COL_3 = "HEARTRATE";
    public static final String COL_4 = "BODYTEMP";
    public static final String COL_5 = "DATE";






    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,SKINCONDUCTANCE TEXT, HEARTRATE TEXT,BODYTEMP TEXT, DATE TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String sConductance, String hRate, String bodyTemp)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,sConductance);
        contentValues.put(COL_3,hRate);
        contentValues.put(COL_4,bodyTemp);
        contentValues.put(COL_5,dateFormat.format(date));
        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }
    public Cursor ReadData(){
        SQLiteDatabase db = this.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                this.COL_1,
                this.COL_2,
                this.COL_3,
                this.COL_4
        };
        // Filter results WHERE "title" = 'My Title'
        String selection = this.COL_1 + " = ?";
        String[] selectionArgs = { "1" };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                this.COL_4 + " DESC";
        Cursor cursor = db.query(
                this.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        return cursor;
    }
    public Cursor GetDataFromTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public boolean updateData(String id, String sConductance, String hRate,String bodyTemp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,sConductance);
        contentValues.put(COL_3,hRate);
        contentValues.put(COL_4,bodyTemp);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}
