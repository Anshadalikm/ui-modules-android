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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.mesibo.api.Mesibo;

import static com.mesibo.messaging.MesiboConfiguration.DEFAULT_GROUP_MODE;

public class CreateNewGroupActivity extends AppCompatActivity {

    int mGroupMode;
    Bundle mGroupEditBundle = null ;
    Fragment mRequestingFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        mGroupMode = getIntent().getIntExtra(MesiboUI.GROUP_MODE, DEFAULT_GROUP_MODE);
        mGroupEditBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.nugroup_toolbar);
        Utils.setActivityStyle(this, toolbar);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String title = MesiboUI.getConfig().createGroupTitle;
        if(mGroupEditBundle != null) {
            title = MesiboUI.getConfig().modifyGroupTitle;
        }

        if(TextUtils.isEmpty(title))
            title = "";

        Utils.setTitleAndColor(ab, title);

        CreateNewGroupFragment createNewGroupFragment = CreateNewGroupFragment.newInstance(mGroupEditBundle);
        //createNewGroupFragment.setArguments(mGroupEditBundle);
        mRequestingFragment = createNewGroupFragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.nugroup_fragment_place,createNewGroupFragment,"null");
        //ft.addToBackStack("nugroup");
        ft.commit();


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
    public void onBackPressed(){

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment instanceof CreateNewGroupFragment && fragment.isVisible())
                ((CreateNewGroupFragment) fragment).OnBackPressed();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment instanceof CreateNewGroupFragment && fragment.isVisible())
                fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mRequestingFragment!=null)
            mRequestingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public  void onResume() {
        super.onResume();

        if(!Mesibo.setAppInForeground(this, 2, true))
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setAppInForeground(this, 2, false);
    }

}
