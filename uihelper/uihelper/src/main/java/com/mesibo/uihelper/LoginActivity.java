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
package com.mesibo.uihelper;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mesibo.contactutils.ContactUtils;
import com.mesibo.uihelper.Utils.ActivityListener;
import com.mesibo.uihelper.Utils.Log;

import static com.google.android.gms.auth.api.credentials.CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE;

public class LoginActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_welcome_login);

        Bundle bundle = getIntent().getExtras();

        int type = 0;

        if(null != bundle)
            type = bundle.getInt("type", 0);

        if(savedInstanceState == null) {
            if(0 == type) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_place, new WelcomeFragment(), "welcome")
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_place,new PhoneVerificationFragment(), "verification")
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_place, new PhoneVerificationFragment(), "verification")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if(fragment instanceof PhoneVerificationFragment && fragment.isVisible()) {
                    ((PhoneVerificationFragment) fragment).onBackKeyPressed();
                    return;
                }
            }

            getSupportFragmentManager().popBackStack();
            //finish();
        }
    }



    //https://developers.google.com/identity/smartlock-passwords/android/overview
    //https://github.com/googlesamples/android-credentials/tree/master/sms-verification/android
    private final int RC_HINT = 111;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment instanceof ActivityListener && fragment.isVisible()) {
                ((ActivityListener) fragment).onActivityResultPrivate(requestCode, resultCode, data);
                return;
            }
        }
    }
}
