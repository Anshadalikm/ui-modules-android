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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.uihelper.Utils.Alert;
import com.mesibo.uihelper.Utils.BaseFragment;
import com.mesibo.uihelper.Utils.Log;

//import com.afollestad.materialdialogs.MaterialDialog;


public class PhoneVerificationFragment extends BaseFragment implements Alert.DialogListener, ILoginResultsInterface, OtpView.OtpViewListener, PhoneAuthenticationHelper.Listener {

    private boolean mForced = true;
    private static Boolean mMode = false;
    private final int PHONEVIEW=1, CODEVIEW=2, PROGRESSVIEW=3;

    private int mCurrentView = PHONEVIEW;
    private ProgressDialog mProgressDialog = null;

    TextView mTitle;
    TextView mDescription;
    TextView mBottomNote1;
    TextView mChangeMode;

    TextView mPhoneNumberText;
    TextView mCountryText;
    TextView mEnterCodeText;

    EditText mCountryCode;
    EditText mPhoneNumber;
    EditText mVerificationCode;
    Button mOk;
    TextView mError;
    View mPhoneFields;
    View mCodeFields;
    View mBottomFields;
    LinearLayout mProgressBar;
    MesiboUiHelperConfig mConfig = MesiboUiHelper.getConfig();
    Activity mActivity = null;

    View mView = null;

    PhoneAuthenticationHelper mAuth = null;
    PhoneAuthenticationHelper.PhoneNumber mPhone = new PhoneAuthenticationHelper.PhoneNumber();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle b = this.getArguments();
        if(null != b) {
            mForced = b.getBoolean("forced", true);
        }

        View v = inflater.inflate(R.layout.fragment_phone_verification_simple, container, false);
        mView = v;
        //mProgressBar = (LinearLayout) v.findViewById(R.id.progressBar_layout);
        mTitle = (TextView) v.findViewById(R.id.title);
        mDescription = (TextView) v.findViewById(R.id.description);
        mBottomNote1 = (TextView) v.findViewById(R.id.bottomNote1);
        mChangeMode = (TextView) v.findViewById(R.id.changemode);
        mCountryCode = (EditText) v.findViewById(R.id.country_code);
        mPhoneNumber = (EditText) v.findViewById(R.id.phone);
        mVerificationCode = (EditText) v.findViewById(R.id.code);

        mPhoneNumber.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        mCountryCode.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        mVerificationCode.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);


        mPhoneNumber.setTextColor(mConfig.mSecondaryTextColor);
        mCountryCode.setTextColor(mConfig.mSecondaryTextColor);
        mVerificationCode.setTextColor(mConfig.mSecondaryTextColor);

        v.setBackgroundColor(mConfig.mBackgroundColor);
        mOk = (Button) v.findViewById(R.id.button_next);
        mOk.setBackgroundColor(mConfig.mButttonColor);
        mOk.setTextColor(mConfig.mButttonTextColor);

        mPhoneNumberText = (TextView) v.findViewById(R.id.phone_number_text);
        mPhoneNumberText.setText(mConfig.mPhoneNumberSubString);
        mPhoneNumberText.setTextColor(mConfig.mSecondaryTextColor);

        mCountryText = (TextView) v.findViewById(R.id.country_text);
        mCountryText.setText(mConfig.mCountrySubString);
        mCountryText.setTextColor(mConfig.mSecondaryTextColor);

        mEnterCodeText = (TextView) v.findViewById(R.id.enter_code_text);
        mEnterCodeText.setText(mConfig.mEnterCodeSubString);
        mEnterCodeText.setTextColor(mConfig.mSecondaryTextColor);


        mTitle.setTextColor(mConfig.mPrimaryTextColor);
        mDescription.setTextColor(mConfig.mPrimaryTextColor);
        mBottomNote1.setTextColor(mConfig.mPrimaryTextColor);
        mChangeMode.setTextColor(mConfig.mPrimaryTextColor);


        mError = (TextView) v.findViewById(R.id.error);
        mError.setTextColor(mConfig.mErrorTextColor);
        mPhoneFields = (View) v.findViewById(R.id.verify_phone_fields);
        mCodeFields = (View) v.findViewById(R.id.verify_code_fields);
        mBottomFields = (View) v.findViewById(R.id.bottomInfoFields);

        mChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneVerification();
            }
        });
        mCountryCode.setText("+"+mConfig.mDefaultCountry);
        mCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.selectCountry();
                //showCountryList();
            }
        });

        //mCode = mConfig.mDefaultCountry;
        mProgressDialog = Alert.getProgressDialog(getActivity(), "Please wait...");

        mPhone.mSmartLockUrl = mConfig.mSmartLockUrl;

        mAuth = new PhoneAuthenticationHelper((AppCompatActivity) getActivity(), mPhone, this, 5001);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mFragmentListener)
            mFragmentListener.onFragmentLoaded(this, this.getClass(), true);
        showView(PHONEVIEW);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDialog(int id, int state) {

        if(0 == id) {
            if (state == Alert.DIALOG_POSITIVE) {
                mMode = false;
                mProgressDialog.show();
                ILoginInterface i = MesiboUiHelper.getLoginInterface();
                i.onLogin(getActivity(), mPhone.getNumber(), null, this);
            }

            return;
        }

        if(1 == id) {
            if (state == Alert.DIALOG_POSITIVE) {
                showOTPDialog();
                return;
            }
        }
    }

    @Override
    public boolean Mesibo_onPhoneAuthenticationNumber(PhoneAuthenticationHelper.PhoneNumber phoneNumber) {
        if(null == phoneNumber)
            return false;

        if(!TextUtils.isEmpty(phoneNumber.mCountryCode)) {
            setCountryCode(phoneNumber.mCountryCode);
        }

        if(!TextUtils.isEmpty(phoneNumber.mNationalNumber))
            mPhoneNumber.setText(phoneNumber.mNationalNumber);

        mPhoneNumber.setFocusableInTouchMode(true);
        mPhoneNumber.setFocusable(true);
        if(mPhoneNumber.requestFocus()) {
            //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        return false;
    }

    @Override
    public void Mesibo_onPhoneAuthenticationComplete() {
        Activity a = getActivity();
        if(null != a)
            a.finish();
    }


    public void startPhoneVerification() {

        // if user entered leading zero etc in number part, lets delete it
        //String number = PhoneUtils.stripNonNumeric(mPhoneNumber.getText().toString().trim(), true, true);
        mPhone.mNationalNumber = mPhoneNumber.getText().toString().trim();
        mPhone = mAuth.update(mPhone);

        if(!mPhone.mValid) {
            showError("Invalid Phone Number");
            Alert.showInvalidPhoneNumber(getActivity());
            return;
        }

        showError(null);

        //String prompt = "We are about to verify your phone number:\n\n+" + mCode+ "-" + number + "\n\nIs this number correct?";
        String conformationPrompt = mConfig.mMobileConfirmationPrompt;
        conformationPrompt = conformationPrompt.replace("%PHONENUMBER%", mPhone.mNationalNumber);
        conformationPrompt = conformationPrompt.replace("%CCODE%", mPhone.mCountryCode);

        Alert.showAlertDialog(getActivity(), mConfig.mMobileConfirmationTitle, conformationPrompt, "Yes", "No", 0, this, true);

    }

    public void onCancel() {
        getActivity().finish();
    }


    public void startCodeVerification(String code) {
        if(null == code)
            return;
        
	    int codeint = 0;
        try {
            codeint = Integer.parseInt(code);
        } catch (Exception e) {
            showError(mConfig.mInvalidPhoneTitle);
            return;
        }

        showError(null);
        mMode = true;
        mProgressDialog.show();
        ILoginInterface i = MesiboUiHelper.getLoginInterface();
        i.onLogin(getActivity(), mPhone.getNumber(), code, this);
    }

    private void setCountryCode(String code) {
        mCountryCode.setText("+" + code);
    }

    private void showError(String error) {
        if(TextUtils.isEmpty(error)) {
            mError.setVisibility(View.GONE);
            return;
        }

        mError.setText(error);
        mError.setVisibility(View.VISIBLE);
    }

    public void toggleView() {
        showError(null);
        if(PHONEVIEW == mCurrentView)
            showView(CODEVIEW);
        else
            showView(PHONEVIEW);
    }

    public void onBackKeyPressed () {
        getActivity().finish();
    }


    private void showView(int vid) {

        // HIDE keyboard --
        if(PROGRESSVIEW == vid) {
            //mProgressDialog.show();
            return;
        }

        mCurrentView = vid;

        if (PHONEVIEW == vid) {
            mChangeMode.setText(mConfig.mPhoneVerificationSkipText);
            mTitle.setText(mConfig.mPhoneVerificationTitle);
            //mDescription.setText("Mesibo will send you a one-time SMS with the verification code before you can start using Mesibo");
            mDescription.setText(mConfig.mPhoneVerificationText);
            mBottomNote1.setText(mConfig.mPhoneVerificationBottomText);
            mPhoneFields.setVisibility(View.VISIBLE);
            mCodeFields.setVisibility(View.GONE);
        } else {
            showOTPDialog();
        }
    }

    @Override
    public void OtpView_onOtp(String enteredOtp) {
        if(null != enteredOtp)
            startCodeVerification(enteredOtp);
    }

    @Override
    public void OtpView_onResend() {

    }

    public void showOTPDialog() {
        String phone = "your phone";
        if(mPhone != null) {
            phone = "+" + mPhone.getNumber();
        }

        OtpView.OtpViewConfig config = new OtpView.OtpViewConfig();
        config.mPhone = phone;

        OtpView otpView = new OtpView(getActivity(), config,this);
        otpView.showPopup(mView);
    }

    @Override
    public void onLoginResult(boolean result, int delay) {
        //mProgressBar.setVisibility(View.GONE);
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();;
        }

        if(result) {
            if (mMode) {
                mAuth.stop(true);
            } else {
                // phone registration success full
                showView(CODEVIEW);
            }
        } else {
            if (mMode) {
                // launch dialogue saying verification failed
                //String prompt = "Invalid code please enter the exact code:\n\n+" +  mVerificationCode.getText().toString() +  "\n\nIs this code correct?";
                String prompt = mConfig.mInvalidOTPMessage;
                Alert.showAlertDialog(getActivity(), mConfig.mInvalidOTPTitle, prompt, "OK", "Cancel", 1, this, true);

            } else {
                // launche dialogue saying invalid number
                //String prompt = "Please enter different phone:\n\n+" + mPhone +  "\n\n above number may be already registered";
                String prompt = mConfig.mInvalidPhoneMessage;
                prompt = prompt.replace("%MOBILENUMBER%", mPhone.getNumber());
                Alert.showAlertDialog(getActivity(), mConfig.mInvalidPhoneTitle, prompt, "OK", null, 2, this, true);

            }
        }
        Log.d("REG-Ver","on MesiboUiHelper results");
    }

    @Override
    public void onActivityResultPrivate(int requestCode, int resultCode, Intent data) {
        mAuth.onActivityResult(requestCode, resultCode, data);
    }
}
