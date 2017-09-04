package com.appsomniac.newshour.data;

/**
 * Created by absolutelysaurabh on 15/8/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class NewsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 14;

    public static final String TABLE_NAME_HEAD = "headlineNews";
    public static final String TABLE_NAME_TECH = "techNews";
    public static final String TABLE_NAME_BOOKMARK = "bookmarks";

    public static final String COLUMN_ID = "_id";

    public static Context mContext;

    public final static String COLUMN_NEWS_TITLE = "news_title";
    public final static String COLUMN_NEWS_DESC = "news_desc";
    public final static String COLUMN_NEWS_URL = "news_url";
    public final static String COLUMN_NEWS_URLTOIMAGE = "news_urlToImage";
    public final static String COLUMN_NEWS_PUBLISHEDAT = "news_publishedAt";
    public final static String COLUMN_IS_BOOKMARKED = "is_bookmarked";
    public final static String ITEM_INDEX = "item_index";


    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_NEWS_TABLE_HEAD =  "CREATE TABLE " + TABLE_NAME_HEAD + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                + COLUMN_NEWS_DESC + " TEXT, "
                + COLUMN_NEWS_URL + " TEXT NOT NULL, "
                + COLUMN_NEWS_URLTOIMAGE + " TEXT,"
                + COLUMN_NEWS_PUBLISHEDAT + " TEXT,"
                + COLUMN_IS_BOOKMARKED + " INTEGER DEFAULT 0);";

        String SQL_CREATE_NEWS_TABLE_BOOKMARK =  "CREATE TABLE " + TABLE_NAME_BOOKMARK + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_INDEX + " INTEGER, "
                + COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                + COLUMN_NEWS_DESC + " TEXT, "
                + COLUMN_NEWS_URL + " TEXT NOT NULL, "
                + COLUMN_NEWS_URLTOIMAGE + " TEXT,"
                + COLUMN_NEWS_PUBLISHEDAT + " TEXT);";

        String SQL_CREATE_NEWS_TABLE_TECH =  "CREATE TABLE " + TABLE_NAME_TECH + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NEWS_TITLE + " TEXT NOT NULL, "
                + COLUMN_NEWS_DESC + " TEXT, "
                + COLUMN_NEWS_URL + " TEXT NOT NULL, "
                + COLUMN_NEWS_URLTOIMAGE + " TEXT,"
                + COLUMN_NEWS_PUBLISHEDAT + " TEXT,"
                + COLUMN_IS_BOOKMARKED + " INTEGER DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NEWS_TABLE_HEAD);
        db.execSQL(SQL_CREATE_NEWS_TABLE_BOOKMARK);
        db.execSQL(SQL_CREATE_NEWS_TABLE_TECH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HEAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TECH);

        onCreate(db);
    }

    public boolean insertNewsHead(String title, String desc, String url, String url_to_image,
                                  String publishedAt, int isBookmarked) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);
        values.put(COLUMN_IS_BOOKMARKED, isBookmarked);

        db.insert(TABLE_NAME_HEAD, null, values);
        return true;
    }
    public boolean insertNewsTech(String title, String desc, String url, String url_to_image,
                                  String publishedAt, int isBookmarked, int item_index) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);
        values.put(COLUMN_IS_BOOKMARKED, isBookmarked);

        db.insert(TABLE_NAME_TECH, null, values);
        return true;
    }

    public boolean insertNewsBookmark(String title, String desc, String url, String url_to_image,
                                      String publishedAt, int item_index) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ITEM_INDEX, item_index);
        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);

        db.insert(TABLE_NAME_BOOKMARK, null, values);
        return true;
    }

    public void deleteAllBookmarks(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_BOOKMARK );

        SharedPreferences prefs = mContext.getSharedPreferences("bookmarksPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bookmarks_item_index", 0);
        editor.commit();


    }

    public int numberOfRowsInBookmarks() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_BOOKMARK);
        return numRows;
    }

    public boolean updateNewsTech(Integer id, String title, String desc, String url, String url_to_image,
                                  String publishedAt, int isBookmarked) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NEWS_TITLE, title);
        values.put(COLUMN_NEWS_DESC, desc);
        values.put(COLUMN_NEWS_URL, url);
        values.put(COLUMN_NEWS_URLTOIMAGE, url_to_image);
        values.put(COLUMN_NEWS_PUBLISHEDAT, publishedAt);
        values.put(COLUMN_IS_BOOKMARKED, isBookmarked);

        db.update(TABLE_NAME_TECH, values, COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteBookmark(int position) {

        //delete the data from the bookmarks table where item_index = position;
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME_BOOKMARK + " WHERE " +
                ITEM_INDEX + " = " + position + ";");

        db.execSQL("UPDATE " + TABLE_NAME_BOOKMARK + " SET " + ITEM_INDEX + " = " +
                ITEM_INDEX + " -1 " + " WHERE " + ITEM_INDEX + " > " + position + ";");

        SharedPreferences prefs = mContext.getSharedPreferences("bookmarksPrefs", MODE_PRIVATE);
        int bookmarks_index = prefs.getInt("bookmarks_item_index", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bookmarks_item_index", bookmarks_index-1);
        editor.commit();

        db.close();

    }

    public void deleteAllRecordHead(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_HEAD);
    }

    public void deleteAllRecordTech(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME_TECH);
    }

    public Cursor getNews(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TABLE_NAME_HEAD + " WHERE " +
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return res;
    }

    public Cursor getAllPersons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_NAME_HEAD, null );
        return res;
    }
}