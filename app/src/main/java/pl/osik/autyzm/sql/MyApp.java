package pl.osik.autyzm.sql;

import android.app.Application;
import android.content.Context;

/**
 * Created by m.osik2 on 2016-04-21.
 */
public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}