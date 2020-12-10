package com.mesibo.messagingapp;

import android.app.Application;
import android.util.Log;

import com.mesibo.api.Mesibo;

/**
 * Created by Mesibo on 29/09/16.
 */

public class MainApplication extends Application implements Mesibo.ConnectionListener {
    public static final String TAG = "MesiboTestApplication";
    @Override
    public void onCreate() {
        super.onCreate();

        Mesibo api = Mesibo.getInstance();
        api.init(getApplicationContext());


        api.addListener(this);

        MesiboFileTransferHelper fileTransferHelper = new MesiboFileTransferHelper();
        api.addListener(fileTransferHelper);

        if(0 != api.setAccessToken("token")) {


            Log.d(TAG, "bad token: ");
        }

        api.setDatabase("messaging.db", 0);

        Mesibo.UserProfile u;

        u = new Mesibo.UserProfile();
        u.name = "Console 2";
        u.address = "18005551234";
        api.setUserProfile(u, false);

        u = new Mesibo.UserProfile();
        u.name = "Group 23";
        u.address = null;
        u.groupid = 23;
        api.setUserProfile(u, false);

        api.setUserProfile(u, false);

        if(0 != api.start()) {

        }
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        Log.d(TAG, "on Mesibo Connection: " + status);
    }
}
