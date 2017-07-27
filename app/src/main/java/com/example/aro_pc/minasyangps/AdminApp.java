package com.example.aro_pc.minasyangps;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Aro-PC on 7/27/2017.
 */

public class AdminApp extends MultiDexApplication {
    private static AdminApp instance;

    public static AdminApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
