/* By obtaining and/or copying this work, you agree that you have read,
 * understood and will comply with the following terms and conditions.
 *
 * Copyright (c) 2020 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the terms and condition mentioned
 * on https://mesibo.com as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions, the following disclaimer and links to documentation and
 * source code repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/ui-modules-android

 */
package com.mesibo.messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mesibo.mediapicker.MediaPicker;

import java.lang.ref.WeakReference;

import static com.mesibo.messaging.MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD;

public class MesiboUIManager {

    private static  boolean mTestMode = false;
    public static void setTestMode(boolean testMode) {
        mTestMode = testMode;
    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle, String forwardMessage) {
        Intent intent = new Intent(context, MesiboActivity.class);
        intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
            .putExtra(MesiboUI.MESSAGE_ID, forwardid)
            .putExtra(MesiboUI.START_IN_BACKGROUND, startInBackground)
            .putExtra(MesiboUI.KEEP_RUNNING, keepRunning);

        if(!TextUtils.isEmpty(forwardMessage)) {
            intent.putExtra(MesiboUI.MESSAGE_CONTENT, forwardMessage);
        }

        if(flag > 0)
            intent.setFlags(flag);

        if(null != bundle)
            intent.putExtra(MesiboUI.BUNDLE,bundle);
        context.startActivity(intent);
    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle) {
        launchContactActivity(context, forwardid, selectionMode, flag, startInBackground, keepRunning, bundle, null);
    }

    public static void launchContactActivity(Context context, int selectionMode, long[] mids) {
        Intent intent = new Intent(context, MesiboActivity.class);
        intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
                .putExtra(MesiboUI.MESSAGE_IDS, mids);
        context.startActivity(intent);
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        Intent intent = new Intent(context, MesiboActivity.class);
        intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MODE_SELECTCONTACT_FORWARD)
                .putExtra(MesiboUI.MESSAGE_CONTENT, forwardMessage)
                .putExtra(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);
        context.startActivity(intent);
    }


    public static void launchGroupActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CreateNewGroupActivity.class);
        if(null != bundle)
            intent.putExtra(MesiboUI.BUNDLE,bundle);
        context.startActivity(intent);
    }

    public static void launchPictureActivity(Context context, String title, String filePath) {
        MediaPicker.launchImageViewer((AppCompatActivity)context, filePath);
        //MediaPicker.launchImageViewer((AppCompatActivity)context, filePath);

    }

    public static void launchMessagingActivity(Context context, long forwardid, String peer, long groupid) {
        if(mTestMode) {
            launchMessagingActivityNew(context, forwardid, peer, groupid);
            return;
        }

        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        intent.putExtra(MesiboUI.MESSAGE_ID, forwardid);
        context.startActivity(intent);

        if(null != mMessagingActivity) {
            MessagingActivity oldActivity = mMessagingActivity.get();
            if (null != oldActivity)
                oldActivity.finish();
        }
    }

    public static void launchMessagingActivityNew(Context context, long forwardid, String peer, long groupid) {
        Intent intent = new Intent(context, MessagingActivityNew.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        intent.putExtra(MesiboUI.MESSAGE_ID, forwardid);
        context.startActivity(intent);

        if(null != mMessagingActivityNew) {
            MessagingActivityNew oldActivity = mMessagingActivityNew.get();
            if (null != oldActivity)
                oldActivity.finish();
        }
    }

    public static void launchPlacePicker(Context context, Intent intent, int REQUEST_CODE) {
        ((AppCompatActivity)context).startActivityForResult(intent, REQUEST_CODE);
    }

    public static void launchImageEditor(Context context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, MediaPicker.ImageEditorListener listener){
        MediaPicker.launchEditor((AppCompatActivity)context, type, drawableid, title, filePath, showEditControls, showTitle, showCropOverlay, squareCrop, maxDimension, listener);
    }

    private static WeakReference<MessagingActivity> mMessagingActivity = null;
    public static void setMessagingActivity(MessagingActivity activity) {
        mMessagingActivity = new WeakReference<MessagingActivity>(activity);
    }

    private static WeakReference<MessagingActivityNew> mMessagingActivityNew = null;
    public static void setMessagingActivityNew(MessagingActivityNew activity) {
        mMessagingActivityNew = new WeakReference<MessagingActivityNew>(activity);
    }

}
