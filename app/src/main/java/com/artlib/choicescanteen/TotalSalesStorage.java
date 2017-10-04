package com.artlib.choicescanteen;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ARTLIB on 04/10/2017.
 */

public class TotalSalesStorage {
    public static final String SP_NAME = "ChoicesCanteen";
    SharedPreferences userLocalDataStore;

    public TotalSalesStorage(Context context) {
        userLocalDataStore = context.getSharedPreferences(SP_NAME, 0);
    }

    public void setTotalAmount(float amount) {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.putFloat("total", amount);
        editor.commit();
    }

    public float getTotalAmount() {
        float num = userLocalDataStore.getFloat("total", 0);
        return num;
    }

    // Method to clear user data from the system when logged out
    public void clearData() {
        SharedPreferences.Editor editor = userLocalDataStore.edit();
        editor.clear();
        editor.commit();
    }
}
