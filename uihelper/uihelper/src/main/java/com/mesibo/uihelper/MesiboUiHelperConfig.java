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

import java.util.List;

/**
 * Created by root on 1/14/17.
 */

public class MesiboUiHelperConfig {
    public static List<WelcomeScreen> mScreens = null;
    public static boolean mScreenAnimation = false;

    public static String mWelcomeTermsText = "By registering, you agree to our <b>Terms of services</b> and <b>privacy policy</b>";
    public static String mWelcomeBottomText = "We will never share your information";
    public static String mWelcomeBottomTextLong = "Mesibo never publishes anything on your facebook wall\n\n";
    public static String mTermsUrl = "https://mesibo.com";
    public static String mWebsite = "https://mesibo.com";
    public static String mSmartLockUrl = "https://mesibo.com/uihelper";
    public static String mWelcomeButtonName = "Sign Up";
    public static String mName = "Mesibo";
    public static String mDefaultCountry = "1";

    public static String mPhoneVerificationTitle = "Enter your phone number";
    public static String mPhoneVerificationText = "Mesibo will send you an SMS with one-time password (OTP) to confirm your number.";
    public static String mPhoneVerificationBottomText = "Note, Mesibo may call instead of sending an SMS if you enter a landline number.";
    public static String mInvalidPhoneTitle = "Invalid Phone Number";
    public static String mPhoneVerificationSkipText = "Already have the OTP?";
    public static String mPhoneSMSVerificatinDiscriptionText = "We have sent a SMS with one-time password (OTP) to %PHONENUMBER%. It may take a few minutes to receive it.";
    public static String mPhoneCALLVerificatinDiscriptionTextRecent = "You will soon receive a call from us on %PHONENUMBER% with one-time password (OTP). Note it down and then enter it here";
    public static String mPhoneCALLVerificatinDiscriptionTextOld = "You might have received a call from us with a verification code on %PHONENUMBER%. Enter that code here";


    public static String mCodeVerificationBottomTextRecent = "You may restart the verification if you don't receive your one-time password (OTP) within 15 minutes";
    public static String mCodeVerificationBottomTextold = "You may restart the verification if you haven't received your one-time password (OTP) so far";


    public static String mCodeVerificationTitle = "Enter one-time password (OTP)";
    public static String mPhoneVerificationRestartText = "Start Again";

    public static String mCountrySubString = "Country";
    public static String mPhoneNumberSubString = "Phone Number";
    public static String mEnterCodeSubString = "Enter one-time password (OTP)";

    public static String mInvalidPhoneMessage = "Invalid phone number: %PHONENUMBER% \n\nPlease check number and try again.";

    public static String mInvalidOTPMessage ="Invalid OTP. Please enter the exact code.";
    public static String mInvalidOTPTitle = "Invalid One-time password (OTP)";

    public static String mMobileConfirmationPrompt = "We are about to verify your phone number:\n\n+%CCODE%-%PHONENUMBER%\n\nIs this number correct?";
    public static String mMobileConfirmationTitle = "Confirm Phone Number";

    public static List<String> mPermissions = null;
    public static String mPermissionsRequestMessage = "Please grant permissions to continue";
    public static String mPermissionsDeniedMessage = "App will close now since the required permissions were not granted";

    public static int mAppIconResourceId = 0;

    //public int mBackgroundColor = 0Xff2196f3;
    public static int mButttonColor = 0Xff1565c0;
    public static int mButttonTextColor = 0Xffffffff;

    public static int mWelcomeBackgroundColor = 0xff2196f3;
    public static int mWelcomeTextColor = 0xffffffff;
    public static int mBackgroundColor = 0xff2196f3;

    public static int mPrimaryTextColor = 0xffffffff;
    public static int mErrorTextColor = 0xFFFF2222;
    public static int mSecondaryTextColor = 0xff000000 ;

    //public int mTextEditorUnderlineColor = ~(mBackgroundColor);

    public MesiboUiHelperConfig() {

    }
}
