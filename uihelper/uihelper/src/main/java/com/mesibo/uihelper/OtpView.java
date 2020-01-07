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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by mesibo on 6/1/17.
 */

public class OtpView {
    Context mContext;

    //String mTitle,mMessage;
    OtpViewConfig mConfig;
    PopupWindow mPopup;
    OtpViewListener mOtpViewListener;
    EditText mOtpEditText;
    TextView mGetOtpByText = null;
    private CountDownTimer mTimer = null;


    interface OtpViewListener {
        void OtpView_onOtp(String enteredOtp);
        void OtpView_onResend();
    }


    public static class OtpViewConfig {
        //String Messages
        public String mOtpTitle = "Enter OTP";
        public String mPhone = "";
        public String mOtpMessage1 = "We have sent an OTP to ";
        public String mOtpMessage2 = ". Please enter it to continue";
        public String mOtpHint = "Enter OTP";
        public String mResendOtp =" Restart";
        public String mGetOtpMessage = "session expires in";
        public String mVerify = "Verify";
        public String mCancel = "Cancel";
        public String mPlaseEnterOtpMessage = "Please enter Otp";
        public String mTimerFinished = "Session Expired - start again!";
        public String mForGotPassword="Forgot Password";

        //Colors
        public  int mButtonTextColor = 0xffffffff;
        public  int mTitleColor = 0xff000000;
        public  int mTextColor = 0xff444444;
        public  int mSecondaryColor = 0xff888888;
        public  int mButtonColor = 0xff00868b;

        public  int mCallTimeoutInMiliSeconds = 10*60000;
        public  int mResendTimeoutInMiliSeconds = 6000;
        public  int mOtpLength = 6;
        public  int mMaxLength = 6;
        public  boolean IsPassword = false;
        public boolean mIsForOtp=true;
        public boolean mAutoSubmit=true;

    }

    OtpView(Context context, OtpViewConfig config, OtpViewListener otpViewListener) {
        mContext = context;
        mConfig = config;
        mOtpViewListener = otpViewListener;
    }

    public void showPopup(View parent){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.otp_view_layout, null, false);
        mPopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mPopup.setContentView(popUpView);


        //mPopup.setTouchable(true);
        mPopup.setFocusable(true);
       // mPopup.setOutsideTouchable(false);

        mPopup.update();
        mPopup.showAtLocation(parent, Gravity.CENTER,0,0);


        //popup.setFocusable(true);
        //mPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //popup.update();


        View container = (View) mPopup.getContentView().getRootView(); //getParent
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);

        intializePopup(popUpView);

    }

    private void intializePopup(View view) {
        final TextView title,Message,resendOtp,verifyText,canceltext,otpTimer;

        LinearLayout layoutbelow;
        View lineview1,lineview2;
        title= (TextView) view.findViewById(R.id.vT_ovl_Title);
        Message= (TextView) view.findViewById(R.id.vT_ovl_message);
        resendOtp= (TextView) view.findViewById(R.id.vT_ovl_resendOtp);
        mGetOtpByText = (TextView) view.findViewById(R.id.vT_ovl_textview);
        verifyText= (TextView) view.findViewById(R.id.vB_ovl_verify);
        canceltext= (TextView) view.findViewById(R.id.vB_cancel);
        mOtpEditText= (EditText) view.findViewById(R.id.vE_ovl_enterOtp);
        layoutbelow= (LinearLayout) view.findViewById(R.id.vL_ovl_belowbuttonLay);
        otpTimer= (TextView) view.findViewById(R.id.vT_ovl_timer);
        lineview1=view.findViewById(R.id.vV_view);
        lineview2=view.findViewById(R.id.vV_view2);
        title.setText(mConfig.mOtpTitle);
        title.setTextColor(mConfig.mTitleColor);

        Message.setText(mConfig.mOtpMessage1 + mConfig.mPhone + mConfig.mOtpMessage2);
        Message.setTextColor(mConfig.mTextColor);
        //resendOtp.setText(mConfig.mResendOtp);
        resendOtp.setTextColor(mConfig.mButtonColor);
        mGetOtpByText.setText(mConfig.mGetOtpMessage);
        mGetOtpByText.setTextColor(mConfig.mSecondaryColor);
        verifyText.setText(mConfig.mVerify);
        verifyText.setTextColor(mConfig.mButtonTextColor);
        verifyText.setAlpha(0.5f);

        canceltext.setText(mConfig.mCancel);
        canceltext.setTextColor(mConfig.mButtonTextColor);
        lineview1.setBackgroundColor(mConfig.mSecondaryColor);
        lineview2.setBackgroundColor(mConfig.mButtonTextColor);
        mOtpEditText.setHint(mConfig.mOtpHint);

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(mConfig.mMaxLength);
        mOtpEditText.setFilters(fArray);

        mOtpEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true){
                    InputMethodManager inputMgr = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    inputMgr.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

                }
            }
        });
        mOtpEditText.requestFocus();

        if(mConfig.IsPassword){

            mOtpEditText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD );
            mOtpEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }   else {

            mOtpEditText.setInputType( InputType.TYPE_CLASS_NUMBER );


        }
         if(mConfig.mIsForOtp){
             resendOtp.setText(mConfig.mResendOtp);
         }else{
             resendOtp.setText(mConfig.mForGotPassword);
         }

        GradientDrawable drawable = (GradientDrawable) layoutbelow.getBackground();

            drawable.setColor(mConfig.mButtonColor);

        mOtpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                        if(null == s || s.length()==0){

                        }   else {
                            if(s.length() >= mConfig.mOtpLength){
                                if(mConfig.mAutoSubmit) {
                                    submitAndClose();
                                    return;
                                }
                                verifyText.setEnabled(true);
                                verifyText.setAlpha(1f);
                            }  else {
                                verifyText.setEnabled(false);
                                verifyText.setAlpha(0.5f);

                            }
                        }
            }
        });






        //Timer
        if(mConfig.mCallTimeoutInMiliSeconds > 0) {


            mTimer = new CountDownTimer(mConfig.mCallTimeoutInMiliSeconds, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    //otpTimer.setText(millisUntilFinished / 1000+"");

                    String text = String.format(Locale.getDefault(), " %02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    otpTimer.setText(text);

                }

                @Override
                public void onFinish() {
                    mGetOtpByText.setText(mConfig.mTimerFinished);
                    otpTimer.setText("");
                }
            }.start();

        }else {
            mGetOtpByText.setVisibility(View.GONE);
            otpTimer.setVisibility(View.GONE);

        }

        if(mConfig.mResendTimeoutInMiliSeconds >=0 ){
            resendOtp.setEnabled(false);
            resendOtp.setAlpha(0.5f);
            new CountDownTimer(mConfig.mResendTimeoutInMiliSeconds,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    resendOtp.setEnabled(true);
                    resendOtp.setAlpha(1f);
                }
            }.start();

        }


    verifyText.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String otpText="";
        otpText=mOtpEditText.getText()+"";

        if(null == otpText || 0 == otpText.length()){

            Toast.makeText(mContext,mConfig.mPlaseEnterOtpMessage,Toast.LENGTH_SHORT).show();

        }else {
            submitAndClose();
        }



    }
});

        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                mPopup.dismiss();

                mOtpViewListener.OtpView_onOtp(null);

            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                mPopup.dismiss();

                mOtpViewListener.OtpView_onResend();
            }
        });

        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                cancelTimer();
            }
        });

    }

    private void cancelTimer() {
        if(null != mTimer)
            mTimer.cancel();
        mTimer = null;
    }

    private void submitAndClose() {
        cancelTimer();

        mPopup.dismiss();
        mOtpViewListener.OtpView_onOtp(mOtpEditText.getText()+"");
    }

    private void getSoftkeyBoard() {

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mOtpEditText, InputMethodManager.SHOW_IMPLICIT);
    }


    /**
     * Created by mesibo on 6/1/17.
     */

}
