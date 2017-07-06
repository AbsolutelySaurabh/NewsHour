package com.example.absolutelysaurabh.petsapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by absolutelysaurabh on 3/4/17.
 */

public class PetContract {

    private PetContract(){}

    //It;s the package name
    public static final String CONTENT_AUTHORITY = "com.example.absolutelysaurabh.petsapp.data";
    //It's the scheme addition in the Content_Authprity
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //It specifies the TableName
    public static final String PATH_PETS = "pets";

    public static final class PetEntry implements BaseColumns{

        //The content uri to access the pet data in the perovider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);


        public final static String TABLE_NAME = "pets";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";

        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_GENDER = "gender";

        public final static String COLUMN_PET_WEIGHT = "weight";

        public final static int GENDER_UNKNOWN =0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;


    }
}
