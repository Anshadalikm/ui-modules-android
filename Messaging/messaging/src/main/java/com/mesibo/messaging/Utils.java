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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.targetSdkVersion;
import static com.mesibo.messaging.MesiboConfiguration.GOOGLE_PLAYSERVICE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_STATUSBAR_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_TEXT_COLOR;

public final class Utils {

    public static void setActivityStyle(AppCompatActivity context, Toolbar toolbar) {
        if(null != toolbar) {
            if(TOOLBAR_COLOR != 0)
                toolbar.setBackgroundColor(TOOLBAR_COLOR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(TOOLBAR_STATUSBAR_COLOR != 0) {
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(TOOLBAR_STATUSBAR_COLOR);
            }
        }
    }


    public static void createRoundDrawable(Context context, View view, int color, float radiusInDp) {
        GradientDrawable drawable = new GradientDrawable();
        //ColorDrawable drawable = new ColorDrawable();

        drawable.setColor(color);
        float radiusInPx = radiusInDp*8; //some random approx if context is null

        if(null != context)
            radiusInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusInDp, context.getResources().getDisplayMetrics());

        drawable.setCornerRadius(radiusInPx);
        //gradientDrawable.setCornerRadius(new float[] { topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft });
        //or setCornerRadius(gradientDrawable, 20f, 40f, 60f, 80f);

        view.setBackground(drawable);
    }

    public static void setTitleAndColor(ActionBar actionBar, String title) {
        SpannableString s = new SpannableString(title);
        if(TOOLBAR_TEXT_COLOR != 0) {
            s.setSpan(new ForegroundColorSpan(TOOLBAR_TEXT_COLOR), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (null != title)
            actionBar.setTitle(s);

    }

    public static void setTextViewColor(TextView textView, int color) {
        if(color != 0) {
            textView.setTextColor(color);
        }
    }


    public static void showAlert(Context context, String title, String mesage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(mesage);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getFileSizeString(long fileSize) {
        String unit = "KB";
        if(fileSize > 1024*1024) {
            unit = "MB";
            fileSize /= 1024*1024;
        } else {
            fileSize /= 1024;
        }

        if(fileSize < 1)
            fileSize = 1;

        return String.valueOf(fileSize) + unit;
    }

    public static  boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if(null != bmp) {

            bmp.compress(Bitmap.CompressFormat.PNG, 70, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static boolean aquireUserPermission(Context context, final String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)context,
                    permission)) {

            } else {
                ActivityCompat.requestPermissions((AppCompatActivity)context,
                        new String[]{permission},
                        REQUEST_CODE);
            }

            return false;
        }

        return true;

    }

    public static boolean aquireUserPermissions(Context context, List<String> permissions, int REQUEST_CODE) {
        List<String> permissionsNeeded = new ArrayList<>();

        for(String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                permissionsNeeded.add(permission);
            }
        }

        if(permissionsNeeded.isEmpty())
            return true;


        ActivityCompat.requestPermissions((AppCompatActivity) context,
                permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_CODE);

        return false;
    }

    public static boolean checkPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }


    /**
     * Method to verify google play services on the device
     * */

    public static boolean checkPlayServices(Activity activity, int requestCode) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        requestCode).show();
            } else {
                Toast.makeText(activity.getApplicationContext(),
                        GOOGLE_PLAYSERVICE_STRING, Toast.LENGTH_LONG)
                        .show();
                activity.finish();
            }
            return false;
        }
        return true;
    }


}
