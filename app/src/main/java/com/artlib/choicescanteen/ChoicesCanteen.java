package com.artlib.choicescanteen;

import android.app.Application;
import android.support.multidex.MultiDex;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ARTLIB on 30/09/2017.
 */

public class ChoicesCanteen extends Application {
    @Override
    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
