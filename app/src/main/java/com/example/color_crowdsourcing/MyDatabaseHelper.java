package com.example.color_crowdsourcing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {


    private Context context;
    private static final String DATABASE_NAME = "color_crowdsource.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "color_history";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "color_hex";






    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "+TABLE_NAME+
                " ("+ COLUMN_NAME +" TEXT);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    void addEntry(String hexValue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, hexValue); // Puts already processed hex value into table
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "Failed to insert data in db",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Successes  insert data in db",Toast.LENGTH_SHORT).show();

    }

    // Method to check if a row with a certain value exists
    boolean doesEntryExist(String hexValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_NAME + "=?";
        String[] selectionArgs = {hexValue};

        Cursor cursor = db.query(
                TABLE_NAME,
                null,  // columns (null for all)
                selection,
                selectionArgs,
                null,  // groupBy
                null,  // having
                null   // orderBy
        );

        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    // Method to delete all entries in the database
    void deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    // Method to get the count of all rows in the table
    int getRowCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }
}
