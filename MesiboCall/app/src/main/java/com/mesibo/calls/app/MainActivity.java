package com.mesibo.calls.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;



public class MainActivity extends AppCompatActivity implements Mesibo.ConnectionListener, MesiboCall.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

	/* refer https://mesibo.com/documentation/tutorials/get-started/ */
        String token = "<usertoken>";

        /* initialize mesibo */
        Mesibo mesibo = Mesibo.getInstance();
        mesibo.init(getApplicationContext());
        mesibo.setAccessToken(token);
        boolean res = mesibo.setDatabase("callapp.db", 0);
        mesibo.addListener(this);
        Mesibo.start();

        /* initialize call */
        MesiboCall.getInstance().init(this);

        String destination =  "18005551234";

        /* set profile so that it is visible in call screen */
        Mesibo.UserProfile u = new Mesibo.UserProfile();
        u.name = "Mabel Bay";
        u.address = destination;
        Mesibo.setUserProfile(u, false);

        FloatingActionButton fab_v = (FloatingActionButton) findViewById(R.id.fab_videocall);
        fab_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean videoCall = true;
                MesiboCall.getInstance().launchCallActivity(MainActivity.this, CallActivity.class,
                        destination, true);
            }
        });

        FloatingActionButton fab_a = (FloatingActionButton) findViewById(R.id.fab_audiocall);
        fab_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MesiboCall.getInstance().launchCallActivity(MainActivity.this, CallActivity.class,
                        destination, false);
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


    @Override
    public void Mesibo_onConnectionStatus(int status) {
        Log.d("Mesibo", "Connection status: " + status);
    }

    @Override
    public MesiboCall.CallContext MesiboCall_OnIncoming(MesiboCall.Call call, Mesibo.UserProfile profile, boolean video) {
        MesiboCall.CallContext cc;
        if(call != null) {
            /* launch activity - will be called only if activity was passed*/
            return null;
        }

        /* set up call infomation and class name of activity to be launched. You can also pass
           an existing activity. In that case, MesiboCall_OnIncoming will be called again ith the call
           object which you can use in your activity.
         */
        cc = new MesiboCall.CallContext(video);
        cc.parent = getApplicationContext();
        cc.className = CallActivity.class;
        /* setup other parameters as required */
        return cc;
    }

    @Override
    public boolean MesiboCall_OnError(MesiboCall.CallContext ctx, int error) {
        return false;
    }

    @Override
    public boolean MesiboCall_onNotify(int type, Mesibo.UserProfile profile, boolean video) {
        /* notify user for msised calls, etc. */
        return false;
    }

}
