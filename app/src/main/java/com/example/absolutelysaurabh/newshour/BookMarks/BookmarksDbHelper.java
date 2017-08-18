package com.example.absolutelysaurabh.newshour.BookMarks;

/**
 * Created by absolutelysaurabh on 15/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by obaro on 02/04/2015.
 */
public class BookmarksDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_NAME = "bookmarks";
    public static final String COLUMN_ID = "_id";

    public final static String COLUMN_NEWS_TITLE = "news_title";
    public final static String COLUMN_NEWS_DESC = "news_desc";
    public final static String COLUMN_NEWS_URL = "news_url";
    public final static String COLUMN_NEWS_URLTOIMAGE = "news_urlToImage";
    public final static String COLUMN_NEWS_PUBLISHEDAT = "news_publishedAt";


    public BookmarksDbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_NEWS_TABLE =  "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                + COLUMN_NEWS_DESC + " TEXT, "
                + COLUMN_NEWS_URL + " TEXT NOT NULL, "
                + COLUMN_NEWS_URLTOIMAGE + " TEXT,"
                + COLUMN_NEWS_PUBLISHEDAT + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertNews(String title, String desc, String url, String url_to_image, String publishedAt) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);
        db.insert(TABLE_NAME, null, values);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updatePerson(Integer id, String title, String desc, String url, String url_to_image, String publishedAt) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);

        db.update(TABLE_NAME, values, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePerson(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }


    public Cursor getNews(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        return res;
    }

    public Cursor getAllPersons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME, null );
        return res;
    }
}