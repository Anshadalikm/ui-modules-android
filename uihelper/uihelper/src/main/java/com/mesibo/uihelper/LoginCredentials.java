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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mesibo.contactutils.ContactUtils;
import com.mesibo.uihelper.Utils.Log;

import java.lang.ref.WeakReference;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.auth.api.credentials.CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE;

public class LoginCredentials implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String mCredentialUrl ;
    private AppCompatActivity mContext;
    public static final String TAG="LoginCredentials";
    private WeakReference<Listener> mListener = null;
    private int mSaveReqCode = -1, mGetReqCode = -1, mGetHintCode=-1;
    private ContactUtils.PhoneNumber mSavedPhone = null;
    private boolean mUseSmartLock = false;

    public interface Listener {
        void onSavedCredentials(ContactUtils.PhoneNumber phone);
        void onSaveCompleted();
    }

    LoginCredentials(AppCompatActivity context, String url, Listener listener) {
        mContext = context;
        mCredentialUrl = url;
        mListener = new WeakReference<Listener>(listener);
        googleClientInit();
    }

    private GoogleApiClient mGoogleApiClient = null;
    private void googleClientInit() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(mContext, this)
                .build();
        mGoogleApiClient.connect();
    }

    private void sendNumberToListener(String phone) {
        if(TextUtils.isEmpty(phone)) {
           return;
        }

        mSavedPhone = ContactUtils.getPhoneNumberInfo(phone);
        Listener l = mListener.get();
        if(null != l)
            l.onSavedCredentials(mSavedPhone);
    }

    void save(ContactUtils.PhoneNumber phoneNumber, final int reqCode) {
        if(null == phoneNumber || null == mCredentialUrl || TextUtils.isEmpty(mCredentialUrl))
            return;

        if(null != mSavedPhone && null != mSavedPhone.mCountryCode && null != mSavedPhone.mNationalNumber &&
                phoneNumber.mNationalNumber.contentEquals(mSavedPhone.mNationalNumber) &&
                phoneNumber.mCountryCode.contentEquals(mSavedPhone.mCountryCode) ) {
            Listener l = mListener.get();
            if(null != l)
                l.onSaveCompleted();
            return;
        }

        String phone ="+" + phoneNumber.mCountryCode + phoneNumber.mNationalNumber;

        mSaveReqCode = reqCode;
        Credential credential = new Credential.Builder(phone)
                .setAccountType(mCredentialUrl)  // a URL specific to the app
                .build();

        Auth.CredentialsApi.save(mGoogleApiClient, credential).setResultCallback(
                new ResultCallback() {
                    public void onResult(Result result) {
                        Status status = result.getStatus();

                        if (status.isSuccess()) {
                            Listener l = mListener.get();
                            if(null != l)
                                l.onSaveCompleted();
                            //Log.d(TAG, "SAVE: OK");  // already saved
                        } else if (status.hasResolution()) {
                            // Prompt the user to save
                            try {
                                status.startResolutionForResult(mContext, mSaveReqCode);
                            } catch (Exception e) {

                            }
                        }
                    }
                });
    }

    private CredentialRequest mCredentialRequest = null;
    void get(int reqCode) {
        mUseSmartLock = false;

        if(null == mCredentialUrl || TextUtils.isEmpty(mCredentialUrl))
            return;

        mGetReqCode = reqCode;

        mCredentialRequest = new CredentialRequest.Builder()
                .setAccountTypes(mCredentialUrl)  // the URL specific to the developer
                .build();

        Auth.CredentialsApi.request(mGoogleApiClient, mCredentialRequest).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            String phone = credentialRequestResult.getCredential().getId();
                            sendNumberToListener(phone);

                        } else if (credentialRequestResult.getStatus().hasResolution()) {
                            // Prompt the user to save
                            try {
                                credentialRequestResult.getStatus().startResolutionForResult(mContext, mGetReqCode);
                            } catch (Exception e) {

                            }
                        }
                    }
                });
    }

    // Construct a request for phone numbers and show the picker
    public void requestHint(int reqCode, boolean getSavedNumberIfFailed) {
        mUseSmartLock = getSavedNumberIfFailed;

        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .setEmailAddressIdentifierSupported(false)
                .build();

        mCredentialRequest = new CredentialRequest.Builder()
                .setAccountTypes(mCredentialUrl)  // the URL specific to the developer
                .build();

        //PendingIntent intent = mCredentialsClient.getHintPickerIntent(hintRequest);
        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);


        try {
            mContext.startIntentSenderForResult(intent.getIntentSender(), reqCode, null, 0, 0, 0);
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(mSaveReqCode == requestCode) {
                Listener l = mListener.get();
                if(null != l)
                    l.onSaveCompleted();
                return;
            }

            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    String phone = credential.getId();
                    sendNumberToListener(phone);
                }
            } else {
                if(mUseSmartLock) {
                    mUseSmartLock = false;
                    get(requestCode);
                    return;
                }

                Listener l = mListener.get();
                if(null != l)
                    l.onSavedCredentials(null);

                if(ACTIVITY_RESULT_NO_HINTS_AVAILABLE == resultCode) {
                    //Toast.makeText(this, "Hint Not available", Toast.LENGTH_SHORT).show();
                } else {
                    //Log.e(TAG, "Hint Read: NOT OK");
                }
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //requestHint();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
