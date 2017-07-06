package dd.com.myq.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.login.LoginManager;

import java.util.HashMap;

import dd.com.myq.Activity.LoginActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SessionManager {


    SharedPreferences pref;
    static SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "MyQPref";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_UID = "UID";

    public static final String KEY_USERNAME = "UserName";

    public static final String KEY_ABOUTME = "AboutMe";

    public static final String KEY_DOB = "dob";

    public static final String KEY_EMAIL = "Email";

    public static final String KEY_GENDER = "Gender";

    public static final String KEY_PROFILE_PIC = "ProfilePic";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String uid, String username,String profile_pic,String email,String aboutme,String dob,String gender){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PROFILE_PIC, profile_pic);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ABOUTME, aboutme);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_GENDER, gender);
        editor.commit();
    }

    public void UpdateLoginSession(String uid, String username,String email,String aboutme,String dob,String gender){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ABOUTME, aboutme);
        editor.putString(KEY_DOB, dob);

        editor.putString(KEY_GENDER, gender);
        editor.commit();
    }


    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_UID, pref.getString(KEY_UID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_ABOUTME, pref.getString(KEY_ABOUTME, null));
        user.put(KEY_DOB, pref.getString(KEY_DOB, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));

        return user;
    }

    public static void logoutUser(){

        editor.clear();
        editor.commit();
        LoginManager.getInstance().logOut();


//        Intent i = new Intent(_context, LoginActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        _context.startActivity(i);
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}