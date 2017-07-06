package com.example.absolutelysaurabh.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;

/**
 * Created by absolutelysaurabh on 8/4/17.
 */

//The ContentProvider acts as a layer between Database and the Activity to avoid dataCorruption or bugs as we'll only allow
//valid data to go into the database via the ContentProvider

//URI Uniform Resource Identifier secifies the resource we are interested in that is the query we have made.

//CatalogActivity-->ContentResolver--->PetProvider--->PetDbHelper(Database)
public class PetProvider extends ContentProvider{

    //As ContentProvider is an abstract methos so we need to implement methods that are insert, query,update,delete and getType();

    //Tag for the LOG messages
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private PetDbHelper mDbHelper;

    //Uri matcher code for the content Uri for the pets table
    private static final int PETS = 100;
    //URI matcher code for the content URI for single pet in the pets table
    private static final int PETS_ID = 101;

    //UriMatcher object to match a content Uri to a corresponding code.
    //The input passed into the constructor represents the code to return for the root URI
    //It's common to use NO_MATCH as the input for this case.

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer . This is run the first time anything is called from the class
    static{

        //The calls is to assUri() go here, for all of the content Uri patterns that the provider
        //should recognise . All paths added to the UriMatcher have a corresponding code to return
        //when a match is found

        // Add 2 content Uris in the URI matcher

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS,PETS);
        //The last one is the integer code for the URI;
        //below we are interested in a specific row in the table
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);


    }

    //Initialize the provider and the database helper onbject
    @Override
    public boolean onCreate() {

        //Create a PetDbHelper object to gain access to the pets database
        mDbHelper = new PetDbHelper(getContext());
        //Make it a global variable to make it referenced from othe methods of this class
        return true;
    }

    //Perform query of the given URI
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // tOD: Perform database query on pets table
                cursor  = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection,selectionArgs, null, null,sortOrder);
                break;

            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        return cursor;
    }
//    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
//                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//
//        //Only Uri parameter is enough too here.
//        //It'll return a cursor
//
//        //Get readable database
//        SQLiteDatabase database = mDbHelper.getReadableDatabase();
//
//        //This cursor will hold the result of the query
//        Cursor cursor;
//
//        //Figure out if the URI matcher can match the URI to a specific code
//        int match = sUriMatcher.match(uri);
//        switch(match){
//
//            case PETS:
//
//                cursor  = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection,selectionArgs, null, null,sortOrder);
//                break;
//
//            case PETS_ID:
//                selection  = PetContract.PetEntry._ID + "=?";
//                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
//
//
//                cursor = database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
//                break;
//
//            default:
//                throw new IllegalArgumentException("Cannot query unknown URI "+uri);
//
//        }
//
//        return cursor;
//    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    //Insert new data in the database of given ContentValues
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //URI tells where it shud be inserted
        //ContentValues tell what actuaklly gonna be inserted
        //Here this method will return the URL telling exactly where the values have been inserted
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        //Here we need only the URI
        //It'll also return the number of rows deleted
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        //needs where and what to update
        //It'll return the number of rown which were inserted intio the DB
        return 0;
    }
}
