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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.AccountKitLoginResult;
import com.mesibo.uihelper.Utils.Alert;
import com.mesibo.uihelper.Utils.Log;

public class AccountKitLauncherActivity extends AppCompatActivity implements ILoginResultsInterface {
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // progress is crashing possible because we don't have view
        //mProgressDialog = Alert.getProgressDialog(this, "Please wait...");
        setContentView(R.layout.activity_accountkitlauncher);

        phoneLogin();
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin() {

        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }




    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = null;
            if(null != data)
                loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (null == loginResult || loginResult.getError() != null || loginResult.wasCancelled()) {
                sendResult(null);
            } else {
                if (loginResult.getAccessToken() != null) {
                    sendResult(loginResult.getAccessToken().getToken());
                } else {
                          //  loginResult.getAuthorizationCode();
                    sendResult(null);
                }
            }

            // Surface the result to your user in an appropriate way.
            //Toast.makeText(this,                   toastMessage,                    Toast.LENGTH_LONG).show();
        }
    }

    private void sendResult(String token) {
        boolean relaunch = false;
        ILoginInterface i = MesiboUiHelper.getLoginInterface();
        if(null != i) {
            if(null != mProgressDialog)
                mProgressDialog.show();

            relaunch = i.onAccountKitLogin(this, token, this);
        }

        if(null == token && relaunch)
            phoneLogin();
        else {
            //finish();
        }
    }

    @Override
    public void onLoginResult(boolean result, int delay) {
        //mProgressBar.setVisibility(View.GONE);
        if(null != mProgressDialog && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        if(result) {
            // if delay < 0, do nothing
            if(0 == delay) {
                finish();
            }
            else if(delay > 0) {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        finish();
                    }
                }, delay);
            }
        } else {
            phoneLogin();
        }

    }

}
