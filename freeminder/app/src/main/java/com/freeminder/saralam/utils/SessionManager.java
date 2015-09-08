package com.freeminder.saralam.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Sasikumar Reddy on 07-09-2015.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "freeminder";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_GMAIL = "gmail";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_SERVICE = "service";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_PARSEOBJECTID = "parse_objectid";
    public static final String TIME_PERIOD = "set";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create password
     * */
    public void saveSetting(String name, String gmail, String password, String service, String number){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_GMAIL, gmail);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_SERVICE,service);
        editor.putString(KEY_NUMBER,number);
        //editor.putString(KEY_PARSEOBJECTID,objectid);
        // commit changes
        editor.commit();
    }

    /**
     * Get stored data
     * */
    public HashMap<String, String> getSetting(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_GMAIL, pref.getString(KEY_GMAIL, ""));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, ""));
        user.put(KEY_SERVICE, pref.getString(KEY_SERVICE, ""));
        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, ""));
        user.put(KEY_PARSEOBJECTID, pref.getString(KEY_PARSEOBJECTID, ""));

        // return user
        return user;
    }

    public void setTimePeriod(String set){
        editor.putString(TIME_PERIOD, set);

        editor.commit();
    }

    public String getTimePeriod(){

        return pref.getString(TIME_PERIOD, "installation");

    }
    public void setParseObjectId(String parseId){
        editor.putString(KEY_PARSEOBJECTID, parseId);
        // commit changes
        editor.commit();
    }

    /*
        Login session
    */


    public boolean isLoggedIn() {
        if(pref.getString(KEY_PARSEOBJECTID,"")!=null || !pref.getString(KEY_PARSEOBJECTID,"").isEmpty() )
            return pref.getBoolean(IS_LOGIN, false);
        else
            return pref.getBoolean(IS_LOGIN,true);
    }

    public void createLoginSession(String email, String parseId) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_GMAIL, email);

        //updating the parseobjectId
        editor.putString(KEY_PARSEOBJECTID, parseId);
        // commit changes
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_GMAIL, null);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

}
