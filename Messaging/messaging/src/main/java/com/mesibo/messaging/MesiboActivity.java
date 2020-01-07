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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mesibo.api.Mesibo;

import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_TEXT_COLOR;


public class MesiboActivity extends AppCompatActivity implements MesiboUserListFragment.FragmentListener {
    public static final String TAG="MesiboMainActivity";
    TextView contactsTitle = null;
    TextView contactsSubTitle = null;
    int mMode = 0;
    long mForwardId = 0;
    long[] mForwardIds;
    Bundle mEditGroupBundle = null;
    boolean mHideHomeBtn = false;
    boolean mKeepRunning = false;


    MesiboUI.Config mMesiboUIOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TBD, this must be fixed
        if(!Mesibo.isReady()) {
            finish();
            return;
        }

        MesiboImages.init(this);

        mMode = getIntent().getIntExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
        mForwardId = getIntent().getLongExtra(MesiboUI.MESSAGE_ID, 0);
        mForwardIds = getIntent().getLongArrayExtra(MesiboUI.MESSAGE_IDS);
        String forwardMessage = getIntent().getStringExtra(MesiboUI.MESSAGE_CONTENT);
        boolean forwardAndClose = getIntent().getBooleanExtra(MesiboUI.FORWARD_AND_CLOSE, false);
        mKeepRunning = getIntent().getBooleanExtra(MesiboUI.KEEP_RUNNING, false);
        if(getIntent().getBooleanExtra(MesiboUI.START_IN_BACKGROUND, false)) {
            moveTaskToBack(true);
        }

        if(mMode == MesiboUserListFragment.MODE_EDITGROUP)
            mEditGroupBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);

        setContentView(R.layout.activity_messages);
        mMesiboUIOptions = MesiboUI.getConfig();

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        contactsSubTitle = findViewById(R.id.contacts_subtitle);
        contactsTitle =  findViewById(R.id.contacts_title);
        Utils.setTextViewColor(contactsTitle, TOOLBAR_TEXT_COLOR);
        Utils.setTextViewColor(contactsSubTitle, TOOLBAR_TEXT_COLOR);
        Utils.setActivityStyle(this, toolbar);

        setSupportActionBar(toolbar);
        Utils.setActivityStyle(this, toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setTitle("Contacts");
        contactsTitle.setText(mMesiboUIOptions.userListTitle);

        if(mMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            contactsSubTitle.setText(mMesiboUIOptions.offlineIndicationTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(mMesiboUIOptions.enableBackButton);
        }

        if(savedInstanceState == null) {

            UserListFragment userListFragment = new UserListFragment();
            userListFragment.setListener(this);
            Bundle bl = new Bundle();
            bl.putInt(MesiboUserListFragment.MESSAGE_LIST_MODE, mMode);
            bl.putLong(MesiboUI.MESSAGE_ID, mForwardId);

            if(!TextUtils.isEmpty(forwardMessage))
                bl.putString(MesiboUI.MESSAGE_CONTENT, forwardMessage);

            bl.putLongArray(MesiboUI.MESSAGE_IDS, mForwardIds);
            if(mMode == MesiboUserListFragment.MODE_EDITGROUP)
                bl.putBundle(MesiboUI.BUNDLE, mEditGroupBundle);

            bl.putBoolean(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);

            userListFragment.setArguments(bl);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.userlist_fragment, userListFragment,"null");
            ft.addToBackStack("userListFragment");
            ft.commit();

        }
    }

    @Override
    public void Mesibo_onUpdateTitle(String title) {
        contactsTitle.setText(title);
    }

    @Override
    public void Mesibo_onUpdateSubTitle(String title) {
        if (title== null) {
            contactsSubTitle.setVisibility(View.GONE);
        } else {
            contactsSubTitle.setVisibility(View.VISIBLE);
            contactsSubTitle.setText(title);
        }
    }

    @Override
    public boolean Mesibo_onClickUser(String address, long groupid, long forwardid) {
        return false;
    }

    @Override
    public boolean Mesibo_onUserListFilter(Mesibo.MessageParams params) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mKeepRunning)
            moveTaskToBack(true);
        else
            finish();

    }

    @Override
    public  void onResume() {
        super.onResume();

        if(!Mesibo.setAppInForeground(this, 0, true))
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Now Mesibo API does it
        //Mesibo.setAppInForeground(this, 0, false);
    }
}
