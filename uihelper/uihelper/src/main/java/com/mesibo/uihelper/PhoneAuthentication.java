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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mesibo.contactutils.ContactUtils;

import java.lang.ref.WeakReference;

public class PhoneAuthentication implements LoginCredentials.Listener {

    private WeakReference<PhoneAuthenticationHelper.Listener> mListener = null;
    private PhoneAuthenticationHelper.PhoneNumber mPhone = null;
    private AppCompatActivity mActivity = null;
    private LoginCredentials mLoginCredentials = null;
    private ContactUtils.PhoneNumber mParsedNumber = null;
    private int mReqCode = -1;

    PhoneAuthentication(AppCompatActivity activity, PhoneAuthenticationHelper.PhoneNumber phone, PhoneAuthenticationHelper.Listener listner, int reqCode) {
        mActivity = activity;
        mReqCode = reqCode;

        if(null != listner) {
            mListener = new WeakReference<PhoneAuthenticationHelper.Listener>(listner);
        }

        update(phone);

        invokePhoneListener();

        mLoginCredentials = new LoginCredentials(getActivity(), mPhone.mSmartLockUrl, this);
        mLoginCredentials.requestHint(mReqCode, mPhone.mSmartLockUrl != null);

    }

    private AppCompatActivity getActivity() {
        return mActivity;
    }

    private void invokePhoneListener() {
        if(null == mListener) return;

        PhoneAuthenticationHelper.Listener l = mListener.get();
        if(null == l)
            return;

        l.Mesibo_onPhoneAuthenticationNumber(mPhone);
    }

    @Override
    public void onSavedCredentials(ContactUtils.PhoneNumber phone) {

        if(null != phone) {
            mPhone.mCountryCode = phone.mCountryCode;
            mPhone.mCountryName = phone.mCountry;
            mPhone.mNationalNumber = phone.mNationalNumber;
            invokePhoneListener();
            return;
        }

        //phone is null - no phone or other saved credentials found
        if(TextUtils.isEmpty(mPhone.mCountryCode))
            selectCountry();
    }

    @Override
    public void onSaveCompleted() {
        if(null == mListener) return;

        PhoneAuthenticationHelper.Listener l = mListener.get();
        if(null == l)
            return;

        l.Mesibo_onPhoneAuthenticationComplete();
    }

    void stop(boolean success) {
        if(success) {
            ContactUtils.PhoneNumber p = new ContactUtils.PhoneNumber();
            p.mCountryCode = mPhone.mCountryCode;
            p.mNationalNumber = mPhone.mNationalNumber;
            mLoginCredentials.save(p, mReqCode+1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoginCredentials.onActivityResult(requestCode, resultCode, data);
    }

    public PhoneAuthenticationHelper.PhoneNumber update(PhoneAuthenticationHelper.PhoneNumber phone) {
        mPhone = phone;
        if(null == mPhone)
            mPhone = new PhoneAuthenticationHelper.PhoneNumber();

        if(null == mPhone.mCountryCode) {
            ContactUtils.init(mActivity.getApplicationContext());
            mPhone.mCountryCode = ContactUtils.getCountryCode();
            mPhone.mCountryName = ContactUtils.getCountryName();
        }

        String phoneNumber = mPhone.getNumber();

        if(!TextUtils.isEmpty(phoneNumber))
            mParsedNumber = ContactUtils.getPhoneNumberInfo(phoneNumber);

        if(null != mParsedNumber) {
            mPhone.mValid = mParsedNumber.mValid;
            if(!TextUtils.isEmpty(mParsedNumber.mCountry))
                mPhone.mCountryName = mParsedNumber.mCountry;
            if(!TextUtils.isEmpty(mParsedNumber.mCountryCode))
                mPhone.mCountryCode = mParsedNumber.mCountryCode;
            if(!TextUtils.isEmpty(mParsedNumber.mNationalNumber))
                mPhone.mNationalNumber = mParsedNumber.mNationalNumber;
        }


        return mPhone;
    }

    public void selectCountry() {

        CountryListFragment countryListFragment = new CountryListFragment();
        //countryListFragment.setCountry(code);

        countryListFragment.setOnCountrySelected(new CountryListFragment.CountryListerer() {
            @Override
            public void onCountrySelected(String name, String code) {
                mPhone.mCountryCode = code;
                update(mPhone);
                invokePhoneListener();
            }

            @Override
            public void onCountryCanceled() {

            }
        });

        countryListFragment.show(mActivity.getSupportFragmentManager(), null);
    }

}
