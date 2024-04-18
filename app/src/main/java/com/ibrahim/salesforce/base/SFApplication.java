package com.ibrahim.salesforce.base;

import android.content.Context;
import android.content.res.Resources;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;



//import cat.ereza.customactivityoncrash.config.CaocConfig;

public class SFApplication extends MultiDexApplication {

    private static SFApplication instance;
    public static Context context;
    public static String TAG = "SalesForce";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SFApplication.context = getApplicationContext();
        SFApplication.instance = this;
       //new UCEHandler.Builder(this).build();

//
//        CaocConfig.Builder.create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
//                .apply();
//
//        FirebaseApp.initializeApp(this);

        //Fabric.with(this, new Crashlytics());
//        Log.i("TAGG", "SERIAL: " + Build.SERIAL);
//        Log.i("TAGG", "MODEL: " + Build.MODEL);
//        Log.i("TAGG", "ID: " + Build.ID);
//        Log.i("TAGG", "Manufacture: " + Build.MANUFACTURER);
//        Log.i("TAGG", "brand: " + Build.BRAND);
//        Log.i("TAGG", "type: " + Build.TYPE);
//        Log.i("TAGG", "user: " + Build.USER);
//        Log.i("TAGG", "BASE: " + Build.VERSION_CODES.BASE);
//        Log.i("TAGG", "INCREMENTAL " + Build.VERSION.INCREMENTAL);
//        Log.i("TAGG", "SDK  " + Build.VERSION.SDK_INT);
//        Log.i("TAGG", "BOARD: " + Build.BOARD);
//        Log.i("TAGG", "BRAND " + Build.BRAND);
//        Log.i("TAGG", "HOST " + Build.HOST);
//        Log.i("TAGG", "FINGERPRINT: " + Build.FINGERPRINT);
//        Log.i("TAGG", "Version Code: " + Build.VERSION.RELEASE);
//        Log.i("TAGG", "Version Code: " + Build.VERSION.RELEASE);
//        Log.i("TAGG", "Version Code: " + Build.VERSION.RELEASE);
//        Log.i("TAGG", "Hardware: " + Build.HARDWARE);
    }

    /**
     * Instantiates a new Sales Force application.
     */
    public SFApplication() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SFApplication getInstance() {
        return SFApplication.instance;
    }

    /**
     * Gets app context.
     *
     * @return the app context
     */
    public static Context getAppContext() {
        return instance;
    }

    public static Resources getAppResources() {
        return instance.getResources();
    }
}
