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
import android.graphics.Bitmap;
import android.os.Bundle;

import com.mesibo.messaging.AllUtils.MyTrace;

public class MesiboUI {



    public static class Config {
        public Bitmap contactPlaceHolder = null;
        public Bitmap messagingBackground = null;

        public boolean useLetterTitleImage = true;

        public boolean enableVoiceCall = false;
        public boolean enableVideoCall = false;
        public boolean enableForward = true;
        public boolean enableSearch = true;
        public boolean enableBackButton = false;

        public String messageListTitle = "Messages";
        public String userListTitle = "Contacts";
        public String createGroupTitle = "Create a New Group";
        public String modifyGroupTitle = "Modify Group details";
        public String selectContactTitle = "Select a contact";
        public String selectGroupContactsTitle = "Select group members";
        public String forwardTitle = "Forward To";
        //public String messagingTitle = "";

        //This is for remote user
        public String userOnlineIndicationTitle = "online";

        // This is for connection online
        public String onlineIndicationTitle = null;
        public String offlineIndicationTitle = "Not connected";
        public String connectingIndicationTitle = "Connecting...";
        public String noNetworkIndicationTitle = "No Network";

        public String emptyUserListMessage = "No Messages";

        public boolean showRecentInForward = true;
        public boolean mConvertSmilyToEmoji = true;

        public int[] mLetterTitleColors = null;

        public int mToolbarColor = 0;
        public int mStatusbarColor = 0;
        public int mToolbarTextColor = 0;
        public int mUserListTypingIndicationColor = 0xFF499944;
        public int mUserListStatusColor = 0xFF868686;

        public long mTypingIndicationTimeMS = 10000;

        public long mMaxImageFileSize = 300*1024;
        public long mMaxVideoFileSize = 20*1024*1024;

        public boolean mEnableNotificationBadge = true;

        public Config() {
        }
    }

    public static final String BUNDLE = "bundle";
    public static final String MESSAGE_ID = "mid";
    public static final String MESSAGE_IDS = "mids";
    public static final String MESSAGE_CONTENT = "message";
    public static final String GROUP_ID = "groupid";
    public static final String GROUP_NAME = "groupname";
    public static final String GROUP_MODE = "groupmode";
    public static final String PEER = "peer";
    public static final String MEMBERS = "members";
    public static final String PICTURE_PATH = "picturepath";
    public static final String KEEP_RUNNING = "keep_running";
    public static final String START_IN_BACKGROUND = "start_in_background";
    public static final String FORWARD_AND_CLOSE = "forwardandclose";


    public static Config mConfig = new Config();

    public static Config getConfig() {
        return mConfig;
    }

    public static void launch(Context context, int flag, boolean startInBackground, boolean keepRunnig) {
        MesiboUIManager.launchContactActivity(context, 0, MesiboUserListFragment.MODE_MESSAGELIST, flag, startInBackground, keepRunnig, null);
    }

    public static void launchContacts(Context context, long forwardid, int selectionMode, int flag, Bundle bundle) {
        MesiboUIManager.launchContactActivity(context, forwardid, selectionMode, flag, false, false, bundle);
    }

    public static void launchContacts(Context context, long forwardid, int selectionMode, int flag, Bundle bundle, String forwardMessage) {
        MesiboUIManager.launchContactActivity(context, forwardid, selectionMode, flag, false, false, bundle, forwardMessage);
    }

    public static void launchMessageView(Context context, String peer, long groupid) {
        MesiboUIManager.launchMessagingActivity(context, 0, peer, groupid);
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        MesiboUIManager.launchForwardActivity(context, forwardMessage, forwardAndClose);
    }

    public static String version() {
        return BuildConfig.BUILD_VERSION;
    }

    public static void setTestMode(boolean testMode) {
        MesiboUIManager.setTestMode(testMode);
    }

    public static void setEnableProfiling(boolean enabled) {
        MyTrace.setEnable(enabled);
    }
}
