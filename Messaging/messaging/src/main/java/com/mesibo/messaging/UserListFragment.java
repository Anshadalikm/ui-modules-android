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
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.messaging.AllUtils.LetterTileProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.os.Looper.getMainLooper;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLINCOMING;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLOUTGOING;
import static com.mesibo.messaging.MesiboActivity.TAG;
import static com.mesibo.messaging.MesiboConfiguration.ALL_USERS_STRING;
import static com.mesibo.messaging.MesiboConfiguration.ATTACHMENT_ICON;
import static com.mesibo.messaging.MesiboConfiguration.ATTACHMENT_STRING;
import static com.mesibo.messaging.MesiboConfiguration.AUDIO_STRING;
import static com.mesibo.messaging.MesiboConfiguration.DELETED_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.EMPTY_MESSAGE_LIST;
import static com.mesibo.messaging.MesiboConfiguration.EMPTY_SEARCH_LIST;
import static com.mesibo.messaging.MesiboConfiguration.EMPTY_USER_LIST;
import static com.mesibo.messaging.MesiboConfiguration.FREQUENT_USERS_STRING;
import static com.mesibo.messaging.MesiboConfiguration.GROUP_MEMBERS_STRING;
import static com.mesibo.messaging.MesiboConfiguration.IMAGE_ICON;
import static com.mesibo.messaging.MesiboConfiguration.IMAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.LOCATION_ICON;
import static com.mesibo.messaging.MesiboConfiguration.LOCATION_STRING;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_SEARCH_READ_USERLIST;
import static com.mesibo.messaging.MesiboConfiguration.MESSAGE_DELETED_STRING;
import static com.mesibo.messaging.MesiboConfiguration.MESSAGE_STRING_USERLIST_SEARCH;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VIDEO_CALL;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VOICECALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VOICE_CALL;
import static com.mesibo.messaging.MesiboConfiguration.USERS_STRING_USERLIST_SEARCH;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_ICON;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_STRING;
import static com.mesibo.messaging.MesiboImages.getMissedCallDrawable;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_EDITGROUP;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_MESSAGELIST;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_SELECTCONTACT;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_SELECTGROUP;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment implements Mesibo.MessageListener, Mesibo.ConnectionListener, Mesibo.UserProfileUpdateListener {

    RecyclerView mRecyclerView=null;
    MessageContactAdapter mAdapter=null;
    public static ArrayList<Mesibo.UserProfile> mMemberProfiles = new ArrayList<>();

    public UserListFragment() {
        // Required empty public constructor
    }

    public TextView mEmptyView ;
    public boolean mContactView =false;
    public long mForwardId = 0;
    private Boolean mIsMessageSearching=false;
    private LinearLayout mforwardLayout;
    private ArrayList<Mesibo.UserProfile> mUserProfiles = null;
    private ArrayList<Mesibo.UserProfile> mSearchResultList = null;
    private ArrayList<Mesibo.UserProfile> mAdhocUserList = null;
    private String mSearchQuery = null;
    private String mReadQuery = null;
    private int mSelectionMode = 0;
    private Mesibo.UIHelperListner mMesiboUIHelperListener = null;
    private long[] mForwardMessageIds = null;
    private String mForwardedMessage = null;
    private boolean mCloseAfterForward = false;
    private WeakReference<MesiboUserListFragment.FragmentListener> mListener = null;

    Bundle mGroupEditBundle = null;
    Mesibo.UserProfile mGroupProfile = null;
    Set<String> mGroupMembers = null;
    long mGroupId = 0;
    MesiboUI.Config mMesiboUIOptions = null;
    LetterTileProvider mLetterTileProvider = null;

    private long mUiUpdateTimestamp = 0;
    private TimerTask mUiUpdateTimerTask = null;
    private Timer mUiUpdateTimer = null;
    private Handler mUiUpdateHandler = new Handler(getMainLooper());
    private Runnable mUiUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if(null != mAdapter) {
                mAdapter.notifyChangeInData();
            }
        }
    };

    private int mTotalUnread = 0;
    private Mesibo.ReadDbSession mDbSession = null;

    public static Set<String> getGroupMembers(String members) {
        if (TextUtils.isEmpty(members))
            return null;

        String[] s = members.split("\\:");
        if (null == s || s.length < 2)
            return null;

        String[] users = s[1].split("\\,");
        if (null == users)
            return null;

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < users.length; i++) {
            set.add(users[i]);
        }
        return set;
    }

    public void updateTitle(String title) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if(null == l)
            return;

        l.Mesibo_onUpdateTitle(title);
    }

    public void updateSubTitle(String title) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if(null == l)
            return;

        l.Mesibo_onUpdateSubTitle(title);
    }

    public boolean onClickUser(String address, long groupid, long forwardid) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if(null == l)
            return false;

        return l.Mesibo_onClickUser(address, groupid, forwardid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MesiboImages.init(getActivity());
        mMesiboUIHelperListener = Mesibo.getUIHelperListner();
        mMesiboUIOptions = MesiboUI.getConfig();


        //final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        mSelectionMode = MODE_MESSAGELIST;
        mReadQuery = null;

        Bundle b = this.getArguments();
        if(null != b) {
            mSelectionMode = b.getInt(MesiboUserListFragment.MESSAGE_LIST_MODE, MODE_MESSAGELIST);
            mReadQuery = b.getString("query", null);
        }

        if(mSelectionMode == MODE_SELECTCONTACT_FORWARD)
            mForwardId = getArguments().getLong(MesiboUI.MESSAGE_ID);
        if(mSelectionMode == MODE_SELECTCONTACT_FORWARD) {
            mForwardMessageIds = getArguments().getLongArray(MesiboUI.MESSAGE_IDS);
            mForwardedMessage = getArguments().getString(MesiboUI.MESSAGE_CONTENT);
            mCloseAfterForward = getArguments().getBoolean(MesiboUI.FORWARD_AND_CLOSE, false);
        }

        if(mSelectionMode== MODE_MESSAGELIST)
            updateTitle(mMesiboUIOptions.messageListTitle);
        else if(mSelectionMode == MODE_SELECTCONTACT)
            updateTitle(mMesiboUIOptions.selectContactTitle);
        else if(mSelectionMode == MODE_SELECTCONTACT_FORWARD)
            updateTitle(mMesiboUIOptions.forwardTitle);
        else if(mSelectionMode == MODE_SELECTGROUP)
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
        else if(mSelectionMode == MODE_EDITGROUP){
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
            mGroupEditBundle = getArguments().getBundle(MesiboUI.BUNDLE);
            if(null != mGroupEditBundle) {
                mGroupId = mGroupEditBundle.getLong(MesiboUI.GROUP_ID);
                mGroupProfile = Mesibo.getUserProfile(mGroupId);
                if(mGroupProfile != null)
                    mGroupMembers = getGroupMembers(mGroupProfile.groupMembers);
            }
        }

        if(mMesiboUIOptions.useLetterTitleImage)
            mLetterTileProvider =  new LetterTileProvider(getActivity(), 60, mMesiboUIOptions.mLetterTitleColors);
        mSearchResultList = new ArrayList<>();
        mUserProfiles = new ArrayList<>();
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_message_contact, container, false);
        setHasOptionsMenu(true);

        //getFillData();
        mforwardLayout = (LinearLayout) view.findViewById(R.id.bottom_forward_btn);
        mforwardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectionMode == MODE_SELECTGROUP) {
                    mAdapter.createNewGroup();
                }else if(mSelectionMode == MODE_EDITGROUP){
                    mAdapter.modifyGroupDetail();
                }else if(mSelectionMode == MODE_SELECTCONTACT_FORWARD) {
                    mAdapter.forwardMessageToContacts();
                }
            }
        });

        mEmptyView = (TextView) view.findViewById(R.id.emptyview_text);
        setEmptyViewText();

        mUserProfiles = new ArrayList<Mesibo.UserProfile>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id. message_contact_frag_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter =new MessageContactAdapter(getActivity(), this, mUserProfiles , mSearchResultList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void setListener(MesiboUserListFragment.FragmentListener listener) {
        mListener = new WeakReference<MesiboUserListFragment.FragmentListener>(listener);
    }

    public MesiboUserListFragment.FragmentListener getListener() {
        if(null == mListener)
            return null;

        return mListener.get();
    }

    public void setEmptyViewText() {
        if(mSelectionMode == MODE_MESSAGELIST)
            mEmptyView.setText(EMPTY_MESSAGE_LIST);
        else
            mEmptyView.setText(EMPTY_USER_LIST);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        if(mMesiboUIHelperListener == null)
            return ;

        //int menuResourceId = mMesiboUIHelperListener.Mesibo_onGetMenuResourceId(0);
        //inflater.inflate(menuResourceId, menu);
        mMesiboUIHelperListener.Mesibo_onGetMenuResourceId(getActivity(), 0, null, menu);

        MenuItem searchViewItem = menu.findItem(R.id.mesibo_search);

        if(null != searchViewItem && mMesiboUIOptions.enableSearch) {
            SearchView searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
            MenuItemCompat.setShowAsAction(searchViewItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setActionView(searchViewItem, searchView);
            searchView.setIconifiedByDefault(true);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.d("search View closed", "SEARCHVIEW");
                    mSearchQuery = null;
                    return false;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mAdapter.filter(newText);
                    mAdapter.notifyDataSetChanged();

                    return true;
                }
            });

            searchView.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                              }
                                          }
            );
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.mesibo_contacts) {
            MesiboUIManager.launchContactActivity(getActivity(), 0, MODE_SELECTCONTACT, 0, false, false, null);
        }else if (item.getItemId() == R.id.mesibo_search) {
            return false;
        }else {
            if(mMesiboUIHelperListener == null)
                return false;

            mMesiboUIHelperListener.Mesibo_onMenuItemSelected(getActivity(), 0, null, item.getItemId());
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mesibo_contacts);
        if(null != item && mSelectionMode != MODE_MESSAGELIST) {
            item.setVisible(false);
        }
    }
    public void showForwardLayout() {
        mforwardLayout.setVisibility(View.VISIBLE);
        return;
    }

    public void handleEmptyUserList (int userListsize){
        if(userListsize == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

    }

    public void hideForwardLayout() {
        mforwardLayout.setVisibility(View.GONE);
        return;
    }

    private void updateNotificationBadge() {
        if(!mMesiboUIOptions.mEnableNotificationBadge)
            return;
        try {
            if(mTotalUnread > 0)
                ShortcutBadger.applyCount(getActivity(), mTotalUnread);
            else
                ShortcutBadger.removeCount(getActivity());

        } catch (Exception e) {
            mMesiboUIOptions.mEnableNotificationBadge = false;
        }
    }

    public synchronized void addNewMessage(Mesibo.MessageParams params, String message) {

        //should only happen for db messages and deleted on unsynced group
        if(params.groupid > 0 && null == params.groupProfile)
            return;

        MesiboUserListFragment.FragmentListener l = getListener();
        if(null != l && l.Mesibo_onUserListFilter(params))
            return;

        UserData data = UserData.getUserData(params);
        if(null != data)
            data.clearTyping();

        // This is to inflate other title message like 2users or 6 messages match
        if(mIsMessageSearching && params.origin == Mesibo.ORIGIN_DBMESSAGE) {
            mAdhocUserList = mAdapter.getActiveUserlist();
            Mesibo.UserProfile mup = null;

            if (mAdhocUserList.size() > mAdapter.mCountProfileMatched + 1) {
                int i = mAdapter.mCountProfileMatched ==0?mAdapter.mCountProfileMatched:mAdapter.mCountProfileMatched+1;
                mup = mAdhocUserList.get(i);
                mup.name = String.valueOf(mAdhocUserList.size() - (i)) + " " + MESSAGE_STRING_USERLIST_SEARCH;
            } else {
                mup = new Mesibo.UserProfile();
                mup.name = "1"+ " " + MESSAGE_STRING_USERLIST_SEARCH;
                mAdhocUserList.add(mAdhocUserList.size(), mup);
            }
        }

        Mesibo.UserProfile user = params.profile;
        if(params.groupProfile != null)
            user = params.groupProfile;

        //This MUST not happen
        if(null == user) {
            Log.d(TAG, "Should not happen");
            //Mesibo.de
            //Mesibo.newUserProfile(params.peer, params.groupid, name);
        }


        // depending on whether we want to show user in search or group in search
        //TBD, this need to be fixed, implementation
        if(mIsMessageSearching) {
            user = new Mesibo.UserProfile();
            //user =  Mesibo.newUserProfile(params.peer, params.groupid, params.profile.name);
            user.name = params.profile.name;
            user.address = params.peer;
            user.groupid = params.groupid;
            user.picturePath = params.profile.picturePath;
            //user.other = params.profile.other;

            if(params.groupProfile != null) {
                user.name = params.groupProfile.name;
                user.address = params.groupProfile.address;
                user.groupid = params.groupProfile.groupid;
                user.picturePath = params.groupProfile.picturePath;
            }
        }

        if(null == user.other) {
            user.other = new UserData(user);
        }

        data = (UserData) user.other;

        /*if(null == data.getImagePath()){
            data.setImagePath(MesiboImages.getDafaultUserPath());
        }*/

        data.setMessage(params.mid, getDate(params.ts), params.getStatus(), params.isDeleted(), message);

        mTotalUnread -= data.getUnreadCount();
        if(mTotalUnread < 0)
            mTotalUnread = 0;

        if(Mesibo.isReading(params)) {
            data.setUnreadCount(0);
        }
        else {
            if (Mesibo.ORIGIN_DBSUMMARY == params.origin || Mesibo.ORIGIN_DBMESSAGE == params.origin)
                data.setUnreadCount(user.unread);
            else
                data.setUnreadCount(data.getUnreadCount() + 1);
        }

        mTotalUnread += data.getUnreadCount();

        if(Mesibo.ORIGIN_REALTIME == params.origin) {
            updateNotificationBadge();
        }

        // remove message from existing position so that it can go to top
        //Note that we must do this always as DB messages may be received while someone else is reading it
        if(true || (Mesibo.ORIGIN_DBSUMMARY != params.origin && Mesibo.ORIGIN_DBMESSAGE != params.origin)) {
            for(int i=0; i< mAdhocUserList.size(); i++) {
                // we are comparing peer and not object as it might have been changed by setUserProfile
                UserData mcd = ((UserData)mAdhocUserList.get(i).other);

                if(null != mcd && params.compare(mcd.getPeer(), mcd.getGroupId())) {
                    //TBD, if we have not reordered the list (not removed element), we can just update the row instead of table
                    mAdhocUserList.remove(i);
                    break;
                }
            }
        }

        if(Mesibo.ORIGIN_DBSUMMARY != params.origin && Mesibo.ORIGIN_DBMESSAGE != params.origin)
            mAdhocUserList.add(0, user);
        else
            mAdhocUserList.add(user);

        if(null != mUiUpdateTimer) {
            mUiUpdateTimer.cancel();
            mUiUpdateTimer = null;
        }

        if(Mesibo.ORIGIN_REALTIME == params.origin) {

            long ts = Mesibo.getTimestamp();

            // if UI is not updated recently, update it
            if((ts-mUiUpdateTimestamp) > 2000) {
                //TBD, if we have not reordered the list (message is for the top element), we can just
                // update row instead of entire table
                mAdapter.notifyChangeInData();
                return;
            }

            long timeout = 2000;
            // if old message (though realtime), then we can update little later (2000ms) else quickly (500ms)
            if((ts - params.ts) < 5000) {
                timeout = 500;
            }

            mUiUpdateTimestamp = ts; // so that it doesn't update even though messages are keep coming

            mUiUpdateTimer = new Timer();
            mUiUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mUiUpdateHandler.post(mUiUpdateRunnable);
                }
            };

            mUiUpdateTimer.schedule(mUiUpdateTimerTask, timeout);
        }

        //TBD, we can use library like

    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams params, int activity) {
        if ( Mesibo.ACTIVITY_TYPING != activity && Mesibo.ACTIVITY_LEFT != activity)
            return;

        //TBD, we got one crash with params null (08 Feb 2018), need to check
        if(null == params || null == params.profile)
            return;

        //should only happen for db messages and deleted on unsynced group
        if(params.groupid > 0 && null == params.groupProfile)
            return;

        Mesibo.UserProfile profile = params.profile;

        UserData data;
        //TBD, why not using params.groupProfile
        if(params.groupid > 0) {
            profile = Mesibo.getUserProfile(params.groupid);
            if(null == profile)
                return; //MUST not happen
        }

        data = UserData.getUserData(profile);

        if(Mesibo.ACTIVITY_LEFT == activity)
            data.clearTyping();
        else {
            // if it is group message then only we need to set profile (else user himself is typing)
            data.setTyping((params.groupid > 0) ? params.profile : null);
        }

        int position = data.getUserListPosition();
        if(position < 0)
            return;

        if(mAdhocUserList.size() <= position)
            return;

        //in case mAdhocUserList is changed
        try {
            if (profile != mAdhocUserList.get(position))
                return;
        }catch (Exception e) {
            return;
        }

        mAdapter.notifyItemChanged(position);

        /*
        for(int i=0; i< mUserProfiles.size(); i++) {
            Mesibo.UserProfile u = mUserProfiles.get(i);
            if(!TextUtils.isEmpty(params.peer) && !TextUtils.isEmpty(u.address) && u.address.equalsIgnoreCase(params.peer)) {
                UserData mcd = (UserData) u.other;
                if(null != mcd) {
                    mcd.setActivity(activity);
                    //mRecyclerView.notifyItemChanged(i);
                    break;
                }

            }
        }
        */

        //TBD

        /*
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserStatus(null, 0);
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, duration);
        */

    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams params, Mesibo.Location location) {

        addNewMessage(params, LOCATION_STRING);
        updateUiIfLastMessage(params);
    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams params, Mesibo.FileInfo fileInfo) {

        String type = ATTACHMENT_STRING;
        if(fileInfo.type == Mesibo.FileInfo.TYPE_IMAGE)
            type = IMAGE_STRING;
        else if(fileInfo.type == Mesibo.FileInfo.TYPE_VIDEO)
            type = VIDEO_STRING;
        else if(fileInfo.type == Mesibo.FileInfo.TYPE_AUDIO)
            type = AUDIO_STRING;

        addNewMessage(params, type);

        updateUiIfLastMessage(params);
        return ;
    }

    private String appendNameToMessage(Mesibo.MessageParams params, String msg) {
        String name = params.peer;
        if(null != params.profile && null != params.profile.name)
            name = params.profile.name;

        if(TextUtils.isEmpty(name)) return msg;
        String[] splited = name.split("\\s+");
        if(splited.length < 1)
            return msg;

        name = splited[0];

        if(name.length() > 12)
            name = name.substring(0, 12);

        return name + ": " + msg;
    }

    private void updateUiIfLastMessage(Mesibo.MessageParams params) {
        if(!params.isLastMessage()) return;

        // TBD, this logic is complicated, need to fix.
        // in any case we are doing non-search read because we need mUsers
        if(!mIsMessageSearching && !TextUtils.isEmpty(mSearchQuery)) {
            mAdapter.filter(mSearchQuery);
        }

        mAdapter.notifyChangeInData();
        updateNotificationBadge();

    }
    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams params, byte[] data) {
        // This we will only get for real-time origin, we filter this in DB
        if(MSGSTATUS_CALLINCOMING == params.getStatus() || MSGSTATUS_CALLOUTGOING == params.getStatus()) {
            updateUiIfLastMessage(params); //?? required
            return true;
        }

        if(params.groupid > 0 && null == params.groupProfile) {
            updateUiIfLastMessage(params);
            return true;
        }

        String str = "";
        try {
            str = new String(data, "UTF-8");
        } catch (Exception e) {
            str = "";
        }

        if(params.isDeleted()) {
            str = MESSAGE_DELETED_STRING;
        }

        if(params.groupid > 0 && params.isIncoming()) {
            str = appendNameToMessage(params, str);
        }

        if(Mesibo.MSGSTATUS_CALLMISSED == params.getStatus()) {
            str = MISSED_VIDEO_CALL;
            if((params.getType()&1) == 0)
                str = MISSED_VOICE_CALL;
        }

        addNewMessage(params, str);
        updateUiIfLastMessage(params);
        return true;
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams params) {

        for(int i=0; i< mUserProfiles.size(); i++) {
            UserData mcd = ((UserData)mUserProfiles.get(i).other);

            if (mcd.getmid() != 0 && mcd.getmid() == params.mid) {
                mcd.setStatus(params.getStatus());
                if(params.isDeleted()) {
                    mcd.setMessage(MESSAGE_DELETED_STRING);
                    mcd.setDeletedMessage(true);
                }
                mAdapter.notifyItemChanged(i);
            }
        }

    }

    //TBD, Move this to mainApplication
    @Override
    public void Mesibo_onConnectionStatus(int status) {
        Log.wtf("DO THIS", " WHEN string FAILS");
        if(status== Mesibo.STATUS_ONLINE)
            updateSubTitle(mMesiboUIOptions.onlineIndicationTitle);
        else if(status == Mesibo.STATUS_CONNECTING)
            updateSubTitle(mMesiboUIOptions.connectingIndicationTitle);
        else if(status == Mesibo.STATUS_NONETWORK)
            updateSubTitle(mMesiboUIOptions.noNetworkIndicationTitle);
        else if(status == Mesibo.STATUS_SHUTDOWN) {
            getActivity().finish();
        }
        else
            updateSubTitle(mMesiboUIOptions.offlineIndicationTitle);
    }

    private String getDate(long time) {
        int days = Mesibo.daysElapsed(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = null;
        if(days == 0) {
            date = DateFormat.format("HH:mm", cal).toString();
        } else if(days == 1) {
            date = "Yesterday";
        } else if(days < 7) {
            date = DateFormat.format("E, dd MMM", cal).toString();
        } else {
            date = DateFormat.format("dd-MM-yyyy", cal).toString();
        }

        return date;
    }

    @Override
    public void onResume(){
        super.onResume();

        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());

        // to notify app to get updated contacts if any
        if(mSelectionMode == MODE_SELECTCONTACT)
            Mesibo.lookupUserProfile(null, 2);

        /*
        Mesibo.UIHelperListner l = Mesibo.getUIHelperListner();
        if(null != l)
            l.Mesibo_onShowProfilesList();
        */
        showUserList();
    }

    @Override
    public void onPause(){
        super.onPause();

        Mesibo.removeListener(this);
    }


    @Override public void onStop(){
        super.onStop();
        if(mSelectionMode == MODE_SELECTCONTACT_FORWARD || mSelectionMode == MODE_SELECTGROUP || mSelectionMode == MODE_EDITGROUP) {
            for (Mesibo.UserProfile d : mUserProfiles) {
                d.flag &= ~Mesibo.UserProfile.FLAG_MARKED;
            }
        }
    }

    public void onLongClick() {

    }

    public void showUserList(){
        setEmptyViewText();

        if(mSelectionMode == MODE_MESSAGELIST) {

            mTotalUnread = 0;
            Mesibo.addListener(this);
            mAdhocUserList = mUserProfiles;
            mUserProfiles.clear();
            mAdapter.onResumeAdapter();

            mDbSession = new Mesibo.ReadDbSession(null, 0, mReadQuery, this);
            mDbSession.enableSummary(true);
            //mDbSession.enableMissedCalls(true); //TBD, currently api by default gives it
            mDbSession.read(MESIBO_INTITIAL_READ_USERLIST);

        } else {
            mUserProfiles.clear();

            if(!TextUtils.isEmpty(mMesiboUIOptions.createGroupTitle) && mSelectionMode == MODE_SELECTCONTACT) {
                Mesibo.UserProfile user = new Mesibo.UserProfile();
                user.address = mMesiboUIOptions.createGroupTitle;
                user.name = mMesiboUIOptions.createGroupTitle;
                user.status = MesiboConfiguration.CREATE_NEW_GROUP_MESSAGE_STRING;
                user.picturePath = null; //MesiboImages.getDefaultGroupPath();
                user.lookedup = true;

                UserData ud = new UserData(user);
                Bitmap b = MesiboImages.getDefaultGroupBitmap();
                ud.setImageThumbnail(b);
                ud.setImage(b);
                ud.setFixedImage(true); // so that it does not reset image on setUser
                user.other = ud;
                ud.setMessage(user.status);
                mUserProfiles.add(user);
            }

            if(mSelectionMode == MODE_SELECTCONTACT_FORWARD && mMesiboUIOptions.showRecentInForward) {
                mUserProfiles.addAll(Mesibo.getRecentUserProfiles());
                if(mUserProfiles.size() > 0) {
                    Mesibo.UserProfile tempUserProfile = new Mesibo.UserProfile();
                    tempUserProfile.name = String.valueOf(MesiboConfiguration.FREQUENT_USERS_STRING);
                    mUserProfiles.add(0,tempUserProfile);
                }
                Mesibo.UserProfile tempUserProfile1 = new Mesibo.UserProfile();
                tempUserProfile1.name = String.valueOf(MesiboConfiguration.ALL_USERS_STRING);
                mUserProfiles.add(tempUserProfile1);

            }



            if(mSelectionMode == MODE_EDITGROUP) {
                ArrayList<Mesibo.UserProfile> members = Mesibo.getUIHelperListner().Mesibo_onGetGroupMembers(getActivity(), mGroupId);
                if(null != members) {
                    mUserProfiles.addAll(members);
                    Mesibo.UserProfile tempUserProfile = new Mesibo.UserProfile();
                    tempUserProfile.name = String.valueOf(MesiboConfiguration.GROUP_MEMBERS_STRING);
                    mUserProfiles.add(0,tempUserProfile);
                }

                Mesibo.UserProfile tempUserProfile1 = new Mesibo.UserProfile();
                tempUserProfile1.name = String.valueOf(MesiboConfiguration.ALL_USERS_STRING);
                mUserProfiles.add(tempUserProfile1);

            }

            mUserProfiles.addAll(Mesibo.getSortedUserProfiles());

            /* filtering userlist for rogue userdata and groups in case for selection of users */
            for (int i = mUserProfiles.size() - 1; i >= 0; i--) {
                Mesibo.UserProfile user = mUserProfiles.get(i);
                if (TextUtils.isEmpty(user.address) && user.groupid==0) {
                    if (!user.name.equalsIgnoreCase(ALL_USERS_STRING) && !user.name.equalsIgnoreCase(FREQUENT_USERS_STRING) && !user.name.equalsIgnoreCase(GROUP_MEMBERS_STRING))
                        mUserProfiles.remove(i);
                }else if (TextUtils.isEmpty(user.name) && user.groupid > 0) {
                    mUserProfiles.remove(i);

                } else if(mSelectionMode == MODE_EDITGROUP || mSelectionMode == MODE_SELECTGROUP) {
                    if (user.groupid > 0) {
                        mUserProfiles.remove(i);
                    } else {
                        if(!TextUtils.isEmpty(user.address) && null != mGroupMembers && mGroupMembers.contains(user.address)) {
                            user.flag = user.flag | Mesibo.UserProfile.FLAG_MARKED;
                            showForwardLayout();
                        }
                    }
                }
            }

        }
        mAdapter.notifyChangeInData();
    }

    private void updateContacts(Mesibo.UserProfile userProfile) {
        if(null == userProfile) {
            showUserList();
            return;
        }

        if(null == userProfile.other)
            return;

        UserData data = UserData.getUserData(userProfile);
        int position = data.getUserListPosition();
        if(position >= 0)
            mAdapter.notifyItemChanged(position);

        return;

        /*
        for(int i=0; i< mAdhocUserList.size(); i++) {
            // we are comparing peer and not object as it might have been changed by setUserProfile
            if(userProfile == mAdhocUserList.get(i)) {
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
        */
    }

    @Override
    public void Mesibo_onUserProfileUpdated(final Mesibo.UserProfile userProfile, int action, boolean refresh) {
        if(!refresh)
            return;

        if(null != userProfile && null == userProfile.other)
            return;

        if(Mesibo.isUiThread()) {
            updateContacts(userProfile);
            return;
        }

        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateContacts(userProfile);
            }
        });

    }

    public class MessageContactAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private int mBackground=0;
        private Context mContext=null;
        private ArrayList<Mesibo.UserProfile> mDataList=null;
        private ArrayList<Mesibo.UserProfile> mUsers=null;
        private ArrayList<Mesibo.UserProfile> mSearchResults=null;
        private UserListFragment mHost;
        private SparseBooleanArray mSelectionItems;
        public int mCountProfileMatched = 0;

        public final static int SECTION_HEADER = 100;
        public final static int SECTION_CELLS = 300;

        public   class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
            public  TextView mSectionTitle=null;


            public SectionHeaderViewHolder(View itemView) {
                super(itemView);
                mSectionTitle = (TextView) itemView.findViewById(R.id.section_header);
            }
        }

        public   class SectionCellsViewHolder extends RecyclerView.ViewHolder  {
            public String mBoundString=null;
            public  View mView=null;
            public  ImageView mContactsProfile=null;
            public  TextView mContactsName=null;
            public  TextView mContactsTime=null;
            public  EmojiconTextView mContactsMessage=null;
            public  ImageView mContactsDeliveryStatus=null;
            public  TextView mNewMesAlert=null;
            public  MenuPopupHelper PopupMenu = null;
            public RelativeLayout mHighlightView = null;
            public int position = 0;
            public Timer mTimer = null;
            public TimerTask mTimerTask = null;

            public SectionCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.mes_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.mes_rv_name);
                mContactsTime = (TextView) view.findViewById(R.id.mes_rv_date);
                mContactsMessage = (EmojiconTextView) view.findViewById(R.id.mes_cont_post_or_details);
                mContactsDeliveryStatus = (ImageView)view.findViewById(R.id.mes_cont_status);
                mNewMesAlert = (TextView) view.findViewById(R.id.mes_alert);
                mHighlightView = (RelativeLayout) view.findViewById(R.id.highlighted_view);
            }

        }

        public MessageContactAdapter(Context context, UserListFragment host, ArrayList<Mesibo.UserProfile> list,ArrayList<Mesibo.UserProfile> searchResults) {

            this.mContext = context;
            mHost = host;
            mUsers = list;
            mSearchResults = searchResults;
            mDataList = list;
            mSelectionItems = new SparseBooleanArray();

        }


        public ArrayList<Mesibo.UserProfile> getActiveUserlist() {
            if(mIsMessageSearching)
                return mSearchResults;
            else
                return mUsers;
        }
        @Override
        public int getItemViewType(int position) {
            String address = mDataList.get(position).address;

            if (address == null && mDataList.get(position).groupid <= 0)
                return SECTION_HEADER;
            else
                return SECTION_CELLS;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == SECTION_HEADER) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_header_title, parent, false);
                return new SectionHeaderViewHolder(v);

            } else  {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list, parent, false);
                return new SectionCellsViewHolder(view);
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {


            if(vh.getItemViewType()== SECTION_HEADER) {

                //final SectionHeaderViewHolder holder = (SectionHeaderViewHolder) holderr;
                //int size = mDataList.size() - 1;
                ((SectionHeaderViewHolder)vh).mSectionTitle.setText( mDataList.get(position).name);


            }else {

                final int pos = position;
                final Mesibo.UserProfile user = mDataList.get(position);

                if(null != user)
                    Mesibo.lookupUserProfile(user, 1);

                final SectionCellsViewHolder holder = (SectionCellsViewHolder) vh;
                if(null != holder.mTimer) {
                    holder.mTimer.cancel();
                    holder.mTimer = null;
                }

                holder.position = position;

                UserData userdata = UserData.getUserData(user);
                userdata.setUser(user); // in case user is changed dynamically
                userdata.setUserListPosition(position);

                final UserData data = userdata;
                holder.mContactsName.setText(data.getUserName());

                if (mHost.mSelectionMode == MODE_MESSAGELIST) {
                    holder.mContactsTime.setVisibility(View.VISIBLE);
                    holder.mContactsTime.setText(data.getTime());
                } else {
                    holder.mContactsTime.setVisibility(View.GONE);
                }

                int imageDrawableId = 0;
                Drawable imageDrawable = null;
                int padding = 5;
                if (data.getLastMessage().equals(MESSAGE_DELETED_STRING)) {
                    imageDrawableId = DELETED_DRAWABLE;
                    imageDrawable = MesiboImages.getDeletedMessageDrawable();
                } else if (data.getLastMessage().equals(ATTACHMENT_STRING)) {
                    imageDrawableId = ATTACHMENT_ICON;
                } else if (data.getLastMessage().equals(LOCATION_STRING)) {
                    imageDrawableId = LOCATION_ICON;
                } else if (data.getLastMessage().equals(VIDEO_STRING)) {
                    imageDrawableId = VIDEO_ICON;
                } else if (data.getLastMessage().equals(IMAGE_STRING)) {
                    imageDrawableId = IMAGE_ICON;
                } else if (data.getLastMessage().equals(MISSED_VIDEO_CALL)) {
                    imageDrawableId = MISSED_VIDEOCALL_DRAWABLE;
                    imageDrawable = getMissedCallDrawable(true);
                } else if (data.getLastMessage().equals(MISSED_VOICE_CALL)) {
                    imageDrawableId = MISSED_VOICECALL_DRAWABLE;
                    imageDrawable = getMissedCallDrawable(false);
                } else {
                    imageDrawableId = 0;
                    padding = 0;
                }

                long typingTimeout = data.getTypingTimeout();
                if(typingTimeout > 0) {
                    imageDrawableId = 0;
                    padding = 0;
                }

                if (mHost.mSelectionMode == MODE_MESSAGELIST) {
                    if(null != imageDrawable) {
                        holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, null, null, null);
                    } else {
                        holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawableId, 0, 0, 0);
                    }

                    holder.mContactsMessage.setCompoundDrawablePadding(padding);

                    if(0 == typingTimeout) {
                        holder.mContactsMessage.setText(userdata.getLastMessage());
                        holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListStatusColor);
                    }
                    else {
                        Mesibo.UserProfile typingProfile = data.getTypingProfile();

                        String typingText = MesiboConfiguration.USER_STATUS_TYPING;
                        if(null != typingProfile) {
                            typingText = typingProfile.name + " is " + MesiboConfiguration.USER_STATUS_TYPING;
                        }

                        holder.mContactsMessage.setText(typingText);
                        holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListTypingIndicationColor);
                        holder.mTimer = new Timer();
                        holder.mTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(null == getActivity())
                                    return;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyItemChanged(position);
                                    }
                                });
                            }
                        };
                        holder.mTimer.schedule(holder.mTimerTask, typingTimeout);
                    }
                }
                else
                    holder.mContactsMessage.setText(null != user.status ? user.status : "");

                Bitmap b = data.getThumbnail();
                String filePath = data.getImagePath();

                if( null == b) {
                    if (filePath != null) {
                        b = BitmapFactory.decodeFile(filePath);
                        //b = null;
                    }

                    if(null == b) {
                        if(mMesiboUIOptions.useLetterTitleImage) {

                            b = mLetterTileProvider.getLetterTile(data.getUserName(), true);
                            //b = new LetterTileProvider(getActivity(),LETTER_TITLAR_SIZE).getLetterTile(data.getUserName());

                        }
                        else if(user.groupid > 0)
                            b = MesiboImages.getDefaultGroupBitmap();
                        else
                            b = MesiboImages.getDefaultUserBitmap();

                    }
                    userdata.setImageThumbnail(b);
                }

                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));

                if (mHost.mSelectionMode == MODE_MESSAGELIST && data.getUnreadCount() > 0) {
                    holder.mNewMesAlert.setVisibility(View.VISIBLE);
                    holder.mNewMesAlert.setText(String.valueOf(data.getUnreadCount()));

                } else {
                    holder.mNewMesAlert.setVisibility(View.INVISIBLE);
                }

                holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                if (0 == typingTimeout && mHost.mSelectionMode == MODE_MESSAGELIST) {
                    holder.mContactsDeliveryStatus.setVisibility(View.VISIBLE);

                    int sts = data.getStatus();
                    if (sts == Mesibo.MSGSTATUS_RECEIVEDREAD || sts == Mesibo.MSGSTATUS_RECEIVEDNEW || sts == Mesibo.MSGSTATUS_CALLMISSED || sts == Mesibo.MSGSTATUS_CUSTOM || data.isDeletedMessage()) {
                        holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                    } else {
                        holder.mContactsDeliveryStatus.setImageBitmap(MesiboImages.getStatusImage(sts));
                    }
                }

                if(mSelectionMode == MODE_SELECTCONTACT_FORWARD || mSelectionMode == MODE_SELECTGROUP || mSelectionMode == MODE_EDITGROUP) {
                    if((mDataList.get(position).flag & Mesibo.UserProfile.FLAG_MARKED ) == Mesibo.UserProfile.FLAG_MARKED) {
                        holder.mHighlightView.setVisibility(View.VISIBLE);
                        mHost.showForwardLayout();
                    }else {
                        holder.mHighlightView.setVisibility(View.GONE);
                    }
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mSelectionMode == MODE_SELECTCONTACT_FORWARD || mSelectionMode == MODE_SELECTGROUP || mSelectionMode == MODE_EDITGROUP) {
                            if ((user.flag & Mesibo.UserProfile.FLAG_MARKED) == Mesibo.UserProfile.FLAG_MARKED) {
                                user.flag = user.flag & ~Mesibo.UserProfile.FLAG_MARKED;
                            } else {
                                user.flag = user.flag | Mesibo.UserProfile.FLAG_MARKED;
                            }

                            //notifyItemChanged(pos);
                            notifyDataSetChanged();

                            if (isForwardContactsSelelcted()) {
                                mHost.showForwardLayout();
                            } else {
                                mHost.hideForwardLayout();
                            }

                        } else {

                            //TBD, it's checking user name, instead we should set flag
                            if(null != user.name && null != mMesiboUIOptions.createGroupTitle && user.name.equals(mMesiboUIOptions.createGroupTitle) && mSelectionMode == MODE_SELECTCONTACT){
                                MesiboUIManager.launchContactActivity(getActivity(), 0, MODE_SELECTGROUP, 0, false, false, null);
                                getActivity().finish();
                                return ;
                            }

                            data.clearUnreadCount();
                            Context context = v.getContext();

                            boolean handledByApp = onClickUser(user.address, user.groupid, mHost.mForwardId);

                            if(!handledByApp) {
                                MesiboUIManager.launchMessagingActivity(getActivity(), mHost.mForwardId, user.address, user.groupid);
                                mHost.mForwardId = 0;
                                if (mSelectionMode != MODE_MESSAGELIST)
                                    getActivity().finish();
                            } else {
                                mHost.mForwardId = 0;
                            }
                        }
                    }
                });

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(mSelectionMode == MODE_SELECTCONTACT_FORWARD || mSelectionMode == MODE_SELECTGROUP || mSelectionMode == MODE_EDITGROUP)
                            return true;

                        if(!TextUtils.isEmpty(mMesiboUIOptions.createGroupTitle) && user.name.equalsIgnoreCase(mMesiboUIOptions.createGroupTitle))
                            return  true;
                        try {
                            if (true || null == holder.PopupMenu) {

                                MenuBuilder menuBuilder = new MenuBuilder(getActivity());
                                MenuInflater inflater = new MenuInflater(getActivity());
                                inflater.inflate(R.menu.selected_contact, menuBuilder);
                                holder.PopupMenu = new MenuPopupHelper(mContext, menuBuilder, holder.mView);
                                holder.PopupMenu.setForceShowIcon(true);
                                menuBuilder.setCallback(new MenuBuilder.Callback() {
                                    @Override
                                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                        if (item.getItemId() == R.id.menu_remove) {
                                            if (mHost.mSelectionMode == MODE_MESSAGELIST) {
                                                Mesibo.deleteMessages(user.address, user.groupid, 0);
                                                UserData data = (UserData) user.other;
                                                if(null != data) {
                                                    data.setUnreadCount(0);
                                                }
                                                mDataList.remove(position);
                                                notifyDataSetChanged();
                                            } else if(mHost.mSelectionMode == MODE_SELECTCONTACT){
                                                mDataList.remove(position);
                                                notifyDataSetChanged();
                                                Mesibo.deleteUserProfile(user, true, false);
                                            }
                                            return true;
                                        } /*else if (item.getItemId() == R.id.menu_archive) {
                                            //user.flag |= Mesibo.UserProfile.FLAG_ARCHIVCE;
                                        } */
                                        return false;

                                    }

                                    @Override
                                    public void onMenuModeChange(MenuBuilder menu) {

                                    }
                                });
                                holder.PopupMenu.show();

                            }

                            holder.PopupMenu.show();
                        } catch (Exception e) {

                        }
                        return true;
                    }
                });
            }

        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if(!(holder instanceof SectionCellsViewHolder)) {
                return;
            }

            SectionCellsViewHolder vh = (SectionCellsViewHolder) holder;
            //vh.clearData();
        }

        @Override
        public int getItemCount() {
            handleEmptyUserList(mDataList.size());
            return mDataList.size();
        }

        public void notifyChangeInData(){

            //???? why
            //mDataList.clear();
            //mDataList.addAll(mUsers);
            mUiUpdateTimestamp = Mesibo.getTimestamp();
            mDataList = getActiveUserlist();
            notifyDataSetChanged();
        }

        public void onResumeAdapter() {
            mSearchResults.clear();
            mIsMessageSearching = false;
            mUsers.clear();
            mDataList = mUsers;
        }

        public Boolean isForwardContactsSelelcted () {
            Boolean retValue = false;
            for(Mesibo.UserProfile d : mDataList ) {
                if((d.flag & Mesibo.UserProfile.FLAG_MARKED )==Mesibo.UserProfile.FLAG_MARKED) {
                    retValue = true;
                }
            }
            return  retValue;
        }

        public void createNewGroup (){
            mMemberProfiles.clear();
            for(Mesibo.UserProfile d : mDataList ) {
                if((d.flag & Mesibo.UserProfile.FLAG_MARKED )==Mesibo.UserProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(getActivity(), null);
            getActivity().finish();
            return ;
        }

        public void modifyGroupDetail (){
            mMemberProfiles.clear();
            for(Mesibo.UserProfile d : mDataList ) {
                if((d.flag & Mesibo.UserProfile.FLAG_MARKED )==Mesibo.UserProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(getActivity(), mGroupEditBundle);
            getActivity().finish();
            return ;
        }

        public void forwardMessageToContacts () {
            mMemberProfiles.clear();
            int i = 0;
            for(Mesibo.UserProfile d : mDataList ) {
                if((d.flag & Mesibo.UserProfile.FLAG_MARKED) == Mesibo.UserProfile.FLAG_MARKED) {
                    d.flag &= ~Mesibo.UserProfile.FLAG_MARKED;
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }

            if(mMemberProfiles.size() == 0) {
                return;
            }

            for(i=0; i< mMemberProfiles.size(); i++) {
                Mesibo.UserProfile user = mMemberProfiles.get(i);
                UserData data = (UserData)(user).other;
                Mesibo.MessageParams messageParams = new Mesibo.MessageParams(data.getPeer(), data.getGroupId(), Mesibo.FLAG_DEFAULT, 0);

                // forward before read so that we get it back on read()
                for(int j=0; null != mForwardMessageIds && j <= mForwardMessageIds.length-1; j++ ) {
                    if (mForwardMessageIds[j] > 0) {
                        long mId = Mesibo.random();
                        Mesibo.forwardMessage(messageParams, mId, mForwardMessageIds[j]);
                    }
                }

                if(!TextUtils.isEmpty(mForwardedMessage)) {
                    long mId = Mesibo.random();
                    Mesibo.sendMessage(messageParams, mId, mForwardedMessage);
                }
            }


            if(!mCloseAfterForward && mMemberProfiles.size() ==1 ) {
                Mesibo.UserProfile user = mMemberProfiles.get(0);
                UserData data = (UserData)(user).other;
                data.clearUnreadCount();
                boolean handledByApp = onClickUser(user.address, user.groupid, 0);
                if(!handledByApp)
                    MesiboUIManager.launchMessagingActivity(getActivity(), 0, user.address, user.groupid);

            }

            getActivity().finish();
            mForwardId = 0;
            return ;
        }

        public void filter(String text) {
            mSearchQuery = text;
            mCountProfileMatched = 0;
            mSearchResults.clear();
            mIsMessageSearching = false;
            if(TextUtils.isEmpty(text)){
                mDataList = mUsers;
            } else {
                //mSearchResults = new ArrayList<Mesibo.UserProfile>();
                mDataList = mSearchResults;
                text = text.toLowerCase();

                for(Mesibo.UserProfile item: mUsers){
                    if(item.name.toLowerCase().contains(text) || item.name.equals(ALL_USERS_STRING) || item.name.equals(FREQUENT_USERS_STRING) || item.name.equals(GROUP_MEMBERS_STRING)) {
                        mSearchResults.add(item);
                    }
                }
                if(mSearchResults.size() > 0 && mSelectionMode == MODE_MESSAGELIST) {

                    Mesibo.UserProfile tempUserProfile = new Mesibo.UserProfile();
                    mCountProfileMatched = mSearchResults.size();
                    tempUserProfile.name = String.valueOf(mSearchResults.size())+" "+ USERS_STRING_USERLIST_SEARCH;
                    mSearchResults.add(0, tempUserProfile);
                }

                mDataList = mSearchResults;
                setEmptyViewText();

                if(mSelectionMode == MODE_MESSAGELIST) {
                    mEmptyView.setText(EMPTY_SEARCH_LIST);
                    if (!TextUtils.isEmpty(text)) {
                        mIsMessageSearching = true;
                        //Dont pass SUMMARY_FLAG when searching
                        //int returnedValue = Mesibo.setReadingSession(Mesibo.READFLAG_SENDEOR, text);
                        //Mesibo.read(MESIBO_SEARCH_READ_USERLIST, UserListFragment.this);
                        Mesibo.ReadDbSession rbd = new Mesibo.ReadDbSession(null, 0, text, UserListFragment.this);
                        rbd.read(MESIBO_SEARCH_READ_USERLIST);

                    }
                }
            }

            //notifyDataSetChanged();
        }
    }

}
