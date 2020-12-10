package com.mesibo.messagingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.mesibo.api.Mesibo;
import com.mesibo.messaging.MesiboActivity;
import com.mesibo.messaging.MesiboUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Mesibo.UIHelperListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mesibo.addListener(this);

        //MesiboUI.launchForwardActivity(this, "test");
       // if(true)
       //     return;

        MesiboUI.setTestMode(true);
        Intent myIntent = new Intent(MainActivity.this, MesiboActivity.class);
        MainActivity.this.startActivity(myIntent);
        MesiboUI.setEnableProfiling(true);

    }

    @Override
    public int Mesibo_onGetMenuResourceId(Context context, int type, Mesibo.MessageParams params, Menu menu) {
        if (type == 0) // Setting menu in userl ist
            return R.menu.messaging_activity_menu;
        else // from User chatbox
            return R.menu.menu_messaging;
    }

    @Override
    public boolean Mesibo_onMenuItemSelected(Context context, int type, Mesibo.MessageParams params, int resourceid) {
        if (type == 0) { // from userlist
            if (resourceid == R.id.action_settings) {
            }
        } else { 

        }

        return false;
    }

    @Override
    public void Mesibo_onShowProfile(Context context, Mesibo.UserProfile userProfile) {

    }

    @Override
    public void Mesibo_onDeleteProfile(Context context, Mesibo.UserProfile userProfile, Handler handler) {

    }

    @Override
    public void Mesibo_onSetGroup(Context context, long l, String s, int i, String s1, String s2, String[] strings, Handler handler) {

    }

    @Override
    public void Mesibo_onGetGroup(Context context, long l, Handler handler) {

    }

    @Override
    public ArrayList<Mesibo.UserProfile> Mesibo_onGetGroupMembers(Context context, long l) {
        return null;
    }

    @Override
    public void Mesibo_onForeground(Context context, int id, boolean foreground) {

    }
}
