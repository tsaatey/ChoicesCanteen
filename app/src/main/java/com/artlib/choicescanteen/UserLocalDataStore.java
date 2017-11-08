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

    public void storeUser(String username) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putString("username", username);
        editor.commit();
    }

    public String getUser() {
        String user = userLocalDataStore.getString("username", "");
        return user;
    }

    public void storeAdminId(String id) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putString("admin_id", id);
        editor.commit();
    }

    public void storeAdminEmail(String email) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putString("admin_email", email);
        editor.commit();
    }

    public void storeAdminPassword(String password) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putString("admin_password", password);
        editor.commit();
    }

    public String getAdminEmail() {
        String email = userLocalDataStore.getString("admin_email", "");
        return email;
    }

    public String getAdminPassword() {
        String password = userLocalDataStore.getString("admin_password", "");
        return password;
    }

    public String getAdminId() {
        String id = userLocalDataStore.getString("admin_id", "");
        return id;
    }

}
