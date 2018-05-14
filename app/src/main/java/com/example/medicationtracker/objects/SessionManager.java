package com.example.medicationtracker.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/*
 * Stores a boolean indicating whether user is logged in
 */

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();
    private SharedPreferences pref;
    private static final int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "MedicationTrackerLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void setLogin(boolean isLoggedIn) {
        Editor editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.apply();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
