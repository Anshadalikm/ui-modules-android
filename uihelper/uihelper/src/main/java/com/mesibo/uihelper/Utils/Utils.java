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

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener pl, DialogInterface.OnClickListener nl) {
        //android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        if(null == context) {
            return; //
        }
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        // dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(true);

        dialog.setPositiveButton(android.R.string.ok, pl);
        dialog.setNegativeButton(android.R.string.cancel, nl);

        try {
            dialog.show();
        } catch (Exception e) {
            //Log.d(TAG, "Exception showing alert: " + e);
        }
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
}
