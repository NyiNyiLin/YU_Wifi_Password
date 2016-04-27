package com.nyi.yuwifipassword;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by IN-3442 on 27-Apr-16.
 */
public class YUWifiPassword extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Firebase.setAndroidContext(this);
    }

    public static Context getContext() {
        return context;
    }
}
