package com.artlib.choicescanteen;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by ARTLIB on 06/07/2017.
 */

public class UserLocalDataStore {

    public static final String SP_NAME = "ChoicesCanteen";
    SharedPreferences userLocalDataStore;

    public UserLocalDataStore(Context context) {
        userLocalDataStore = context.getSharedPreferences(SP_NAME, 0);
    }

    // Method to clear user data from the system when logged out
    public void clearUserData() {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.clear();
        editor.commit();
    }

    public void isAdminLoggedIn(boolean id){
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putBoolean("userIsAdmin", id);
        editor.commit();
    }

    public boolean getIsAdminLoggedIn() {
        boolean userIsAdmin = userLocalDataStore.getBoolean("userIsAdmin", false);
        return userIsAdmin;
    }

    public void storeFoodItemId(String id) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putString("foodItemId", id);
        editor.commit();
    }

    public void storeBackPressed (boolean back) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putBoolean("isBackPressed", back);
        editor.commit();
    }

    public boolean getBackPressed() {
        boolean back = userLocalDataStore.getBoolean("isBackPressed", false);
        return back;
    }

}
