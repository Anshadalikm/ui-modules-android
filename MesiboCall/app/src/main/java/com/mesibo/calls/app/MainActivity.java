package com.mesibo.calls.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;



public class MainActivity extends AppCompatActivity implements Mesibo.ConnectionListener, MesiboCall.IncomingListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = "your app token";

        /* initialize mesibo */
        Mesibo mesibo = Mesibo.getInstance();
        mesibo.init(getApplicationContext());
        mesibo.setAccessToken(token);
        boolean res = mesibo.setDatabase("callapp.db", 0);
        mesibo.addListener(this);
        Mesibo.start();

        /* initialize call */
        MesiboCall.getInstance().init(this);

        String destination =  "destination";

        /* set profile so that it is visible in call screen */
        Mesibo.UserProfile u = new Mesibo.UserProfile();
        u.name = "Mabel Bay";
        u.address = destination;
        Mesibo.setUserProfile(u, false);

        FloatingActionButton fab_v = (FloatingActionButton) findViewById(R.id.fab_videocall);
        fab_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCustomCallActivity(destination, true, false);
            }
        });

        FloatingActionButton fab_a = (FloatingActionButton) findViewById(R.id.fab_audiocall);
        fab_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCustomCallActivity(destination, false, false);
            }
        });

        FloatingActionButton fab_m = (FloatingActionButton) findViewById(R.id.fab_message);
        fab_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mesibo.MessageParams params = new Mesibo.MessageParams();
                params.peer = destination;
                Mesibo.sendMessage(params, Mesibo.random(), "Hello from mesibo calls");
            }
        });
    }

    protected void launchCustomCallActivity(String destination, boolean video, boolean incoming) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("video", video);
        intent.putExtra("address", destination);
        intent.putExtra("incoming", incoming);
        startActivity(intent);
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        Log.d("Mesibo", "Connection status: " + status);
    }

    @Override
    public MesiboCall.CallProperties MesiboCall_OnIncoming(Mesibo.UserProfile profile, boolean video) {
        MesiboCall.CallProperties cc = MesiboCall.getInstance().createCallProperties(video);
        cc.parent = getApplicationContext();
        cc.user = profile;
        cc.className = CallActivity.class;
        return cc;
    }

    @Override
    public boolean MesiboCall_OnShowUserInterface(MesiboCall.Call call, MesiboCall.CallProperties cp) {
        launchCustomCallActivity(cp.user.address, cp.video.enabled, true);
        return true;
    }

    @Override
    public void MesiboCall_OnError(MesiboCall.CallProperties properties, int error) {

    }


    @Override
    public boolean MesiboCall_onNotify(int type, Mesibo.UserProfile profile, boolean video) {
        return false;
    }

}
