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
package com.mesibo.uihelper.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import android.support.v7.app.AlertDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.mesibo.uihelper.MesiboUiHelperConfig;
import com.mesibo.uihelper.R;

import java.util.List;


/**
 * Created by root on 6/19/15.
 */
public class Alert {
    static int mDialogid = -1;
    static DialogListener mListener = null;
    static Alert _instance;
    final public static String TAG="Alert";

    public final static int DIALOG_POSITIVE = 1;
    public final static int DIALOG_NEGATIVE = 2;
    public final static int DIALOG_CANCELED = 3;

    public static interface DialogListener {
        public void onDialog(int id, int state);
    }


    private static DialogInterface.OnClickListener mOnClickListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (null == mListener)
                return;

            dialogInterface.dismiss();

            if (i == dialogInterface.BUTTON_POSITIVE)
                mListener.onDialog(mDialogid, DIALOG_POSITIVE);
            else
                mListener.onDialog(mDialogid, DIALOG_NEGATIVE);

            mDialogid = -1;
            mListener = null;
        }
    };

    private static DialogInterface.OnCancelListener mOnCancelListner=new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if(null == mListener)
                return;

            mListener.onDialog(mDialogid, DIALOG_CANCELED);
            mDialogid = -1;
            mListener = null;
        }
    };

    public static Dialog showAlertMessage(Context activityContext, String title, String message) {
        return showAlertMessage(activityContext, title, message, true);
    }

    public static void showAlertDialog(Context context, String title, String message) {
        showAlertDialog(context, title, message, null, null);
    }

    public static void showAlertDialog(Context context, String title, String message, String positivetitle) {
        showAlertDialog(context, title, message, positivetitle, null);
    }

    public static void showAlertDialog(Context context, String title, String message, String positivetitle, String negativetitile) {
        showAlertDialog(context, title, message, positivetitle, negativetitile, true);
    }

    public static void showAlertDialog(Context context, String title, String message,
                                       String positivetitle, String negativetitile,
                                       boolean cancelable) {
        showAlertDialog(context, title, message, positivetitle, negativetitile, 0, null, cancelable);
    }

    // we assume that there will be only one dialog at a time
    public static void showAlertDialog(Context context,
                                       String title, String message,
                                       String positivetitle, String negativetitile,
                                       int id, DialogListener listener, boolean cancelable) {
        //android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        if(null == context) {
            Log.d(TAG, "Null context in showAlertDialog");
            return; //
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        // dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(cancelable);
        if(TextUtils.isEmpty(positivetitle)) {
            dialog.setPositiveButton(android.R.string.ok, listener!=null?mOnClickListener:null);
        }
        else
            dialog.setPositiveButton(positivetitle, listener!=null?mOnClickListener:null);

        if(TextUtils.isEmpty(negativetitile)) {
            dialog.setNegativeButton(android.R.string.cancel, listener != null ? mOnClickListener : null);
        }
        else
            dialog.setNegativeButton(negativetitile, listener != null ? mOnClickListener : null);

        if(null != listener) {
            mDialogid = id;
            mListener = listener;
            dialog.setOnCancelListener(mOnCancelListner);
        }

        try {
            dialog.show();
        } catch (Exception e) {
            Log.d(TAG, "Exception showing alert: " + e);
        }
    }

    public final static int PROMPT_NONETWORK=1;
    public final static int PROMPT_NOCREDITS=2;
    public final static int PROMPT_SLOWCONNECTION=3;
    public final static int PROMPT_NOTCONNECTED=4;
    public final static int PROMPT_LOGOUT=5;
    public final static int PROMPT_BUYCREDITS=6;
    public final static int PROMPT_EARNFREECREDITS=7;
    public final static int PROMPT_REDEEMFREECREDITS=8;
    public final static int PROMPT_CHANGEPHONENUMBER=9;
    public final static int PROMPT_CALLCHARGE=10; //this is handled by CALLACTIVITY and not here


    public static void prompt(Context context, int id, DialogListener listener, boolean cancelable) {
        String title=null, message = null, positivetitle = null, negativetitle = null;
        if(PROMPT_SLOWCONNECTION == id) {
            title = "Slow Connection";
            message = "You are presently connected over a slow data connection (2G) and hence the call quality MAY NOT be good. You might want to use callback instead.";
            positivetitle = "call";
            negativetitle = "Use callback";
        }
        else if(PROMPT_NOTCONNECTED == id){
            title = "No Internet Connection";
            message = "Your phone is not connected to the internet. Please check your internet connection and try again later.";
            positivetitle = "ok";
            negativetitle = null;
        }
        else if(PROMPT_NONETWORK == id){
            title = "Netwrok error";
            message = "We could not place the call. Check internet connection on your phone and try again later. You may also use callback if you have a slow conection.";
            positivetitle = "ok";
            negativetitle = "Use callback";
        }
        else if(PROMPT_NOCREDITS == id) {
            /*
            title = "No Credits";
            message = "You do not have TringMe credits to make calls.";
            if(AppRMS.getCredits() > 0.05) {
                title = "Insufficient Credits";
                message = "Your current balance is too low to make calls.";
            }
            positivetitle = "Buy Credits";
            negativetitle = null;*/
        }
        else if(PROMPT_LOGOUT == id) {
            title = "Logout?";
            message = "You will not be able to make calls till you login again. Continue?";
            positivetitle = "Logout";
        }
        else if(PROMPT_BUYCREDITS == id) {
            /*
            title = "Buy Credits";
            if(AppRMS.isFirstPayment())
                message = "Would you like to buy credits?";
            else
                message = "Would you like to add more credits?";
                */
        }
        else if(PROMPT_EARNFREECREDITS == id) {
            title = "Earn Bonus Credits";
            message = "Earn bonus credits by inviting your friends and family to use TringMe. We add bonus credits to your account when they make a purchase";
            positivetitle = "Invite & Earn";

        }
        else if(PROMPT_REDEEMFREECREDITS == id) {
            title = "Redeem Bonus Credits";
            message = "Awesome, bonus credits will be redeemed on your next purchase. Redeem now?";
            positivetitle = "Redeem";
        }
        else if(PROMPT_CHANGEPHONENUMBER == id) {
            title = "Change Phone Number?";
            message = "We will send you an SMS with the verification code to change the phone number. Continue?";
        }
        else
            return;

        showAlertDialog(context, title, message, positivetitle, negativetitle,
                id,
                listener,
                cancelable
        );
    }


    @SuppressWarnings("unused")
    public static Dialog showAlertMessage(Context context, String title, String message, boolean cancelable) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);

        if(null==adb)
            throw new NullPointerException("Application Context not available");

        adb.setCancelable(cancelable);
        if(false==cancelable) {
            DialogInterface.OnClickListener dcl = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
            adb.setPositiveButton("OK", dcl);
        }
        if(MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle(title);
        adb.setMessage(message);
        return adb.create();
    }

    public static Dialog showSelectionDialog(Context activityContext, String title, String message,
                                             String nText, DialogInterface.OnClickListener nClickHandler, String pText, DialogInterface.OnClickListener pClickHandler) {
        return showSelectionDialog(activityContext, title, message, true, nText, nClickHandler, pText, pClickHandler);
    }

    public static AlertDialog showInfoDialog(String text, Context c) {
        AlertDialog.Builder adb = new AlertDialog.Builder(c);
        if(MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setMessage(text);
        final AlertDialog ad = adb.create();
        ad.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (ad.isShowing())
                    ad.cancel();
            }
        }, 5000);
        return ad;
    }



    @SuppressWarnings("unused")
    public static Dialog showSelectionDialog(Context activityContext, String title, String message, boolean cancelable,
                                             String nText, DialogInterface.OnClickListener nClickHandler, String pText, DialogInterface.OnClickListener pClickHandler) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activityContext);

        if(null==adb)
            throw new NullPointerException("Application Context not available");

        adb.setCancelable(cancelable);
        if(MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle(title);
        adb.setMessage(message);
        if(null!=nClickHandler) {
            nText = null==nText?"Cancel":nText;
            adb.setNegativeButton(nText, nClickHandler);
        }
        if(null!=pClickHandler) {
            pText = null==pText?"OK":pText;
            adb.setPositiveButton(pText, pClickHandler);
        }
        return adb.create();
    }

    public static Dialog createCustomViewDialog(Context activityContext, String title, View contentView, boolean cancelable,
                                                String nText, DialogInterface.OnClickListener nClickHandler, String pText, DialogInterface.OnClickListener pClickHandler) {
        return createCustomViewDialog(activityContext, title, contentView, cancelable, nText, nClickHandler, pText, pClickHandler, null, null);
    }

    @SuppressWarnings("unused")
    public static Dialog createCustomViewDialog(Context activityContext, String title, View contentView, boolean cancelable,
                                                String nText, DialogInterface.OnClickListener nClickHandler, String pText, DialogInterface.OnClickListener pClickHandler,
                                                String mText, DialogInterface.OnClickListener mClickHandler) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activityContext);

        if(null==adb)
            throw new NullPointerException("Application Context not available");

        adb.setCancelable(cancelable);
        if(MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle(title);
        adb.setView(contentView);
        if(null!=nClickHandler) {
            nText = null==nText?"Cancel":nText;
            adb.setNegativeButton(nText, nClickHandler);
        }
        if(null!=pClickHandler) {
            pText = null==pText?"OK":pText;
            adb.setPositiveButton(pText, pClickHandler);
        }
        if(null!=mClickHandler) {
            mText = null==mText?"None":mText;
            adb.setNeutralButton(mText, mClickHandler);
        }
        return adb.create();
    }

    public static ProgressDialog getProgressDialog(Context c, String message) {
        //ProgressDialog progressDialog = ProgressDialog.show(c, "", "Loading..");
        //progressDialog.dismiss();

        final ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static void showCountryDialog(Context context) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        //dialog.setIcon(R.drawable.ic_launcher);
        dialog.setTitle("Select Country");
        /*
        final PhoneUtils.CountryListAdapter adapter =
                new PhoneUtils.CountryListAdapter(context, R.layout.country_list_item, R.id.country_name, R.id.country_code);
        */
        dialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        /*
        dialog.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String strName = adapter.getItem(which);

                    }
                });*/
        dialog.show();
    }

    public static void showNetworkError(Context c) {
        Alert.showAlertDialog(c,
                "No Internet Connection",
                "Your phone is not connected to the internet. Please check your internet connection and try again later."
        );
    }

    public static void showConnectionError(Context c) {
        Alert.showAlertDialog(c,
                "Connection Failed",
                "Sorry, we could not connect. Please check your internet connection and try again later"
        );
    }

    public static void showCallbackInProgress(Context c) {
        Alert.showAlertDialog(c,
                "Previous Callback in Progress",
                "Please wait for your previous callback request to finish."
        );
    }

    public static void showUnknownError(Context c) {
        Alert.showAlertDialog(c,
                "Error",
                "Sorry, something went wrong. Please try again later"
        );
    }

    public static void showPassworReset(Context c) {
        Alert.showAlertDialog(c,
                "Reset password",
                "Instructions for resetting your password have been emailed to you. Please check your spam folder if you don't see it in your Inbox"
        );
    }

    public static void showInvalidPhoneNumber(Context c) {
        Alert.showAlertDialog(c,
                "Invalid Phone Number",
                "The phone number you entered is not a valid number. Please check the number and try again. "
        );
    }

    //https://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog

    public static void showChoicesDialog(final Activity context, String title, String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builderSingle.setIcon(R.drawable.ic_launcher);

        if(!TextUtils.isEmpty(title))
            builder.setTitle(title);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}
