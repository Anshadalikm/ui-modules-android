package com.mesibo.calls.ui;

import android.content.Context;
import android.content.Intent;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.ui.CallLogs.CallLogsActivity;

import java.lang.ref.WeakReference;

import static org.webrtc.ContextUtils.getApplicationContext;

public class MesiboCallUi implements MesiboCall.IncomingListener {
    public interface Listener {
        MesiboCall.CallProperties MesiboCallUi_OnConfig(MesiboCall.CallProperties cp);
        boolean MesiboCallUi_OnError(MesiboCall.CallProperties cp, int error);
        boolean MesiboCallUi_onNotify(int type, Mesibo.UserProfile profile, boolean video);
    }

    private static MesiboCallUi _instance = null;
    public static MesiboCallUi getInstance() {
        if(null==_instance)
            synchronized(MesiboCall.class) {
                if(null == _instance) {
                    _instance = new MesiboCallUi();
                }
            }

        return _instance;
    }

    private WeakReference<Listener> mListener = null;
    public void setListener(Listener listener) {
        mListener = new WeakReference<Listener> (listener);
    }

    public Listener getListener() {
        if(null == mListener) return null;
        return mListener.get();
    }

    private Context mAppContext = null;
    public void init(Context context) {
        mAppContext = context;
        /* initialize call */
        MesiboCall.getInstance().init(context);
        MesiboCall.getInstance().setListener(this);
    }

    public static void call(Context parent, String destination, boolean video) {
         //MesiboCall.getInstance().launchCallActivity(parent, CallActivity.class, destination, video);
        MesiboCall.getInstance().callUi(parent, destination, video);
    }

    /* set up call infomation and class name of activity to be launched. You can also pass
           an existing activity. In that case, MesiboCall_OnIncoming will be called again ith the call
           object which you can use in your activity.
         */

    @Override
    public MesiboCall.CallProperties MesiboCall_OnIncoming(Mesibo.UserProfile userProfile, boolean video) {

        MesiboCall.CallProperties cc = MesiboCall.getInstance().createCallProperties(video);
        cc.parent = getApplicationContext();
        cc.user = userProfile;
        cc.className = CallActivity.class;


        MesiboCallUi.Listener cul = MesiboCallUi.getInstance().getListener();
        if(null != cul)
            cul.MesiboCallUi_OnConfig(cc);

        return cc;
    }

    @Override
    public boolean MesiboCall_OnShowUserInterface(MesiboCall.Call call, MesiboCall.CallProperties cp) {
        return false; // launch default UI
    }

    @Override
    public void MesiboCall_OnError(MesiboCall.CallProperties cp, int error) {
        MesiboCallUi.Listener cul = MesiboCallUi.getInstance().getListener();
        if(null != cul)
            cul.MesiboCallUi_OnError(cp, error);
    }

    @Override
    public boolean MesiboCall_onNotify(int type, Mesibo.UserProfile userProfile, boolean vidoe) {
        MesiboCallUi.Listener cul = MesiboCallUi.getInstance().getListener();
        if(null != cul)
            cul.MesiboCallUi_onNotify(type, userProfile, vidoe);
        return true;
    }

    public boolean isCallInProgress() {
        return MesiboCall.getInstance().isCallInProgress();
    }

    public MesiboCall getCallApi() {
        return MesiboCall.getInstance();
    }

    public void launchCallLogs(Context context, int flag) {

        Intent intent = new Intent(context, CallLogsActivity.class);

        if(flag > 0)
            intent.setFlags(flag);
        context.startActivity(intent);

    }
}
